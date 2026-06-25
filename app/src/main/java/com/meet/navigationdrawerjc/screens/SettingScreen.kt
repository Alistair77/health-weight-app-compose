package com.meet.navigationdrawerjc.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.meet.navigationdrawerjc.viewmodel.SettingsViewModel

@Composable
fun SettingScreen(viewModel: SettingsViewModel, innerPadding: PaddingValues) {
    // ViewModel state collection
    val motivation by viewModel.motivation.collectAsState()
    val ability by viewModel.ability.collectAsState()
    val triggersEnabled by viewModel.triggers.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(16.dp)
            .background(Color.White),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Settings",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Motivation Selection
        SettingCard(title = "Motivation") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Motivation: ${if (motivation == 0) "Low" else "High"}",
                    color = if (motivation == 0) Color.Red else Color.Blue)
                Switch(
                    checked = motivation == 1,
                    onCheckedChange = {
                        viewModel.updateMotivation(if (it) 1 else 0)
                    }
                )
            }
        }

        // Ability Selection
        SettingCard(title = "Ability") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Ability: ${if (ability == 0) "Low" else "High"}",
                    color = if (ability == 0) Color.Red else Color.Blue)
                Switch(
                    checked = ability == 1,
                    onCheckedChange = {
                        viewModel.updateAbility(if (it) 1 else 0)
                    }
                )
            }
        }

        // Context-aware Triggers
        SettingCard(title = "Context-aware Triggers") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = if (triggersEnabled) "Enabled Triggers" else "Disabled Triggers",
                    color = if (triggersEnabled) Color.Blue else Color.Red)
                Switch(
                    checked = triggersEnabled,
                    onCheckedChange = { viewModel.updateTriggers(it) }
                )
            }
        }
    }
}

// Reusable Setting Card Component
@Composable
fun SettingCard(title: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}
