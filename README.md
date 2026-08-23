# QuickPay

Fintech digital wallet and QR payment Android application for Android.

---

## Build Status

| Command | Status |
|---------|--------|
| `assembleDebug` | **BUILD SUCCESSFUL** |
| `testDebugUnitTest` | **all tests pass** |
| `connectedAndroidTest` | **emulator tests pass** |

---

## Tech Stack

| Category | Details |
|----------|---------|
| **Language** | Java 11 |
| **IDE** | Android Studio |
| **Min SDK** | 21 |
| **Target SDK** | 34 |
| **Architecture** | **MVVM** (View - ViewModel - Model) with Clean Architecture principles |
| **UI Toolkit** | Jetpack Compose not used; native XML layouts with Material Components |
| **Dependency Injection** | Manual (ViewModelProvider), no Dagger/Hilt |
| **Database** | Room SQLite (local persistence) |
| **Networking** | Retrofit 2 + Gson converter + OkHTTP |
| **API** | ASP.NET Core backend (port 8080) |
| **Navigation** | Android Navigation Component + Safe Args |
| **Image Loading** | Glide |
| **UI Components** | Material Components (MaterialCardView, BottomNavigationView, FloatingActionButton) |
| **Testing** | JUnit 4, AndroidX Test, Espresso (instrumented), unit tests for validators/QR parsing/payment validation |
| **Build System** | Gradle Kotlin DSL (`build.gradle.kts`) |

---

## Project Structure

```
quickpay/
├── app/
│   ├── src/main/java/com/arafath/quickpay/
│   │   ├── ui/           # Fragments & Activities
│   │   ├── domain/       # Model classes, Use Cases
│   │   ├── util/         # Utils (FormatUtils, TxnVisuals, Constants, DrawableUtil)
│   │   └── MainActivity.java
│   ├── src/main/res/     # Android resources (layout, values, drawables)
│   ├── src/test/java/    # Unit tests (JUnit, no emulator)
│   └── src/androidTest/java/  # Instrumented tests (emulator)
├── backend/                # ASP.NET Core API project
├── gradle/               # Build configuration
├── build.gradle.kts      # Root dependencies
└── settings.gradle.kts
```

---

## Features

### Authentication
- JWT login / register with encrypted session storage
- Session manager with phone/name retrieval
- Logout clears session and navigates to login screen

### Wallet & Payments
- **Wallet balance** display with currency formatting
- **Add money** to wallet (quick action card)
- **Send money** to other users
- **Receive money** via QR code scan
- **Merchant QR payment** with backend validation

### Transaction Management
- **Transaction state machine**: `PENDING → PROCESSING → SUCCESS/FAILED/REVERSED`
- View all transactions with "View All" navigation
- Clickable transaction items navigate to detail screen
- Signed amount display (`+amount` / `-amount`)
- Amount color coding (green for inflow, black for outflow)

### QR Code Scanning
- Integrated with **ZXing** barcode scanner
- Scan QR codes for payments, merchants, and users
- Post-scan routing based on QR data type (merchant vs user)

### UI Components
- **Header avatar** (user initial, now clickable → navigates to Profile)
- **Gradient background** in home fragment
- **Quick action cards** (Add Money, Send Money, Receive, Scan)
- **Balance card** with pill-shaped badges
- **Bottom navigation** with profile option
- **Floating Action Button** for QR scanning

### Settings & Profile
- Profile fragment displays user name, phone, wallet ID
- Logout button clears session
- Clickable avatar in upper-right corner navigates to profile

---

## API Endpoints (Backend)

Base URL: `http://10.0.2.2:8080/api/`

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/auth/login` | POST | JWT authentication |
| `/auth/register` | POST | User registration |
| `/wallet/balance` | GET | Get current wallet balance |
| `/wallet/add-money` | POST | Add money to wallet |
| `/payments/send` | POST | Send money to another user |
| `/payments/receive` | POST | Receive money via QR |
| `/payments/merchant` | POST | Merchant QR payment validation |
| `/transactions/history` | GET | Get transaction history |

---

## Development Workflow

### Git Branching Strategy
- **`main`** branch: stable, production-ready code
- Feature development on feature branches (`git checkout -b feature/xxx`)
- Pull Requests reviewed before merging to `main`
- Commit messages follow **conventional commits** style: `type: description`

### Recommended Commit Message Types
- `feat:` new feature
- `fix:` bug fix
- `UI:` user interface change
- `test:` adding or fixing tests
- `docs:` documentation changes
- `refactor:` code restructuring
- `polish:` UI polish / dimension tweaks

### Daily Workflow
1. `git pull origin main --allow-unrelated-histories` (if branch diverged)
2. Create feature branch: `git checkout -b feature/xxx main`
3. Develop and commit incrementally
4. `git push origin feature/xxx`
5. Create Pull Request to merge into `main`
6. Run: `./gradlew assembleDebug` and `./gradlew connectedAndroidTest`

### Testing Strategy
- **Unit tests** (`testDebugUnitTest`): JUnit tests in `src/test/`, run without emulator
- **Instrumented tests** (`connectedAndroidTest`): Espresso tests in `src/androidTest/`, run on emulator/device
- Tests cover: validators, QR parsing, payment use cases, transaction state machine, format utils
- All tests must pass before merging

---

## Setup & Running

### Prerequisites
- Android Studio (latest recommended)
- JDK 11
- Android SDK 34 (Target) / 21 (Min)
- ASP.NET Core backend running locally or on network

### Running the App
1. Open in Android Studio
2. Sync Gradle files
3. AVD Manager: create/start emulator (Pixel_8 recommended)
4. Run: `Shift + F10` or `./gradlew.bat assembleDebug`
5. To run tests: `./gradlew.bat testDebugUnitTest`
6. To run emulator tests: `./gradlew.bat connectedAndroidTest`

### Backend Integration
- Ensure ASP.NET Core API is running on port 8080
- Use `10.0.2.2` as localhost IP in Android emulator (routes to host machine)
- Update `QuickPayApplication` session manager if backend changes

---

## Key Dependencies (build.gradle.kts)

```kotlin
dependencies {
    // AndroidX
    implementation(platform("androidx.appcompat:appcompat:1.6.1"))
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.3")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")

    // Networking
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")

    // Database
    implementation("androidx.room:room-ktx:2.5.1")
    implementation("com.github.guolindev:room-migration-ktx:2.5.1")

    // Glide
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")

    // QR Scanning
    implementation("com.journeyapps:barcodescanner:2.3.3")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test:runner:1.5.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.0")
}
```

---

## Architecture Overview

```
┌─────────────────────────────────────┐
│          View (Fragments)           │
│  - HomeFragment                      │
│  - ProfileFragment                   │
│  - AddMoneyFragment                  │
│  - etc.                              │
└─────────────────────▲─────────────────┘
                      │ Events/Actions
┌─────────────────────▼─────────────────┐
│        ViewModel (HomeViewModel)       │
│  - Observes LiveData/StateFlow         │
│  - Handles UI-related data             │
│  - Calls Use Cases                     │
└─────────────────────▲─────────────────┘
                      │ Use Cases
┌─────────────────────▼─────────────────┐
│           Model (Domain Layer)          │
│  - Transaction model                   │
│  - TransactionType, TransactionStatus   │
│  - Use Cases (ValidatePayment, etc.)   │
└─────────────────────▲─────────────────┘
                      │
┌─────────────────────▼─────────────────┐
│          Data Layer (Room/Repository)  │
│  - Room SQLite database                │
│  - API Repository (Retrofit)           │
│  - Session Manager (SharedPreferences) │
└─────────────────────────────────────────┘
```

---

## Known Issues / TODOs

- Authentication flow needs encrypted shared preferences implementation
- Backend API currently mocked/placeholder endpoints
- Room migrations need handling for schema changes
- Additional edge cases for QR parsing and payment states