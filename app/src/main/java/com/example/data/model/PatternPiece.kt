package com.example.data.model

data class Point2D(val x: Float, val y: Float)

data class PatternDart(
    val apex: Point2D,
    val legLeft: Point2D,
    val legRight: Point2D,
    val widthInches: Float,
    val lengthInches: Float,
    val label: String
)

data class PatternNotch(
    val position: Point2D,
    val normalAngleDeg: Float,
    val label: String
)

data class PatternMeasurementLabel(
    val label: String,
    val valueInches: Float,
    val startPoint: Point2D,
    val endPoint: Point2D,
    val isPrimary: Boolean = false
) {
    fun formattedValue(unit: UnitSystem): String {
        return if (unit == UnitSystem.INCHES) {
            val rounded = String.format("%.2f\"", valueInches)
            rounded
        } else {
            val cm = valueInches * 2.54f
            String.format("%.1f cm", cm)
        }
    }
}

enum class FoldPosition {
    NONE,
    LEFT,
    RIGHT,
    BOTTOM
}

data class PatternPiece(
    val id: String,
    val name: String,
    val cutQuantity: String, // e.g. "Cut 1 on Fold" or "Cut 2"
    val fabricType: String, // e.g. "Main Fabric" or "Lining"
    val seamAllowanceInches: Float,
    val easeInches: Float,
    val widthInches: Float,
    val heightInches: Float,
    val bodyReferenceLabel: String,
    val grainlineDirection: String = "Straight Grain (Parallel to Center Front)",
    val foldPosition: FoldPosition = FoldPosition.NONE,
    val outlinePoints: List<Point2D>, // Normalized or coordinate polygon points in inches
    val controlCurves: List<List<Point2D>> = emptyList(), // Cubic/Quadratic bezier anchors if needed
    val darts: List<PatternDart> = emptyList(),
    val notches: List<PatternNotch> = emptyList(),
    val measurementLabels: List<PatternMeasurementLabel> = emptyList(),
    val constructionNotes: String = ""
) {
    val totalCuttingWidthInches: Float
        get() = widthInches + (if (foldPosition != FoldPosition.NONE) seamAllowanceInches else seamAllowanceInches * 2)

    val totalCuttingHeightInches: Float
        get() = heightInches + seamAllowanceInches * 2

    fun formattedDimensions(unit: UnitSystem): String {
        return if (unit == UnitSystem.INCHES) {
            String.format("%.1f\" × %.1f\"", widthInches, heightInches)
        } else {
            String.format("%.1f cm × %.1f cm", widthInches * 2.54f, heightInches * 2.54f)
        }
    }
}

data class PatternCalculationBreakdown(
    val pieceName: String,
    val parameterName: String,
    val bodyMeasurement: Float,
    val easeApplied: Float,
    val patternMeasurement: Float,
    val seamAllowance: Float,
    val finalCuttingMeasurement: Float,
    val formulaExplanation: String
)

data class PatternVersion(
    val versionNumber: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val note: String,
    val pieces: List<PatternPiece>,
    val measurementsSnapshot: BodyMeasurements
)
