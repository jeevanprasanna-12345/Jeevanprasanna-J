package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "events")
data class Event(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    val date: String,
    val location: String,
    val flowerTheme: String, // e.g. "Heirloom Red Roses", "Cherry Blossoms", "Golden Sunflowers", "Water Lilies"
    val category: String, // e.g. "Gala", "Workshop", "Garden Tour", "High Tea"
    val ticketPrice: Double,
    val totalCapacity: Int = 100,
    val bookedSeats: Int = 0
)

@Entity(
    tableName = "event_registrations",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["username"],
            childColumns = ["username"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Event::class,
            parentColumns = ["id"],
            childColumns = ["eventId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("username"), Index("eventId")]
)
data class EventRegistration(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val username: String,
    val eventId: Long,
    val numTickets: Int,
    val registrationDate: Long = System.currentTimeMillis(),
    val bouquetPreference: String = "Classic Rose Wreath" // Floral personalization included page-to-page!
)

// A clean join class to reactive-populate the booked events list in dashboard
data class UserRegistrationWithEvent(
    @Embedded val registration: EventRegistration,
    @Relation(
        parentColumn = "eventId",
        entityColumn = "id"
    )
    val event: Event
)

@Dao
interface EventDao {
    @Query("SELECT * FROM events ORDER BY date ASC")
    fun getAllEvents(): Flow<List<Event>>

    @Query("SELECT * FROM events WHERE id = :id LIMIT 1")
    fun getEventById(id: Long): Flow<Event?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: Event)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRegistration(registration: EventRegistration)

    @Update
    suspend fun updateEvent(event: Event)

    @Transaction
    @Query("SELECT * FROM event_registrations WHERE username = :username ORDER BY registrationDate DESC")
    fun getRegistrationsForUser(username: String): Flow<List<UserRegistrationWithEvent>>

    @Query("DELETE FROM event_registrations WHERE id = :registrationId")
    suspend fun cancelRegistration(registrationId: Long)
}
