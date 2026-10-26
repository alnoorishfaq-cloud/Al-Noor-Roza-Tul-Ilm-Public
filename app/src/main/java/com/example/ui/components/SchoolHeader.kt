package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R

@Composable
fun SchoolHeader(
    modifier: Modifier = Modifier
) {
    val gradientBrush = Brush.linearGradient(
        colors = listOf(
            Color(0xFF0F6B57),
            Color(0xFF12839A),
            Color(0xFF1D5FD1)
        )
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(gradientBrush)
            .padding(14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // School Emblem / Logo
            Box(
                modifier = Modifier
                    .size(58.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color.White.copy(alpha = 0.85f), CircleShape)
                    .background(Color.White)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.school_logo),
                    contentDescription = "Al Noor School Emblem",
                    modifier = Modifier
                        .size(58.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Al Noor Roza Tul Ilm",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        letterSpacing = 0.2.sp
                    )
                )
                Text(
                    text = "Public High School",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color(0xFFD6EFE6),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "📍 Manki, Swabi",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color(0xFFBCEBF5),
                        fontWeight = FontWeight.Medium,
                        fontSize = 11.5.sp
                    )
                )
            }
        }
    }
}
