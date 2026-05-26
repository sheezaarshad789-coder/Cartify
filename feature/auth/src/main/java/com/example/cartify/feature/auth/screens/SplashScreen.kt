package com.example.cartify.feature.auth.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cartify.core.common.R
import com.example.cartify.core.common.theme.*
import kotlinx.coroutines.delay

/**
 * Highly Polished Splash Screen.
 * Implements fluid entry animations and Japandi aesthetic.
 */
@Composable
fun SplashScreen(
    onAnimationFinished: () -> Unit
) {
    var startAnimation by remember { mutableStateOf(false) }
    
    val alphaAnim = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 1500, easing = FastOutSlowInEasing),
        label = "alpha"
    )

    val scaleAnim = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.8f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessVeryLow
        ),
        label = "scale"
    )

    LaunchedEffect(Unit) {
        startAnimation = true
        delay(2500)
        onAnimationFinished()
    }

    CartifyTheme {
        SplashContent(
            alpha = alphaAnim.value,
            scale = scaleAnim.value
        )
    }
}

@Composable
fun SplashContent(
    alpha: Float,
    scale: Float
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(JapandiCanvas),
        contentAlignment = Alignment.Center
    ) {
        // Subtle background texture or element
        Box(
            modifier = Modifier
                .size(400.dp)
                .alpha(0.03f)
                .background(JapandiCharcoal, CircleShape)
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .alpha(alpha)
                .scale(scale)
        ) {
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.online_groceries_cuate),
                    contentDescription = "Cartify Logo",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Cartify",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.Black,
                    fontStyle = FontStyle.Italic,
                    letterSpacing = (-2).sp
                ),
                color = JapandiCharcoal
            )

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(2.dp)
                    .background(JapandiSage)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "ELEVATING YOUR DAILY ESSENTIALS",
                style = MaterialTheme.typography.labelSmall.copy(
                    letterSpacing = 3.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = JapandiSage,
                textAlign = TextAlign.Center
            )
        }

        // Bottom tagline
        Text(
            text = "DESIGNED FOR SERENITY",
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp)
                .alpha(alpha * 0.5f),
            style = MaterialTheme.typography.labelSmall.copy(
                letterSpacing = 1.sp
            ),
            color = JapandiEarthyGray
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSplashContent() {
    CartifyTheme {
        SplashContent(alpha = 1f, scale = 1f)
    }
}
