package com.anastube.app.ui.screens.library

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class LocalMedia(
    val name: String,
    val size: String,
    val isAudio: Boolean,
    val path: String,
    val isVault: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    onPlayMedia: (String, String) -> Unit
) {
    var selectedFilter by remember { mutableStateOf("All") }
    var showVaultAuthDialog by remember { mutableStateOf(false) }
    var isVaultUnlocked by remember { mutableStateOf(false) }
    var vaultPinInput by remember { mutableStateOf("") }
    var vaultPinError by remember { mutableStateOf(false) }

    val sampleFiles = remember {
        listOf(
            LocalMedia("Ambient Relaxation Vibes.mp3", "8.4 MB", true, "/sdcard/Download/ambient.mp3"),
            LocalMedia("Nature 4K Ultra Cinematic.mp4", "45.2 MB", false, "/sdcard/Download/nature.mp4"),
            LocalMedia("Coding Lo-Fi Session Track 1.m4a", "6.1 MB", true, "/sdcard/Download/lofi.m4a"),
            LocalMedia("WhatsApp Video Status Story.mp4", "12.3 MB", false, "/sdcard/WhatsApp/status.mp4")
        )
    }

    val vaultFiles = remember {
        listOf(
            LocalMedia("Secret Family Vacation Video.mp4", "68.2 MB", false, "/vault/vacation.mp4", true),
            LocalMedia("Private Voice Recording Memo.m4a", "4.8 MB", true, "/vault/memo.m4a", true)
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "My Files & Library",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                actions = {
                    IconButton(onClick = { showVaultAuthDialog = true }) {
                        Icon(
                            imageVector = if (isVaultUnlocked) Icons.Default.LockOpen else Icons.Default.Lock,
                            contentDescription = "Vault",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Private Vault Banner (SnapTube Vault feature)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clickable { showVaultAuthDialog = true },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(42.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (isVaultUnlocked) Icons.Default.LockOpen else Icons.Default.Lock,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isVaultUnlocked) "Private Media Vault (Unlocked)" else "Private Media Vault",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = if (isVaultUnlocked) "${vaultFiles.size} hidden private files" else "Password protected - tap to unlock",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            }

            // Media Categories
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val filters = if (isVaultUnlocked) listOf("All", "Videos", "Music", "WhatsApp", "Vault") else listOf("All", "Videos", "Music", "WhatsApp")
                items(filters) { filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { selectedFilter = filter },
                        label = { Text(filter) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            val currentList = when (selectedFilter) {
                "Videos" -> sampleFiles.filter { !it.isAudio && !it.name.contains("WhatsApp") }
                "Music" -> sampleFiles.filter { it.isAudio }
                "WhatsApp" -> sampleFiles.filter { it.name.contains("WhatsApp") }
                "Vault" -> vaultFiles
                else -> sampleFiles
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(currentList) { file ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                            .clickable { onPlayMedia(file.path, file.name) }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (file.isAudio) Icons.Default.Audiotrack else Icons.Default.Videocam,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = file.name,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = file.size,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        }
                        IconButton(onClick = { onPlayMedia(file.path, file.name) }) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Play",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }

    // Vault PIN Dialog (SnapTube's SingleVaultActivity)
    if (showVaultAuthDialog) {
        AlertDialog(
            onDismissRequest = { showVaultAuthDialog = false },
            title = {
                Text(
                    text = if (isVaultUnlocked) "Lock Private Vault" else "Enter Vault 4-Digit PIN",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                if (isVaultUnlocked) {
                    Text("Your private vault is currently unlocked. Do you want to lock it now?")
                } else {
                    Column {
                        Text(
                            text = "Protect your private downloads from appearing in the gallery.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = vaultPinInput,
                            onValueChange = {
                                if (it.length <= 4) {
                                    vaultPinInput = it
                                    vaultPinError = false
                                }
                            },
                            label = { Text("4-digit PIN") },
                            visualTransformation = PasswordVisualTransformation(),
                            singleLine = true,
                            isError = vaultPinError
                        )
                        if (vaultPinError) {
                            Text(
                                text = "Please enter any 4 digits (e.g. 1234)",
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (isVaultUnlocked) {
                            isVaultUnlocked = false
                            if (selectedFilter == "Vault") selectedFilter = "All"
                            showVaultAuthDialog = false
                        } else {
                            if (vaultPinInput.length == 4) {
                                isVaultUnlocked = true
                                selectedFilter = "Vault"
                                showVaultAuthDialog = false
                                vaultPinInput = ""
                            } else {
                                vaultPinError = true
                            }
                        }
                    }
                ) {
                    Text(if (isVaultUnlocked) "Lock Now" else "Unlock")
                }
            },
            dismissButton = {
                TextButton(onClick = { showVaultAuthDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
