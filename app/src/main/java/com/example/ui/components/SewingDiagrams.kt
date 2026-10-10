package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.VisualDiagramType

@Composable
fun SewingTechniqueDiagram(
    diagramType: VisualDiagramType,
    modifier: Modifier = Modifier
) {
    if (diagramType == VisualDiagramType.NONE) return

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFFFBF9F5), RoundedCornerShape(12.dp))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val title = when (diagramType) {
            VisualDiagramType.PATTERN_ON_FOLD -> "Visual Guide: Placing Pattern on Fold"
            VisualDiagramType.SEWING_A_DART -> "Visual Guide: Sewing a Tapered Dart"
            VisualDiagramType.GATHERING_STITCHES -> "Visual Guide: Gathering with 2 Basting Rows"
            VisualDiagramType.INVISIBLE_ZIPPER -> "Visual Guide: Invisible Zipper Insertion"
            VisualDiagramType.SET_IN_SLEEVE -> "Visual Guide: Setting In a Sleeve Cap"
            VisualDiagramType.NECKLINE_FACING -> "Visual Guide: Understitching Neckline Facing"
            VisualDiagramType.HEMMING_STITCH -> "Visual Guide: Double-Fold Blind Hem"
            else -> "Visual Technique"
        }

        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
        ) {
            val w = size.width
            val h = size.height

            when (diagramType) {
                VisualDiagramType.PATTERN_ON_FOLD -> {
                    // Fabric folded: Draw folded fabric block
                    val foldX = 40f
                    val fabricW = w - 80f
                    // Fabric background
                    drawRect(Color(0xFFE8DCC4), Offset(foldX, 10f), Size(fabricW, h - 20f))
                    // Fold edge highlight
                    drawLine(Color(0xFF7B2869), Offset(foldX, 10f), Offset(foldX, h - 10f), 5f)
                    // Pattern piece sitting on fold
                    val patternPath = Path().apply {
                        moveTo(foldX, 20f)
                        lineTo(foldX + 100f, 20f)
                        lineTo(foldX + 120f, 65f)
                        lineTo(foldX + 90f, h - 20f)
                        lineTo(foldX, h - 20f)
                        close()
                    }
                    drawPath(patternPath, Color(0x337B2869))
                    drawPath(patternPath, Color(0xFF7B2869), style = Stroke(2.5f))

                    // Labels
                    drawContext.canvas.nativeCanvas.apply {
                        val paint = android.graphics.Paint().apply {
                            color = android.graphics.Color.parseColor("#7B2869")
                            textSize = 24f
                            isFakeBoldText = true
                        }
                        drawText("FOLD EDGE", foldX + 10f, 40f, paint)
                        val subPaint = android.graphics.Paint().apply {
                            color = android.graphics.Color.parseColor("#333333")
                            textSize = 20f
                        }
                        drawText("Pattern piece flush with fold", foldX + 120f, 65f, subPaint)
                        drawText("Selvages (raw edges) on right →", foldX + 120f, 95f, subPaint)
                    }
                }

                VisualDiagramType.SEWING_A_DART -> {
                    // Dart triangle
                    val apexX = w * 0.75f
                    val apexY = h * 0.5f
                    val legTopX = w * 0.15f
                    val legTopY = h * 0.2f
                    val legBotX = w * 0.15f
                    val legBotY = h * 0.8f

                    // Fabric fold
                    drawLine(Color(0xFF888888), Offset(legTopX - 30f, apexY), Offset(apexX + 40f, apexY), 1.5f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 4f), 0f))

                    val dartPath = Path().apply {
                        moveTo(legTopX, legTopY)
                        lineTo(apexX, apexY)
                        lineTo(legBotX, legBotY)
                    }
                    drawPath(dartPath, Color(0xFFB33939), style = Stroke(3f))
                    drawCircle(Color(0xFFB33939), 5f, Offset(apexX, apexY))

                    // Stitches running along dart legs
                    val stitchPath = Path().apply {
                        moveTo(legTopX + 10f, legTopY + 8f)
                        lineTo(apexX, apexY)
                    }
                    drawPath(stitchPath, Color(0xFF2C3E50), style = Stroke(2f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 4f), 0f)))

                    drawContext.canvas.nativeCanvas.apply {
                        val paint = android.graphics.Paint().apply {
                            color = android.graphics.Color.parseColor("#B33939")
                            textSize = 22f
                            isFakeBoldText = true
                        }
                        drawText("Apex: Tie knot by hand", apexX - 100f, apexY - 14f, paint)
                        val sub = android.graphics.Paint().apply {
                            color = android.graphics.Color.parseColor("#444444")
                            textSize = 20f
                        }
                        drawText("Stitch from wide edge → taper off fold", legTopX, h - 8f, sub)
                    }
                }

                VisualDiagramType.GATHERING_STITCHES -> {
                    // Two parallel basting rows
                    val row1Y = 35f
                    val row2Y = 60f
                    val lineStart = 30f
                    val lineEnd = w - 30f

                    drawLine(Color(0xFF7B2869), Offset(lineStart, row1Y), Offset(lineEnd, row1Y), 2.5f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 6f), 0f))
                    drawLine(Color(0xFF2C3E50), Offset(lineStart, row2Y), Offset(lineEnd, row2Y), 2.5f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 6f), 0f))

                    // Ripples below
                    val ripplePath = Path().apply {
                        moveTo(lineStart, 95f)
                        var curX = lineStart
                        while (curX < lineEnd) {
                            cubicTo(curX + 15f, 85f, curX + 25f, 105f, curX + 40f, 95f)
                            curX += 40f
                        }
                    }
                    drawPath(ripplePath, Color(0xFFE5A93B), style = Stroke(3f))

                    drawContext.canvas.nativeCanvas.apply {
                        val paint = android.graphics.Paint().apply {
                            color = android.graphics.Color.parseColor("#333333")
                            textSize = 20f
                        }
                        drawText("Row 1: 1/4\" from edge (basting 4.0mm)", lineStart, row1Y - 8f, paint)
                        drawText("Row 2: 1/2\" from edge (permanent seam sits between)", lineStart, row2Y - 8f, paint)
                        drawText("Pull both bobbin threads together for even ripples", lineStart, 120f, paint)
                    }
                }

                VisualDiagramType.SET_IN_SLEEVE -> {
                    // Armhole & Sleeve Crown
                    val cx = w * 0.5f
                    val armholePath = Path().apply {
                        moveTo(cx - 100f, 30f)
                        cubicTo(cx - 60f, 100f, cx + 60f, 100f, cx + 100f, 30f)
                    }
                    drawPath(armholePath, Color(0xFF2C3E50), style = Stroke(3f))

                    val sleeveCapPath = Path().apply {
                        moveTo(cx - 95f, 35f)
                        cubicTo(cx - 40f, 5f, cx + 40f, 5f, cx + 95f, 35f)
                    }
                    drawPath(sleeveCapPath, Color(0xFF7B2869), style = Stroke(3f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 4f), 0f)))

                    drawCircle(Color(0xFFC0392B), 4f, Offset(cx, 15f))

                    drawContext.canvas.nativeCanvas.apply {
                        val paint = android.graphics.Paint().apply {
                            color = android.graphics.Color.parseColor("#7B2869")
                            textSize = 22f
                            isFakeBoldText = true
                            textAlign = android.graphics.Paint.Align.CENTER
                        }
                        drawText("▲ Shoulder Match Notch", cx, 32f, paint)
                        val sub = android.graphics.Paint().apply {
                            color = android.graphics.Color.parseColor("#444444")
                            textSize = 20f
                            textAlign = android.graphics.Paint.Align.CENTER
                        }
                        drawText("Ease excess fullness into shoulder crown", cx, 115f, sub)
                    }
                }

                VisualDiagramType.INVISIBLE_ZIPPER -> {
                    // Fabric edges and zipper coil
                    val midX = w * 0.5f
                    // Left and right fabric
                    drawRect(Color(0xFFE8DCC4), Offset(20f, 10f), Size(midX - 22f, h - 20f))
                    drawRect(Color(0xFFE8DCC4), Offset(midX + 2f, 10f), Size(midX - 22f, h - 20f))

                    // Zipper coils
                    drawLine(Color(0xFF4A6572), Offset(midX - 6f, 10f), Offset(midX - 6f, h - 10f), 3f)
                    drawLine(Color(0xFF4A6572), Offset(midX + 6f, 10f), Offset(midX + 6f, h - 10f), 3f)

                    // Stitches right next to coil
                    drawLine(Color(0xFFB33939), Offset(midX - 9f, 10f), Offset(midX - 9f, h - 10f), 2f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 4f), 0f))
                    drawLine(Color(0xFFB33939), Offset(midX + 9f, 10f), Offset(midX + 9f, h - 10f), 2f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 4f), 0f))

                    drawContext.canvas.nativeCanvas.apply {
                        val paint = android.graphics.Paint().apply {
                            color = android.graphics.Color.parseColor("#B33939")
                            textSize = 20f
                            isFakeBoldText = true
                        }
                        drawText("Stitch line directly beside coil", 30f, 45f, paint)
                        val sub = android.graphics.Paint().apply {
                            color = android.graphics.Color.parseColor("#333333")
                            textSize = 19f
                        }
                        drawText("Press zipper teeth open flat first", 30f, 75f, sub)
                        drawText("Use invisible zipper foot", 30f, 105f, sub)
                    }
                }

                VisualDiagramType.NECKLINE_FACING -> {
                    // Understitching diagram
                    val midY = h * 0.5f
                    drawLine(Color(0xFF7B2869), Offset(30f, midY), Offset(w - 30f, midY), 3.5f)
                    // Facing seam allowance pushed to facing side
                    drawLine(Color(0xFF4A6572), Offset(30f, midY + 14f), Offset(w - 30f, midY + 14f), 2f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(5f, 4f), 0f))

                    drawContext.canvas.nativeCanvas.apply {
                        val paint = android.graphics.Paint().apply {
                            color = android.graphics.Color.parseColor("#7B2869")
                            textSize = 22f
                            isFakeBoldText = true
                        }
                        drawText("Neckline Seam", 35f, midY - 12f, paint)
                        val sub = android.graphics.Paint().apply {
                            color = android.graphics.Color.parseColor("#4A6572")
                            textSize = 20f
                        }
                        drawText("Understitch: 1/16\" away, catching seam allowance to facing only", 35f, midY + 40f, sub)
                    }
                }

                VisualDiagramType.HEMMING_STITCH -> {
                    // Folded hem
                    val baseY = h * 0.4f
                    drawLine(Color(0xFF333333), Offset(30f, baseY), Offset(w - 30f, baseY), 2.5f)
                    drawLine(Color(0xFF333333), Offset(30f, baseY + 30f), Offset(w - 30f, baseY + 30f), 2.5f)

                    // Blind hem zig-zag bite
                    val blindPath = Path().apply {
                        moveTo(30f, baseY + 30f)
                        var bx = 30f
                        while (bx < w - 30f) {
                            lineTo(bx + 40f, baseY + 30f)
                            lineTo(bx + 44f, baseY + 2f) // tiny bite
                            lineTo(bx + 48f, baseY + 30f)
                            bx += 50f
                        }
                    }
                    drawPath(blindPath, Color(0xFFB33939), style = Stroke(2f))

                    drawContext.canvas.nativeCanvas.apply {
                        val sub = android.graphics.Paint().apply {
                            color = android.graphics.Color.parseColor("#333333")
                            textSize = 20f
                        }
                        drawText("Blind stitch: takes a tiny 1-thread bite into the main garment", 35f, baseY + 60f, sub)
                    }
                }

                else -> {}
            }
        }
    }
}
