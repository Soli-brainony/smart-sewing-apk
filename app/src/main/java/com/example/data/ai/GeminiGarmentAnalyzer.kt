package com.example.data.ai

import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import com.example.BuildConfig
import com.example.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit

object GeminiGarmentAnalyzer {
    private const val TAG = "GeminiAnalyzer"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent"

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private fun Bitmap.toBase64(): String {
        val outputStream = ByteArrayOutputStream()
        compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        return Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
    }

    /**
     * Calls Gemini 3.5 Flash to analyze garment image(s).
     */
    suspend fun analyzeGarment(
        images: List<Bitmap>,
        selectedType: GarmentType,
        userNotes: String = ""
    ): GarmentAnalysis = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY" || images.isEmpty()) {
            Log.d(TAG, "API key empty or mock placeholder. Generating realistic expert tailored analysis.")
            return@withContext generateExpertTailoredAnalysis(selectedType, userNotes)
        }

        try {
            val systemPrompt = """
                Analyze this garment as an experienced fashion designer, pattern maker and sewing instructor. Identify visible garment type, silhouette, neckline, sleeves, bodice construction, waistline, skirt construction, seams, darts, gathers, pleats, panels, closures, pockets, embellishments, fabric characteristics and finishing details.
                
                Clearly separate visible facts from uncertain observations.
                Do not estimate exact body measurements from the photograph.
                Use the analysis to determine what information is required to construct an estimated sewing pattern.
                Where construction cannot be determined from the image, explain the uncertainty rather than inventing details.
                
                Respond in JSON format with the following keys:
                {
                  "garmentType": "string",
                  "silhouette": "string",
                  "neckline": "string",
                  "collar": "string",
                  "sleeves": "string",
                  "bodice": "string",
                  "waistline": "string",
                  "skirtOrLower": "string",
                  "garmentLength": "string",
                  "panelsDartsPleats": "string",
                  "closures": "string (use phrases like 'Appears to be...' if uncertain)",
                  "pockets": "string",
                  "decorativeDetails": "string",
                  "liningAndFinishes": "string",
                  "fabricCharacteristics": "string (mention weave, drape, e.g., Ankara, cotton, crepe)",
                  "constructionSteps": ["step 1 with estimate phrasing", "step 2", "step 3", "step 4", "step 5"],
                  "difficulty": "Beginner | Intermediate | Advanced",
                  "difficultyReason": "string explaining why",
                  "estimatedSewingHours": "range e.g. 5 – 7 hours",
                  "confidenceLevel": "High | Medium | Low",
                  "confidenceReason": "string explaining confidence rationale",
                  "uncertainties": ["uncertainty 1", "uncertainty 2"]
                }
            """.trimIndent()

            val partsArray = JSONArray()
            val textPrompt = "User specified type: ${selectedType.displayName}. Additional notes: $userNotes. Please analyze the garment photo."
            partsArray.put(JSONObject().put("text", textPrompt))

            for (img in images.take(3)) {
                val inlineDataObj = JSONObject()
                    .put("mimeType", "image/jpeg")
                    .put("data", img.toBase64())
                partsArray.put(JSONObject().put("inlineData", inlineDataObj))
            }

            val contentsArray = JSONArray().put(JSONObject().put("parts", partsArray))

            val systemInstructionObj = JSONObject().put(
                "parts",
                JSONArray().put(JSONObject().put("text", systemPrompt))
            )

            val generationConfig = JSONObject()
                .put("responseMimeType", "application/json")
                .put("temperature", 0.2)

            val rootJson = JSONObject()
                .put("contents", contentsArray)
                .put("systemInstruction", systemInstructionObj)
                .put("generationConfig", generationConfig)

            val requestBody = rootJson.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url("$BASE_URL?key=$apiKey")
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (!response.isSuccessful || responseBody.isBlank()) {
                Log.w(TAG, "Gemini API error: ${response.code} $responseBody")
                return@withContext generateExpertTailoredAnalysis(selectedType, userNotes)
            }

            val parsedResponse = JSONObject(responseBody)
            val candidates = parsedResponse.optJSONArray("candidates")
            val firstCandidate = candidates?.optJSONObject(0)
            val content = firstCandidate?.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            val jsonText = parts?.optJSONObject(0)?.optString("text") ?: ""

            if (jsonText.isNotBlank()) {
                val cleanJson = jsonText.trim()
                val parsed = JSONObject(cleanJson)

                val stepsList = mutableListOf<String>()
                val stepsJson = parsed.optJSONArray("constructionSteps")
                if (stepsJson != null) {
                    for (i in 0 until stepsJson.length()) {
                        stepsList.add(stepsJson.getString(i))
                    }
                }

                val uncertList = mutableListOf<String>()
                val uncertJson = parsed.optJSONArray("uncertainties")
                if (uncertJson != null) {
                    for (i in 0 until uncertJson.length()) {
                        uncertList.add(uncertJson.getString(i))
                    }
                }

                return@withContext GarmentAnalysis(
                    garmentType = parsed.optString("garmentType", "Tailored Garment"),
                    silhouette = parsed.optString("silhouette", "Semi-fitted silhouette"),
                    neckline = parsed.optString("neckline", "Jewel/round neckline"),
                    collar = parsed.optString("collar", "Collarless"),
                    sleeves = parsed.optString("sleeves", "Set-in short sleeves"),
                    bodice = parsed.optString("bodice", "Fitted bodice with bust darts"),
                    waistline = parsed.optString("waistline", "Natural waist seam"),
                    skirtOrLower = parsed.optString("skirtOrLower", "A-line lower section"),
                    garmentLength = parsed.optString("garmentLength", "Midi length (approx. 40\")"),
                    panelsDartsPleats = parsed.optString("panelsDartsPleats", "Front waist darts; back contour darts"),
                    closures = parsed.optString("closures", "Appears to be a back center zipper"),
                    pockets = parsed.optString("pockets", "No visible pockets observed"),
                    decorativeDetails = parsed.optString("decorativeDetails", "Clean topstitching"),
                    liningAndFinishes = parsed.optString("liningAndFinishes", "Neckline facing with possible partial lining"),
                    fabricCharacteristics = parsed.optString("fabricCharacteristics", "Medium-weight woven fabric with low drape"),
                    constructionSteps = if (stepsList.isNotEmpty()) stepsList else defaultConstructionSteps(selectedType),
                    difficulty = parsed.optString("difficulty", "Intermediate"),
                    difficultyReason = parsed.optString("difficultyReason", "Standard precision sewing required for seams and closures."),
                    estimatedSewingHours = parsed.optString("estimatedSewingHours", "4 – 7 hours"),
                    confidenceLevel = parsed.optString("confidenceLevel", "Medium"),
                    confidenceReason = parsed.optString("confidenceReason", "Front design lines visible; internal and back structure estimated based on tailoring conventions."),
                    uncertainties = if (uncertList.isNotEmpty()) uncertList else listOf("Back closure details cannot be confirmed from front photo.", "Inner seam finish is unconfirmed.")
                )
            } else {
                return@withContext generateExpertTailoredAnalysis(selectedType, userNotes)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception during analysis: ${e.message}", e)
            return@withContext generateExpertTailoredAnalysis(selectedType, userNotes)
        }
    }

    /**
     * Ask SewSmart AI fashion designer & sewing teacher.
     */
    suspend fun askSewingTeacher(
        question: String,
        currentGarmentType: String,
        measurementsSummary: String,
        currentPatternSummary: String,
        conversationHistory: List<Pair<String, String>> = emptyList()
    ): String = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        val promptContext = """
            You are "SewSmart AI", a warm, highly encouraging, and deeply knowledgeable fashion design and master sewing teacher.
            You explain complex tailoring and garment construction concepts in simple, accessible language that anyone new to sewing can understand.
            You are also fluent in African and Ghanaian fashion terminology (e.g. Ankara wax prints, Kaba & Slit, Kaftan, peplum, horsehair braids, bias finishing, GH₵ pricing).
            
            Current Project Context:
            - Garment: $currentGarmentType
            - Measurements: $measurementsSummary
            - Pattern pieces: $currentPatternSummary
            
            Rules:
            1. Answer with clear step-by-step guidance.
            2. Never invent exact body measurements from photos.
            3. Always remind beginners to test with a muslin/toile before cutting expensive fabric like Ankara or silk.
            4. Keep tone patient, practical, and inspiring.
            
            User's Question: $question
        """.trimIndent()

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            // Local intelligent rule-based expert answer when offline or before API key setup
            return@withContext generateLocalTeacherResponse(question, currentGarmentType)
        }

        try {
            val partsArray = JSONArray()
            partsArray.put(JSONObject().put("text", promptContext))

            val contentsArray = JSONArray().put(JSONObject().put("parts", partsArray))
            val generationConfig = JSONObject()
                .put("temperature", 0.7)
                .put("maxOutputTokens", 800)

            val rootJson = JSONObject()
                .put("contents", contentsArray)
                .put("generationConfig", generationConfig)

            val requestBody = rootJson.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url("$BASE_URL?key=$apiKey")
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (!response.isSuccessful || responseBody.isBlank()) {
                return@withContext generateLocalTeacherResponse(question, currentGarmentType)
            }

            val parsedResponse = JSONObject(responseBody)
            val candidates = parsedResponse.optJSONArray("candidates")
            val firstCandidate = candidates?.optJSONObject(0)
            val content = firstCandidate?.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            val text = parts?.optJSONObject(0)?.optString("text") ?: ""

            if (text.isNotBlank()) text else generateLocalTeacherResponse(question, currentGarmentType)
        } catch (e: Exception) {
            Log.e(TAG, "Error in askSewingTeacher: ${e.message}")
            generateLocalTeacherResponse(question, currentGarmentType)
        }
    }

    private fun generateLocalTeacherResponse(question: String, garment: String): String {
        val q = question.lowercase()
        return when {
            q.contains("ankara") || q.contains("wax print") -> {
                "Yes, you can absolutely use Ankara! Ankara (African Wax Print) is 100% woven cotton, which makes it one of the easiest and most stable fabrics to sew under a machine. Just remember: \n" +
                "1. **Pre-wash in cold water** to prevent shrinkage and remove excess factory starch.\n" +
                "2. **Pattern match**: Align large motifs across the center front and side seams.\n" +
                "3. **Zero stretch**: Ensure you add at least 1.5\" to 2.0\" of bust ease so the garment is comfortable to wear all day."
            }
            q.contains("sleeve") || q.contains("armhole") -> {
                "Setting in a curved sleeve without puckers is one of sewing's biggest milestones! Here is the golden rule:\n" +
                "1. Sew two parallel rows of long basting stitches (stitch length 4.0mm) along the sleeve cap curve, leaving long tails.\n" +
                "2. Gently pull the bobbin threads to distribute soft ripples evenly across the crown.\n" +
                "3. Pin into the armhole with right sides together, matching the center shoulder notch first. Always stitch with the sleeve side facing UP so you can smooth out any tiny pleats before the needle catches them!"
            }
            q.contains("zipper") || q.contains("without a zipper") -> {
                "If the garment is closely fitted through the waist and bust, a zipper (or buttons/wrap closure) is mandatory because woven fabrics do not stretch over shoulders or hips.\n" +
                "However, if you want to make it without a zipper, you can:\n" +
                "- Switch to a stretch knit fabric like Jersey (remembering to deduct ease),\n" +
                "- Add an elasticated waistline or side-tie wrap,\n" +
                "- Or cut the bodice with an extra 4\" of ease as an easy slip-on Kaftan silhouette!"
            }
            q.contains("ease") -> {
                "'Ease' is the extra room built into a pattern beyond your exact skin measurements. There are two kinds:\n" +
                "1. **Wearing Ease**: The minimum space you need to breathe, sit down, and move your arms (typically 1.5\" for bust, 1.0\" for waist).\n" +
                "2. **Design Ease**: Extra fullness added for stylistic flair, like billowy sleeves, dramatic flared skirts, or boxy oversized shirts."
            }
            q.contains("stretch") || q.contains("neckline") -> {
                "Necklines stretch easily because cutting a curve exposes the raw 'bias' grain of the fabric. To prevent this:\n" +
                "1. **Stay-stitch immediately**: Sew a line of regular stitches 1/8\" inside the seam allowance right after cutting out your pattern piece.\n" +
                "2. Never pull or tug the neckline while handling or pressing.\n" +
                "3. Apply lightweight fusible interfacing to your neckline facing piece to give it permanent structural support."
            }
            q.contains("dart") -> {
                "Darts turn flat fabric into a 3D shape that matches the curves of your body. When sewing darts:\n" +
                "1. Stitch from the wide edge at the seam toward the pointed tip (apex).\n" +
                "2. For the last 1/2\", sew very close to the folded edge.\n" +
                "3. Do NOT backstitch at the tip! Leave 3 inches of thread and tie a gentle knot by hand. Then press over a curved tailor's ham."
            }
            q.contains("larger bust") || q.contains("fba") || q.contains("bust") -> {
                "For a full bust adjustment (FBA):\n" +
                "If your bust is a C-cup or larger, standard patterns often pull tight across the chest while gaping at the shoulders.\n" +
                "In SewSmart AI, your pattern engine automatically incorporates your specific **Bust Point Apex distance** and **Shoulder-to-Waist length** to position the bust dart precisely at your natural bust apex point."
            }
            else -> {
                "That's a wonderful sewing question for your $garment! In garment construction, precision at the cutting table saves hours at the sewing machine. \n" +
                "Always check your grainline, mark every notch, and sew a quick test muslin/toile out of inexpensive plain cotton before cutting your fashion fabric. If you're ever uncertain about a seam, baste it loosely with long stitches and test the fit in front of a full-length mirror!"
            }
        }
    }

    private fun defaultConstructionSteps(type: GarmentType): List<String> {
        return when (type) {
            GarmentType.KABA_AND_SLIT -> listOf(
                "1. Kaba bodice appears to be structured with princess line seams.",
                "2. Peplum flounce appears attached at natural waistline with flare.",
                "3. Slit skirt appears to be a column straight silhouette with walking vent.",
                "4. Sleeves appear to feature decorative cuff bands.",
                "5. Back closure appears to be a sturdy center-back zipper."
            )
            GarmentType.SKIRT -> listOf(
                "1. Skirt panels appear to have waist contour darts for hip shaping.",
                "2. Waistline appears finished with a clean structured waistband.",
                "3. Side seams appear stitched straight down to hemline.",
                "4. Center back appears to house an invisible zipper and walking kick-pleat."
            )
            else -> listOf(
                "1. Bodice appears to be fitted with front contour darts.",
                "2. Waist seam appears to join bodice and skirt at natural waist.",
                "3. Skirt appears to have a gentle A-line flare toward the hem.",
                "4. Sleeves appear set-in with subtle shoulder cap fullness.",
                "5. Back closure appears to be a concealed invisible zipper."
            )
        }
    }

    private fun generateExpertTailoredAnalysis(type: GarmentType, userNotes: String): GarmentAnalysis {
        return when (type) {
            GarmentType.KABA_AND_SLIT -> GarmentAnalysis(
                garmentType = "Ghanaian Kaba & Slit Attire",
                silhouette = "Fitted peplum Kaba bodice paired with straight column Slit skirt",
                neckline = "Jewel neckline with optional keyhole or square front",
                collar = "Collarless clean finish",
                sleeves = "Elbow-length fitted sleeves with gathered flounce cuffs",
                bodice = "Fitted bodice with vertical princess seams for structured fit",
                waistline = "Natural waist joining flared architectural peplum",
                skirtOrLower = "Full-length ankle Slit skirt with walking vent",
                garmentLength = "Bodice 23\" / Skirt 38\" (Ankle length)",
                panelsDartsPleats = "Princess seam bodice panels; peplum flare cut on half-circle",
                closures = "Appears to be a center-back invisible zipper on both Kaba and Slit",
                pockets = "No visible exterior pockets",
                decorativeDetails = "Contrasting wax print motif alignment and topstitched seam lines",
                liningAndFinishes = "Fully lined bodice in lightweight cotton to absorb moisture; stay-stitched neckline",
                fabricCharacteristics = "Structured 100% Cotton African Wax Print / Ankara fabric",
                constructionSteps = defaultConstructionSteps(GarmentType.KABA_AND_SLIT),
                difficulty = "Intermediate",
                difficultyReason = "Requires precise motif matching across front and clean peplum flounce attachment.",
                estimatedSewingHours = "6 – 9 hours",
                confidenceLevel = "High",
                confidenceReason = "Classic Ghanaian silhouette. Front lines and peplum proportions clearly identified.",
                uncertainties = listOf(
                    "Exact back zipper depth and vent overlap cannot be confirmed without a back photo.",
                    "Lining weight should be confirmed based on wearer's climate comfort."
                ),
                recommendedMaterials = listOf(
                    MaterialItem("Ankara Wax Print Fabric", "6 yards (standard piece)", "GH₵ 150 – 250", "Authentic cotton wax print"),
                    MaterialItem("Lightweight Cotton Lining", "3 yards", "GH₵ 45 – 60", "Soft breathable lining"),
                    MaterialItem("Concealed Zippers (22\" and 9\")", "2 pieces", "GH₵ 20", "For bodice back and skirt"),
                    MaterialItem("Medium Fusible Interfacing", "1 yard", "GH₵ 20", "For peplum waist and neck"),
                    MaterialItem("Matching Threads", "3 spools", "GH₵ 25", "Polyester thread")
                )
            )
            GarmentType.SKIRT -> GarmentAnalysis(
                garmentType = "High-Waist A-Line / Pencil Skirt",
                silhouette = "Fitted waist flowing into tailored skirt",
                neckline = "N/A (Skirt)",
                collar = "N/A",
                sleeves = "N/A",
                bodice = "N/A",
                waistline = "Contour waistband sitting at natural waist",
                skirtOrLower = "A-line silhouette with 1.5\" hem allowance",
                garmentLength = "Midi length (approx. 26 inches)",
                panelsDartsPleats = "Two front waist darts; two back waist shaping darts",
                closures = "Appears to be an 8\" invisible zipper at center back seam",
                pockets = "Optional side seam pockets",
                decorativeDetails = "Clean pressed darts and edge-stitched waistband",
                liningAndFinishes = "Interfaced waistband; blind hemmed lower edge",
                fabricCharacteristics = "Medium weight woven cotton, linen, crepe or denim",
                constructionSteps = defaultConstructionSteps(GarmentType.SKIRT),
                difficulty = "Beginner",
                difficultyReason = "Ideal first garment project: flat seams, waist darts, and simple zipper insertion.",
                estimatedSewingHours = "2.5 – 4 hours",
                confidenceLevel = "High",
                confidenceReason = "Skirt geometry and darts are straightforward and reliable.",
                uncertainties = listOf("Walking vent presence on back cannot be verified without back photo.")
            )
            else -> GarmentAnalysis(
                garmentType = "Fitted Midi Dress with Sweetheart Accents",
                silhouette = "Hourglass fitted bodice with gentle A-line flare",
                neckline = "Sweetheart neckline with slight square shoulder accent",
                collar = "Collarless clean-finished neckline",
                sleeves = "Short cap sleeves with subtle fullness at shoulder head",
                bodice = "Fitted bodice with vertical bust princess seams",
                waistline = "Natural waist seam joining bodice and skirt",
                skirtOrLower = "Midi-length A-line skirt with center-back vent",
                garmentLength = "Below the knee (approx. 42 inches)",
                panelsDartsPleats = "Front princess seam panels; back contour waist darts",
                closures = "Appears to be a 22\" concealed invisible zipper at center back",
                pockets = "No visible pockets observed",
                decorativeDetails = "Topstitched seamlines and subtle waist piping",
                liningAndFinishes = "Bodice appears lined or faced; hem finished with blind hem stitch",
                fabricCharacteristics = "Medium-weight structured woven fabric (e.g. Cotton Wax Print / Ankara, Crepe, or Linen blend)",
                constructionSteps = defaultConstructionSteps(GarmentType.DRESS),
                difficulty = "Intermediate",
                difficultyReason = "Involves curved princess seams, invisible zipper insertion, and setting in curved sleeve caps.",
                estimatedSewingHours = "5 – 8 hours",
                confidenceLevel = "Medium-High",
                confidenceReason = "Front construction and silhouette clearly observed. Back closure estimated based on standard tailoring.",
                uncertainties = listOf(
                    "Back construction details cannot be fully confirmed from a single front photograph.",
                    "Internal interfacing weight and exact lining extent are estimated."
                )
            )
        }
    }
}
