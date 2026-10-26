package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PreparedByFooter(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val phoneNumber = "03149768207"

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalDivider(
            color = DividerDefaults.color.copy(alpha = 0.5f),
            thickness = 1.dp,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Text(
            text = "Prepared by Muhammad Ishfaq",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Color(0xFF6D3BD2),
                fontWeight = FontWeight.Bold,
                fontSize = 13.5.sp
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(top = 2.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber"))
                        context.startActivity(intent)
                    }
                    .padding(horizontal = 8.dp, vertical = 6.dp)
                    .testTag("call_prepared_by_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Call,
                    contentDescription = "Call",
                    tint = Color(0xFF0F6B57),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Phone # $phoneNumber",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = Color(0xFF0F6B57),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.5.sp
                    )
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/923149768207"))
                        context.startActivity(intent)
                    }
                    .padding(horizontal = 8.dp, vertical = 6.dp)
                    .testTag("whatsapp_prepared_by_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Chat,
                    contentDescription = "WhatsApp",
                    tint = Color(0xFF1D5FD1),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "WhatsApp",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = Color(0xFF1D5FD1),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.5.sp
                    )
                )
            }
        }
    }
}
