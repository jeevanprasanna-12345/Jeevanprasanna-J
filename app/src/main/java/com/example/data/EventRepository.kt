package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EventRepository(private val database: AppDatabase) {
    private val userDao = database.userDao()
    private val eventDao = database.eventDao()

    val allEvents: Flow<List<Event>> = eventDao.getAllEvents()

    suspend fun getEventById(id: Long): Flow<Event?> {
        return eventDao.getEventById(id)
    }

    suspend fun getUserByUsername(username: String): User? = withContext(Dispatchers.IO) {
        userDao.getUserByUsername(username)
    }

    suspend fun registerUser(user: User) = withContext(Dispatchers.IO) {
        userDao.registerUser(user)
    }

    suspend fun updateUser(user: User) = withContext(Dispatchers.IO) {
        userDao.updateUser(user)
    }

    suspend fun bookEvent(username: String, eventId: Long, numTickets: Int, flowerPref: String): Boolean = withContext(Dispatchers.IO) {
        // Find the event
        val eventList = eventDao.getAllEvents().first()
        val event = eventList.find { it.id == eventId } ?: return@withContext false

        if (event.bookedSeats + numTickets <= event.totalCapacity) {
            // Register booking
            val registration = EventRegistration(
                username = username,
                eventId = eventId,
                numTickets = numTickets,
                bouquetPreference = flowerPref
            )
            eventDao.insertRegistration(registration)

            // Update booked seats on event
            val updatedEvent = event.copy(bookedSeats = event.bookedSeats + numTickets)
            eventDao.updateEvent(updatedEvent)
            true
        } else {
            false
        }
    }

    fun getRegistrationsForUser(username: String): Flow<List<UserRegistrationWithEvent>> {
        return eventDao.getRegistrationsForUser(username)
    }

    suspend fun cancelBooking(registrationId: Long, eventId: Long, ticketsReleased: Int) = withContext(Dispatchers.IO) {
        // Cancel registration
        eventDao.cancelRegistration(registrationId)

        // Find the event to update seats
        val eventList = eventDao.getAllEvents().first()
        val event = eventList.find { it.id == eventId }
        if (event != null) {
            val updatedBooked = (event.bookedSeats - ticketsReleased).coerceAtLeast(0)
            eventDao.updateEvent(event.copy(bookedSeats = updatedBooked))
        }
    }

    // Seed beautiful flower-themed events if there's nothing in databases
    suspend fun prepopulateEvents() = withContext(Dispatchers.IO) {
        val currentEvents = eventDao.getAllEvents().first()
        if (currentEvents.isEmpty()) {
            val seedEvents = listOf(
                Event(
                    title = "Royal Rose Lantern Festival",
                    description = "Celebrate the majestic beauty of rare heirloom roses under starlit glass conservatory canopies. Features organic rose mocktails, custom flower arrangements, and an evening string quartet.",
                    date = "Sept 12, 2026 • 6:30 PM",
                    location = "Royal Conservatory Gardens, Glass Pavilion",
                    flowerTheme = "Crimson Heirloom & White Iceberg Roses",
                    category = "Gala Evening",
                    ticketPrice = 45.00,
                    totalCapacity = 80,
                    bookedSeats = 12
                ),
                Event(
                    title = "Cherry Blossom Vintage High Tea",
                    description = "An exquisite morning tea gathering surrounded by pastel weeping cherry blossoms in full bloom. Enjoy handmade hibiscus macarons, sakura scones, and a curated set of local loose leaf teas.",
                    date = "Oct 04, 2026 • 11:00 AM",
                    location = "Sakura Zen Garden Orchard",
                    flowerTheme = "Weeping Pink Sakura Blossoms",
                    category = "High Tea",
                    ticketPrice = 30.00,
                    totalCapacity = 45,
                    bookedSeats = 8
                ),
                Event(
                    title = "Lavender & Botanical Watercolor Workshop",
                    description = "Unleash your inner painter! A professional artist guides you in painting deep lavender fields using premium organic paints. Refreshing iced lavender lemonade and canvas kits are fully included.",
                    date = "Nov 15, 2026 • 2:00 PM",
                    location = "Scented Meadows, Hillside Rows",
                    flowerTheme = "Munstead & English Lavender Rows",
                    category = "Creative Workshop",
                    ticketPrice = 25.00,
                    totalCapacity = 30,
                    bookedSeats = 5
                ),
                Event(
                    title = "Floating Lotus Lantern Soiree",
                    description = "Release hand-crafted eco-friendly paper lotus lanterns carrying your wishes onto the shimmering twilight lake. Features an ambient flute presentation, botanical food stations, and firefly tours.",
                    date = "Dec 21, 2026 • 7:15 PM",
                    location = "Willow Springs Tranquil Lake",
                    flowerTheme = "Floating White & Gold Water Lilies",
                    category = "Spiritual Gala",
                    ticketPrice = 50.00,
                    totalCapacity = 120,
                    bookedSeats = 34
                ),
                Event(
                    title = "Orchids of the Amazon Greenhouse Walk",
                    description = "An exclusive, guided horticultural tour led by expert botanists. Explore rare humming orchid corridors, marvel at giant insectivorous tropical plants, and sample fresh cacao fruit.",
                    date = "Jan 18, 2027 • 10:00 AM",
                    location = "Conservatory Dome 4, Amazon Core",
                    flowerTheme = "Rare Amazonian Orchids & Bromeliads",
                    category = "Guided Tour",
                    ticketPrice = 20.00,
                    totalCapacity = 50,
                    bookedSeats = 0
                )
            )
            for (ev in seedEvents) {
                eventDao.insertEvent(ev)
            }
        }
    }
}
