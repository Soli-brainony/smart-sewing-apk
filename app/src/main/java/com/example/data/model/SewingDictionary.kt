package com.example.data.model

data class DictionaryTerm(
    val term: String,
    val pronunciation: String,
    val simpleDefinition: String,
    val whyItMatters: String,
    val beginnerTip: String,
    val category: String // "Basics", "Pattern Making", "Techniques", "Fabrics"
)

object SewingDictionaryData {
    val terms = listOf(
        DictionaryTerm(
            term = "Seam Allowance",
            pronunciation = "SEEM uh-LOW-uhns",
            simpleDefinition = "The extra strip of fabric between the stitching line and the raw cut edge.",
            whyItMatters = "Without seam allowance, sewing your pieces together will make the finished garment too tight or small.",
            beginnerTip = "In commercial patterns and SewSmart AI, standard seam allowance is 1/2\" (1.3 cm) or 5/8\" (1.5 cm). Follow your sewing machine throat plate markings.",
            category = "Basics"
        ),
        DictionaryTerm(
            term = "Grainline",
            pronunciation = "GRAYN-lyne",
            simpleDefinition = "The direction of the warp threads running parallel to the finished edge (selvage) of the fabric.",
            whyItMatters = "Woven fabrics stretch very little along the straight grain. If pattern pieces are cut off-grain, the clothes twist awkwardly around your body.",
            beginnerTip = "Always measure from both ends of the pattern's grainline arrow to the selvage edge to ensure they are exactly equal before cutting.",
            category = "Pattern Making"
        ),
        DictionaryTerm(
            term = "Bias",
            pronunciation = "BY-uhs",
            simpleDefinition = "A 45-degree angle diagonal across the woven grain of the fabric.",
            whyItMatters = "Woven fabric cut on the bias has maximum natural stretch, fluid drape, and hugs body curves beautifully.",
            beginnerTip = "Bias binding strips make neat finishes for curved armholes and round necklines because they bend effortlessly around curves without puckering.",
            category = "Techniques"
        ),
        DictionaryTerm(
            term = "Dart",
            pronunciation = "DART",
            simpleDefinition = "A stitched fold of fabric tapering to a point, turning flat fabric into a 3D curved shape.",
            whyItMatters = "Darts contour flat cloth to fit body curves like the bust, waist, hips, and shoulder blades.",
            beginnerTip = "Never backstitch at the pointed apex tip of a dart! Stitch gently off the folded edge, leave long thread tails, and tie a double knot by hand.",
            category = "Techniques"
        ),
        DictionaryTerm(
            term = "Ease",
            pronunciation = "EEZ",
            simpleDefinition = "The difference between your actual body measurements and the larger measurements of the garment.",
            whyItMatters = "Wearing ease lets you breathe, sit, walk, and raise your arms. Design ease provides dramatic silhouettes (oversized, flowing, or puff).",
            beginnerTip = "A fitted woven dress requires at least 1.5\" to 2.0\" of bust ease and 1.0\" of waist ease so you can sit and eat comfortably.",
            category = "Basics"
        ),
        DictionaryTerm(
            term = "Facing",
            pronunciation = "FAY-sing",
            simpleDefinition = "A small piece of fabric shaped like the garment edge (e.g. neckline or armhole) used to finish raw edges smoothly on the inside.",
            whyItMatters = "Facings give a clean, enclosed finish without visible exterior stitches.",
            beginnerTip = "Always understitch the facing (sewing the seam allowance to the facing) to keep it rolling neatly toward the inside.",
            category = "Techniques"
        ),
        DictionaryTerm(
            term = "Interfacing",
            pronunciation = "IN-ter-fay-sing",
            simpleDefinition = "An extra layer of stiffening fabric (usually iron-on fusible) placed inside collars, cuffs, and facings.",
            whyItMatters = "It adds crispness, structure, and prevents buttonholes and zippers from distorting soft fabric.",
            beginnerTip = "Use lightweight fusible interfacing for silk, crepe, and Ankara facings; use medium weight for collars and waistbands.",
            category = "Basics"
        ),
        DictionaryTerm(
            term = "Gather",
            pronunciation = "GATH-er",
            simpleDefinition = "Pulling parallel basting threads to draw up fabric into small, even soft ripples or ruffles.",
            whyItMatters = "Allows a wider piece of fabric (like a skirt or puff sleeve) to fit neatly onto a smaller section (like a waist or cuff).",
            beginnerTip = "Always sew two parallel rows of long basting stitches 1/4\" apart; pulling both bobbin threads together produces perfectly distributed ripples.",
            category = "Techniques"
        ),
        DictionaryTerm(
            term = "Pleat",
            pronunciation = "PLEET",
            simpleDefinition = "A deliberate fold formed by doubling fabric back upon itself and pressing or stitching it into place.",
            whyItMatters = "Provides crisp, tailored fullness and architectural lines in skirts, kilt folds, and trousers.",
            beginnerTip = "Use tailor's chalk to mark fold lines, pin securely, and steam press with a pressing cloth for razor-sharp creases.",
            category = "Techniques"
        ),
        DictionaryTerm(
            term = "Notch",
            pronunciation = "NOTCH",
            simpleDefinition = "A small V-shaped mark or slit cut along the seam allowance of a pattern piece.",
            whyItMatters = "Notches act as guideposts showing exactly where two corresponding pieces (e.g. front bodice and back bodice, or sleeve and armhole) must align.",
            beginnerTip = "Never clip notches deeper than 1/8\" into the seam allowance, or you risk weakening the structural seam.",
            category = "Pattern Making"
        ),
        DictionaryTerm(
            term = "Right Side & Wrong Side",
            pronunciation = "RYT side / RAWNG side",
            simpleDefinition = "'Right side' is the outside face of the fabric meant to be seen; 'Wrong side' is the interior underside.",
            whyItMatters = "Almost all garment seams start with pieces pinned 'Right Sides Together' (RST) so stitching is hidden on the inside.",
            beginnerTip = "For solid fabrics where both sides look identical, use tailor's chalk to draw a quick 'X' on the wrong side of each cut piece.",
            category = "Basics"
        ),
        DictionaryTerm(
            term = "Basting",
            pronunciation = "BAY-sting",
            simpleDefinition = "Long, loose temporary stitches sewn by hand or machine (maximum stitch length 4.0–5.0mm).",
            whyItMatters = "Holds slippery pieces together temporarily for a fitting check before permanent tight stitching.",
            beginnerTip = "Do not backstitch when basting! Leave long tails so threads pull out effortlessly once the fitting is approved.",
            category = "Techniques"
        ),
        DictionaryTerm(
            term = "Stay Stitching",
            pronunciation = "STAY STITCH-ing",
            simpleDefinition = "A single row of regular stitching sewn just inside the seamline (usually 1/8\" inside) on curved edges before assembly.",
            whyItMatters = "Curved necklines and armholes stretch out of shape easily when handled; stay stitching locks their shape permanently.",
            beginnerTip = "Stitch curves from the shoulder seam toward center front, never pulling or stretching the fabric while feeding.",
            category = "Techniques"
        ),
        DictionaryTerm(
            term = "Muslin / Toile",
            pronunciation = "MUHZ-lin / TWAHL",
            simpleDefinition = "A test garment sewn in inexpensive plain cotton muslin or scrap fabric before cutting into expensive fabric.",
            whyItMatters = "Lets you check fit, proportion, and make alterations on your body without risking expensive Ankara, silk, or wool.",
            beginnerTip = "Sew your muslin with long basting stitches, omit facings and zips (use safety pins to close), and draw fit tweaks directly on the fabric with a pencil.",
            category = "Pattern Making"
        ),
        DictionaryTerm(
            term = "Pattern Grading",
            pronunciation = "PAT-urn GRAY-ding",
            simpleDefinition = "The systematic process of proportionally increasing or decreasing a finished pattern into different standard body sizes (e.g., S, M, L, XL).",
            whyItMatters = "Bodies do not scale uniformly; grading applies specific multi-point shifts to preserve armhole balance, bust drape, and waist placement.",
            beginnerTip = "When grading between two sizes, trace between size lines smoothly using a French curve ruler.",
            category = "Pattern Making"
        )
    )
}
