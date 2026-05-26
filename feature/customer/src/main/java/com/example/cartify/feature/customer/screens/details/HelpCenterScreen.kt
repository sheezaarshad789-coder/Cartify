package com.example.cartify.feature.customer.screens.details

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cartify.core.common.model.Faq
import com.example.cartify.core.common.theme.*

/**
 * Help Center Content - Decoupled UI layer for FAQ and Support.
 * Focuses on a minimalist layout and clear communication.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpCenterContent(
    faqs: List<Faq>,
    isLoading: Boolean = false,
    onBackClick: () -> Unit,
    onWhatsAppClick: () -> Unit,
    onEmailSupportClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Help Center",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = (-0.5).sp
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = JapandiCharcoal)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = JapandiCanvas)
            )
        },
        containerColor = JapandiCanvas
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Frequently Asked Questions",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = JapandiCharcoal
                ),
                modifier = Modifier.padding(bottom = 20.dp)
            )

            Box(modifier = Modifier.weight(1f)) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = JapandiSage
                    )
                } else if (faqs.isEmpty()) {
                    Text(
                        text = "No FAQs available at the moment.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = JapandiEarthyGray,
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(faqs) { faq ->
                            FaqItemCard(question = faq.question, answer = faq.answer)
                        }
                        item { Spacer(modifier = Modifier.height(24.dp)) }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Contact Section
            Text(
                text = "Still need help?",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = JapandiCharcoal
                ),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ContactButton(
                    icon = Icons.Default.Call,
                    label = "WhatsApp",
                    containerColor = Color(0xFF25D366),
                    modifier = Modifier.weight(1f),
                    onClick = onWhatsAppClick
                )

                ContactButton(
                    icon = Icons.Default.Email,
                    label = "Email Us",
                    containerColor = JapandiSage,
                    modifier = Modifier.weight(1f),
                    onClick = onEmailSupportClick
                )
            }
        }
    }
}

@Composable
private fun FaqItemCard(question: String, answer: String) {
    var isExpanded by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(1.dp, RoundedCornerShape(16.dp))
            .clickable { isExpanded = !isExpanded },
        shape = RoundedCornerShape(16.dp),
        color = Color.White
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = question,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = if (isExpanded) FontWeight.Bold else FontWeight.Medium
                    ),
                    color = JapandiCharcoal,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = JapandiEarthyGray,
                    modifier = Modifier.size(24.dp)
                )
            }
            if (isExpanded) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = answer,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        lineHeight = 22.sp,
                        color = JapandiEarthyGray
                    )
                )
            }
        }
    }
}

@Composable
private fun ContactButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    containerColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = containerColor),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp), tint = Color.White)
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
            color = Color.White
        )
    }
}

@Preview(showBackground = true, name = "Help Center - Content")
@Composable
fun PreviewHelpCenter() {
    val mockFaqs = listOf(
        Faq("1", "How do I track my order?", "You can track your order in real-time from the 'Orders' section in the app."),
        Faq("2", "What is the delivery time?", "Most orders are delivered within 20-40 minutes depending on your location."),
        Faq("3", "Can I return fresh items?", "Fresh produce can only be returned at the time of delivery if quality is not satisfactory.")
    )
    HelpCenterContent(
        faqs = mockFaqs,
        onBackClick = {},
        onWhatsAppClick = {},
        onEmailSupportClick = {}
    )
}
