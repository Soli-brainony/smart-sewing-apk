package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.components.WorkflowStepper
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.SewSmartViewModel

@Composable
fun MeasurementsScreen(
    viewModel: SewSmartViewModel,
    modifier: Modifier = Modifier
) {
    val measurements by viewModel.currentMeasurements.collectAsState()
    val unitSystem by viewModel.measurementUnit.collectAsState()

    var selectedGuideInfo by remember { mutableStateOf<MeasurementInfo?>(null) }
    var showSavedNotice by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Column {
                WorkflowStepper(
                    currentStep = 2,
                    onStepClick = { screen -> viewModel.navigateTo(screen) }
                )
            }
        },
        bottomBar = {
            Surface(
                tonalElevation = 8.dp,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            viewModel.saveMeasurementsToProfile()
                            showSavedNotice = true
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("save_profile_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (showSavedNotice) "Saved!" else "Save Profile")
                    }

                    Button(
                        onClick = {
                            viewModel.recalculatePattern(notifyUpdate = true)
                            viewModel.navigateTo(AppScreen.PATTERN_STEP_3)
                        },
                        modifier = Modifier
                            .weight(1.5f)
                            .height(50.dp)
                            .testTag("generate_pattern_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Calculate Pattern", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.Default.ArrowForward, contentDescription = null)
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .testTag("measurements_screen_scroll"),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Step 2 — Enter Your Measurements",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Precise body measurements are the foundation of a great sewing pattern.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Unit Toggle: Inches vs Centimeters
                    SingleChoiceSegmentedButtonRow {
                        SegmentedButton(
                            selected = unitSystem == UnitSystem.INCHES,
                            onClick = { viewModel.setUnitSystem(UnitSystem.INCHES) },
                            shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
                        ) {
                            Text("Inches")
                        }
                        SegmentedButton(
                            selected = unitSystem == UnitSystem.CENTIMETERS,
                            onClick = { viewModel.setUnitSystem(UnitSystem.CENTIMETERS) },
                            shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
                        ) {
                            Text("cm")
                        }
                    }
                }
            }

            // Standard Preset Quick Chooser
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Quick Presets (or enter custom measurements below):",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterChip(
                                selected = false,
                                onClick = { applyStandardPreset(viewModel, 34f, 26f, 36f, unitSystem) },
                                label = { Text("Small (US 4 / UK 8)") }
                            )
                            FilterChip(
                                selected = false,
                                onClick = { applyStandardPreset(viewModel, 36f, 28f, 39f, unitSystem) },
                                label = { Text("Medium (US 8 / UK 12)") }
                            )
                            FilterChip(
                                selected = false,
                                onClick = { applyStandardPreset(viewModel, 40f, 32f, 43f, unitSystem) },
                                label = { Text("Large (US 12 / UK 16)") }
                            )
                        }
                    }
                }
            }

            // Measurement Fields
            item {
                Text(
                    text = "Core Body Dimensions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            items(MeasurementGuides.items) { guide ->
                val currentValue = when (guide.key) {
                    "bust" -> measurements.bust
                    "waist" -> measurements.waist
                    "hip" -> measurements.hip
                    "shoulderWidth" -> measurements.shoulderWidth
                    "neckCircumference" -> measurements.neckCircumference
                    "bustPoint" -> measurements.bustPoint
                    "bustHeight" -> measurements.bustHeight
                    "shoulderToWaist" -> measurements.shoulderToWaist
                    "backWaistLength" -> measurements.backWaistLength
                    "armholeDepth" -> measurements.armholeDepth
                    "upperArmCircumference" -> measurements.upperArmCircumference
                    "sleeveLength" -> measurements.sleeveLength
                    "wristCircumference" -> measurements.wristCircumference
                    "desiredGarmentLength" -> measurements.desiredGarmentLength
                    else -> 0f
                }

                MeasurementInputRow(
                    guide = guide,
                    value = currentValue,
                    unit = unitSystem,
                    onValueChange = { newVal ->
                        viewModel.updateMeasurement(guide.key, newVal)
                    },
                    onInfoClick = { selectedGuideInfo = guide }
                )
            }
        }
    }

    // Modal Sheet showing "How to Measure" explanation
    if (selectedGuideInfo != null) {
        val info = selectedGuideInfo!!
        AlertDialog(
            onDismissRequest = { selectedGuideInfo = null },
            icon = { Icon(Icons.Default.Straighten, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
            title = { Text(info.title, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(info.description, style = MaterialTheme.typography.bodyMedium)
                    Divider()
                    Text("How to measure:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
                    Text(info.howToMeasure, style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Typical range: ${if (unitSystem == UnitSystem.INCHES) info.typicalRangeInches else info.typicalRangeCm}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedGuideInfo = null }) {
                    Text("Got it")
                }
            }
        )
    }
}

@Composable
fun MeasurementInputRow(
    guide: MeasurementInfo,
    value: Float,
    unit: UnitSystem,
    onValueChange: (Float) -> Unit,
    onInfoClick: () -> Unit
) {
    var textValue by remember(value) {
        mutableStateOf(String.format(if (unit == UnitSystem.INCHES) "%.1f" else "%.0f", value))
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 14.dp, vertical = 10.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = guide.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    IconButton(
                        onClick = onInfoClick,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.HelpOutline,
                            contentDescription = "Measurement Info",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Text(
                    text = guide.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            OutlinedTextField(
                value = textValue,
                onValueChange = { input ->
                    textValue = input
                    val parsed = input.toFloatOrNull()
                    if (parsed != null && parsed > 0) {
                        onValueChange(parsed)
                    }
                },
                trailingIcon = {
                    Text(
                        text = unit.symbol,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier
                    .width(105.dp)
                    .testTag("measurement_input_${guide.key}"),
                shape = RoundedCornerShape(8.dp)
            )
        }
    }
}

private fun applyStandardPreset(
    viewModel: SewSmartViewModel,
    bustIn: Float,
    waistIn: Float,
    hipIn: Float,
    unit: UnitSystem
) {
    val factor = if (unit == UnitSystem.CENTIMETERS) 2.54f else 1.0f
    viewModel.updateMeasurement("bust", bustIn * factor)
    viewModel.updateMeasurement("waist", waistIn * factor)
    viewModel.updateMeasurement("hip", hipIn * factor)
}
