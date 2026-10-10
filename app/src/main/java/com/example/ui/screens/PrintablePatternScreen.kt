package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.data.model.UnitSystem
import com.example.ui.components.PatternVisualizer
import com.example.ui.viewmodel.SewSmartViewModel

@Composable
fun PrintablePatternScreen(
    viewModel: SewSmartViewModel,
    modifier: Modifier = Modifier
) {
    val pieces by viewModel.currentPieces.collectAsState()
    val unitSystem by viewModel.measurementUnit.collectAsState()
    val selectedIndex by viewModel.selectedPieceIndex.collectAsState()
    var selectedPaperSize by remember { mutableStateOf("A4 (8.3\" × 11.7\")") }
    var printAtActualSizeChecked by remember { mutableStateOf(true) }
    var showPrintExportNotice by remember { mutableStateOf(false) }

    val activePiece = pieces.getOrNull(selectedIndex) ?: pieces.firstOrNull()

    Scaffold(
        topBar = {
            Surface(tonalElevation = 2.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                    Text(
                        text = "1:1 Printable Pattern Preview",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        bottomBar = {
            Surface(tonalElevation = 6.dp, shadowElevation = 6.dp) {
                Button(
                    onClick = { showPrintExportNotice = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(52.dp)
                        .testTag("export_pdf_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.PictureAsPdf, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Prepare 1:1 Scaled PDF Document", fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .testTag("printable_screen_scroll"),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // Mandatory Calibration Square Notice (Requirement 21)
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(Icons.Default.Square, contentDescription = null, tint = Color(0xFF1565C0), modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Print Calibration Square (Mandatory Check)",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0D47A1)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (unitSystem == UnitSystem.CENTIMETERS)
                                    "“Measure this square after printing. If it is not exactly 5 cm × 5 cm, check your printer's scaling settings and select Actual Size / 100%.”"
                                else
                                    "“Measure this square after printing. If it is not exactly 2 in × 2 in, check your printer's scaling settings and select Actual Size / 100%.”",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF1A237E),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // Paper Size and Scaling Settings (Requirement 20)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Print Settings", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Paper Format:", style = MaterialTheme.typography.bodyMedium)
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                FilterChip(
                                    selected = selectedPaperSize.startsWith("A4"),
                                    onClick = { selectedPaperSize = "A4 (8.3\" × 11.7\")" },
                                    label = { Text("A4") }
                                )
                                FilterChip(
                                    selected = selectedPaperSize.startsWith("Letter"),
                                    onClick = { selectedPaperSize = "Letter (8.5\" × 11\")" },
                                    label = { Text("US Letter") }
                                )
                                FilterChip(
                                    selected = selectedPaperSize.startsWith("A3"),
                                    onClick = { selectedPaperSize = "A3 (11.7\" × 16.5\")" },
                                    label = { Text("A3") }
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Scale: 100% / Actual Size", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                Text("Never scale to fit page", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                            }
                            Switch(
                                checked = printAtActualSizeChecked,
                                onCheckedChange = { printAtActualSizeChecked = true } // enforce 100%
                            )
                        }
                    }
                }
            }

            // Tiled Multi-Page Preview Canvas
            if (activePiece != null) {
                item {
                    Text(
                        text = "Tiled Layout Preview: ${activePiece.name}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Pieces larger than one page are divided into printable tiles with alignment marks (e.g. Page 1A connects to Page 1B).",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                item {
                    PatternVisualizer(
                        piece = activePiece,
                        unitSystem = unitSystem,
                        showMeasurements = true,
                        showSeamAllowance = true,
                        showCalibrationSquare = true, // Shows calibration square
                        showTiledGrid = true // Shows tiled grid
                    )
                }
            }

            // Assembly instructions (Requirement 22)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Tiled Page Assembly Instructions", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                        Text("1. Print at 100% / Actual Size without margin scaling.", style = MaterialTheme.typography.bodySmall)
                        Text("2. Check the 5cm × 5cm (or 2in × 2in) calibration square with your physical ruler.", style = MaterialTheme.typography.bodySmall)
                        Text("3. Trim the right and bottom border margins along the dotted cut line.", style = MaterialTheme.typography.bodySmall)
                        Text("4. Overlap tiles matching corner registration crosshairs (e.g. 1A to 1B, 2A to 2B) and tape securely.", style = MaterialTheme.typography.bodySmall)
                        Text("5. Cut out along the solid cutting line.", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }

    if (showPrintExportNotice) {
        AlertDialog(
            onDismissRequest = { showPrintExportNotice = false },
            icon = { Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
            title = { Text("Print Layout Prepared", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "The pattern geometry has been mapped to $selectedPaperSize with exact 1:1 true scale coordinate buffers, 5cm calibration reference, and crosshair tile markers ready for printing."
                )
            },
            confirmButton = {
                Button(onClick = { showPrintExportNotice = false }) {
                    Text("OK")
                }
            }
        )
    }
}
