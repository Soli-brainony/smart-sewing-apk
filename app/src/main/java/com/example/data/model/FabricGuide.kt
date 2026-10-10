package com.example.data.model

data class FabricProfile(
    val name: String,
    val localContext: String,
    val drape: String, // "Crisp & Structured", "Fluid & Flowing", "Moderate", "Heavy & Rigid"
    val easeRecommendation: String,
    val difficulty: String, // "Beginner", "Intermediate", "Advanced"
    val needsLining: Boolean,
    val liningReason: String,
    val interfacingType: String,
    val needleType: String,
    val patternAdjustments: String,
    val summary: String
)

object FabricSubstitutionData {
    val fabrics = listOf(
        FabricProfile(
            name = "Ankara / African Wax Print",
            localContext = "Beloved iconic 100% cotton print across Ghana and West Africa; vibrant motifs, double-sided wax resist.",
            drape = "Crisp & Structured (softens slightly with repeated washings)",
            easeRecommendation = "Standard woven ease (1.5\"–2.0\" bust ease, 1.0\" waist ease). Has zero stretch.",
            difficulty = "Beginner-Friendly",
            needsLining = true,
            liningReason = "Recommended for fitted bodices to protect skin from stiff starch and absorb perspiration.",
            interfacingType = "Lightweight woven or fusible knit interfacing for facings.",
            needleType = "Universal size 80/12 or 90/14 with all-purpose polyester thread.",
            patternAdjustments = "Account for print matching: large directional motifs require 15%–25% extra fabric length so designs match across center front and side seams.",
            summary = "Excellent for structured dresses, Kaba & Slit, peplum tops, and A-line skirts. Very stable under the presser foot."
        ),
        FabricProfile(
            name = "Cotton Lawn / Poplin",
            localContext = "Everyday breathable woven cotton fabric.",
            drape = "Light-to-medium crisp drape",
            easeRecommendation = "Standard ease (+1.5\" bust, +1.0\" waist). Non-stretch.",
            difficulty = "Beginner-Friendly",
            needsLining = false,
            liningReason = "Only if white or sheer; otherwise opaque.",
            interfacingType = "Lightweight fusible non-woven or featherweight.",
            needleType = "Universal 70/10 or 80/12.",
            patternAdjustments = "None required; direct 1:1 match with basic pattern slopers.",
            summary = "The gold standard for beginners and toile/muslin fitting tests. Easy to press and sew."
        ),
        FabricProfile(
            name = "Linen / Linen Blends",
            localContext = "Luxurious, breathable natural fiber with rustic texture and natural slubs.",
            drape = "Medium, relaxed fluid drape with earthy creases",
            easeRecommendation = "Slightly relaxed ease (+2.0\" to +2.5\" bust ease) to accommodate lack of stretch and relaxed aesthetic.",
            difficulty = "Beginner to Intermediate",
            needsLining = false,
            liningReason = "Usually unlined for maximum breathability; bind internal seams with bias tape.",
            interfacingType = "Lightweight woven fusible interfacing.",
            needleType = "Universal or Jeans 80/12 or 90/14.",
            patternAdjustments = "Linen stretches slightly along bias lines during wear; stay-stitch necklines immediately after cutting.",
            summary = "Ideal for summer dresses, relaxed shirts, and palazzo trousers. Embraces natural softening wrinkles."
        ),
        FabricProfile(
            name = "Crepe (Scuba, Pebble, or Georgette Crepe)",
            localContext = "Sophisticated crinkled-texture fabric popular for bridal, formal dresses, and evening wear.",
            drape = "Heavy, fluid drape with elegant swish and bounce",
            easeRecommendation = "Fitted ease (+1.0\"–1.5\" bust ease). If stretch crepe, decrease ease by 0.5\".",
            difficulty = "Intermediate",
            needsLining = true,
            liningReason = "Crepe clings to undergarments without a smooth slippery lining.",
            interfacingType = "Ultra-light fusible knit interfacing.",
            needleType = "Microtex or Sharp 75/11 or 80/12.",
            patternAdjustments = "Hang garment for 24 hours before hemming; crepe can drop and stretch on the bias.",
            summary = "Produces stunning, slimming silhouettes with fluid movement and low wrinkling."
        ),
        FabricProfile(
            name = "Satin / Duchess Satin",
            localContext = "Glossy, lustrous face with dull back. Duchess satin is stiff and bridal; poly satin is fluid.",
            drape = "Duchess is structured; silk/charmeuse satin is ultra-fluid and liquid",
            easeRecommendation = "Fitted with precise ease (+1.5\" bust). Slippery fabric shows pull lines if too tight.",
            difficulty = "Advanced",
            needsLining = true,
            liningReason = "Prevents static cling, protects delicate satin fibers, and adds body.",
            interfacingType = "Featherweight fusible interfacing applied with silk pressing cloth.",
            needleType = "Microtex/Sharp 65/9 or 70/10. Replace needle frequently to prevent snagging threads.",
            patternAdjustments = "Avoid excessive pins that leave puncture holes. Cut in single layers with rotary cutter and weights.",
            summary = "High-shine elegance suitable for gowns, cocktail dresses, and bridal attire. Requires patience and tissue paper."
        ),
        FabricProfile(
            name = "Chiffon / Georgette",
            localContext = "Sheer, ethereal lightweight fabric with delicate see-through transparency.",
            drape = "Airy, floating, cloud-like drape",
            easeRecommendation = "Generous design ease (+3.0\" or more) with gathers or blouson effect. Never make skin-tight.",
            difficulty = "Advanced",
            needsLining = true,
            liningReason = "Mandatory unless designing an intentional sheer overlay or cape.",
            interfacingType = "Organza or tissue-weight interfacing.",
            needleType = "Microtex 60/8 or 65/9 with fine silk thread.",
            patternAdjustments = "Use French seams throughout; raw edges fray aggressively. Cut with tissue paper beneath fabric.",
            summary = "Breathtaking for flutter sleeves, layered skirts, and scarves. Demands slow, meticulous stitching."
        ),
        FabricProfile(
            name = "Jersey / Stretch Knit",
            localContext = "Knitted fabric with built-in mechanical elasticity.",
            drape = "Soft, body-hugging drape",
            easeRecommendation = "Negative ease (-1.0\" to -2.0\") or zero ease. Do NOT use standard woven pattern without reducing width!",
            difficulty = "Intermediate",
            needsLining = false,
            liningReason = "Usually unlined to preserve natural 4-way stretch.",
            interfacingType = "Fusible stretch knit stay tape along shoulders.",
            needleType = "Ballpoint or Stretch needle 75/11 with stretch stitch or serger.",
            patternAdjustments = "Deduct 10%–15% from bodice width; eliminate waist darts because knit contours naturally.",
            summary = "Supreme comfort for T-shirt dresses, wrap tops, and casual jumpsuits."
        ),
        FabricProfile(
            name = "Denim / Cotton Twill",
            localContext = "Sturdy diagonal twill weave cotton.",
            drape = "Heavy, rigid, structured shape",
            easeRecommendation = "Generous ease (+2.0\"–3.0\" bust) unless stretch denim (+1.0\").",
            difficulty = "Intermediate",
            needsLining = false,
            liningReason = "Sturdy enough to be completely self-supporting.",
            interfacingType = "Medium-weight fusible interfacing.",
            needleType = "Jeans/Denim needle 90/14 or 100/16 with topstitching thread.",
            patternAdjustments = "Grade seam allowances heavily to prevent thick bulk at intersections.",
            summary = "Durable, fashionable, and rugged for jacket dresses, pinafores, and utility skirts."
        ),
        FabricProfile(
            name = "Lace / Guipure",
            localContext = "Openwork decorative fabric with floral or geometric corded motifs.",
            drape = "Varies from delicate floaty Chantilly to stiff architectural Guipure",
            easeRecommendation = "Depends on backing; usually +1.5\" ease when underlined.",
            difficulty = "Advanced",
            needsLining = true,
            liningReason = "Required for modesty and structural support.",
            interfacingType = "Nude illusion mesh or skin-tone organza backing.",
            needleType = "Universal 70/10 or hand-basted applique stitching.",
            patternAdjustments = "Use scalloped selvage edge for hem and sleeve cuffs without folding regular hems.",
            summary = "Timeless luxury for celebration garments, church wear, and red carpet looks."
        ),
        FabricProfile(
            name = "Velvet / Velveteen",
            localContext = "Plush pile fabric with directional nap and deep color richness.",
            drape = "Rich, heavy, draping column",
            easeRecommendation = "Standard ease (+1.5\"–2.0\").",
            difficulty = "Advanced",
            needsLining = true,
            liningReason = "Pile creates friction against tights or body without smooth lining.",
            interfacingType = "Sew-in interfacing (heat/steam from fusible can crush velvet pile).",
            needleType = "Sharp/Universal 80/12; use walking foot to stop creeping layers.",
            patternAdjustments = "Critical: Cut all pieces with nap running the same direction (usually smooth downward or rich upward).",
            summary = "Opulent, regal evening wear fabric requiring a needle board for pressing."
        )
    )
}
