# core:datastore - Multiplatform Preferences Storage

Clean Architecture implementation of key-value storage using platform-native APIs:
- **Android**: DataStore
- **iOS**: NSUserDefaults  
- **JVM/Desktop**: Java Preferences

## Architecture

```
commonMain/
├── domain/
│   └── repository/
│       └── PreferencesRepository.kt    # Repository interface
├── data/
│   ├── datasource/
│   │   └── PreferencesDataSource.kt    # DataSource interface
│   └── repository/
│       └── PreferencesRepositoryImpl.kt # Repository implementation
└── di/
    └── DataStoreModule.kt              # Common Koin module

androidMain/
└── data/datasource/
    └── AndroidPreferencesDataSource.kt  # DataStore implementation

iosMain/
└── data/datasource/
    └── IOSPreferencesDataSource.kt      # NSUserDefaults implementation

jvmMain/
└── data/datasource/
    └── JvmPreferencesDataSource.kt      # Preferences implementation
```

## Setup

### 1. Add to Koin

```kotlin
import com.dqc.kit.datastore.di.dataStoreModule
import org.koin.core.context.startKoin

fun initKoin() {
    startKoin {
        modules(
            dataStoreModule(),
            // ... other modules
        )
    }
}
```

### 2. Use Repository

```kotlin
import com.dqc.kit.datastore.domain.repository.PreferencesRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SettingsRepository : KoinComponent {
    
    private val preferences: PreferencesRepository by inject()
    
    // Simple operations
    suspend fun saveUserId(userId: String) {
        preferences.putString("user_id", userId)
    }
    
    suspend fun getUserId(): String {
        return preferences.getString("user_id", "")
    }
    
    // Reactive with Flow
    fun userIdFlow() = preferences.getStringFlow("user_id", "")
    
    // Boolean
    suspend fun setDarkMode(enabled: Boolean) {
        preferences.putBoolean("dark_mode", enabled)
    }
    
    suspend fun isDarkMode(): Boolean {
        return preferences.getBoolean("dark_mode", false)
    }
    
    // Clear all
    suspend fun clearAll() {
        preferences.clear()
    }
}
```

### 3. In ViewModel

```kotlin
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel : ViewModel() {
    
    private val settingsRepository = SettingsRepository()
    
    // Observe changes reactively
    val userId = settingsRepository.userIdFlow()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            ""
        )
    
    fun updateUserId(id: String) {
        viewModelScope.launch {
            settingsRepository.saveUserId(id)
        }
    }
}
```

## Supported Types

- `String`
- `Int`
- `Long`
- `Boolean`
- `Float`
- `Double`

## API Reference

### Synchronous (suspend)

```kotlin
suspend fun getString(key: String, defaultValue: String = ""): String
suspend fun putString(key: String, value: String)

suspend fun getInt(key: String, defaultValue: Int = 0): Int
suspend fun putInt(key: String, value: Int)

suspend fun getBoolean(key: String, defaultValue: Boolean = false): Boolean
suspend fun putBoolean(key: String, value: Boolean)

// ... and more

suspend fun remove(key: String)
suspend fun clear()
suspend fun contains(key: String): Boolean
suspend fun getAll(): Map<String, Any?>
```

### Reactive (Flow)

```kotlin
fun getStringFlow(key: String, defaultValue: String = ""): Flow<String>
fun getIntFlow(key: String, defaultValue: Int = 0): Flow<Int>
fun getBooleanFlow(key: String, defaultValue: Boolean = false): Flow<Boolean>
// ... and more
```

## Platform Details

### Android
- Uses `androidx.datastore:datastore-preferences`
- File location: `/data/data/<package>/files/datastore/app_preferences.preferences_pb`
- Supports data migration from SharedPreferences

### iOS
- Uses `NSUserDefaults.standardUserDefaults()`
- Automatically synced with iCloud if enabled
- Survives app updates

### JVM/Desktop
- Uses `java.util.prefs.Preferences`
- Linux: `~/.java/.userPrefs/`
- macOS: `~/Library/Preferences/`
- Windows: Registry

## Example: Token Storage

```kotlin
class TokenStorage : KoinComponent {
    
    private val preferences: PreferencesRepository by inject()
    
    companion object {
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_USER_ID = "user_id"
    }
    
    suspend fun saveTokens(accessToken: String, refreshToken: String) {
        preferences.putString(KEY_ACCESS_TOKEN, accessToken)
        preferences.putString(KEY_REFRESH_TOKEN, refreshToken)
    }
    
    suspend fun getAccessToken(): String? {
        val token = preferences.getString(KEY_ACCESS_TOKEN, "")
        return token.ifEmpty { null }
    }
    
    suspend fun getRefreshToken(): String? {
        val token = preferences.getString(KEY_REFRESH_TOKEN, "")
        return token.ifEmpty { null }
    }
    
    suspend fun saveUserId(userId: String) {
        preferences.putString(KEY_USER_ID, userId)
    }
    
    suspend fun getUserId(): String? {
        val id = preferences.getString(KEY_USER_ID, "")
        return id.ifEmpty { null }
    }
    
    suspend fun clear() {
        preferences.clear()
    }
    
    // Reactive
    fun accessTokenFlow() = preferences.getStringFlow(KEY_ACCESS_TOKEN, "")
    fun isLoggedInFlow() = accessTokenFlow()
        .map { it.isNotEmpty() }
}
```

## Clean Architecture Benefits

✅ **Domain Layer**: Pure Kotlin interface, no Android dependencies  
✅ **Data Layer**: Platform-specific implementation hidden behind interface  
✅ **Testable**: Easy to mock PreferencesRepository in tests  
✅ **Multiplatform**: Same API works on Android, iOS, Desktop  
✅ **Reactive**: Flow support for real-time UI updates  
✅ **Type Safe**: Compile-time type checking for all operations