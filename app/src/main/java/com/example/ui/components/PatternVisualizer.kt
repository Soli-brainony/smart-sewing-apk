package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.data.pattern.GeometryUtils
import kotlin.math.max
import kotlin.math.min

@Composable
fun PatternVisualizer(
    piece: PatternPiece,
    unitSystem: UnitSystem,
    showMeasurements: Boolean = true,
    showSeamAllowance: Boolean = true,
    showCalibrationSquare: Boolean = false,
    showTiledGrid: Boolean = false,
    modifier: Modifier = Modifier,
    onPieceClick: (() -> Unit)? = null
) {
    // Zoom and pan state
    var scale by remember(piece.id) { mutableFloatStateOf(1.0f) }
    var offsetX by remember(piece.id) { mutableFloatStateOf(0f) }
    var offsetY by remember(piece.id) { mutableFloatStateOf(0f) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(380.dp)
            .background(Color(0xFFF9F7F2), RoundedCornerShape(16.dp))
            .pointerInput(piece.id) {
                detectTransformGestures { _, pan, zoom, _ ->
                    scale = (scale * zoom).coerceIn(0.5f, 4.0f)
                    offsetX += pan.x
                    offsetY += pan.y
                }
            }
            .testTag("pattern_visualizer_canvas")
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            // Calculate bounding box of the pattern piece in inches
            val bounds = GeometryUtils.calculateBounds(piece.outlinePoints)
            val paddingInches = 3.0f
            val contentWidth = max(bounds.width + paddingInches * 2, 12f)
            val contentHeight = max(bounds.height + paddingInches * 2, 16f)

            // Auto-fit scale factor: pixels per inch
            val basePixelsPerInch = min(canvasWidth / contentWidth, canvasHeight / contentHeight)
            val ppi = basePixelsPerInch * scale

            val originX = (canvasWidth - bounds.width * ppi) / 2f + offsetX
            val originY = (canvasHeight - bounds.height * ppi) / 2f + offsetY

            // 1. Draw Tailor's Drafting Paper Grid (1-inch grid)
            val gridStep = ppi * 1.0f
            if (gridStep > 12f) {
                val startX = (originX % gridStep)
                val startY = (originY % gridStep)
                var gx = startX
                while (gx < canvasWidth) {
                    drawLine(
                        color = Color(0xFFE4DFD5),
                        start = Offset(gx, 0f),
                        end = Offset(gx, canvasHeight),
                        strokeWidth = 1f
                    )
                    gx += gridStep
                }
                var gy = startY
                while (gy < canvasHeight) {
                    drawLine(
                        color = Color(0xFFE4DFD5),
                        start = Offset(0f, gy),
                        end = Offset(canvasWidth, gy),
                        strokeWidth = 1f
                    )
                    gy += gridStep
                }
            }

            // 2. Draw Tiled Multi-Page Printing Grid if enabled (A4 simulation: 8.27" x 11.69")
            if (showTiledGrid) {
                val a4W = 8.27f * ppi
                val a4H = 11.69f * ppi
                val cols = 3
                val rows = 3
                for (r in 0 until rows) {
                    for (c in 0 until cols) {
                        val tileX = originX - paddingInches * ppi + c * a4W
                        val tileY = originY - paddingInches * ppi + r * a4H

                        drawRect(
                            color = Color(0x334A6572),
                            topLeft = Offset(tileX, tileY),
                            size = Size(a4W, a4H),
                            style = Stroke(
                                width = 1.5f,
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                            )
                        )

                        // Registration crosshair at page corners
                        val crossSize = 8f
                        drawLine(Color(0xFF8B3A62), Offset(tileX - crossSize, tileY), Offset(tileX + crossSize, tileY), 1.5f)
                        drawLine(Color(0xFF8B3A62), Offset(tileX, tileY - crossSize), Offset(tileX, tileY + crossSize), 1.5f)

                        // Tile Label e.g. "Page 1A"
                        val pageChar = ('A' + c).toString()
                        val pageNum = r + 1
                        drawContext.canvas.nativeCanvas.apply {
                            val paint = android.graphics.Paint().apply {
                                color = android.graphics.Color.parseColor("#4A6572")
                                textSize = 24f
                                isFakeBoldText = true
                            }
                            drawText("Tile $pageNum$pageChar", tileX + 16f, tileY + 36f, paint)
                        }
                    }
                }
            }

            // Helper to project pattern inches to screen coordinates
            fun toScreen(pt: Point2D): Offset {
                return Offset(originX + pt.x * ppi, originY + pt.y * ppi)
            }

            // 3. Draw Seam Allowance Buffer if enabled
            if (showSeamAllowance && piece.outlinePoints.isNotEmpty()) {
                val saInches = piece.seamAllowanceInches
                val saPath = Path()
                piece.outlinePoints.forEachIndexed { i, pt ->
                    // Offset point outward based on fold status
                    val xOffset = if (piece.foldPosition == FoldPosition.LEFT && pt.x < 0.2f) 0f else saInches
                    val yOffset = saInches
                    val projected = Offset(
                        originX + (pt.x + if (pt.x > bounds.width * 0.5f) xOffset else -xOffset * 0.3f) * ppi,
                        originY + (pt.y + if (pt.y > bounds.height * 0.5f) yOffset else -yOffset) * ppi
                    )
                    if (i == 0) saPath.moveTo(projected.x, projected.y) else saPath.lineTo(projected.x, projected.y)
                }
                saPath.close()

                drawPath(
                    path = saPath,
                    color = Color(0xFFD4A373),
                    style = Stroke(
                        width = 1.5f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f), 0f)
                    )
                )
            }

            // 4. Draw Main Pattern Piece Perimeter (Solid sewing line)
            if (piece.outlinePoints.isNotEmpty()) {
                val mainPath = Path()
                piece.outlinePoints.forEachIndexed { index, pt ->
                    val screenPt = toScreen(pt)
                    if (index == 0) mainPath.moveTo(screenPt.x, screenPt.y) else mainPath.lineTo(screenPt.x, screenPt.y)
                }
                mainPath.close()

                // Subtle semi-transparent fill for piece shape
                drawPath(
                    path = mainPath,
                    color = Color(0x157B2869)
                )

                // Bold outline
                drawPath(
                    path = mainPath,
                    color = Color(0xFF33202C),
                    style = Stroke(width = 3.5f)
                )
            }

            // 5. Draw Fold Line indicator if applicable
            if (piece.foldPosition == FoldPosition.LEFT && piece.outlinePoints.size >= 2) {
                val foldStart = toScreen(piece.outlinePoints[0])
                val foldEnd = toScreen(piece.outlinePoints[1])
                // Highlight fold edge in primary burgundy
                drawLine(
                    color = Color(0xFF7B2869),
                    start = foldStart,
                    end = foldEnd,
                    strokeWidth = 5f
                )

                // Fold text and brackets
                val midFoldY = (foldStart.y + foldEnd.y) / 2f
                drawContext.canvas.nativeCanvas.apply {
                    val paint = android.graphics.Paint().apply {
                        color = android.graphics.Color.parseColor("#7B2869")
                        textSize = 28f
                        isFakeBoldText = true
                    }
                    drawText("◄ FOLD LINE (Place on fabric fold) ►", foldStart.x + 12f, midFoldY, paint)
                }
            }

            // 6. Draw Darts
            for (dart in piece.darts) {
                val apexScreen = toScreen(dart.apex)
                val legLScreen = toScreen(dart.legLeft)
                val legRScreen = toScreen(dart.legRight)

                val dartPath = Path().apply {
                    moveTo(legLScreen.x, legLScreen.y)
                    lineTo(apexScreen.x, apexScreen.y)
                    lineTo(legRScreen.x, legRScreen.y)
                }

                drawPath(
                    path = dartPath,
                    color = Color(0xFFB33939),
                    style = Stroke(width = 2.5f)
                )

                // Dart apex circle point
                drawCircle(
                    color = Color(0xFFB33939),
                    radius = 4f,
                    center = apexScreen
                )

                // Center fold line of dart
                val midLeg = Offset((legLScreen.x + legRScreen.x) / 2f, (legLScreen.y + legRScreen.y) / 2f)
                drawLine(
                    color = Color(0x88B33939),
                    start = apexScreen,
                    end = midLeg,
                    strokeWidth = 1.5f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 4f), 0f)
                )
            }

            // 7. Draw Straight Grainline Arrow
            val grainlineX = originX + (bounds.width * 0.45f) * ppi
            val grainlineTop = originY + (bounds.height * 0.15f) * ppi
            val grainlineBottom = originY + (bounds.height * 0.85f) * ppi

            if (grainlineBottom - grainlineTop > 30f) {
                // Line
                drawLine(
                    color = Color(0xFF2C3E50),
                    start = Offset(grainlineX, grainlineTop),
                    end = Offset(grainlineX, grainlineBottom),
                    strokeWidth = 2.5f
                )
                // Top Arrowhead
                val arrowHeadLen = 14f
                drawLine(Color(0xFF2C3E50), Offset(grainlineX, grainlineTop), Offset(grainlineX - arrowHeadLen * 0.5f, grainlineTop + arrowHeadLen), 2.5f)
                drawLine(Color(0xFF2C3E50), Offset(grainlineX, grainlineTop), Offset(grainlineX + arrowHeadLen * 0.5f, grainlineTop + arrowHeadLen), 2.5f)
                // Bottom Arrowhead
                drawLine(Color(0xFF2C3E50), Offset(grainlineX, grainlineBottom), Offset(grainlineX - arrowHeadLen * 0.5f, grainlineBottom - arrowHeadLen), 2.5f)
                drawLine(Color(0xFF2C3E50), Offset(grainlineX, grainlineBottom), Offset(grainlineX + arrowHeadLen * 0.5f, grainlineBottom - arrowHeadLen), 2.5f)

                // Label along grainline
                drawContext.canvas.nativeCanvas.apply {
                    val paint = android.graphics.Paint().apply {
                        color = android.graphics.Color.parseColor("#2C3E50")
                        textSize = 22f
                        isFakeBoldText = true
                    }
                    drawText("GRAINLINE", grainlineX + 8f, (grainlineTop + grainlineBottom) / 2f, paint)
                }
            }

            // 8. Draw Notches
            for (notch in piece.notches) {
                val notchScreen = toScreen(notch.position)
                val notchTickLen = 14f
                drawLine(
                    color = Color(0xFFC0392B),
                    start = notchScreen,
                    end = Offset(notchScreen.x + notchTickLen, notchScreen.y),
                    strokeWidth = 3f
                )
            }

            // 9. Draw Measurement Dimension Labels if enabled
            if (showMeasurements) {
                for (lbl in piece.measurementLabels) {
                    val p1 = toScreen(lbl.startPoint)
                    val p2 = toScreen(lbl.endPoint)

                    // Dimension line with small ticks
                    drawLine(
                        color = if (lbl.isPrimary) Color(0xFF0F4C81) else Color(0xFF7F8C8D),
                        start = p1,
                        end = p2,
                        strokeWidth = if (lbl.isPrimary) 2f else 1.5f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 4f), 0f)
                    )

                    // Label text
                    val midX = (p1.x + p2.x) / 2f
                    val midY = (p1.y + p2.y) / 2f
                    val formatted = lbl.formattedValue(unitSystem)

                    drawContext.canvas.nativeCanvas.apply {
                        val paint = android.graphics.Paint().apply {
                            color = if (lbl.isPrimary) android.graphics.Color.parseColor("#0F4C81") else android.graphics.Color.parseColor("#555555")
                            textSize = 22f
                            isFakeBoldText = lbl.isPrimary
                        }
                        drawText("${lbl.label} ($formatted)", midX - 40f, midY - 8f, paint)
                    }
                }
            }

            // 10. Draw Calibration Square if toggled (5cm x 5cm or 2in x 2in)
            if (showCalibrationSquare) {
                val squareSideInches = if (unitSystem == UnitSystem.CENTIMETERS) (5f / 2.54f) else 2.0f
                val squareSidePx = squareSideInches * ppi
                val sqX = canvasWidth - squareSidePx - 24f
                val sqY = 24f

                drawRect(
                    color = Color(0xFFFFFFFF),
                    topLeft = Offset(sqX, sqY),
                    size = Size(squareSidePx, squareSidePx)
                )

                drawRect(
                    color = Color(0xFF1B4965),
                    topLeft = Offset(sqX, sqY),
                    size = Size(squareSidePx, squareSidePx),
                    style = Stroke(width = 2.5f)
                )

                // Diagonal alignment check
                drawLine(Color(0x551B4965), Offset(sqX, sqY), Offset(sqX + squareSidePx, sqY + squareSidePx), 1f)

                drawContext.canvas.nativeCanvas.apply {
                    val paint = android.graphics.Paint().apply {
                        color = android.graphics.Color.parseColor("#1B4965")
                        textSize = 20f
                        isFakeBoldText = true
                        textAlign = android.graphics.Paint.Align.CENTER
                    }
                    val label = if (unitSystem == UnitSystem.CENTIMETERS) "CALIBRATION\n5 cm × 5 cm" else "CALIBRATION\n2 in × 2 in"
                    val lines = label.split("\n")
                    drawText(lines[0], sqX + squareSidePx / 2f, sqY + squareSidePx / 2f - 6f, paint)
                    drawText(lines[1], sqX + squareSidePx / 2f, sqY + squareSidePx / 2f + 18f, paint)
                }
            }

            // Center piece stamp: Title, Cut qty, Seam allowance
            drawContext.canvas.nativeCanvas.apply {
                val titlePaint = android.graphics.Paint().apply {
                    color = android.graphics.Color.parseColor("#1A1C29")
                    textSize = 30f
                    isFakeBoldText = true
                    textAlign = android.graphics.Paint.Align.CENTER
                }
                val subPaint = android.graphics.Paint().apply {
                    color = android.graphics.Color.parseColor("#5A5B6A")
                    textSize = 22f
                    textAlign = android.graphics.Paint.Align.CENTER
                }

                val centerX = originX + (bounds.width * 0.5f) * ppi
                val centerY = originY + (bounds.height * 0.48f) * ppi

                drawText(piece.name.uppercase(), centerX, centerY - 28f, titlePaint)
                drawText("${piece.cutQuantity} • ${piece.fabricType}", centerX, centerY, subPaint)
                val saFormatted = if (unitSystem == UnitSystem.INCHES) "${String.format("%.2f", piece.seamAllowanceInches)}\" SA" else "${String.format("%.1f", piece.seamAllowanceInches * 2.54f)} cm SA"
                drawText("Seam Allowance: $saFormatted", centerX, centerY + 26f, subPaint)
            }
        }

        // Overlay floating controls: Zoom controls & Reset
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            SmallFloatingActionButton(
                onClick = { scale = (scale * 1.25f).coerceAtMost(4f) },
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(36.dp).testTag("zoom_in_button")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Zoom In", modifier = Modifier.size(18.dp))
            }
            SmallFloatingActionButton(
                onClick = { scale = (scale / 1.25f).coerceAtLeast(0.5f) },
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(36.dp).testTag("zoom_out_button")
            ) {
                Icon(Icons.Default.Remove, contentDescription = "Zoom Out", modifier = Modifier.size(18.dp))
            }
            SmallFloatingActionButton(
                onClick = {
                    scale = 1.0f
                    offsetX = 0f
                    offsetY = 0f
                },
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(36.dp).testTag("reset_view_button")
            ) {
                Icon(Icons.Default.Refresh, contentDescription = "Reset View", modifier = Modifier.size(18.dp))
            }
        }

        // Top-left dimensions chip
        Surface(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(12.dp),
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(Icons.Default.SquareFoot, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.primary)
                Text(
                    text = piece.formattedDimensions(unitSystem),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
