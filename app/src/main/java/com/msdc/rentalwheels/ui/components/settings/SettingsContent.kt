package com.msdc.rentalwheels.ui.components.settings

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.msdc.rentalwheels.auth.LoginActivity
import com.msdc.rentalwheels.models.UserData
import com.msdc.rentalwheels.ui.theme.Typography

@Composable
fun SettingsContent(
    userData: UserData,
    onEditProfileClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()

    var notificationsEnabled by remember { mutableStateOf(true) }
    var biometricsEnabled by remember { mutableStateOf(false) }
    var selectedLanguage by remember { mutableStateOf("English") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Profile Section
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Profile Picture",
                modifier = Modifier.size(60.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Text(
            text = "${userData.firstName} ${userData.lastName}",
            style = Typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 16.dp)
        )

        Text(
            text = userData.email,
            style = Typography.bodyLarge,
            modifier = Modifier.padding(top = 4.dp)
        )

        Text(
            text = userData.phoneNumber,
            style = Typography.bodyLarge,
            modifier = Modifier.padding(top = 4.dp)
        )

        // Preferences Section
        SettingsSection(title = "Preferences") {
            LanguageSelector(
                selectedLanguage = selectedLanguage,
                onLanguageSelected = { selectedLanguage = it }
            )
        }

        // Notifications Section
        SettingsSection(title = "Notifications") {
            SettingsSwitch("Push Noti", notificationsEnabled) { notificationsEnabled = it }
            SettingsSwitch("Email Noti", notificationsEnabled) { notificationsEnabled = it }
            SettingsSwitch("Promo Updates", notificationsEnabled) { notificationsEnabled = it }
        }

        // Privacy & Security
        SettingsSection(title = "Privacy & Security") {
            SettingsSwitch("Bio Auth", biometricsEnabled) { biometricsEnabled = it }
            SettingsItem("Change Password") { /* Handle password change */ }
            SettingsItem("Privacy Policy") { /* Open privacy policy */ }
        }

        // App Information
        SettingsSection(title = "App Information") {
            SettingsItem("Version 1.0.0", subtitle = "Check for updates") { /* Check updates */ }
            SettingsItem("Terms of Service") { /* Open terms */ }
            SettingsItem("Licenses") { /* Open licenses */ }
        }

        // Account Management
        SettingsSection(title = "Account") {
            SettingsItem(
                text = "Delete Account",
                textColor = MaterialTheme.colorScheme.error,
                onClick = {
                    AlertDialog.Builder(context).apply {
                        setTitle("Confirm Deletion")
                        setMessage("Bạn có muốn delete account này? Một đi không trở lại.")
                        setPositiveButton("Delete") { _, _ ->
                            val user = auth.currentUser
                            val uid = user?.uid
                            if (user != null && uid != null) {
                                user.delete()
                                    .addOnSuccessListener {
                                        firestore.collection("users").document(uid).delete()
                                        context.getSharedPreferences("shared_prefs", Context.MODE_PRIVATE)
                                            .edit().clear().apply()
                                        val intent = Intent(context, LoginActivity::class.java).apply {
                                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        }
                                        context.startActivity(intent)
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(
                                            context,
                                            "Failed to delete account: ${e.message}",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                            }
                        }
                        setNegativeButton("Cancel", null)
                    }.show()
                }
            )

            Button(
                onClick = {
                    auth.signOut()
                    context.getSharedPreferences("shared_prefs", Context.MODE_PRIVATE)
                        .edit().clear().apply()
                    val intent = Intent(context, LoginActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    context.startActivity(intent)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text("Logout")
            }
        }
    }
}
