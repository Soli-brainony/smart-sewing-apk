package com.example.data.model

enum class UnitSystem(val label: String, val symbol: String) {
    INCHES("Inches", "in"),
    CENTIMETERS("Centimeters", "cm")
}

data class BodyMeasurements(
    val unitSystem: UnitSystem = UnitSystem.INCHES,
    val bust: Float = 36f,
    val waist: Float = 28f,
    val hip: Float = 39f,
    val shoulderWidth: Float = 15f,
    val neckCircumference: Float = 14f,
    val bustPoint: Float = 10f,
    val bustHeight: Float = 10f,
    val shoulderToWaist: Float = 16f,
    val backWaistLength: Float = 15.5f,
    val armholeDepth: Float = 7.5f,
    val upperArmCircumference: Float = 11.5f,
    val sleeveLength: Float = 22f,
    val wristCircumference: Float = 6.5f,
    val desiredGarmentLength: Float = 40f
) {
    fun toInches(): BodyMeasurements {
        if (unitSystem == UnitSystem.INCHES) return this
        val factor = 0.393701f
        return copy(
            unitSystem = UnitSystem.INCHES,
            bust = bust * factor,
            waist = waist * factor,
            hip = hip * factor,
            shoulderWidth = shoulderWidth * factor,
            neckCircumference = neckCircumference * factor,
            bustPoint = bustPoint * factor,
            bustHeight = bustHeight * factor,
            shoulderToWaist = shoulderToWaist * factor,
            backWaistLength = backWaistLength * factor,
            armholeDepth = armholeDepth * factor,
            upperArmCircumference = upperArmCircumference * factor,
            sleeveLength = sleeveLength * factor,
            wristCircumference = wristCircumference * factor,
            desiredGarmentLength = desiredGarmentLength * factor
        )
    }

    fun toCm(): BodyMeasurements {
        if (unitSystem == UnitSystem.CENTIMETERS) return this
        val factor = 2.54f
        return copy(
            unitSystem = UnitSystem.CENTIMETERS,
            bust = bust * factor,
            waist = waist * factor,
            hip = hip * factor,
            shoulderWidth = shoulderWidth * factor,
            neckCircumference = neckCircumference * factor,
            bustPoint = bustPoint * factor,
            bustHeight = bustHeight * factor,
            shoulderToWaist = shoulderToWaist * factor,
            backWaistLength = backWaistLength * factor,
            armholeDepth = armholeDepth * factor,
            upperArmCircumference = upperArmCircumference * factor,
            sleeveLength = sleeveLength * factor,
            wristCircumference = wristCircumference * factor,
            desiredGarmentLength = desiredGarmentLength * factor
        )
    }

    val bustQuarterInches: Float
        get() = toInches().bust / 4f

    val waistQuarterInches: Float
        get() = toInches().waist / 4f

    val hipQuarterInches: Float
        get() = toInches().hip / 4f
}

data class MeasurementInfo(
    val key: String,
    val title: String,
    val description: String,
    val howToMeasure: String,
    val typicalRangeInches: String,
    val typicalRangeCm: String
)

object MeasurementGuides {
    val items = listOf(
        MeasurementInfo(
            key = "bust",
            title = "Bust",
            description = "Circumference at the fullest part of the bust.",
            howToMeasure = "Measure around the fullest part of your bust while keeping the tape measure level across your back.",
            typicalRangeInches = "30\" – 54\"",
            typicalRangeCm = "76 cm – 137 cm"
        ),
        MeasurementInfo(
            key = "waist",
            title = "Waist",
            description = "Natural waistline circumference (narrowest point above navel).",
            howToMeasure = "Tie a ribbon around your natural waist. Measure snug but comfortably where the ribbon sits.",
            typicalRangeInches = "24\" – 48\"",
            typicalRangeCm = "61 cm – 122 cm"
        ),
        MeasurementInfo(
            key = "hip",
            title = "Hip",
            description = "Fullest circumference of hips and buttocks.",
            howToMeasure = "Stand with feet together. Measure around the widest part of your hips and seat (usually 7-9 inches below waist).",
            typicalRangeInches = "32\" – 58\"",
            typicalRangeCm = "81 cm – 147 cm"
        ),
        MeasurementInfo(
            key = "shoulderWidth",
            title = "Shoulder Width",
            description = "Distance across back between shoulder bone tips.",
            howToMeasure = "Measure from the bony tip of one shoulder blade across the base of neck to the other shoulder bone.",
            typicalRangeInches = "13\" – 19\"",
            typicalRangeCm = "33 cm – 48 cm"
        ),
        MeasurementInfo(
            key = "neckCircumference",
            title = "Neck Circumference",
            description = "Base of the neck circumference.",
            howToMeasure = "Wrap tape measure around base of neck where a collar sits, allowing one finger space for comfort.",
            typicalRangeInches = "12\" – 18\"",
            typicalRangeCm = "30 cm – 46 cm"
        ),
        MeasurementInfo(
            key = "bustPoint",
            title = "Bust Point to Point (Apex Distance)",
            description = "Distance between the two bust apex points.",
            howToMeasure = "Measure horizontally from one nipple apex to the other across the chest.",
            typicalRangeInches = "6.5\" – 11\"",
            typicalRangeCm = "16 cm – 28 cm"
        ),
        MeasurementInfo(
            key = "shoulderToWaist",
            title = "Front Shoulder to Waist",
            description = "Front bodice length over bust apex.",
            howToMeasure = "From mid-shoulder over the fullest part of the bust straight down to the natural waist tape.",
            typicalRangeInches = "14\" – 20\"",
            typicalRangeCm = "35 cm – 51 cm"
        ),
        MeasurementInfo(
            key = "backWaistLength",
            title = "Back Waist Length",
            description = "Nape of neck down to waistline.",
            howToMeasure = "From the prominent bone at base of neck (C7 vertebra) straight down center back to waist tape.",
            typicalRangeInches = "14\" – 18.5\"",
            typicalRangeCm = "35 cm – 47 cm"
        ),
        MeasurementInfo(
            key = "armholeDepth",
            title = "Armhole Depth",
            description = "Vertical depth from shoulder to base of armpit.",
            howToMeasure = "Place a ruler snugly under armpit parallel to floor; measure from back neck base to level of ruler.",
            typicalRangeInches = "6.5\" – 9.5\"",
            typicalRangeCm = "16 cm – 24 cm"
        ),
        MeasurementInfo(
            key = "upperArmCircumference",
            title = "Upper Arm / Bicep",
            description = "Fullest part of bicep muscle.",
            howToMeasure = "With arm relaxed at side, measure around fullest point of upper arm.",
            typicalRangeInches = "9.5\" – 18\"",
            typicalRangeCm = "24 cm – 46 cm"
        ),
        MeasurementInfo(
            key = "sleeveLength",
            title = "Sleeve Length",
            description = "Shoulder point down to wrist with elbow slightly bent.",
            howToMeasure = "From shoulder bone tip, past slightly bent elbow, down to the wrist bone.",
            typicalRangeInches = "18\" – 26\"",
            typicalRangeCm = "46 cm – 66 cm"
        ),
        MeasurementInfo(
            key = "wristCircumference",
            title = "Wrist Circumference",
            description = "Circumference at wrist bone.",
            howToMeasure = "Measure around wrist bone comfortably.",
            typicalRangeInches = "5.5\" – 8.5\"",
            typicalRangeCm = "14 cm – 22 cm"
        ),
        MeasurementInfo(
            key = "desiredGarmentLength",
            title = "Desired Garment Length",
            description = "Total finished length of garment from shoulder to hem.",
            howToMeasure = "From shoulder point straight down to desired hemline (knee, midi, ankle, or floor).",
            typicalRangeInches = "20\" – 60\"",
            typicalRangeCm = "50 cm – 152 cm"
        )
    )
}
