package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.User
import com.example.ui.PortalViewModel
import com.example.ui.Screen
import com.example.ui.components.FloralBackground
import com.example.ui.components.FloraliaBrandingHeader
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    viewModel: PortalViewModel,
    modifier: Modifier = Modifier
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var displayName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var selectedFlower by remember { mutableStateOf("Red Rose") }
    var dropdownExpanded by remember { mutableStateOf(false) }

    val registerError by viewModel.registerError.collectAsState()
    val scrollState = rememberScrollState()

    val flowerSpeciesList = listOf("Red Rose", "Sakura Blossom", "Hillside Lavender", "Golden Sunflower", "Amazon Orchid")

    FloralBackground {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(scrollState)
                .systemBarsPadding()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            
            FloraliaBrandingHeader(
                modifier = Modifier
                    .padding(bottom = 24.dp)
                    .testTag("register_brand_header")
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .testTag("register_card"),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(
                    containerColor = GlassBgColor
                ),
                border = BorderStroke(1.5.dp, GlassBorderColor),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 2.dp
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                            .clip(RoundedCornerShape(16.dp))
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_flower_login),
                            contentDescription = "Flower petals decoration",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(Color.Transparent, CharcoalBark.copy(alpha = 0.7f))
                                    )
                                )
                        )
                        Text(
                            text = "Register Horticultural Pass",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Username Input
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Username") },
                        placeholder = { Text("Username tag") },
                        leadingIcon = { Icon(Icons.Filled.Fingerprint, null, tint = RosewoodDeep) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = RosewoodDeep, focusedLabelColor = RosewoodDeep, unfocusedBorderColor = SageLight
                        ),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("register_username_input"),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Display Name Input
                    OutlinedTextField(
                        value = displayName,
                        onValueChange = { displayName = it },
                        label = { Text("Full Name") },
                        placeholder = { Text("e.g. Flora Peterson") },
                        leadingIcon = { Icon(Icons.Filled.Face, null, tint = RosewoodDeep) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = RosewoodDeep, focusedLabelColor = RosewoodDeep, unfocusedBorderColor = SageLight
                        ),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("register_display_input"),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Email Input
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email Address") },
                        placeholder = { Text("yourname@domain.com") },
                        leadingIcon = { Icon(Icons.Filled.Mail, null, tint = RosewoodDeep) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = RosewoodDeep, focusedLabelColor = RosewoodDeep, unfocusedBorderColor = SageLight
                        ),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("register_email_input"),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Password Input
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password Code") },
                        placeholder = { Text("Min 5 symbols") },
                        leadingIcon = { Icon(Icons.Filled.Lock, null, tint = RosewoodDeep) },
                        visualTransformation = PasswordVisualTransformation(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = RosewoodDeep, focusedLabelColor = RosewoodDeep, unfocusedBorderColor = SageLight
                        ),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("register_password_input"),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Favorite Flower Selector Dropdown (Enhancing page-to-page botanical branding)
                    ExposedDropdownMenuBox(
                        expanded = dropdownExpanded,
                        onExpandedChange = { dropdownExpanded = !dropdownExpanded }
                    ) {
                        OutlinedTextField(
                            value = selectedFlower,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Aesthetic Flower Style preference") },
                            leadingIcon = { Icon(Icons.Filled.LocalFlorist, null, tint = RosewoodDeep) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = RosewoodDeep, focusedLabelColor = RosewoodDeep, unfocusedBorderColor = SageLight
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                                .testTag("register_flower_pref_selection")
                        )
                        ExposedDropdownMenu(
                            expanded = dropdownExpanded,
                            onDismissRequest = { dropdownExpanded = false }
                        ) {
                            flowerSpeciesList.forEach { fl ->
                                DropdownMenuItem(
                                    text = { Text(fl) },
                                    leadingIcon = { Icon(Icons.Filled.Spa, null, tint = SoftSage) },
                                    onClick = {
                                        selectedFlower = fl
                                        dropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Register Failure Warnings
                    AnimatedVisibility(
                        visible = registerError != null,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        registerError?.let { err ->
                            Spacer(modifier = Modifier.height(14.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Filled.Error, null, tint = MaterialTheme.colorScheme.error)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = err,
                                    color = MaterialTheme.colorScheme.error,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Finalize Button
                    Button(
                        onClick = {
                            viewModel.register(username, password, displayName, email, selectedFlower)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = RosewoodDeep, contentColor = WarmLinen),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("register_button")
                    ) {
                        Icon(Icons.Filled.AppRegistration, null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Register Free Pass",
                            fontSize = 16.sp,
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Text Link to Login
            TextButton(
                onClick = { viewModel.navigateTo(Screen.Login) },
                modifier = Modifier
                    .testTag("go_to_login_button")
                    .minimumInteractiveComponentSize()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.ArrowBack, null, tint = SoftSage, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Already have a pass? Authorize here",
                        color = SoftSage,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
