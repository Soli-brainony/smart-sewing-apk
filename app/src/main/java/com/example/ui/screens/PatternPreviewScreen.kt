package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.components.PatternVisualizer
import com.example.ui.components.WorkflowStepper
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.SewSmartViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatternPreviewScreen(
    viewModel: SewSmartViewModel,
    modifier: Modifier = Modifier
) {
    val pieces by viewModel.currentPieces.collectAsState()
    val breakdowns by viewModel.calculationBreakdowns.collectAsState()
    val selectedIndex by viewModel.selectedPieceIndex.collectAsState()
    val unitSystem by viewModel.measurementUnit.collectAsState()
    val easeInches by viewModel.customEaseInches.collectAsState()
    val seamAllowanceInches by viewModel.customSeamAllowanceInches.collectAsState()
    val version by viewModel.patternVersion.collectAsState()
    val showUpdatedNotice by viewModel.patternUpdatedNotice.collectAsState()
    val testGarmentLog by viewModel.testGarmentLog.collectAsState()

    var showMeasurements by remember { mutableStateOf(true) }
    var showSeamAllowance by remember { mutableStateOf(true) }
    var showCalibrationSquare by remember { mutableStateOf(false) }
    var showTiledGrid by remember { mutableStateOf(false) }

    var showPieceDetailsSheet by remember { mutableStateOf<PatternPiece?>(null) }
    var showAdjustersDialog by remember { mutableStateOf(false) }
    var showTestGarmentDialog by remember { mutableStateOf(false) }

    val activePiece = pieces.getOrNull(selectedIndex) ?: pieces.firstOrNull()

    Scaffold(
        topBar = {
            Column {
                WorkflowStepper(
                    currentStep = 3,
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
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = { viewModel.navigateTo(AppScreen.PRINTABLE_PATTERN) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("printable_pattern_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Print 1:1")
                    }

                    Button(
                        onClick = { viewModel.navigateTo(AppScreen.CUTTING_PLAN_STEP_4) },
                        modifier = Modifier
                            .weight(1.5f)
                            .height(50.dp)
                            .testTag("proceed_to_cutting_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Step 4: Cutting Plan", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(6.dp))
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
                .testTag("pattern_preview_scroll"),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // Header with Version and Updated Banner
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Step 3 — Pattern Preview",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Calculated from body geometry & tailoring ease.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Version $version",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                // "Pattern Updated" dynamic notice
                AnimatedVisibility(
                    visible = showUpdatedNotice,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Pattern Updated with new measurements & ease parameters!",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color(0xFF1B5E20),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Piece selector tabs
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    itemsIndexed(pieces) { idx, piece ->
                        val isSelected = idx == selectedIndex
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.selectedPieceIndex.value = idx },
                            label = { Text(piece.name) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    }
                }
            }

            // Interactive Pattern Visualizer Canvas
            if (activePiece != null) {
                item {
                    PatternVisualizer(
                        piece = activePiece,
                        unitSystem = unitSystem,
                        showMeasurements = showMeasurements,
                        showSeamAllowance = showSeamAllowance,
                        showCalibrationSquare = showCalibrationSquare,
                        showTiledGrid = showTiledGrid,
                        onPieceClick = { showPieceDetailsSheet = activePiece }
                    )
                }

                // Visualizer Quick Toggles & Param Adjusters
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            FilledTonalIconButton(
                                onClick = { showMeasurements = !showMeasurements },
                                modifier = Modifier.size(38.dp).testTag("toggle_measurements_btn")
                            ) {
                                Icon(
                                    imageVector = if (showMeasurements) Icons.Default.Straighten else Icons.Outlined.Straighten,
                                    contentDescription = "Toggle Measurements",
                                    tint = if (showMeasurements) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            FilledTonalIconButton(
                                onClick = { showSeamAllowance = !showSeamAllowance },
                                modifier = Modifier.size(38.dp).testTag("toggle_seam_allowance_btn")
                            ) {
                                Icon(
                                    imageVector = if (showSeamAllowance) Icons.Default.LineStyle else Icons.Outlined.LineStyle,
                                    contentDescription = "Toggle Seam Allowance",
                                    tint = if (showSeamAllowance) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            FilledTonalIconButton(
                                onClick = { showCalibrationSquare = !showCalibrationSquare },
                                modifier = Modifier.size(38.dp).testTag("toggle_calibration_btn")
                            ) {
                                Icon(
                                    imageVector = if (showCalibrationSquare) Icons.Default.Square else Icons.Outlined.Square,
                                    contentDescription = "Toggle 5cm/2in Calibration Square",
                                    tint = if (showCalibrationSquare) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            FilledTonalIconButton(
                                onClick = { showTiledGrid = !showTiledGrid },
                                modifier = Modifier.size(38.dp).testTag("toggle_tiles_btn")
                            ) {
                                Icon(
                                    imageVector = if (showTiledGrid) Icons.Default.GridOn else Icons.Outlined.GridOff,
                                    contentDescription = "Toggle A4 Tiled Grid",
                                    tint = if (showTiledGrid) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = { showAdjustersDialog = true },
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Adjust Ease")
                            }

                            Button(
                                onClick = { showPieceDetailsSheet = activePiece },
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Piece Info")
                            }
                        }
                    }
                }
            }

            // Test Garment (Muslin/Toile) Mode Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Science, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Recommended: Make a Test Garment", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                            }
                            TextButton(onClick = { showTestGarmentDialog = true }) {
                                Text("Record Tweak")
                            }
                        }
                        Text(
                            text = "Sew an inexpensive muslin or toile first to test bust, waist, armhole and drape before cutting expensive fashion fabric.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )

                        if (testGarmentLog.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Recorded Alterations:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                            testGarmentLog.forEach { log ->
                                Text("• $log", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }

            // Calculation Breakdown Table (Formula vs Ease vs Seam Allowance)
            item {
                Text(
                    text = "Pattern Calculation Breakdown",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Clearly distinguishing body measurement, pattern ease, and final cutting line.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            items(breakdowns) { b ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(b.pieceName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
                            Text(b.parameterName, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Medium)
                        }
                        Text(b.formulaExplanation, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Divider(modifier = Modifier.padding(vertical = 4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Body", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                                Text("${String.format("%.1f", b.bodyMeasurement)}\"", fontWeight = FontWeight.SemiBold)
                            }
                            Column {
                                Text("Total Ease", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                                Text("+${String.format("%.2f", b.easeApplied)}\"", fontWeight = FontWeight.SemiBold)
                            }
                            Column {
                                Text("Pattern Width", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                                Text("${String.format("%.2f", b.patternMeasurement)}\"", fontWeight = FontWeight.SemiBold)
                            }
                            Column {
                                Text("Final Cut Line", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                                Text("${String.format("%.2f", b.finalCuttingMeasurement)}\"", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            }
        }
    }

    // Bottom Sheet for Pattern Piece Information (Requirement 13)
    if (showPieceDetailsSheet != null) {
        val p = showPieceDetailsSheet!!
        ModalBottomSheet(
            onDismissRequest = { showPieceDetailsSheet = null }
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = p.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Divider()
                FeatureRow("Quantity to Cut", p.cutQuantity)
                FeatureRow("Fabric Type", p.fabricType)
                FeatureRow("Seam Allowance", "${String.format("%.2f", p.seamAllowanceInches)}\" (${String.format("%.1f", p.seamAllowanceInches * 2.54f)} cm)")
                FeatureRow("Grainline", p.grainlineDirection)
                FeatureRow("Fold Requirement", if (p.foldPosition != com.example.data.model.FoldPosition.NONE) "Cut along fabric fold" else "Separate flat pieces")
                Divider()
                Text("Cutting & Sewing Instructions:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
                Text(p.constructionNotes, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { showPieceDetailsSheet = null },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Close")
                }
            }
        }
    }

    // Adjusters Dialog (Requirement 12)
    if (showAdjustersDialog) {
        var tempEase by remember { mutableFloatStateOf(easeInches) }
        var tempSa by remember { mutableFloatStateOf(seamAllowanceInches) }

        AlertDialog(
            onDismissRequest = { showAdjustersDialog = false },
            title = { Text("Adjust Pattern Parameters", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        text = "Fine-tune pattern ease and seam allowances. The geometry recalculates automatically.",
                        style = MaterialTheme.typography.bodySmall
                    )

                    Column {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Wearing Ease: ${String.format("%.1f", tempEase)}\"", fontWeight = FontWeight.Bold)
                            Text(if (tempEase <= 1.0f) "Fitted" else if (tempEase <= 2.0f) "Standard" else "Relaxed")
                        }
                        Slider(
                            value = tempEase,
                            onValueChange = { tempEase = it },
                            valueRange = 0.5f..4.0f,
                            steps = 6
                        )
                    }

                    Column {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Seam Allowance: ${String.format("%.2f", tempSa)}\"", fontWeight = FontWeight.Bold)
                            Text(if (tempSa < 0.6f) "1/2\" (Standard)" else "5/8\" (Commercial)")
                        }
                        Slider(
                            value = tempSa,
                            onValueChange = { tempSa = it },
                            valueRange = 0.375f..0.75f,
                            steps = 2
                        )
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.customEaseInches.value = tempEase
                    viewModel.customSeamAllowanceInches.value = tempSa
                    viewModel.recalculatePattern(notifyUpdate = true)
                    showAdjustersDialog = false
                }) {
                    Text("Apply & Recalculate")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAdjustersDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Test Garment Fit Alterations Dialog (Requirement 24)
    if (showTestGarmentDialog) {
        var noteInput by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showTestGarmentDialog = false },
            title = { Text("Record Muslin / Toile Fitting Tweak", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Enter fit observations from your test garment (e.g., 'Reduce waist by 1 in', 'Add 2 cm to sleeve length', 'Take in side seams').",
                        style = MaterialTheme.typography.bodySmall
                    )
                    OutlinedTextField(
                        value = noteInput,
                        onValueChange = { noteInput = it },
                        placeholder = { Text("e.g. Reduce waist by 1 in") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (noteInput.isNotBlank()) {
                        viewModel.recordTestGarmentAdjustment(noteInput)
                    }
                    showTestGarmentDialog = false
                }) {
                    Text("Apply Adjustment")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTestGarmentDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
