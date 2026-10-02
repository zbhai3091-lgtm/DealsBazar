package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DealPlatform
import com.example.ui.theme.FirebaseOrange

@Composable
fun EarningGuideScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "🌐 Multi-Platform Affiliate Guide",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = FirebaseOrange
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Har platform ka affiliate link kaise hasil karein aur mahana commission kaise kamayein:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        item {
            Text(
                text = "Sabse Ziyada Munafa Dene Wale Platforms:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        // Platform 1: Daraz
        item {
            PlatformGuideCard(
                name = "1. Daraz Affiliate Program (Pakistan)",
                tag = "🛒 10% - 15% Commission",
                badgeColor = Color(0xFFEA580C),
                description = "Pakistan ka #1 shopping platform. Har order par commission milta hai. Free signup hota hai aur payout bank / JazzCash mein aata hai.",
                portalUrl = "https://www.daraz.pk/affiliate-program/",
                context = context
            )
        }

        // Platform 2: AliExpress
        item {
            PlatformGuideCard(
                name = "2. AliExpress Portals (Global Gadgets)",
                tag = "🚀 Up to 20% Commission",
                badgeColor = Color(0xFFE11D48),
                description = "Chinese gadgets, smartwatches, electronics ka sabse bara platform. International shipping Pakistan mein direct hoti hai. Dollars ($) mein commission banta hai.",
                portalUrl = "https://portals.aliexpress.com/",
                context = context
            )
        }

        // Platform 3: Amazon Associates
        item {
            PlatformGuideCard(
                name = "3. Amazon Associates (Global Brand)",
                tag = "📦 Up to 10% Commission",
                badgeColor = Color(0xFFD97706),
                description = "Dunya ka sabse motabar store. Jab aap Amazon ka link lagate hain to log kisi bhi mulk se khareedein, commission aapke Payoneer / Bank mein transfer hota hai.",
                portalUrl = "https://affiliate-program.amazon.com/",
                context = context
            )
        }

        // Platform 4: Markaz App
        item {
            PlatformGuideCard(
                name = "4. Markaz Dropship / Resell (0 Investment)",
                tag = "🛍️ Rs. 500 - 1500 Munafa / Product",
                badgeColor = Color(0xFF7C3AED),
                description = "Pakistan ki zero investment reselling app. Suit, chadar, bedsheet ya watches ke orders lein, Markaz wale customer ke ghar bhejte hain aur aapka profit JazzCash mein dete hain.",
                portalUrl = "https://www.markaz.app/",
                context = context
            )
        }

        // Platform 5: Digital / Hostinger
        item {
            PlatformGuideCard(
                name = "5. Digital Tools (Hostinger / Canva)",
                tag = "🌐 $20 - $50 (Rs. 10,000+) Per Sale",
                badgeColor = Color(0xFF0284C7),
                description = "Web hosting aur digital tools sabse mota commission dete hain. Sirf ek customer agar Hostinger leta hai to aapko Rs. 10,000+ commission milta hai!",
                portalUrl = "https://www.hostinger.com/affiliates",
                context = context
            )
        }

        // Formula for Daily Profit
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "💡 Earning Formula (Kamyabi Ka Nuskha):",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "1. Rozana 2 se 3 trending aur sasti deals is app mein post karein.\n" +
                               "2. 'Share on WhatsApp' button se Deals ko WhatsApp status aur shopping groups mein bhejain.\n" +
                               "3. Agar rozana sirf 5 log bhi khareedein, to aap Rs. 2,000 se Rs. 4,000 rozana ghar bethe kama sakte hain!",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }
}

@Composable
fun PlatformGuideCard(
    name: String,
    tag: String,
    badgeColor: Color,
    description: String,
    portalUrl: String,
    context: Context
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Surface(
                color = badgeColor.copy(alpha = 0.15f),
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = tag,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = badgeColor,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(portalUrl))
                    context.startActivity(intent)
                },
                colors = ButtonDefaults.buttonColors(containerColor = badgeColor),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.height(38.dp)
            ) {
                Icon(Icons.Default.OpenInBrowser, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Open Affiliate Portal", style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}
