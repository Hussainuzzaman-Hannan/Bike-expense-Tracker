package com.example.bikeexpensetracker.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ModernCard(
    title: String,
    value: String,
    icon: ImageVector,
    iconColor: Color,
    gradientColors: List<Color>,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier
            .then(
                if (onClick != null) {
                    Modifier.clickable { onClick.invoke() }
                } else Modifier
            )
            .shadow(8.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        colors = gradientColors,
                        startX = 0.0f,
                        endX = 1.0f
                    ),
                    alpha = 0.08f
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Icon(
                        icon,
                        contentDescription = title,
                        tint = iconColor,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Column {
                    AnimatedNumber(
                        value = value,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (title != "Avg. Mileage") {
                        Text(
                            text = "Total spent",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AnimatedNumber(
    value: String,
    modifier: Modifier = Modifier
) {
    var animatedValue by remember { mutableStateOf(0f) }

    LaunchedEffect(value) {
        val targetNumber = value.filter { it.isDigit() || it == '.' }.toDoubleOrNull() ?: 0.0
        animate(
            initialValue = animatedValue,
            targetValue = targetNumber.toFloat(),
            animationSpec = tween(
                durationMillis = 500,
                easing = FastOutSlowInEasing
            )
        ) { current, _ ->
            animatedValue = current
        }
    }

    Text(
        text = when {
            value.contains("km/l") -> String.format("%.1f", animatedValue) + " km/l"
            value.contains("৳") -> "৳" + String.format("%.2f", animatedValue)
            else -> String.format("%.0f", animatedValue)
        },
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = modifier
    )
}