package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed interface Screen {
    object Login : Screen
    object Register : Screen
    object Dashboard : Screen
    data class EventDetails(val eventId: Long) : Screen
}

class PortalViewModel(application: Application, private val repository: EventRepository) : AndroidViewModel(application) {

    // Navigation and UX Flow
    private val _currentScreen = MutableStateFlow<Screen>(Screen.Login)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    // Session State
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    // Events State
    val events: StateFlow<List<Event>> = repository.allEvents
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // User Registrations State (dynamically collects when user logs in)
    private val _userRegistrations = MutableStateFlow<List<UserRegistrationWithEvent>>(emptyList())
    val userRegistrations: StateFlow<List<UserRegistrationWithEvent>> = _userRegistrations.asStateFlow()

    // Form errors and notifications
    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError.asStateFlow()

    private val _registerError = MutableStateFlow<String?>(null)
    val registerError: StateFlow<String?> = _registerError.asStateFlow()

    private val _bookingError = MutableStateFlow<String?>(null)
    val bookingError: StateFlow<String?> = _bookingError.asStateFlow()

    private val _bookingSuccess = MutableStateFlow<Boolean>(false)
    val bookingSuccess: StateFlow<Boolean> = _bookingSuccess.asStateFlow()

    // Flower Filter for search
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _categoryFilter = MutableStateFlow("All")
    val categoryFilter: StateFlow<String> = _categoryFilter.asStateFlow()

    init {
        // Automatically seed botanical events on startup
        viewModelScope.launch {
            repository.prepopulateEvents()
        }
    }

    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
        _loginError.value = null
        _registerError.value = null
        _bookingError.value = null
        _bookingSuccess.value = false
    }

    fun login(usernameInput: String, passwordInput: String) {
        if (usernameInput.isBlank() || passwordInput.isBlank()) {
            _loginError.value = "Credentials cannot be blank"
            return
        }

        viewModelScope.launch {
            val user = repository.getUserByUsername(usernameInput.trim())
            if (user != null && user.passwordHash == passwordInput) {
                _currentUser.value = user
                _loginError.value = null
                observeUserRegistrations(user.username)
                navigateTo(Screen.Dashboard)
            } else {
                _loginError.value = "Invalid username or password"
            }
        }
    }

    fun register(usernameInput: String, passwordInput: String, displayInput: String, emailInput: String, favFlowerInput: String) {
        if (usernameInput.isBlank() || passwordInput.isBlank() || displayInput.isBlank() || emailInput.isBlank()) {
            _registerError.value = "All fields are required"
            return
        }

        viewModelScope.launch {
            val existing = repository.getUserByUsername(usernameInput.trim())
            if (existing != null) {
                _registerError.value = "Username already exists"
                return@launch
            }

            val newUser = User(
                username = usernameInput.trim(),
                passwordHash = passwordInput,
                displayName = displayInput.trim(),
                email = emailInput.trim(),
                favFlower = favFlowerInput
            )
            try {
                repository.registerUser(newUser)
                _currentUser.value = newUser
                _registerError.value = null
                observeUserRegistrations(newUser.username)
                navigateTo(Screen.Dashboard)
            } catch (e: Exception) {
                _registerError.value = "Error registering user: ${e.message}"
            }
        }
    }

    fun logout() {
        _currentUser.value = null
        _userRegistrations.value = emptyList()
        navigateTo(Screen.Login)
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setCategoryFilter(category: String) {
        _categoryFilter.value = category
    }

    fun bookTickets(eventId: Long, ticketsCount: Int, bouquetPref: String) {
        val user = _currentUser.value
        if (user == null) {
            _bookingError.value = "Please login first"
            return
        }

        viewModelScope.launch {
            val success = repository.bookEvent(user.username, eventId, ticketsCount, bouquetPref)
            if (success) {
                _bookingSuccess.value = true
                _bookingError.value = null
                // Go back to dashboard on success
                navigateTo(Screen.Dashboard)
            } else {
                _bookingError.value = "Failed to book tickets. Exceeds remaining seats capacity!"
                _bookingSuccess.value = false
            }
        }
    }

    fun cancelTickets(registrationId: Long, eventId: Long, ticketsReleased: Int) {
        viewModelScope.launch {
            repository.cancelBooking(registrationId, eventId, ticketsReleased)
        }
    }

    private fun observeUserRegistrations(username: String) {
        viewModelScope.launch {
            repository.getRegistrationsForUser(username).collect { list ->
                _userRegistrations.value = list
            }
        }
    }

    // Custom Factory
    class Factory(private val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(PortalViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                val database = AppDatabase.getDatabase(application)
                val repository = EventRepository(database)
                return PortalViewModel(application, repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
