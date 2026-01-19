\# Production-Grade Android Audio Player - Technical Specification



---



\## 1. Executive Summary



This specification outlines the development of a production-grade audio player application for Android devices and Android Auto, leveraging modern Android development practices including Jetpack Compose, Media3, Kotlin Coroutines, and clean architecture principles.



---



\## 2. Technology Stack



\### 2.1 Core Technologies

\- \*\*Language:\*\* Kotlin 2.0.0+

\- \*\*Minimum SDK:\*\* API 24 (Android 7.0)

\- \*\*Target SDK:\*\* API 35 (Android 15)

\- \*\*Compile SDK:\*\* API 35

\- \*\*Build System:\*\* Gradle 8+ with Kotlin DSL

\- \*\*IDE:\*\* Android Studio Otter



\### 2.2 Architecture \& Libraries



\#### UI Framework

\- \*\*Jetpack Compose:\*\* 1.6.0+ (Material3)

\- \*\*Compose Navigation:\*\* 2.7.6+

\- \*\*Accompanist:\*\* System UI Controller, Permissions



\#### Media Handling

\- \*\*AndroidX Media3:\*\* 1.5.0+ (Unified media library)

&nbsp; - `media3-exoplayer` - Audio playback engine

&nbsp; - `media3-session` - Media session management

&nbsp; - `media3-ui` - UI components

&nbsp; - `media3-common` - Common utilities

\- \*\*MediaStore API\*\* - System audio file scanning

\- \*\*DocumentFile API\*\* - External storage access



\#### Background Processing

\- \*\*WorkManager:\*\* 2.9.0+ - Periodic media scanning

\- \*\*Foreground Service\*\* - Continuous playback

\- \*\*Kotlin Coroutines:\*\* 1.7.3+ with Flow



\#### Dependency Injection

\- \*\*Hilt:\*\* 2.50+ (Dagger-based DI)



\#### Database

\- \*\*Room:\*\* 2.6.1+

&nbsp; - Audio metadata storage

&nbsp; - Playlist management

&nbsp; - Playback history

&nbsp; - User preferences



\#### Image Loading

\- \*\*Coil:\*\* 2.5.0+ (Compose-native image loading)

&nbsp; - Album art caching

&nbsp; - Blur transformations for backgrounds



\#### Android Auto

\- \*\*AndroidX Car App Library:\*\* 1.5.0+

\- \*\*Media3 Session integration\*\*



\#### Analytics \& Monitoring

\- \*\*Firebase Crashlytics\*\* - Crash reporting

\- \*\*Firebase Analytics\*\* - User behavior tracking

\- \*\*Timber / FileLoggingTree\*\* - Structured logging with persistent file-based logs for production debugging



\#### Testing

\- \*\*JUnit5:\*\* 5.10.1

\- \*\*Mockk:\*\* 1.13.8

\- \*\*Turbine:\*\* 1.0.0 (Flow testing)

\- \*\*Espresso \& Compose Testing\*\*

\- \*\*Robolectric:\*\* 4.11.1



\#### Code Quality

\- \*\*Detekt:\*\* Static code analysis

\- \*\*ktlint:\*\* Code formatting

\- \*\*LeakCanary:\*\* Memory leak detection (debug builds)



---



\## 3. Architecture



\### 3.1 Clean Architecture Layers



```

┌─────────────────────────────────────┐

│     Presentation Layer (UI)                     │

│  - Compose UI Screens                           │

│  - ViewModels (State Management)                │

│  - Navigation Graph                             │

└─────────────────────────────────────┘

&nbsp;             ↓

┌─────────────────────────────────────┐

│       Domain Layer                  │

│  - Use Cases (Business Logic)       │

│  - Domain Models                    │

│  - Repository Interfaces            │

└─────────────────────────────────────┘

&nbsp;             ↓

┌─────────────────────────────────────┐

│        Data Layer                   │

│  - Repository Implementations       │

│  - Local Data Sources (Room)        │

│  - Remote Data Sources (MediaStore) │

│  - Mappers (Entity ↔ Domain)        │

└─────────────────────────────────────┘

&nbsp;             ↓

┌─────────────────────────────────────┐

│    Service Layer                    │

│  - MediaPlaybackService             │

│  - MediaSessionService              │

│  - NotificationManager              │

└─────────────────────────────────────┘

```



\### 3.2 Module Structure



```

app/

├── data/

│   ├── local/

│   │   ├── database/

│   │   │   ├── dao/

│   │   │   ├── entities/

│   │   │   └── AppDatabase.kt

│   │   └── preferences/

│   │       └── UserPreferences.kt

│   ├── repository/

│   │   └── (Repository Implementations)

│   └── mapper/

│       └── (Data Mappers)

├── domain/

│   ├── model/

│   │   └── (Domain Models)

│   ├── repository/

│   │   └── (Repository Interfaces)

│   └── usecase/

│       └── (Use Cases)

├── presentation/

│   ├── ui/

│   │   ├── screens/

│   │   ├── components/

│   │   └── theme/

│   ├── viewmodel/

│   └── navigation/

├── service/

│   ├── playback/

│   │   ├── MediaPlaybackService.kt

│   │   ├── PlaybackController.kt

│   │   └── NotificationManager.kt

│   └── scanner/

│       └── MediaScannerWorker.kt

├── di/

│   └── (Hilt Modules)

└── util/

&nbsp;   └── (Utilities \& Extensions)

```



---



\## 4. Data Models



\### 4.1 Domain Models



```kotlin

// Core Audio Model

data class AudioTrack(

&nbsp;   val id: String,

&nbsp;   val title: String,

&nbsp;   val artist: String,

&nbsp;   val album: String,

&nbsp;   val albumArtist: String?,

&nbsp;   val duration: Long, // milliseconds

&nbsp;   val path: String,

&nbsp;   val albumArtUri: String?,

&nbsp;   val trackNumber: Int?,

&nbsp;   val year: Int?,

&nbsp;   val genre: String?,

&nbsp;   val bitrate: Int?,

&nbsp;   val sampleRate: Int?,

&nbsp;   val dateAdded: Long,

&nbsp;   val dateModified: Long,

&nbsp;   val size: Long,

&nbsp;   val mimeType: String

)



// Playlist Model

data class Playlist(

&nbsp;   val id: String,

&nbsp;   val name: String,

&nbsp;   val description: String? = null,

&nbsp;   val trackCount: Int,

&nbsp;   val createdAt: Long,

&nbsp;   val updatedAt: Long,

&nbsp;   val artworkUri: String? = null,

&nbsp;   val trackIds: List<String> = emptyList()

)



// Album Model

data class Album(

&nbsp;   val id: String,

&nbsp;   val title: String,

&nbsp;   val artist: String,

&nbsp;   val year: Int?,

&nbsp;   val trackCount: Int,

&nbsp;   val artworkUri: String?,

&nbsp;   val duration: Long,

&nbsp;   val genre: String?

)



// Artist Model

data class Artist(

&nbsp;   val id: String,

&nbsp;   val name: String,

&nbsp;   val albumCount: Int,

&nbsp;   val trackCount: Int,

&nbsp;   val artworkUri: String?

)



// Playback State

data class PlaybackState(

&nbsp;   val currentTrack: AudioTrack?,

&nbsp;   val isPlaying: Boolean,

&nbsp;   val position: Long,

&nbsp;   val duration: Long,

&nbsp;   val playbackSpeed: Float,

&nbsp;   val repeatMode: RepeatMode,

&nbsp;   val shuffleEnabled: Boolean,

&nbsp;   val queue: List<AudioTrack>,

&nbsp;   val queueIndex: Int

)



enum class RepeatMode {

&nbsp;   OFF, ONE, ALL

}

```



\### 4.2 Room Database Entities



```kotlin

@Entity(tableName = "audio\_tracks")

data class AudioTrackEntity(

&nbsp;   @PrimaryKey val id: String,

&nbsp;   val title: String,

&nbsp;   val artist: String,

&nbsp;   val album: String,

&nbsp;   val albumArtist: String?,

&nbsp;   val duration: Long,

&nbsp;   val path: String,

&nbsp;   val albumArtUri: String?,

&nbsp;   val trackNumber: Int?,

&nbsp;   val year: Int?,

&nbsp;   val genre: String?,

&nbsp;   val bitrate: Int?,

&nbsp;   val sampleRate: Int?,

&nbsp;   val dateAdded: Long,

&nbsp;   val dateModified: Long,

&nbsp;   val size: Long,

&nbsp;   val mimeType: String,

&nbsp;   @ColumnInfo(name = "last\_played") val lastPlayed: Long? = null,

&nbsp;   @ColumnInfo(name = "play\_count") val playCount: Int = 0

)



@Entity(tableName = "playlists")

data class PlaylistEntity(

&nbsp;   @PrimaryKey val id: String,

&nbsp;   val name: String,

&nbsp;   val description: String?,

&nbsp;   @ColumnInfo(name = "created\_at") val createdAt: Long,

&nbsp;   @ColumnInfo(name = "updated\_at") val updatedAt: Long,

&nbsp;   @ColumnInfo(name = "artwork\_uri") val artworkUri: String?

)



@Entity(

&nbsp;   tableName = "playlist\_tracks",

&nbsp;   primaryKeys = \["playlist\_id", "position"],

&nbsp;   foreignKeys = \[

&nbsp;       ForeignKey(

&nbsp;           entity = PlaylistEntity::class,

&nbsp;           parentColumns = \["id"],

&nbsp;           childColumns = \["playlist\_id"],

&nbsp;           onDelete = ForeignKey.CASCADE

&nbsp;       ),

&nbsp;       ForeignKey(

&nbsp;           entity = AudioTrackEntity::class,

&nbsp;           parentColumns = \["id"],

&nbsp;           childColumns = \["track\_id"],

&nbsp;           onDelete = ForeignKey.CASCADE

&nbsp;       )

&nbsp;   ]

)

data class PlaylistTrackCrossRef(

&nbsp;   @ColumnInfo(name = "playlist\_id") val playlistId: String,

&nbsp;   @ColumnInfo(name = "track\_id") val trackId: String,

&nbsp;   val position: Int,

&nbsp;   @ColumnInfo(name = "added\_at") val addedAt: Long

)

```



---



\## 5. Core Features Specification



\### 5.1 Media Scanning \& Library Management



\#### Requirements

\- Scan device storage for audio files on first launch and periodically

\- Support for common audio formats: MP3, AAC, FLAC, OGG, WAV, M4A, OPUS

\- Extract metadata using MediaMetadataRetriever and MediaStore

\- Cache album artwork efficiently

\- Handle large libraries (10,000+ tracks) without performance degradation

\- Incremental scanning for new/removed files
\- \*\*Visual Progress\*\*: Real-time progress indicator during scanning
\- \*\*Empty Library Handling\*\*: Dedicated empty state with quick-access scan action



\#### Implementation Details



\*\*Media Scanner Worker\*\*

```kotlin

class MediaScannerWorker @WorkerInject constructor(

&nbsp;   context: Context,

&nbsp;   params: WorkerParameters,

&nbsp;   private val mediaRepository: MediaRepository

) : CoroutineWorker(context, params) {

&nbsp;   

&nbsp;   override suspend fun doWork(): Result {

&nbsp;       return try {

&nbsp;           scanMediaFiles()

&nbsp;           Result.success()

&nbsp;       } catch (e: Exception) {

&nbsp;           Timber.e(e, "Media scan failed")

&nbsp;           Result.retry()

&nbsp;       }

&nbsp;   }

&nbsp;   

&nbsp;   private suspend fun scanMediaFiles() {

&nbsp;       // Query MediaStore for audio files

&nbsp;       // Compare with database

&nbsp;       // Add new tracks, remove deleted tracks

&nbsp;       // Update metadata for modified tracks

&nbsp;   }

}

```



\*\*Supported Audio Formats\*\*

\- MP3 (MPEG Audio Layer 3)

\- AAC (Advanced Audio Coding)

\- FLAC (Free Lossless Audio Codec)

\- OGG Vorbis

\- WAV (Waveform Audio)

\- M4A (MPEG-4 Audio)

\- OPUS

\- WMA (Windows Media Audio) - if device supports



\*\*Metadata Extraction\*\*

\- Use `MediaMetadataRetriever` for deep metadata

\- Fallback to `MediaStore` for performance

\- Extract embedded album art

\- Parse ID3v2 tags comprehensively



\### 5.2 Audio Playback Engine



\#### Requirements

\- Gapless playback between tracks

\- Support for various audio formats

\- Equalizer support (10-band)

\- Playback speed control (0.5x - 2.0x)

\- Crossfade between tracks (configurable)

\- Audio focus management

\- Sleep timer functionality

\- Fade in/out on play/pause



\#### Implementation with Media3



```kotlin

class PlaybackController @Inject constructor(

&nbsp;   private val context: Context,

&nbsp;   private val playbackStateManager: PlaybackStateManager

) {

&nbsp;   

&nbsp;   private val player: ExoPlayer = ExoPlayer.Builder(context)

&nbsp;       .setAudioAttributes(

&nbsp;           AudioAttributes.Builder()

&nbsp;               .setContentType(C.AUDIO\_CONTENT\_TYPE\_MUSIC)

&nbsp;               .setUsage(C.USAGE\_MEDIA)

&nbsp;               .build(),

&nbsp;           true

&nbsp;       )

&nbsp;       .setHandleAudioBecomingNoisy(true)

&nbsp;       .setWakeMode(C.WAKE\_MODE\_NETWORK)

&nbsp;       .build()

&nbsp;       .apply {

&nbsp;           addListener(playerListener)

&nbsp;       }

&nbsp;   

&nbsp;   fun prepare(tracks: List<AudioTrack>, startIndex: Int = 0) {

&nbsp;       val mediaItems = tracks.map { track ->

&nbsp;           MediaItem.Builder()

&nbsp;               .setUri(track.path)

&nbsp;               .setMediaId(track.id)

&nbsp;               .setMediaMetadata(

&nbsp;                   MediaMetadata.Builder()

&nbsp;                       .setTitle(track.title)

&nbsp;                       .setArtist(track.artist)

&nbsp;                       .setAlbumTitle(track.album)

&nbsp;                       .setArtworkUri(track.albumArtUri?.toUri())

&nbsp;                       .build()

&nbsp;               )

&nbsp;               .build()

&nbsp;       }

&nbsp;       

&nbsp;       player.setMediaItems(mediaItems, startIndex, 0L)

&nbsp;       player.prepare()

&nbsp;   }

}

```



\*\*Audio Focus Handling\*\*

\- Request audio focus before playback

\- Handle transient focus loss (duck volume)

\- Handle permanent focus loss (pause playback)

\- Resume on focus regain



\*\*Equalizer Integration\*\*

```kotlin

class EqualizerManager(private val audioSessionId: Int) {

&nbsp;   private val equalizer = Equalizer(0, audioSessionId)

&nbsp;   

&nbsp;   fun setPreset(preset: EqualizerPreset) {

&nbsp;       // Apply preset (Rock, Pop, Jazz, Classical, etc.)

&nbsp;   }

&nbsp;   

&nbsp;   fun setBandLevel(band: Int, level: Int) {

&nbsp;       // Set individual band level (-15dB to +15dB)

&nbsp;   }

}

```



\### 5.3 User Interface Design



\#### Design System - Material 3



\*\*Color Scheme\*\*

\- \*\*Premium Dark Aesthetics\*\*: High-contrast dark theme by default for a premium feel
- \*\*Consistent Visual Brand\*\*: Explicitly disabled dynamic color (Material You) to maintain a controlled, high-end aesthetic across all devices
- \*\*Adaptive UI\*\*: High contrast mode support



\*\*Typography\*\*

```kotlin

val Typography = Typography(

&nbsp;   displayLarge = TextStyle(

&nbsp;       fontWeight = FontWeight.Normal,

&nbsp;       fontSize = 57.sp,

&nbsp;       lineHeight = 64.sp

&nbsp;   ),

&nbsp;   headlineMedium = TextStyle(

&nbsp;       fontWeight = FontWeight.SemiBold,

&nbsp;       fontSize = 28.sp,

&nbsp;       lineHeight = 36.sp

&nbsp;   ),

&nbsp;   bodyLarge = TextStyle(

&nbsp;       fontWeight = FontWeight.Normal,

&nbsp;       fontSize = 16.sp,

&nbsp;       lineHeight = 24.sp

&nbsp;   )

&nbsp;   // ... other text styles

)

```



\#### Screen Specifications



\*\*1. Now Playing Screen\*\*

\- \*\*Vinyl Record Aesthetic\*\*: Circular album art with infinite rotation animation during playback
\- Custom gramophone icon fallback for tracks without artwork
\- Large play/pause button (56dp)

\- Track progress slider with time indicators

\- Previous/Next track buttons

\- Shuffle and repeat toggle buttons

\- Volume slider

\- Queue button

\- Options menu (add to playlist, track info, share)

\- Swipe down to minimize gesture

\- Swipe left/right to change tracks



\*\*2. Library Screens\*\*



\*Tracks View\*

\- Lazy column with virtualization

\- Track item: artwork thumbnail (48dp), title, artist, duration

\- Fast scroll with section headers (alphabetical)

\- Multi-select mode for batch operations

\- Sort options: title, artist, album, date added, duration

\- Filter by genre, artist, album



\*Albums View\*

\- Lazy grid (2-3 columns based on screen size)

\- Album card: artwork, title, artist, track count

\- Grid/List view toggle



\*Artists View\*

\- Lazy list with artist thumbnails

\- Artist item: name, album count, track count

\- Circular artwork thumbnail



\*Playlists View\*

\- List of user-created playlists

\- Playlist card: artwork grid (4 tracks), name, track count

\- Create new playlist FAB



\*\*3. Mini Player (Bottom Sheet)\*\*

\- Persistent across all screens

\- Compact view: artwork, title, artist, play/pause

\- Expandable to full now playing screen

\- Swipe to dismiss stops playback



\*\*4. Search Screen\*\*

\- Real-time search across tracks, albums, artists, playlists

\- Recent searches

\- Search suggestions

\- Categorized results



\*\*5. Settings Screen\*\*

\- Appearance (theme, grid size, album art quality)

\- Playback (gapless, crossfade, audio focus)

\- Library (scan folders, auto-scan frequency)

\- Android Auto settings

\- About \& Licenses



\#### Animations \& Transitions

\- Shared element transitions for album art

\- Smooth page transitions with slide animations

\- Ripple effects on all interactive elements

\- Skeleton loading states for async content

\- Pull-to-refresh with custom animation



\### 5.4 Playlist Management



\#### Features

\- Create/rename/delete playlists

\- Add/remove tracks from playlists
\- \*\*Batch Track Addition\*\*: Searchable dialog for adding multiple tracks to a playlist simultaneously
\- Reorder tracks with drag-and-drop

\- Duplicate playlists

\- Smart playlists (most played, recently added, favorites)

\- Export/import playlists (M3U format)

\- Playlist artwork (auto-generated from top 4 tracks)



\#### Implementation

```kotlin

interface PlaylistRepository {

&nbsp;   fun getAllPlaylists(): Flow<List<Playlist>>

&nbsp;   fun getPlaylist(id: String): Flow<Playlist?>

&nbsp;   suspend fun createPlaylist(name: String, description: String?): Result<Playlist>

&nbsp;   suspend fun addTracksToPlaylist(playlistId: String, trackIds: List<String>)

&nbsp;   suspend fun removeTrackFromPlaylist(playlistId: String, trackId: String)

&nbsp;   suspend fun reorderPlaylistTracks(playlistId: String, fromIndex: Int, toIndex: Int)

&nbsp;   suspend fun deletePlaylist(playlistId: String)

}

```



\### 5.5 Background Playback \& Service



\#### Media Session Service

```kotlin

@UnstableApi

class AudioPlaybackService : MediaSessionService() {

&nbsp;   

&nbsp;   private lateinit var mediaSession: MediaSession

&nbsp;   private lateinit var player: ExoPlayer

&nbsp;   private lateinit var notificationManager: PlaybackNotificationManager

&nbsp;   

&nbsp;   override fun onCreate() {

&nbsp;       super.onCreate()

&nbsp;       

&nbsp;       player = ExoPlayer.Builder(this).build()

&nbsp;       

&nbsp;       mediaSession = MediaSession.Builder(this, player)

&nbsp;           .setCallback(MediaSessionCallback())

&nbsp;           .build()

&nbsp;       

&nbsp;       notificationManager = PlaybackNotificationManager(this, mediaSession)

&nbsp;   }

&nbsp;   

&nbsp;   override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession {

&nbsp;       return mediaSession

&nbsp;   }

&nbsp;   

&nbsp;   override fun onTaskRemoved(rootIntent: Intent?) {

&nbsp;       if (!player.playWhenReady) {

&nbsp;           stopSelf()

&nbsp;       }

&nbsp;   }

&nbsp;   

&nbsp;   override fun onDestroy() {

&nbsp;       mediaSession.release()

&nbsp;       player.release()

&nbsp;       super.onDestroy()

&nbsp;   }

}

```



\#### Notification Design (Media3)

\- Custom notification layout matching app theme

\- Album artwork

\- Track title and artist

\- Previous, Play/Pause, Next buttons

\- Seek bar (Android 13+)

\- Close button

\- Notification actions configurable in settings

\- Support for notification styles on different Android versions



\#### Foreground Service Requirements

\- Notification channel for playback

\- Proper foreground service type: `mediaPlayback`

\- Battery optimization exclusion guidance

\- Handle system-initiated stops gracefully



\### 5.6 Android Auto Integration



\#### Requirements

\- Full Android Auto compatibility (phone and car screen)

\- Browse library by tracks, albums, artists, playlists

\- Queue management

\- Voice command support

\- Safe driving mode UI (large touch targets)

\- Minimized driver distraction



\#### Implementation with Car App Library



```kotlin

class AudioCarAppService : CarAppService() {

&nbsp;   

&nbsp;   override fun createHostValidator(): HostValidator {

&nbsp;       return HostValidator.ALLOW\_ALL\_HOSTS\_VALIDATOR

&nbsp;   }

&nbsp;   

&nbsp;   override fun onCreateSession(): Session {

&nbsp;       return AudioCarSession()

&nbsp;   }

}



class AudioCarSession : Session() {

&nbsp;   

&nbsp;   override fun onCreateScreen(intent: Intent): Screen {

&nbsp;       return LibraryScreen(carContext)

&nbsp;   }

&nbsp;   

&nbsp;   override fun onNewIntent(intent: Intent) {

&nbsp;       // Handle intent from notification or voice command

&nbsp;   }

}

```



\*\*Car Screens\*\*

1\. Library Browser (Grid/List template)

2\. Now Playing (Message template with controls)

3\. Queue Management (List template)

4\. Search (Search template with voice)



\*\*Optimization for Car Display\*\*

\- Large fonts (minimum 16sp)

\- High contrast colors

\- Maximum 6 items per list

\- Distraction-optimized templates

\- Quick access to playback controls



---



\## 6. Advanced Features



\### 6.1 Notification System



\*\*Rich Media Notifications\*\*

\- MediaStyle notification with custom actions

\- Lockscreen controls with album art

\- Android 13+ media controls in Quick Settings

\- Notification priority: HIGH for playback



\*\*Notification Actions\*\*

\- Configurable (up to 5 actions)

\- Default: Previous, Play/Pause, Next, Close

\- Optional: Like, Repeat, Shuffle



\### 6.2 Audio Effects



\*\*Built-in Equalizer\*\*

\- 10-band equalizer (-15dB to +15dB per band)

\- Presets: Normal, Rock, Pop, Jazz, Classical, Metal, Bass Boost, Treble Boost

\- Custom presets (save user configurations)

\- Bass boost effect

\- Virtualizer effect

\- Reverb presets



\*\*Audio Routing\*\*

\- Detect and switch between outputs (speaker, wired headset, Bluetooth)

\- A2DP profile support for high-quality Bluetooth

\- Handle audio device disconnection gracefully



\### 6.3 Smart Features



\*\*Intelligent Queue Management\*\*

\- Add to queue / Play next functionality

\- Auto-queue from album/playlist

\- Queue persistence across app restarts

\- Clear queue option



\*\*Playback History\*\*

\- Track recently played songs

\- Listen statistics (play count, last played)

\- Most played tracks/albums/artists



\*\*Sleep Timer\*\*

\- Timer durations: 5, 10, 15, 30, 45, 60 minutes, end of track

\- Fade out audio before stopping

\- Cancel timer option



\*\*Lyrics Support\*\* (Future Enhancement)

\- LRC file support (synced lyrics)

\- Display lyrics on now playing screen

\- Auto-scroll with playback position



---



\## 7. Performance Optimization



\### 7.1 Database Optimization

\- Indexed columns: artist, album, title, dateAdded

\- Database queries on background threads

\- Use Flow for reactive queries

\- Pagination for large result sets (Paging 3)

\- Database migrations handled with Room migration strategies



\### 7.2 Image Loading Optimization

\- Coil disk and memory caching

\- Downsampling large album art

\- Placeholder images during load

\- Blur transformation for backgrounds (cached)

\- Request thumbnail size from MediaStore first



\### 7.3 Memory Management

\- Bitmap pooling for album art

\- Release player resources when app is backgrounded

\- Use weak references for listeners

\- Profile memory with LeakCanary in debug builds

\- Limit queue size to prevent OOM (soft limit: 500 tracks)



\### 7.4 Startup Optimization

\- Lazy initialization of non-critical components

\- Background media scanning on first launch

\- Splash screen with progress indicator

\- Startup tracing with Jetpack Macrobenchmark



\### 7.5 Battery Optimization

\- Use WorkManager for scheduled tasks (media scanning)

\- Efficient wake locks (only during playback)

\- Doze mode compatibility

\- Battery optimization prompt for background playback



---



\## 8. Permissions \& Security



\### 8.1 Required Permissions



\*\*Android 13+ (API 33+)\*\*

```xml

<uses-permission android:name="android.permission.READ\_MEDIA\_AUDIO" />

<uses-permission android:name="android.permission.POST\_NOTIFICATIONS" />

<uses-permission android:name="android.permission.FOREGROUND\_SERVICE" />

<uses-permission android:name="android.permission.FOREGROUND\_SERVICE\_MEDIA\_PLAYBACK" />

<uses-permission android:name="android.permission.WAKE\_LOCK" />

```



\*\*Android 12 and below (API 32-)\*\*

```xml

<uses-permission android:name="android.permission.READ\_EXTERNAL\_STORAGE" 

&nbsp;   android:maxSdkVersion="32" />

<uses-permission android:name="android.permission.WRITE\_EXTERNAL\_STORAGE"

&nbsp;   android:maxSdkVersion="29" />

```



\### 8.2 Runtime Permission Handling

\- Use Accompanist Permissions library

\- Request permissions with clear rationale dialogs

\- Handle permission denial gracefully

\- Deep link to app settings for manual permission grant

\- Scoped storage compliance (Android 11+)



\### 8.3 Data Privacy

\- No data collection without user consent

\- Local storage only (no cloud sync in v1)

\- Analytics opt-in/opt-out in settings

\- Clear privacy policy

\- Crash reports anonymized



---



\## 9. Testing Strategy



\### 9.1 Unit Tests

\- Repository implementations (mock DAOs)

\- Use cases with test doubles

\- ViewModels with fake repositories

\- Utils and extension functions

\- Coverage target: 80%+



```kotlin

class PlaybackUseCaseTest {

&nbsp;   

&nbsp;   @Test

&nbsp;   fun `play track updates playback state`() = runTest {

&nbsp;       val fakeRepository = FakeMediaRepository()

&nbsp;       val useCase = PlayTrackUseCase(fakeRepository)

&nbsp;       

&nbsp;       useCase(trackId = "123")

&nbsp;       

&nbsp;       val state = fakeRepository.playbackState.first()

&nbsp;       assertThat(state.isPlaying).isTrue()

&nbsp;       assertThat(state.currentTrack?.id).isEqualTo("123")

&nbsp;   }

}

```



\### 9.2 Integration Tests

\- Database operations with in-memory Room database

\- MediaStore scanning with test content provider

\- Service binding and lifecycle

\- WorkManager scheduling



\### 9.3 UI Tests

\- Compose UI tests for all screens

\- Navigation flow tests

\- Accessibility tests (TalkBack, content descriptions)

\- Screenshot tests for visual regression



```kotlin

class NowPlayingScreenTest {

&nbsp;   

&nbsp;   @get:Rule

&nbsp;   val composeTestRule = createComposeRule()

&nbsp;   

&nbsp;   @Test

&nbsp;   fun playPauseButton\_togglesPlayback() {

&nbsp;       composeTestRule.setContent {

&nbsp;           NowPlayingScreen(

&nbsp;               state = testPlaybackState,

&nbsp;               onPlayPause = { /\* verify called \*/ }

&nbsp;           )

&nbsp;       }

&nbsp;       

&nbsp;       composeTestRule.onNodeWithContentDescription("Play")

&nbsp;           .performClick()

&nbsp;   }

}

```



\### 9.4 Performance Tests

\- Startup time measurement

\- Frame rate monitoring (Macrobenchmark)

\- Memory profiling

\- Large dataset handling (10,000+ tracks)

\- Battery consumption testing



\### 9.5 Android Auto Testing

\- Desktop Head Unit (DHU) emulator testing

\- Test all driving scenarios

\- Voice command integration tests

\- Distraction level compliance



---



\## 10. Build Configuration



\### 10.1 Build Variants



```kotlin

android {

&nbsp;   buildTypes {

&nbsp;       debug {

&nbsp;           applicationIdSuffix = ".debug"

&nbsp;           isDebuggable = true

&nbsp;           isMinifyEnabled = false

&nbsp;           manifestPlaceholders\["appName"] = "AudioPlayer Debug"

&nbsp;       }

&nbsp;       

&nbsp;       release {

&nbsp;           isMinifyEnabled = true

&nbsp;           isShrinkResources = true

&nbsp;           proguardFiles(

&nbsp;               getDefaultProguardFile("proguard-android-optimize.txt"),

&nbsp;               "proguard-rules.pro"

&nbsp;           )

&nbsp;           manifestPlaceholders\["appName"] = "AudioPlayer"

&nbsp;           

&nbsp;           signingConfig = signingConfigs.getByName("release")

&nbsp;       }

&nbsp;   }

&nbsp;   

&nbsp;   flavorDimensions += "version"

&nbsp;   productFlavors {

&nbsp;       create("free") {

&nbsp;           dimension = "version"

&nbsp;           applicationIdSuffix = ".free"

&nbsp;       }

&nbsp;       

&nbsp;       create("pro") {

&nbsp;           dimension = "version"

&nbsp;       }

&nbsp;   }

}

```



\### 10.2 ProGuard Rules



```proguard

\# Keep Media3 classes

-keep class androidx.media3.\*\* { \*; }

-keepclassmembers class androidx.media3.\*\* { \*; }



\# Keep Room database

-keep class \* extends androidx.room.RoomDatabase

-keep @androidx.room.Entity class \*



\# Keep Hilt generated classes

-keep class dagger.hilt.\*\* { \*; }

-keep class javax.inject.\*\* { \*; }



\# Keep Kotlin metadata

-keep class kotlin.Metadata { \*; }



\# Keep serialization classes

-keepattributes \*Annotation\*, InnerClasses

-dontnote kotlinx.serialization.\*\*

```



\### 10.3 Gradle Optimization



```kotlin

// gradle.properties

org.gradle.jvmargs=-Xmx4096m -XX:MaxMetaspaceSize=512m

org.gradle.parallel=true

org.gradle.caching=true

org.gradle.configureondemand=true

android.useAndroidX=true

kotlin.code.style=official

kapt.use.worker.api=true

kapt.incremental.apt=true

```



---



\## 11. Accessibility



\### 11.1 Requirements

\- Content descriptions for all interactive elements

\- Semantic headings for screen readers

\- Minimum touch target size: 48dp

\- Sufficient color contrast (WCAG AA)

\- Support for large text sizes

\- Keyboard navigation support



\### 11.2 TalkBack Optimization

\- Announce playback state changes

\- Custom accessibility actions for complex gestures

\- Proper focus ordering

\- Live region announcements for time updates



```kotlin

@Composable

fun PlayPauseButton(

&nbsp;   isPlaying: Boolean,

&nbsp;   onClick: () -> Unit,

&nbsp;   modifier: Modifier = Modifier

) {

&nbsp;   IconButton(

&nbsp;       onClick = onClick,

&nbsp;       modifier = modifier.semantics {

&nbsp;           contentDescription = if (isPlaying) "Pause" else "Play"

&nbsp;           role = Role.Button

&nbsp;       }

&nbsp;   ) {

&nbsp;       Icon(

&nbsp;           imageVector = if (isPlaying) 

&nbsp;               Icons.Filled.Pause else Icons.Filled.PlayArrow,

&nbsp;           contentDescription = null // Handled by parent semantics

&nbsp;       )

&nbsp;   }

}

```



---



\## 12. Localization



\### 12.1 Supported Languages (Initial Release)

\- English (default)

\- Spanish

\- French

\- German

\- Italian

\- Portuguese

\- Russian

\- Japanese

\- Korean

\- Chinese (Simplified \& Traditional)

\- Hindi



\### 12.2 Implementation

\- All strings in `strings.xml` resource files

\- Support for RTL languages

\- Number and date formatting with locale

\- Plurals handling for track/album counts

\- Context-aware translations



```xml

<!-- strings.xml -->

<string name="track\_count">%d tracks</string>

<plurals name="track\_count\_plural">

&nbsp;   <item quantity="one">%d track</item>

&nbsp;   <item quantity="other">%d tracks</item>

</plurals>

```



---



\## 13. Analytics \& Crash Reporting



\### 13.1 Events to Track

\- App launches

\- Playback starts/stops

\- Skip track events

\- Playlist creation

\- Search queries

\- Feature usage (equalizer, sleep timer, etc.)

\- Android Auto connections

\- Error events



\### 13.2 Crashlytics Configuration

```kotlin

FirebaseCrashlytics.getInstance().apply {

&nbsp;   setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG)

&nbsp;   setCustomKey("user\_library\_size", librarySize)

&nbsp;   setUserId(anonymousUserId)

}

```



\### 13.3 Privacy-Compliant Tracking

\- Anonymous user IDs

\- No PII collection

\- Opt-out mechanism in settings

\- GDPR/CCPA compliance

\### 13.4 Local File Logging
\- \*\*Persistent Logs\*\*: Daily log files stored in app's internal storage
\- \*\*Structured Format\*\*: Timestamped logs with priority levels and tags
\- \*\*Auto-Cleanup\*\*: Automatic deletion of logs older than 7 days to preserve storage
\- \*\*Production Debugging\*\*: Allows capturing issues that occur in the field without real-time tracking



---



\## 14. Release Checklist



\### 14.1 Pre-Release

\- \[ ] All unit tests passing (80%+ coverage)

\- \[ ] UI tests passing on multiple device sizes

\- \[ ] Manual testing on Android 7-15

\- \[ ] Android Auto testing on DHU

\- \[ ] Performance profiling completed

\- \[ ] Memory leak checks passed

\- \[ ] ProGuard configuration verified

\- \[ ] Accessibility audit completed

\- \[ ] Security review performed

\- \[ ] Privacy policy updated

\- \[ ] Translations finalized

\- \[ ] App signing configured

\- \[ ] Version code/name updated

