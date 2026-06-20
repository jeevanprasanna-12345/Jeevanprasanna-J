package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.Event
import com.example.data.UserRegistrationWithEvent
import com.example.data.User
import com.example.ui.PortalViewModel
import com.example.ui.Screen
import com.example.ui.components.FloralBackground
import com.example.ui.theme.*

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: PortalViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val events by viewModel.events.collectAsState()
    val userRegistrations by viewModel.userRegistrations.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val categoryFilter by viewModel.categoryFilter.collectAsState()

    // Internal tab toggle to swap between "Horticultural Catalog" and "My Booked Passes"
    var selectedTab by remember { mutableIntStateOf(0) }

    val categories = listOf("All", "Gala Evening", "High Tea", "Creative Workshop", "Spiritual Gala", "Guided Tour")

    // Filter events dynamically based on search inquiries and category pills
    val filteredEvents = remember(events, searchQuery, categoryFilter) {
        events.filter { ev ->
            val matchesSearch = ev.title.contains(searchQuery, ignoreCase = true) ||
                    ev.description.contains(searchQuery, ignoreCase = true) ||
                    ev.flowerTheme.contains(searchQuery, ignoreCase = true)
            val matchesCategory = categoryFilter == "All" || ev.category == categoryFilter
            matchesSearch && matchesCategory
        }
    }

    FloralBackground {
        Column(
            modifier = modifier
                .fillMaxSize()
                .systemBarsPadding()
                .navigationBarsPadding()
        ) {
            // 1. TOP HEADER BANNER (Floralia Cover Banner)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(170.dp)
                    .testTag("dashboard_header_card"),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(id = R.drawable.img_flower_banner),
                        contentDescription = "Floral cover banner artwork",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    // Gradient shading for text legibility
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        CharcoalBark.copy(alpha = 0.85f),
                                        CharcoalBark.copy(alpha = 0.4f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )

                    // Welcome Text Overlay
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.LocalFlorist,
                                    contentDescription = null,
                                    tint = BlushPink,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "FLORALIA PORTAL",
                                    color = BlushPink,
                                    fontSize = 11.sp,
                                    letterSpacing = 2.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // Logout Button
                            IconButton(
                                onClick = { viewModel.logout() },
                                modifier = Modifier
                                    .size(34.dp)
                                    .background(Color.White.copy(alpha = 0.2f), ShapeDefaults.Medium)
                                    .testTag("dashboard_logout_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Logout,
                                    contentDescription = "Logout",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        Column {
                            Text(
                                text = "Welcome home, ${currentUser?.displayName ?: "Guest"}",
                                color = Color.White,
                                fontSize = 21.sp,
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            // Dynamic flavor badge referencing user interest (flower style preference)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .background(Color.White.copy(alpha = 0.2f), ShapeDefaults.ExtraSmall)
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Eco,
                                    contentDescription = null,
                                    tint = AccentHoney,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Botanical Affinity: ${currentUser?.favFlower ?: "Standard Rose"}",
                                    color = Color.White.copy(alpha = 0.9f),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            // 2. TAB TOGGLES
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                contentColor = RosewoodDeep,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.CardMembership, null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Horticultural Catalog", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    },
                    modifier = Modifier.testTag("tab_catalog")
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            BadgedBox(
                                badge = {
                                    if (userRegistrations.isNotEmpty()) {
                                        Badge(containerColor = RosewoodDeep, contentColor = WarmLinen) {
                                            Text(userRegistrations.size.toString())
                                        }
                                    }
                                }
                            ) {
                                Icon(Icons.Filled.ConfirmationNumber, null, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("My Booked Passes", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    },
                    modifier = Modifier.testTag("tab_bookings")
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 3. CONTENTS BASED ON TAB SELECT
            if (selectedTab == 0) {
                // HORTICULTURAL EVENTS CATALOG TAB
                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    placeholder = { Text("Search botanical festivals, classes...") },
                    leadingIcon = { Icon(Icons.Filled.Search, null, tint = RosewoodDeep) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = RosewoodDeep,
                        unfocusedBorderColor = SageLight,
                        focusedLabelColor = RosewoodDeep,
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White
                    ),
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .testTag("event_search")
                )

                // Horizontal Flow Categories Filters
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(categories) { cat ->
                        val isSelected = categoryFilter == cat
                        val containerCol = if (isSelected) RosewoodDeep else GlassBgColor
                        val textCol = if (isSelected) Color.White else CharcoalBark

                        Card(
                            onClick = { viewModel.setCategoryFilter(cat) },
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = containerCol),
                            border = if (isSelected) null else BorderStroke(1.dp, GlassBorderColor),
                            modifier = Modifier
                                .testTag("filter_pill_$cat")
                                .minimumInteractiveComponentSize()
                        ) {
                            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                                Text(
                                    text = cat,
                                    color = textCol,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }

                // Dynamic list of Events
                if (filteredEvents.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Filled.Spa,
                                contentDescription = null,
                                tint = SageLight,
                                modifier = Modifier.size(72.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No flower festivals found",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = SoftSage
                            )
                            Text(
                                text = "Try modifying your flower filter terms",
                                fontSize = 13.sp,
                                color = Color.Gray
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .testTag("event_list"),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(filteredEvents) { ev ->
                            BotanicalEventCard(
                                event = ev,
                                onDetailsClick = { viewModel.navigateTo(Screen.EventDetails(ev.id)) }
                            )
                        }
                    }
                }
            } else {
                // ACTIVE BOOKED TICKETS TAB
                if (userRegistrations.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Filled.LocalActivity,
                                contentDescription = null,
                                tint = SageLight,
                                modifier = Modifier.size(72.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No booked passes yet",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = SoftSage
                            )
                            Text(
                                text = "Your event tickets will bloom here!",
                                fontSize = 13.sp,
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { selectedTab = 0 },
                                colors = ButtonDefaults.buttonColors(containerColor = RosewoodDeep)
                            ) {
                                Text("Discover Blossoms")
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .testTag("my_bookings_list"),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(userRegistrations) { entry ->
                            UserBookingCard(
                                booking = entry,
                                onCancel = {
                                    viewModel.cancelTickets(
                                        registrationId = entry.registration.id,
                                        eventId = entry.registration.eventId,
                                        ticketsReleased = entry.registration.numTickets
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

// Gorgeous Item Card representing an event listing
@Composable
fun BotanicalEventCard(
    event: Event,
    onDetailsClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("event_card_${event.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = GlassBgColor),
        border = BorderStroke(1.5.dp, GlassBorderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Horizontal colored bar representing the floral motif color
            val blockColor = when {
                event.flowerTheme.contains("Rose", true) -> RosewoodDeep
                event.flowerTheme.contains("Sakura", true) -> BlushPink
                event.flowerTheme.contains("Lavender", true) -> SageLight
                event.flowerTheme.contains("Sunflower", true) -> AccentHoney
                else -> SoftSage
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .background(blockColor)
            )

            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = blockColor.copy(alpha = 0.15f)),
                            shape = CircleShape
                        ) {
                            Text(
                                text = event.category.uppercase(),
                                color = if (blockColor == BlushPink) RosewoodDeep else blockColor,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = event.title,
                            fontSize = 18.sp,
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            color = CharcoalBark,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Text(
                        text = "$${String.format("%.2f", event.ticketPrice)}",
                        color = RosewoodDeep,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = event.description,
                    color = Color.DarkGray,
                    fontSize = 13.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Time and Place Parameters
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.CalendarMonth, null, tint = SoftSage, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(event.date, color = Color.Gray, fontSize = 12.sp)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.LocationOn, null, tint = SoftSage, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = event.location.split(",").first(),
                            color = Color.Gray,
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Divider(color = WarmLinen, thickness = 1.dp)

                Spacer(modifier = Modifier.height(12.dp))

                // Booking capacity bar
                val seatsRemaining = (event.totalCapacity - event.bookedSeats).coerceAtLeast(0)
                val isSoldOut = seatsRemaining <= 0
                val ratio = event.bookedSeats.toFloat() / event.totalCapacity.toFloat()

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = if (isSoldOut) "Sold Out" else "$seatsRemaining seats left",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSoldOut) Color.Red else SoftSage
                            )
                            Text(
                                text = "${event.bookedSeats}/${event.totalCapacity} locked",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = ratio,
                            color = if (isSoldOut) Color.LightGray else blockColor,
                            trackColor = WarmLinen,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(CircleShape)
                        )
                    }

                    Spacer(modifier = Modifier.width(20.dp))

                    // Booking pass link
                    IconButton(
                        onClick = onDetailsClick,
                        modifier = Modifier
                            .size(38.dp)
                            .background(RosewoodDeep, CircleShape)
                            .testTag("view_event_details_button_${event.id}")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Show passes",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

// Custom card displaying active registrations for current user
@Composable
fun UserBookingCard(
    booking: UserRegistrationWithEvent,
    onCancel: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("booked_card_${booking.registration.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = GlassBgColor),
        border = BorderStroke(1.5.dp, GlassBorderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Circle floral emblem
            val themeColor = when {
                booking.event.flowerTheme.contains("Rose", true) -> RosewoodDeep
                booking.event.flowerTheme.contains("Sakura", true) -> BlushPink
                booking.event.flowerTheme.contains("Lavender", true) -> SageLight
                booking.event.flowerTheme.contains("Sunflower", true) -> AccentHoney
                else -> SoftSage
            }

            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(themeColor.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.ConfirmationNumber,
                    contentDescription = null,
                    tint = if (themeColor == BlushPink) RosewoodDeep else themeColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = booking.event.title,
                    fontSize = 15.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    color = CharcoalBark,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.CardTravel, "", tint = Color.Gray, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${booking.registration.numTickets} Reserved Seat${if (booking.registration.numTickets > 1) "s" else ""}",
                        color = Color.Gray,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Yard, "", tint = SoftSage, modifier = Modifier.size(11.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Bouquet Style: ${booking.registration.bouquetPreference}",
                        color = SoftSage,
                        fontSize = 11.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }
            }

            // Quick Cancel Button with a flower delete marker
            IconButton(
                onClick = onCancel,
                modifier = Modifier
                    .testTag("cancel_booking_button_${booking.registration.id}")
                    .minimumInteractiveComponentSize()
            ) {
                Icon(
                    imageVector = Icons.Filled.CancelPresentation,
                    contentDescription = "Cancel registration",
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
                    modifier = Modifier.size(26.dp)
                )
            }
        }
    }
}
