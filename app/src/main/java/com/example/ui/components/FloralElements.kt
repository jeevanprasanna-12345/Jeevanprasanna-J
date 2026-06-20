package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun FloralBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    // Infinite transition for gently drifting rose petals
    val infiniteTransition = rememberInfiniteTransition(label = "petal_drift")
    
    val driftY1 by infiniteTransition.animateFloat(
        initialValue = -50f,
        targetValue = 1200f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "drift_y1"
    )
    val driftX1 by infiniteTransition.animateFloat(
        initialValue = 50f,
        targetValue = 400f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "drift_x1"
    )
    val rotate1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(14000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotate_1"
    )

    val driftY2 by infiniteTransition.animateFloat(
        initialValue = -100f,
        targetValue = 1100f,
        animationSpec = infiniteRepeatable(
            animation = tween(16000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "drift_y2"
    )
    val driftX2 by infiniteTransition.animateFloat(
        initialValue = 350f,
        targetValue = -50f,
        animationSpec = infiniteRepeatable(
            animation = tween(16000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "drift_x2"
    )
    val rotate2 by infiniteTransition.animateFloat(
        initialValue = 45f,
        targetValue = -315f,
        animationSpec = infiniteRepeatable(
            animation = tween(18000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotate_2"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(WarmLinen)
    ) {
        // Draw the static corner vines and drifting petals on canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // 0. Render the abstract blurred glass background blobs
            // top-right pink-300: bg-pink-300/30 (alpha = 0.3)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFF9A8D4).copy(alpha = 0.35f), Color.Transparent),
                    center = Offset(width * 1.05f, -height * 0.05f),
                    radius = 350.dp.toPx()
                ),
                center = Offset(width * 1.05f, -height * 0.05f),
                radius = 350.dp.toPx()
            )

            // bottom-left purple-300: bg-purple-300/20 (alpha = 0.2)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFD8B4FE).copy(alpha = 0.25f), Color.Transparent),
                    center = Offset(-width * 0.15f, height * 0.9f),
                    radius = 450.dp.toPx()
                ),
                center = Offset(-width * 0.15f, height * 0.9f),
                radius = 450.dp.toPx()
            )

            // mid-right rose-200: bg-rose-200/40 (alpha = 0.4)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFFECDD3).copy(alpha = 0.40f), Color.Transparent),
                    center = Offset(width * 0.95f, height * 0.4f),
                    radius = 250.dp.toPx()
                ),
                center = Offset(width * 0.95f, height * 0.4f),
                radius = 250.dp.toPx()
            )

            // 1. Top-Right Corner Leaf Vine
            drawCornerVine(
                centerX = width,
                centerY = 0f,
                startAngle = 135f,
                color = SageLight.copy(alpha = 0.3f)
            )

            // 2. Bottom-Left Corner Leaf Vine
            drawCornerVine(
                centerX = 0f,
                centerY = height,
                startAngle = -45f,
                color = SageLight.copy(alpha = 0.3f)
            )

            // 3. Render Drifting Rose Petals dynamically on Canvas!
            // First drifting rose petal
            rotate(rotate1, pivot = Offset(driftX1, driftY1)) {
                drawPetal(
                    offset = Offset(driftX1, driftY1),
                    color = BlushPink.copy(alpha = 0.5f)
                )
            }

            // Second drifting rose petal
            rotate(rotate2, pivot = Offset(driftX2, driftY2)) {
                drawPetal(
                    offset = Offset(driftX2, driftY2),
                    color = RosewoodDeep.copy(alpha = 0.2f)
                )
            }
        }

        // Main overlay content
        Box(modifier = Modifier.fillMaxSize()) {
            content()
        }
    }
}

// Extension to draw a beautiful leaf vine in corners
private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawCornerVine(
    centerX: Float,
    centerY: Float,
    startAngle: Float,
    color: Color
) {
    rotate(startAngle, pivot = Offset(centerX, centerY)) {
        val vinePath = Path().apply {
            moveTo(centerX, centerY)
            cubicTo(
                centerX - 100f, centerY + 150f,
                centerX - 50f, centerY + 300f,
                centerX - 250f, centerY + 450f
            )
        }
        drawPath(
            path = vinePath,
            color = color,
            style = Stroke(width = 3.dp.toPx())
        )
        // Draw little leaves along the vine
        drawLeafPair(centerX - 50f, centerY + 100f, color)
        drawLeafPair(centerX - 100f, centerY + 240f, color)
        drawLeafPair(centerX - 180f, centerY + 365f, color)
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawLeafPair(
    x: Float,
    y: Float,
    color: Color
) {
    // Leaf 1
    val leafPath1 = Path().apply {
        moveTo(x, y)
        quadraticTo(x + 25f, y - 15f, x + 40f, y - 5f)
        quadraticTo(x + 25f, y + 20f, x, y)
    }
    drawPath(path = leafPath1, color = color)

    // Leaf 2
    val leafPath2 = Path().apply {
        moveTo(x, y)
        quadraticTo(x - 25f, y + 15f, x - 40f, y + 5f)
        quadraticTo(x - 25f, y - 20f, x, y)
    }
    drawPath(path = leafPath2, color = color)
}

// Function to draw a single custom flower petal
private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawPetal(
    offset: Offset,
    color: Color
) {
    val size = 50f
    val petalPath = Path().apply {
        moveTo(offset.x, offset.y)
        cubicTo(
            offset.x - size, offset.y - size,
            offset.x - size * 1.5f, offset.y + size * 0.5f,
            offset.x, offset.y + size
        )
        cubicTo(
            offset.x + size * 1.5f, offset.y + size * 0.5f,
            offset.x + size, offset.y - size,
            offset.x, offset.y
        )
    }
    drawPath(path = petalPath, color = color)
}

@Composable
fun FloraliaBrandingHeader(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Filled.LocalFlorist,
            contentDescription = "Floralia Bloom Logo",
            tint = RosewoodDeep,
            modifier = Modifier.size(34.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = "Floralia",
            color = CharcoalBark,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Serif,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.width(6.dp))
        Icon(
            imageVector = Icons.Filled.Eco,
            contentDescription = null,
            tint = SoftSage,
            modifier = Modifier.size(18.dp)
        )
    }
}
