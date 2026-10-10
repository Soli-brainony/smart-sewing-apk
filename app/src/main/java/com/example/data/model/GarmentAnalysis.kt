package com.example.data.model

enum class GarmentType(val displayName: String, val iconName: String, val description: String) {
    DRESS("Dress", "dress", "One-piece garment covering torso and legs (mini, midi, maxi, A-line, fitted)."),
    SKIRT("Skirt", "skirt", "Lower-body garment hanging from waist (pencil, pleated, flared, gathered)."),
    BLOUSE("Blouse", "blouse", "Upper-body tailored top or peplum blouse."),
    SHIRT("Shirt", "shirt", "Button-down shirt with collar, placket, and set-in sleeves."),
    TROUSERS("Trousers", "trousers", "Pants with waistband, pockets, fly front, and legs."),
    JUMPSUIT("Jumpsuit", "jumpsuit", "One-piece garment combining bodice and trousers."),
    KAFTAN("Kaftan", "kaftan", "Flowing, elegant tunic garment popular across West Africa and worldwide."),
    KABA_AND_SLIT("Kaba & Slit", "kaba", "Traditional Ghanaian two-piece attire: tailored bodice (Kaba) & long skirt (Slit)."),
    SUIT("Suit / Blazer", "suit", "Structured jacket or tailored two-piece set with lapels and lining."),
    OTHER("Other Garment", "other", "Specialized or custom garment design."),
    AUTO_IDENTIFY("Let AI Identify", "auto", "Gemini AI will inspect the photo to classify the silhouette.")
}

data class MaterialItem(
    val name: String,
    val quantity: String,
    val estimatedPriceGhc: String,
    val notes: String
)

data class GarmentAnalysis(
    val garmentType: String = "Fitted Midi Dress",
    val silhouette: String = "Hourglass fitted bodice with gentle A-line skirt",
    val neckline: String = "Sweetheart neckline with slight square accent",
    val collar: String = "Collarless clean-finished neckline",
    val sleeves: String = "Short cap sleeves with slight gathering at shoulder head",
    val bodice: String = "Fitted bodice with vertical bust princess seams",
    val waistline: String = "Natural waist seam joining bodice and skirt",
    val skirtOrLower: String = "Midi-length A-line skirt with center-back vent",
    val garmentLength: String = "Below the knee (approx. 42 inches)",
    val panelsDartsPleats: String = "Front princess seam panels; back contour waist darts",
    val closures: String = "Appears to be a 22\" concealed invisible zipper at center back",
    val pockets: String = "No visible pockets (in-seam pockets can be optionally added)",
    val decorativeDetails: String = "Topstitched seamlines and subtle waist piping",
    val liningAndFinishes: String = "Bodice appears lined or faced; hem finished with blind hem stitch",
    val fabricCharacteristics: String = "Medium-weight structured woven fabric (e.g. Cotton Wax Print / Ankara, Crepe, or Linen blend)",
    val constructionSteps: List<String> = listOf(
        "1. Bodice appears to be fitted with curved princess seams joining side front and center front.",
        "2. Waist seam appears to join the bodice to the skirt with matching seam notches.",
        "3. Sleeves appear attached into a contoured armhole with slight easing at the cap.",
        "4. Back closure appears to be an invisible zipper extending past the waistline.",
        "5. Neckline appears finished with a shaped neckline facing or full bodice lining."
    ),
    val difficulty: String = "Intermediate",
    val difficultyReason: String = "Involves curved princess seams, invisible zipper insertion, and setting in curved sleeve caps.",
    val estimatedSewingHours: String = "5 – 8 hours",
    val confidenceLevel: String = "Medium-High",
    val confidenceReason: String = "Front construction, seam lines, and silhouette are clearly visible. Back zipper closure and internal facing are deduced from standard tailoring principles.",
    val uncertainties: List<String> = listOf(
        "Back construction details cannot be fully confirmed from a single front photograph.",
        "Internal interfacing weight and exact lining extent are estimated."
    ),
    val recommendedMaterials: List<MaterialItem> = listOf(
        MaterialItem("Main Fabric (Ankara / Woven Cotton / Crepe)", "2.5 – 3.0 yards (approx. 2.7m)", "GH₵ 120 – 180", "Preshrunk recommended"),
        MaterialItem("Lightweight Fusible Interfacing", "0.5 yard", "GH₵ 15 – 25", "For neckline facings"),
        MaterialItem("Invisible Zipper (22-inch)", "1 piece", "GH₵ 10 – 15", "Matching fabric color"),
        MaterialItem("Matching All-Purpose Polyester Thread", "2 spools", "GH₵ 16 – 20", "Gutermann or Coats & Clark"),
        MaterialItem("Hook & Eye Closure", "1 pair", "GH₵ 5", "For waist / neckline reinforcement")
    ),
    val recommendedTools: List<String> = listOf(
        "Sewing machine (straight stitch & zigzag/overcast)",
        "Fabric shears & thread snips",
        "Tailor's chalk or disappearing fabric marker",
        "Measuring tape & clear French curve ruler",
        "Glass-head pins & magnetic pin cushion",
        "Steam iron & pressing ham",
        "Seam ripper"
    )
)
