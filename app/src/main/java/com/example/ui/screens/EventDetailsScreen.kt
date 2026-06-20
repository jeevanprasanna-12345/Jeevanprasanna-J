package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.PortalViewModel
import com.example.ui.Screen
import com.example.ui.components.FloralBackground
import com.example.ui.theme.*
import com.example.data.Event
import com.example.data.User

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun EventDetailsScreen(
    eventId: Long,
    viewModel: PortalViewModel,
    modifier: Modifier = Modifier
) {
    val events by viewModel.events.collectAsState()
    val event = remember(events, eventId) { events.find { it.id == eventId } }

    val bookingError by viewModel.bookingError.collectAsState()

    var ticketsCount by remember { mutableIntStateOf(1) }
    var selectedBouquet by remember { mutableStateOf("Classic Rose Wreath") }
    var dropdownExpanded by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    val bouquetOptions = listOf(
        "Classic Rose Wreath",
        "Meadow Lavender Pot",
        "Royal Blooming Peony",
        "Golden Pollen Basket",
        "Orchid Canopy Vase"
    )

    // Calculate total price reactively using derivedStateOf to prevent excessive recomposition
    val totalPrice by remember(event, ticketsCount) {
        derivedStateOf {
            val price = event?.ticketPrice ?: 0.0
            price * ticketsCount
        }
    }

    FloralBackground {
        if (event == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Horticultural event not found.", color = RosewoodDeep, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { viewModel.navigateTo(Screen.Dashboard) },
                    colors = ButtonDefaults.buttonColors(containerColor = RosewoodDeep)
                ) {
                    Text("Return to Dashboard")
                }
            }
        } else {
            val themeColor = when {
                event.flowerTheme.contains("Rose", true) -> RosewoodDeep
                event.flowerTheme.contains("Sakura", true) -> BlushPink
                event.flowerTheme.contains("Lavender", true) -> SageLight
                event.flowerTheme.contains("Sunflower", true) -> AccentHoney
                else -> SoftSage
            }

            Column(
                modifier = modifier
                    .fillMaxSize()
                    .systemBarsPadding()
                    .navigationBarsPadding()
            ) {
                // Top Custom Header Actions
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { viewModel.navigateTo(Screen.Dashboard) },
                        modifier = Modifier
                            .background(Color.White, CircleShape)
                            .testTag("details_back_btn")
                    ) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Return", tint = RosewoodDeep)
                    }

                    Text(
                        text = "Pass configuration",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif,
                        color = CharcoalBark
                    )

                    // Dummy spacer for design alignment balance
                    Spacer(modifier = Modifier.size(48.dp))
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(scrollState)
                        .padding(horizontal = 16.dp)
                ) {
                    // Category and Header Detail Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = GlassBgColor),
                        border = BorderStroke(1.5.dp, GlassBorderColor),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(24.dp)) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = themeColor.copy(alpha = 0.15f)),
                                shape = CircleShape
                            ) {
                                Text(
                                    text = event.category.uppercase(),
                                    color = if (themeColor == BlushPink) RosewoodDeep else themeColor,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = event.title,
                                fontSize = 24.sp,
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                color = CharcoalBark
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.LocalFlorist, null, tint = themeColor, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Flower Theme: ${event.flowerTheme}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (themeColor == BlushPink) RosewoodDeep else themeColor
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            Divider(color = WarmLinen)
                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = event.description,
                                fontSize = 14.sp,
                                color = Color.DarkGray,
                                lineHeight = 20.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Date, location, and metadata details
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = GlassBgColor),
                        border = BorderStroke(1.5.dp, GlassBorderColor),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(SageLight.copy(alpha = 0.3f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Filled.CalendarToday, null, tint = SoftSage, modifier = Modifier.size(16.dp))
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text("Date & Time", fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.SemiBold)
                                    Text(event.date, fontSize = 14.sp, color = CharcoalBark, fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(SageLight.copy(alpha = 0.3f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Filled.Map, null, tint = SoftSage, modifier = Modifier.size(16.dp))
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text("Botanical Venue", fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.SemiBold)
                                    Text(event.location, fontSize = 14.sp, color = CharcoalBark, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Booking Options Card (Counter, Bouquet Preference Dropdown!)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("booking_config_card"),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = GlassBgColor),
                        border = BorderStroke(1.5.dp, GlassBorderColor),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(22.dp)) {
                            Text(
                                text = "Personalize your seat",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Serif,
                                color = CharcoalBark
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Counter Selector
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("tickets_counter"),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Select Quantity (Max 5)",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.DarkGray
                                )

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    IconButton(
                                        onClick = { if (ticketsCount > 1) ticketsCount-- },
                                        modifier = Modifier
                                            .size(34.dp)
                                            .background(WarmLinen, CircleShape)
                                            .testTag("decrement_tickets_btn")
                                    ) {
                                        Icon(Icons.Filled.Remove, null, tint = RosewoodDeep, modifier = Modifier.size(16.dp))
                                    }

                                    Text(
                                        text = ticketsCount.toString(),
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CharcoalBark
                                    )

                                    IconButton(
                                        onClick = { if (ticketsCount < 5) ticketsCount++ },
                                        modifier = Modifier
                                            .size(34.dp)
                                            .background(WarmLinen, CircleShape)
                                            .testTag("increment_tickets_btn")
                                    ) {
                                        Icon(Icons.Filled.Add, null, tint = RosewoodDeep, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            // Custom Table Bouquet Dropdown Selection (Page-to-page themed accessory)
                            ExposedDropdownMenuBox(
                                expanded = dropdownExpanded,
                                onExpandedChange = { dropdownExpanded = !dropdownExpanded }
                            ) {
                                OutlinedTextField(
                                    value = selectedBouquet,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Ornament Arrangement Style") },
                                    leadingIcon = { Icon(Icons.Filled.LocalFlorist, null, tint = RosewoodDeep) },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = RosewoodDeep, focusedLabelColor = RosewoodDeep, unfocusedBorderColor = SageLight
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor()
                                        .testTag("booking_bouquet_selection")
                                )
                                ExposedDropdownMenu(
                                    expanded = dropdownExpanded,
                                    onDismissRequest = { dropdownExpanded = false }
                                ) {
                                    bouquetOptions.forEach { bq ->
                                        DropdownMenuItem(
                                            text = { Text(bq) },
                                            leadingIcon = { Icon(Icons.Filled.Yard, null, tint = SoftSage) },
                                            onClick = {
                                                selectedBouquet = bq
                                                dropdownExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }

                // Bottom Action Bar with pricing and confirm trigger button
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shadowElevation = 4.dp,
                    color = GlassNavigationBarBg
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 16.dp)
                    ) {
                        // Display booking error warning if seats exceeds
                        AnimatedVisibility(
                            visible = bookingError != null,
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            bookingError?.let { err ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Filled.Error, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(err, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Total Price", fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.SemiBold)
                                Text(
                                    text = "$${String.format("%.2f", totalPrice)}",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Serif,
                                    color = RosewoodDeep
                                )
                            }

                            val isSoldOut = (event.totalCapacity - event.bookedSeats) <= 0

                            Button(
                                onClick = {
                                    viewModel.bookTickets(event.id, ticketsCount, selectedBouquet)
                                },
                                enabled = !isSoldOut,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = RosewoodDeep,
                                    disabledContainerColor = Color.LightGray
                                ),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .height(50.dp)
                                    .widthIn(min = 180.dp)
                                    .testTag("book_event_btn")
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.LocalActivity, null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (isSoldOut) "Sold Out" else "Book Bouquet Pass",
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Serif
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
