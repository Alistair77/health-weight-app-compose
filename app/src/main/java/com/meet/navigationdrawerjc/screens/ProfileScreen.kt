package com.meet.navigationdrawerjc.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ProfileScreen(innerPadding: PaddingValues) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color.LightGray),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Profile Settings",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(20.dp))

        // How Mindful Moments Works
        SettingCard(title = "How Mindful Moments Works", value = "Mindful Moments gently encourages mental well-being by drawing on Fogg’s Behavior Model and NHS guidance. We want to reach you at moments when you can comfortably take a quick break." )

        // When does it send notifications?
        SettingCard(title = "When does it send notifications?", value = "- Between 8:00 and 22:00 so we don’t disturb your sleep.\n" +
                "- When connected to your Wi-Fi and plugged in, ensuring your phone is stable and charging." )

        // Why these conditions?
        SettingCard(title = "Why these conditions?", value = "These triggers ensure that you’re in a relaxed environment where a short mindfulness reminder is both possible and beneficial. Plus, by checking Wi-Fi and power status, we avoid draining your battery.")

        // Wi-Fi SSID
        SettingCard(title = "*", value = "Your well-being is our priority.")

    }
}

// Reusable Setting Card Component
@Composable
fun SettingCard(title: String, value: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)), // Light Gray Background
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, fontSize = 16.sp, color = Color.DarkGray)
        }
    }
}
