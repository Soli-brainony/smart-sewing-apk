package com.example.data.pattern

import com.example.data.model.*
import kotlin.math.max

object PatternEngine {

    /**
     * Calculates structured geometric pattern pieces for a garment based on body measurements,
     * garment type, ease adjustments, and seam allowances.
     */
    fun calculatePattern(
        measurements: BodyMeasurements,
        garmentType: GarmentType,
        customEaseInches: Float = 1.5f,
        seamAllowanceInches: Float = 0.5f,
        customGarmentLengthInches: Float? = null
    ): Pair<List<PatternPiece>, List<PatternCalculationBreakdown>> {
        val m = measurements.toInches()
        val breakdowns = mutableListOf<PatternCalculationBreakdown>()
        val pieces = mutableListOf<PatternPiece>()

        val garmentLength = customGarmentLengthInches ?: m.desiredGarmentLength
        val bustQuarter = m.bust / 4f
        val waistQuarter = m.waist / 4f
        val hipQuarter = m.hip / 4f

        val bustEasePerQuarter = customEaseInches / 4f
        val waistEasePerQuarter = (customEaseInches * 0.6f) / 4f
        val hipEasePerQuarter = (customEaseInches * 0.8f) / 4f

        when (garmentType) {
            GarmentType.DRESS, GarmentType.AUTO_IDENTIFY, GarmentType.OTHER -> {
                // Front Bodice
                val frontBodiceWidth = bustQuarter + bustEasePerQuarter + 0.5f // +0.5" front chest allowance
                val frontBodiceHeight = m.shoulderToWaist
                val frontWaistWidth = waistQuarter + waistEasePerQuarter + 0.75f // +0.75" for front waist dart

                breakdowns.add(
                    PatternCalculationBreakdown(
                        pieceName = "Front Bodice",
                        parameterName = "Bust Width (Quarter)",
                        bodyMeasurement = m.bust,
                        easeApplied = customEaseInches,
                        patternMeasurement = frontBodiceWidth,
                        seamAllowance = seamAllowanceInches,
                        finalCuttingMeasurement = frontBodiceWidth + seamAllowanceInches, // cut on fold so 1 seam allowance on side
                        formulaExplanation = "Bust ÷ 4 (${String.format("%.2f", bustQuarter)}\") + Quarter Ease (${String.format("%.2f", bustEasePerQuarter)}\") + 0.5\" front balance"
                    )
                )

                breakdowns.add(
                    PatternCalculationBreakdown(
                        pieceName = "Front Bodice",
                        parameterName = "Waist Width (Quarter)",
                        bodyMeasurement = m.waist,
                        easeApplied = customEaseInches * 0.6f,
                        patternMeasurement = frontWaistWidth,
                        seamAllowance = seamAllowanceInches,
                        finalCuttingMeasurement = frontWaistWidth + seamAllowanceInches,
                        formulaExplanation = "Waist ÷ 4 (${String.format("%.2f", waistQuarter)}\") + Quarter Ease (${String.format("%.2f", waistEasePerQuarter)}\") + 0.75\" waist dart"
                    )
                )

                // Outline points in inches:
                // (0,0) is Center Front at Neckline
                // Down to (0, height) Center Front Waist
                val fbPoints = listOf(
                    Point2D(0f, 0f), // Center Front Neck
                    Point2D(0f, frontBodiceHeight), // Center Front Waist
                    Point2D(frontWaistWidth, frontBodiceHeight), // Side Waist
                    Point2D(frontBodiceWidth, frontBodiceHeight * 0.55f), // Underarm
                    Point2D(frontBodiceWidth * 0.85f, frontBodiceHeight * 0.25f), // Armhole curve midway
                    Point2D(m.shoulderWidth * 0.5f, frontBodiceHeight * 0.08f), // Shoulder tip
                    Point2D(m.shoulderWidth * 0.22f, 0f) // Neck shoulder point
                )

                val fbLabels = listOf(
                    PatternMeasurementLabel("Center Front (Fold): ${String.format("%.1f", frontBodiceHeight)}\"", frontBodiceHeight, Point2D(0f, 0f), Point2D(0f, frontBodiceHeight), true),
                    PatternMeasurementLabel("Bust Line: ${String.format("%.2f", frontBodiceWidth)}\"", frontBodiceWidth, Point2D(0f, frontBodiceHeight * 0.55f), Point2D(frontBodiceWidth, frontBodiceHeight * 0.55f), true),
                    PatternMeasurementLabel("Waist Line: ${String.format("%.2f", frontWaistWidth)}\"", frontWaistWidth, Point2D(0f, frontBodiceHeight), Point2D(frontWaistWidth, frontBodiceHeight), true),
                    PatternMeasurementLabel("Shoulder: ${String.format("%.1f", (m.shoulderWidth * 0.5f) - (m.shoulderWidth * 0.22f))}\"", (m.shoulderWidth * 0.5f) - (m.shoulderWidth * 0.22f), Point2D(m.shoulderWidth * 0.22f, 0f), Point2D(m.shoulderWidth * 0.5f, frontBodiceHeight * 0.08f))
                )

                val fbDart = PatternDart(
                    apex = Point2D(m.bustPoint * 0.5f, m.bustHeight),
                    legLeft = Point2D(m.bustPoint * 0.5f - 0.375f, frontBodiceHeight),
                    legRight = Point2D(m.bustPoint * 0.5f + 0.375f, frontBodiceHeight),
                    widthInches = 0.75f,
                    lengthInches = frontBodiceHeight - m.bustHeight,
                    label = "Waist Contour Dart"
                )

                pieces.add(
                    PatternPiece(
                        id = "front_bodice",
                        name = "Front Bodice",
                        cutQuantity = "Cut 1 on Fold",
                        fabricType = "Main Fabric",
                        seamAllowanceInches = seamAllowanceInches,
                        easeInches = customEaseInches,
                        widthInches = frontBodiceWidth,
                        heightInches = frontBodiceHeight,
                        bodyReferenceLabel = "Bust: ${m.bust}\", Waist: ${m.waist}\"",
                        grainlineDirection = "Straight Grain (Parallel to Center Front fold)",
                        foldPosition = FoldPosition.LEFT,
                        outlinePoints = fbPoints,
                        darts = listOf(fbDart),
                        notches = listOf(
                            PatternNotch(Point2D(frontBodiceWidth, frontBodiceHeight * 0.55f), 0f, "Armhole Side Notch"),
                            PatternNotch(Point2D(frontWaistWidth, frontBodiceHeight), 90f, "Waist Seam Notch")
                        ),
                        measurementLabels = fbLabels,
                        constructionNotes = "Place center front exactly on folded fabric edge. Transfer bust apex mark and waist dart legs using tailor's tack or tracing wheel."
                    )
                )

                // Back Bodice
                val backBodiceWidth = bustQuarter + bustEasePerQuarter - 0.5f // back balance deduction
                val backBodiceHeight = m.backWaistLength
                val backWaistWidth = waistQuarter + waistEasePerQuarter + 1.0f // +1.0" back waist dart

                breakdowns.add(
                    PatternCalculationBreakdown(
                        pieceName = "Back Bodice",
                        parameterName = "Bust Width (Quarter)",
                        bodyMeasurement = m.bust,
                        easeApplied = customEaseInches,
                        patternMeasurement = backBodiceWidth,
                        seamAllowance = seamAllowanceInches,
                        finalCuttingMeasurement = backBodiceWidth + seamAllowanceInches * 2, // cut 2, so both edges have seam allowances
                        formulaExplanation = "Bust ÷ 4 (${String.format("%.2f", bustQuarter)}\") + Quarter Ease (${String.format("%.2f", bustEasePerQuarter)}\") - 0.5\" back balance"
                    )
                )

                val bbPoints = listOf(
                    Point2D(0f, 0f), // Center Back Neck
                    Point2D(0f, backBodiceHeight), // Center Back Waist
                    Point2D(backWaistWidth, backBodiceHeight), // Side Waist
                    Point2D(backBodiceWidth, backBodiceHeight * 0.55f), // Underarm
                    Point2D(backBodiceWidth * 0.88f, backBodiceHeight * 0.28f), // Armhole curve
                    Point2D(m.shoulderWidth * 0.5f, backBodiceHeight * 0.05f), // Shoulder tip
                    Point2D(m.shoulderWidth * 0.22f, 0f) // Back neck shoulder
                )

                val bbLabels = listOf(
                    PatternMeasurementLabel("Center Back (Zipper): ${String.format("%.1f", backBodiceHeight)}\"", backBodiceHeight, Point2D(0f, 0f), Point2D(0f, backBodiceHeight), true),
                    PatternMeasurementLabel("Bust Line: ${String.format("%.2f", backBodiceWidth)}\"", backBodiceWidth, Point2D(0f, backBodiceHeight * 0.55f), Point2D(backBodiceWidth, backBodiceHeight * 0.55f), true),
                    PatternMeasurementLabel("Waist Line: ${String.format("%.2f", backWaistWidth)}\"", backWaistWidth, Point2D(0f, backBodiceHeight), Point2D(backWaistWidth, backBodiceHeight), true)
                )

                val bbDart = PatternDart(
                    apex = Point2D(backWaistWidth * 0.45f, backBodiceHeight * 0.35f),
                    legLeft = Point2D(backWaistWidth * 0.45f - 0.5f, backBodiceHeight),
                    legRight = Point2D(backWaistWidth * 0.45f + 0.5f, backBodiceHeight),
                    widthInches = 1.0f,
                    lengthInches = backBodiceHeight * 0.65f,
                    label = "Back Waist Shaping Dart"
                )

                pieces.add(
                    PatternPiece(
                        id = "back_bodice",
                        name = "Back Bodice",
                        cutQuantity = "Cut 2 (Left & Right)",
                        fabricType = "Main Fabric",
                        seamAllowanceInches = seamAllowanceInches,
                        easeInches = customEaseInches,
                        widthInches = backBodiceWidth,
                        heightInches = backBodiceHeight,
                        bodyReferenceLabel = "Back Length: ${m.backWaistLength}\", Bust: ${m.bust}\"",
                        grainlineDirection = "Straight Grain (Parallel to Center Back seam)",
                        foldPosition = FoldPosition.NONE,
                        outlinePoints = bbPoints,
                        darts = listOf(bbDart),
                        notches = listOf(
                            PatternNotch(Point2D(0f, backBodiceHeight * 0.5f), 180f, "Zipper Stop Notch"),
                            PatternNotch(Point2D(backBodiceWidth * 0.88f, backBodiceHeight * 0.28f), 45f, "Double Armhole Notch")
                        ),
                        measurementLabels = bbLabels,
                        constructionNotes = "Cut 2 mirrored pieces. Center back seam requires 0.75\" allowance to accommodate standard concealed invisible zipper."
                    )
                )

                // Skirt Front
                val skirtLength = max(18f, garmentLength - frontBodiceHeight)
                val frontSkirtWaist = waistQuarter + waistEasePerQuarter + 0.5f
                val frontSkirtHip = hipQuarter + hipEasePerQuarter + 0.5f
                val frontSkirtHem = frontSkirtHip * 1.25f // Gentle A-line flare

                breakdowns.add(
                    PatternCalculationBreakdown(
                        pieceName = "Front Skirt",
                        parameterName = "Hip Width (Quarter)",
                        bodyMeasurement = m.hip,
                        easeApplied = customEaseInches * 0.8f,
                        patternMeasurement = frontSkirtHip,
                        seamAllowance = seamAllowanceInches,
                        finalCuttingMeasurement = frontSkirtHip + seamAllowanceInches,
                        formulaExplanation = "Hip ÷ 4 (${String.format("%.2f", hipQuarter)}\") + Quarter Ease (${String.format("%.2f", hipEasePerQuarter)}\") + 0.5\" front curve"
                    )
                )

                val fsPoints = listOf(
                    Point2D(0f, 0f), // Center Front Waist
                    Point2D(0f, skirtLength), // Center Front Hem
                    Point2D(frontSkirtHem, skirtLength), // Side Hem
                    Point2D(frontSkirtHip, skirtLength * 0.28f), // Side Hip (approx 8" below waist)
                    Point2D(frontSkirtWaist, 0f) // Side Waist
                )

                val fsLabels = listOf(
                    PatternMeasurementLabel("Center Front (Fold): ${String.format("%.1f", skirtLength)}\"", skirtLength, Point2D(0f, 0f), Point2D(0f, skirtLength), true),
                    PatternMeasurementLabel("Waist: ${String.format("%.2f", frontSkirtWaist)}\"", frontSkirtWaist, Point2D(0f, 0f), Point2D(frontSkirtWaist, 0f), true),
                    PatternMeasurementLabel("Hip Line: ${String.format("%.2f", frontSkirtHip)}\"", frontSkirtHip, Point2D(0f, skirtLength * 0.28f), Point2D(frontSkirtHip, skirtLength * 0.28f), true),
                    PatternMeasurementLabel("Hem Flare: ${String.format("%.2f", frontSkirtHem)}\"", frontSkirtHem, Point2D(0f, skirtLength), Point2D(frontSkirtHem, skirtLength))
                )

                pieces.add(
                    PatternPiece(
                        id = "front_skirt",
                        name = "Front Skirt",
                        cutQuantity = "Cut 1 on Fold",
                        fabricType = "Main Fabric",
                        seamAllowanceInches = seamAllowanceInches,
                        easeInches = customEaseInches,
                        widthInches = frontSkirtHem,
                        heightInches = skirtLength,
                        bodyReferenceLabel = "Waist: ${m.waist}\", Hip: ${m.hip}\", Length: ${skirtLength}\"",
                        grainlineDirection = "Straight Grain (Parallel to Center Front)",
                        foldPosition = FoldPosition.LEFT,
                        outlinePoints = fsPoints,
                        measurementLabels = fsLabels,
                        constructionNotes = "Cut 1 on fold. Ensure side seam flare matches Back Skirt angle. Press side seams open after sewing."
                    )
                )

                // Skirt Back
                val backSkirtWaist = waistQuarter + waistEasePerQuarter + 0.75f
                val backSkirtHip = hipQuarter + hipEasePerQuarter
                val backSkirtHem = backSkirtHip * 1.25f

                val bsPoints = listOf(
                    Point2D(0f, 0f), // Center Back Waist
                    Point2D(0f, skirtLength), // Center Back Hem (Vent)
                    Point2D(backSkirtHem, skirtLength),
                    Point2D(backSkirtHip, skirtLength * 0.28f),
                    Point2D(backSkirtWaist, 0f)
                )

                val bsLabels = listOf(
                    PatternMeasurementLabel("Center Back: ${String.format("%.1f", skirtLength)}\"", skirtLength, Point2D(0f, 0f), Point2D(0f, skirtLength), true),
                    PatternMeasurementLabel("Waist: ${String.format("%.2f", backSkirtWaist)}\"", backSkirtWaist, Point2D(0f, 0f), Point2D(backSkirtWaist, 0f)),
                    PatternMeasurementLabel("Hip: ${String.format("%.2f", backSkirtHip)}\"", backSkirtHip, Point2D(0f, skirtLength * 0.28f), Point2D(backSkirtHip, skirtLength * 0.28f), true)
                )

                pieces.add(
                    PatternPiece(
                        id = "back_skirt",
                        name = "Back Skirt",
                        cutQuantity = "Cut 2 (Left & Right)",
                        fabricType = "Main Fabric",
                        seamAllowanceInches = seamAllowanceInches,
                        easeInches = customEaseInches,
                        widthInches = backSkirtHem,
                        heightInches = skirtLength,
                        bodyReferenceLabel = "Waist: ${m.waist}\", Hip: ${m.hip}\"",
                        grainlineDirection = "Straight Grain (Parallel to Center Back)",
                        foldPosition = FoldPosition.NONE,
                        outlinePoints = bsPoints,
                        notches = listOf(
                            PatternNotch(Point2D(0f, skirtLength * 0.65f), 180f, "Walking Vent Top Notch")
                        ),
                        measurementLabels = bsLabels,
                        constructionNotes = "Cut 2 mirrored pieces. Leave 7\" open at top for zipper and 8\" at bottom for comfortable walking vent."
                    )
                )

                // Set-in Sleeve
                val bicepWidth = m.upperArmCircumference + 1.5f // 1.5" ease for movement
                val capHeight = m.armholeDepth * 0.65f
                val sleeveLen = m.sleeveLength
                val wristWidth = m.wristCircumference + 1.25f

                breakdowns.add(
                    PatternCalculationBreakdown(
                        pieceName = "Set-in Sleeve",
                        parameterName = "Bicep Line Width",
                        bodyMeasurement = m.upperArmCircumference,
                        easeApplied = 1.5f,
                        patternMeasurement = bicepWidth,
                        seamAllowance = seamAllowanceInches,
                        finalCuttingMeasurement = bicepWidth + seamAllowanceInches * 2,
                        formulaExplanation = "Upper Arm Circumference (${m.upperArmCircumference}\") + 1.5\" comfort ease"
                    )
                )

                val sleevePoints = listOf(
                    Point2D(bicepWidth * 0.5f, 0f), // Sleeve Crown Peak
                    Point2D(bicepWidth, capHeight), // Back Underarm
                    Point2D(bicepWidth * 0.5f + wristWidth * 0.5f, sleeveLen), // Back Wrist
                    Point2D(bicepWidth * 0.5f - wristWidth * 0.5f, sleeveLen), // Front Wrist
                    Point2D(0f, capHeight) // Front Underarm
                )

                val slLabels = listOf(
                    PatternMeasurementLabel("Sleeve Length: ${String.format("%.1f", sleeveLen)}\"", sleeveLen, Point2D(bicepWidth * 0.5f, 0f), Point2D(bicepWidth * 0.5f, sleeveLen), true),
                    PatternMeasurementLabel("Bicep Width: ${String.format("%.2f", bicepWidth)}\"", bicepWidth, Point2D(0f, capHeight), Point2D(bicepWidth, capHeight), true),
                    PatternMeasurementLabel("Wrist: ${String.format("%.2f", wristWidth)}\"", wristWidth, Point2D(bicepWidth * 0.5f - wristWidth * 0.5f, sleeveLen), Point2D(bicepWidth * 0.5f + wristWidth * 0.5f, sleeveLen))
                )

                pieces.add(
                    PatternPiece(
                        id = "sleeve",
                        name = "Set-in Sleeve",
                        cutQuantity = "Cut 2 (Mirrored Pair)",
                        fabricType = "Main Fabric",
                        seamAllowanceInches = seamAllowanceInches,
                        easeInches = 1.5f,
                        widthInches = bicepWidth,
                        heightInches = sleeveLen,
                        bodyReferenceLabel = "Upper Arm: ${m.upperArmCircumference}\", Length: ${m.sleeveLength}\"",
                        grainlineDirection = "Straight Grain (Center fold from cap to wrist)",
                        foldPosition = FoldPosition.NONE,
                        outlinePoints = sleevePoints,
                        notches = listOf(
                            PatternNotch(Point2D(bicepWidth * 0.5f, 0f), 90f, "Shoulder Seam Center Notch"),
                            PatternNotch(Point2D(0.3f * bicepWidth, capHeight * 0.5f), 135f, "Front Single Notch"),
                            PatternNotch(Point2D(0.7f * bicepWidth, capHeight * 0.5f), 45f, "Back Double Notch")
                        ),
                        measurementLabels = slLabels,
                        constructionNotes = "Sew two rows of basting stitches along the sleeve head cap to gather and ease smoothly into the bodice armhole without puckers."
                    )
                )

                // Neckline Facing
                val facingWidth = frontBodiceWidth * 0.6f
                val facingHeight = 2.5f // standard 2.5" facing depth
                val facingPoints = listOf(
                    Point2D(0f, 0f),
                    Point2D(0f, facingHeight),
                    Point2D(facingWidth, facingHeight),
                    Point2D(facingWidth, 0f)
                )
                pieces.add(
                    PatternPiece(
                        id = "neckline_facing",
                        name = "Neckline Facing",
                        cutQuantity = "Cut 1 on Fold (Fabric & Interfacing)",
                        fabricType = "Main Fabric & Fusible Interfacing",
                        seamAllowanceInches = seamAllowanceInches,
                        easeInches = 0f,
                        widthInches = facingWidth,
                        heightInches = facingHeight,
                        bodyReferenceLabel = "Neck Circumference: ${m.neckCircumference}\"",
                        grainlineDirection = "Straight Grain",
                        foldPosition = FoldPosition.LEFT,
                        outlinePoints = facingPoints,
                        measurementLabels = listOf(
                            PatternMeasurementLabel("Depth: 2.5\"", 2.5f, Point2D(0f, 0f), Point2D(0f, 2.5f))
                        ),
                        constructionNotes = "Apply lightweight fusible interfacing to wrong side before sewing. Understitch seam allowance to facing to keep it rolling inward."
                    )
                )
            }

            GarmentType.KABA_AND_SLIT -> {
                // Traditional Ghanaian Kaba (Bodice with Peplum) & Slit (Long wrap/straight skirt)
                val kabaBodiceWidth = bustQuarter + bustEasePerQuarter + 0.75f
                val kabaBodiceHeight = m.shoulderToWaist
                val peplumHeight = 7.5f // flared peplum flounce
                val peplumWidth = (waistQuarter + 0.5f) * 1.6f

                breakdowns.add(
                    PatternCalculationBreakdown(
                        pieceName = "Kaba Bodice (Front)",
                        parameterName = "Bust Width (Quarter)",
                        bodyMeasurement = m.bust,
                        easeApplied = customEaseInches,
                        patternMeasurement = kabaBodiceWidth,
                        seamAllowance = seamAllowanceInches,
                        finalCuttingMeasurement = kabaBodiceWidth + seamAllowanceInches,
                        formulaExplanation = "Bust ÷ 4 + Ease + 0.75\" princess styling for structured Ankara fabric"
                    )
                )

                val kabaPoints = listOf(
                    Point2D(0f, 0f),
                    Point2D(0f, kabaBodiceHeight),
                    Point2D(waistQuarter + 0.5f, kabaBodiceHeight),
                    Point2D(kabaBodiceWidth, kabaBodiceHeight * 0.5f),
                    Point2D(m.shoulderWidth * 0.5f, 0.5f),
                    Point2D(m.shoulderWidth * 0.2f, 0f)
                )

                pieces.add(
                    PatternPiece(
                        id = "kaba_front",
                        name = "Kaba Front Bodice",
                        cutQuantity = "Cut 1 on Fold",
                        fabricType = "Ankara / Wax Print",
                        seamAllowanceInches = seamAllowanceInches,
                        easeInches = customEaseInches,
                        widthInches = kabaBodiceWidth,
                        heightInches = kabaBodiceHeight,
                        bodyReferenceLabel = "Bust: ${m.bust}\", Waist: ${m.waist}\"",
                        grainlineDirection = "Straight Grain (Align with Wax Print motifs)",
                        foldPosition = FoldPosition.LEFT,
                        outlinePoints = kabaPoints,
                        measurementLabels = listOf(
                            PatternMeasurementLabel("Bust Line: ${String.format("%.2f", kabaBodiceWidth)}\"", kabaBodiceWidth, Point2D(0f, kabaBodiceHeight * 0.5f), Point2D(kabaBodiceWidth, kabaBodiceHeight * 0.5f), true),
                            PatternMeasurementLabel("Front Length: ${String.format("%.1f", kabaBodiceHeight)}\"", kabaBodiceHeight, Point2D(0f, 0f), Point2D(0f, kabaBodiceHeight), true)
                        ),
                        constructionNotes = "Carefully align print motifs across center front fold before cutting wax print fabric."
                    )
                )

                // Peplum Flounce
                val peplumPoints = listOf(
                    Point2D(0f, 0f),
                    Point2D(0f, peplumHeight),
                    Point2D(peplumWidth, peplumHeight),
                    Point2D(waistQuarter + 0.5f, 0f)
                )
                pieces.add(
                    PatternPiece(
                        id = "kaba_peplum",
                        name = "Kaba Peplum Flounce",
                        cutQuantity = "Cut 1 on Fold (or 2 flared panels)",
                        fabricType = "Ankara / Wax Print",
                        seamAllowanceInches = seamAllowanceInches,
                        easeInches = 1.0f,
                        widthInches = peplumWidth,
                        heightInches = peplumHeight,
                        bodyReferenceLabel = "Waist: ${m.waist}\"",
                        grainlineDirection = "Straight Grain or 45° Bias for extra drape",
                        foldPosition = FoldPosition.LEFT,
                        outlinePoints = peplumPoints,
                        measurementLabels = listOf(
                            PatternMeasurementLabel("Peplum Flare: ${String.format("%.1f", peplumWidth)}\"", peplumWidth, Point2D(0f, peplumHeight), Point2D(peplumWidth, peplumHeight)),
                            PatternMeasurementLabel("Length: 7.5\"", 7.5f, Point2D(0f, 0f), Point2D(0f, peplumHeight))
                        ),
                        constructionNotes = "Hem with a narrow rolled hem or horsehair braid for dramatic architectural flare."
                    )
                )

                // Slit (Ankle-length straight wrap skirt)
                val slitLength = 38f
                val slitWidth = hipQuarter + hipEasePerQuarter + 0.5f
                val slitPoints = listOf(
                    Point2D(0f, 0f),
                    Point2D(0f, slitLength),
                    Point2D(slitWidth, slitLength),
                    Point2D(slitWidth, 0f)
                )
                pieces.add(
                    PatternPiece(
                        id = "slit_skirt_front",
                        name = "Slit Skirt (Front)",
                        cutQuantity = "Cut 1 on Fold",
                        fabricType = "Ankara / Wax Print",
                        seamAllowanceInches = seamAllowanceInches,
                        easeInches = customEaseInches,
                        widthInches = slitWidth,
                        heightInches = slitLength,
                        bodyReferenceLabel = "Waist: ${m.waist}\", Hip: ${m.hip}\", Length: 38\"",
                        grainlineDirection = "Straight Grain",
                        foldPosition = FoldPosition.LEFT,
                        outlinePoints = slitPoints,
                        measurementLabels = listOf(
                            PatternMeasurementLabel("Hip: ${String.format("%.2f", slitWidth)}\"", slitWidth, Point2D(0f, 8f), Point2D(slitWidth, 8f), true),
                            PatternMeasurementLabel("Length: 38.0\"", 38f, Point2D(0f, 0f), Point2D(0f, 38f), true)
                        ),
                        constructionNotes = "Floor/ankle length traditional slit. Finish center back with overlap walking vent or concealed zipper."
                    )
                )
            }

            GarmentType.SKIRT -> {
                // Pure Skirt
                val skirtLength = garmentLength
                val frontSkirtWaist = waistQuarter + waistEasePerQuarter + 0.5f
                val frontSkirtHip = hipQuarter + hipEasePerQuarter + 0.5f
                val frontSkirtHem = frontSkirtHip * 1.3f

                val fsPoints = listOf(
                    Point2D(0f, 0f),
                    Point2D(0f, skirtLength),
                    Point2D(frontSkirtHem, skirtLength),
                    Point2D(frontSkirtHip, 8f),
                    Point2D(frontSkirtWaist, 0f)
                )

                pieces.add(
                    PatternPiece(
                        id = "front_skirt_solo",
                        name = "Front Skirt",
                        cutQuantity = "Cut 1 on Fold",
                        fabricType = "Main Fabric",
                        seamAllowanceInches = seamAllowanceInches,
                        easeInches = customEaseInches,
                        widthInches = frontSkirtHem,
                        heightInches = skirtLength,
                        bodyReferenceLabel = "Waist: ${m.waist}\", Hip: ${m.hip}\"",
                        grainlineDirection = "Straight Grain",
                        foldPosition = FoldPosition.LEFT,
                        outlinePoints = fsPoints,
                        measurementLabels = listOf(
                            PatternMeasurementLabel("Waist: ${String.format("%.2f", frontSkirtWaist)}\"", frontSkirtWaist, Point2D(0f, 0f), Point2D(frontSkirtWaist, 0f)),
                            PatternMeasurementLabel("Hip: ${String.format("%.2f", frontSkirtHip)}\"", frontSkirtHip, Point2D(0f, 8f), Point2D(frontSkirtHip, 8f), true),
                            PatternMeasurementLabel("Length: ${String.format("%.1f", skirtLength)}\"", skirtLength, Point2D(0f, 0f), Point2D(0f, skirtLength), true)
                        ),
                        constructionNotes = "Place center front on fold. Sew darts matching waist shaping marks."
                    )
                )

                // Waistband
                val waistbandLength = m.waist + 1.0f + (seamAllowanceInches * 2) + 1.5f // 1.5" overlap for button
                val waistbandWidth = 3.0f // folded to 1.5" finished
                pieces.add(
                    PatternPiece(
                        id = "skirt_waistband",
                        name = "Contour Waistband",
                        cutQuantity = "Cut 1 (Fabric & Interfacing)",
                        fabricType = "Main Fabric & Fusible Interfacing",
                        seamAllowanceInches = seamAllowanceInches,
                        easeInches = 1.0f,
                        widthInches = waistbandLength,
                        heightInches = waistbandWidth,
                        bodyReferenceLabel = "Waist: ${m.waist}\"",
                        grainlineDirection = "Straight Grain (Crosswise or Lengthwise)",
                        foldPosition = FoldPosition.NONE,
                        outlinePoints = listOf(
                            Point2D(0f, 0f),
                            Point2D(0f, waistbandWidth),
                            Point2D(waistbandLength, waistbandWidth),
                            Point2D(waistbandLength, 0f)
                        ),
                        measurementLabels = listOf(
                            PatternMeasurementLabel("Length: ${String.format("%.1f", waistbandLength)}\"", waistbandLength, Point2D(0f, 0f), Point2D(waistbandLength, 0f), true),
                            PatternMeasurementLabel("Width: 3.0\"", 3.0f, Point2D(0f, 0f), Point2D(0f, 3.0f))
                        ),
                        constructionNotes = "Fuse heavy waistband interfacing to prevent rolling. Add buttonhole on overlap extension."
                    )
                )
            }

            else -> {
                // Kaftan / Blouse / Shirt / Trousers / Jumpsuit fallback
                val bodyWidth = bustQuarter + bustEasePerQuarter + 1.0f
                val bodyHeight = garmentLength
                val tunicPoints = listOf(
                    Point2D(0f, 0f),
                    Point2D(0f, bodyHeight),
                    Point2D(bodyWidth * 1.15f, bodyHeight),
                    Point2D(bodyWidth, bodyHeight * 0.35f),
                    Point2D(m.shoulderWidth * 0.5f, 1f),
                    Point2D(m.neckCircumference * 0.25f, 0f)
                )

                pieces.add(
                    PatternPiece(
                        id = "front_body",
                        name = "Front Garment Panel",
                        cutQuantity = "Cut 1 on Fold",
                        fabricType = "Main Fabric",
                        seamAllowanceInches = seamAllowanceInches,
                        easeInches = customEaseInches,
                        widthInches = bodyWidth * 1.15f,
                        heightInches = bodyHeight,
                        bodyReferenceLabel = "Bust: ${m.bust}\", Length: ${bodyHeight}\"",
                        grainlineDirection = "Straight Grain",
                        foldPosition = FoldPosition.LEFT,
                        outlinePoints = tunicPoints,
                        measurementLabels = listOf(
                            PatternMeasurementLabel("Chest Width: ${String.format("%.2f", bodyWidth)}\"", bodyWidth, Point2D(0f, bodyHeight * 0.35f), Point2D(bodyWidth, bodyHeight * 0.35f), true),
                            PatternMeasurementLabel("Length: ${String.format("%.1f", bodyHeight)}\"", bodyHeight, Point2D(0f, 0f), Point2D(0f, bodyHeight), true)
                        ),
                        constructionNotes = "Comfortable ease calculated. Stitch side seams leaving lower side vents open for movement."
                    )
                )
            }
        }

        return Pair(pieces, breakdowns)
    }

    /**
     * Estimates fabric yardage and meters required based on body dimensions,
     * fabric bolt width (45" or 60"), garment length, and pattern matching.
     */
    fun calculateFabricRequirements(
        measurements: BodyMeasurements,
        fabricWidthInches: Int = 45, // 45" (standard Ankara / wax print width) or 60" (standard dressmaking crepe/linen)
        garmentLengthInches: Float,
        hasSleeves: Boolean = true,
        matchPrints: Boolean = false // e.g. Ankara directional matching requires ~20% extra
    ): FabricEstimationResult {
        val m = measurements.toInches()
        // Basic rule of thumb in pattern making:
        // (Bodice Length + Skirt Length + Sleeve Length + Hem & Seam Allowances)
        val bodiceLength = m.shoulderToWaist
        val skirtLength = max(18f, garmentLengthInches - bodiceLength)
        val sleeveLength = if (hasSleeves) m.sleeveLength else 6f

        var totalLinearInches = 0f

        if (fabricWidthInches >= 58) {
            // Wide fabric (60"): bodice front and back can often sit side-by-side
            totalLinearInches = bodiceLength + skirtLength + (sleeveLength * 0.8f) + 12f
        } else {
            // Narrow fabric (45" like African wax print): pieces must be cut sequentially
            totalLinearInches = (bodiceLength * 1.2f) + (skirtLength * 1.5f) + sleeveLength + 16f
        }

        if (matchPrints) {
            totalLinearInches *= 1.20f // 20% pattern repeat buffer
        }

        val totalYards = totalLinearInches / 36f
        val roundedYards = (Math.ceil(totalYards.toDouble() * 4) / 4).toFloat() // round to nearest quarter yard
        val totalMeters = roundedYards * 0.9144f

        return FabricEstimationResult(
            estimatedYards = roundedYards,
            estimatedMeters = String.format("%.1f", totalMeters).toFloat(),
            fabricWidthInches = fabricWidthInches,
            notes = if (matchPrints) {
                "Includes +20% buffer for matching directional African wax print / Ankara motifs."
            } else {
                "Standard cutting layout with 0.5\" seam allowance and 1.5\" hem allowance included."
            }
        )
    }
}

data class FabricEstimationResult(
    val estimatedYards: Float,
    val estimatedMeters: Float,
    val fabricWidthInches: Int,
    val notes: String
)
