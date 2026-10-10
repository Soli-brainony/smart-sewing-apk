package com.example.data.model

enum class VisualDiagramType {
    NONE,
    PATTERN_ON_FOLD,
    SEWING_A_DART,
    GATHERING_STITCHES,
    INVISIBLE_ZIPPER,
    SET_IN_SLEEVE,
    NECKLINE_FACING,
    HEMMING_STITCH
}

data class SewingStep(
    val stepNumber: Int,
    val title: String,
    val summary: String,
    val whatToDo: String,
    val howToDoIt: String,
    val whyItIsDone: String,
    val whatCanGoWrong: String,
    val howToCorrectMistakes: String,
    val diagramType: VisualDiagramType = VisualDiagramType.NONE
)

object DefaultSewingGuide {
    val steps = listOf(
        SewingStep(
            stepNumber = 1,
            title = "Prepare the Fabric (Pre-wash & Press)",
            summary = "Wash, dry, and thoroughly press fabric before laying out patterns.",
            whatToDo = "Pre-wash your fabric using the exact water temperature you plan to wash the finished garment in (e.g. cold for Ankara wax prints). Press thoroughly with steam.",
            howToDoIt = "Submerge fabric in gentle wash, hang to dry in shade, and press flat on an ironing board along the grainline. Check that the fabric grain is squared (weft threads perpendicular to selvage).",
            whyItIsDone = "Natural fibers like cotton, linen, and Ankara shrink 3%–10% on their first wash. Washing beforehand ensures your finished garment will not shrink out of fit later.",
            whatCanGoWrong = "Skipping pre-washing causes the dress to shrink and become unwearable after its first wash. Ironing distorted fabric warps grainlines.",
            howToCorrectMistakes = "If unwashed fabric was already cut, steam press gently without pulling. Hand wash only in cold water for all future cleans.",
            diagramType = VisualDiagramType.NONE
        ),
        SewingStep(
            stepNumber = 2,
            title = "Prepare the Pattern & Place on Fabric Fold",
            summary = "Align pattern pieces with straight grain and folded selvage edges.",
            whatToDo = "Fold fabric lengthwise, right sides together, bringing selvages together. Place 'Cut on Fold' pieces directly along the folded edge.",
            howToDoIt = "Use a ruler to verify the grainline arrow is strictly equidistant from the fabric selvage along its entire length. Pin or weigh down pieces every 4 inches.",
            whyItIsDone = "Off-grain pattern cutting causes garments to twist around the body when worn, with seams spiraling uncomfortably forward.",
            whatCanGoWrong = "Cutting with grainline tilted or placing 'Cut on Fold' pieces away from the fold leaves you with two split halves instead of a solid bodice.",
            howToCorrectMistakes = "If cut apart accidentally, sew with 0.25\" seam allowance and press open, or cut a new piece correctly on fold.",
            diagramType = VisualDiagramType.PATTERN_ON_FOLD
        ),
        SewingStep(
            stepNumber = 3,
            title = "Cut the Fabric & Transfer Markings",
            summary = "Cut along pattern lines with sharp shears and mark notches/dart apexes.",
            whatToDo = "Cut smoothly around pattern edges with sharp fabric shears, keeping the lower blade resting flat against the cutting table. Clip notches outward 1/8\".",
            howToDoIt = "Cut with long, confident scissor strokes. Use tailor's chalk, tracing paper, or pins to mark dart apexes, waist match notches, and zipper stops.",
            whyItIsDone = "Accurate cutting preserves your calculated seam allowances. Marking notches now prevents guessing alignment when at the sewing machine.",
            whatCanGoWrong = "Clipping notches too deep (more than 1/4\") will cut into your 1/2\" seam allowance, creating a permanent hole along the seam.",
            howToCorrectMistakes = "Always clip outward or no deeper than 1/8\". If clipped too deep, reinforce the spot on the wrong side with a tiny scrap of fusible interfacing.",
            diagramType = VisualDiagramType.NONE
        ),
        SewingStep(
            stepNumber = 4,
            title = "Sew the Bodice Darts & Princess Seams",
            summary = "Fold and stitch shaping darts from wide base to pointed apex.",
            whatToDo = "Stitch the front and back waist darts and any bust contour lines to shape flat fabric into 3D body contours.",
            howToDoIt = "Fold dart right sides together matching leg markings. Stitch from raw edge towards apex. For the final 1/2\", stitch very close to fold, do not backstitch; leave long tails and tie a square knot by hand. Press front darts toward center, press bust darts downward.",
            whyItIsDone = "Backstitching at a dart apex creates an unsightly dimple or 'puckered nipple' effect on your bust or waist.",
            whatCanGoWrong = "Puckered dart tip due to stitching too abruptly off the edge or heavy backstitching.",
            howToCorrectMistakes = "Unpick the last 1\" with a seam ripper, re-stitch tapering off the fabric fold gently over 3–4 tiny stitches, and press over a tailor's ham.",
            diagramType = VisualDiagramType.SEWING_A_DART
        ),
        SewingStep(
            stepNumber = 5,
            title = "Assemble Bodice & Finish Neckline Facing",
            summary = "Join shoulder seams and attach interfacing-backed neckline facing.",
            whatToDo = "Stitch shoulder seams right sides together. Apply fusible interfacing to neckline facing, join facing shoulders, and pin around neckline.",
            howToDoIt = "Sew neckline at 1/2\" seam allowance. Clip curved seam allowances at 1/2\" intervals. Understitch the facing: stitch facing to seam allowances 1/16\" from seamline. Press facing to inside.",
            whyItIsDone = "Understitching permanently prevents the facing from rolling out and showing on the outside of your neckline.",
            whatCanGoWrong = "Facing keeps poking outward, or neckline seam ripples because inner curves were not clipped.",
            howToCorrectMistakes = "Clip V-notches along inside seam allowance curve without cutting stitching thread, then press firmly with a damp pressing cloth.",
            diagramType = VisualDiagramType.NECKLINE_FACING
        ),
        SewingStep(
            stepNumber = 6,
            title = "Set In the Sleeves",
            summary = "Ease sleeve cap into armhole using two rows of basting stitches.",
            whatToDo = "Run two parallel rows of long basting stitches (stitch length 4.0mm) between notches on sleeve cap. Pull threads gently to create a rounded cap without folds.",
            howToDoIt = "Pin sleeve into armhole right sides together: match underarm seams, match single notch (front), double notch (back), and center notch to shoulder seam. Stitch with sleeve side facing up.",
            whyItIsDone = "The sleeve cap is intentionally 1\"–1.5\" larger than the armhole to provide natural shoulder curvature and arm movement.",
            whatCanGoWrong = "Tucks or tiny pleats forming along the sleeve head.",
            howToCorrectMistakes = "Sew with the sleeve side up so your fingers can guide and smooth ease under the presser foot. If a tuck occurs, unpick 1.5\" and re-distribute ease.",
            diagramType = VisualDiagramType.SET_IN_SLEEVE
        ),
        SewingStep(
            stepNumber = 7,
            title = "Assemble the Skirt & Join to Bodice at Waist",
            summary = "Sew skirt darts and side seams, then connect bodice to skirt at waistline.",
            whatToDo = "Stitch skirt darts and side seams. Pin bodice to skirt at waistline, right sides together, matching side seams and darts perfectly.",
            howToDoIt = "Pin at center front, side seams, and dart points. Stitch at 1/2\" seam allowance. Press seam allowance upward into bodice or finish raw edges with serger / zigzag.",
            whyItIsDone = "Connecting bodice and skirt secures the dress waistline and ensures seamlines align continuously.",
            whatCanGoWrong = "Waist seams misaligned by 1/4\" across the side seam or zipper opening.",
            howToCorrectMistakes = "Baste waistline first with a long stitch to check alignment in mirror; once aligned, sew permanent straight stitch.",
            diagramType = VisualDiagramType.GATHERING_STITCHES
        ),
        SewingStep(
            stepNumber = 8,
            title = "Install Concealed Invisible Zipper",
            summary = "Insert back invisible zipper using invisible zipper foot.",
            whatToDo = "Install the 22\" invisible zipper into the center back seam before closing the lower skirt seam.",
            howToDoIt = "Press zipper coils open flat on low heat. Pin zipper face down on right side of fabric edge. Using an invisible zipper foot, stitch close to teeth without catching them. Close zipper to verify waist seam lines match, then stitch remaining lower skirt seam.",
            whyItIsDone = "An invisible zipper creates a flawless, couture finish with only the tiny pull tab visible from the outside.",
            whatCanGoWrong = "Stitching on top of zipper teeth jams the slider; waist seam mismatch creates an uneven horizontal line.",
            howToCorrectMistakes = "If slider gets stuck, unpick 1\" where teeth are trapped and re-stitch 1mm further away from teeth.",
            diagramType = VisualDiagramType.INVISIBLE_ZIPPER
        ),
        SewingStep(
            stepNumber = 9,
            title = "Hem the Garment",
            summary = "Mark level hemline while wearing and finish with blind hem or topstitch.",
            whatToDo = "Hang garment on dress form or body for 24 hours (allows bias to drop). Measure from floor to hemline and press 1.5\" hem allowance upward.",
            howToDoIt = "Fold raw edge under 1/4\", press, then fold up 1.25\" and blind hem by hand or machine with matching thread.",
            whyItIsDone = "A well-pressed, level hemline gives a clean, professional finish that makes custom clothing look store-bought or runway-ready.",
            whatCanGoWrong = "Uneven wavy hemline due to pulling fabric while sewing.",
            howToCorrectMistakes = "Never pull fabric through feed dogs; let machine feed it naturally. Use pressing cloth and steam iron to flatten any rippling.",
            diagramType = VisualDiagramType.HEMMING_STITCH
        )
    )
}
