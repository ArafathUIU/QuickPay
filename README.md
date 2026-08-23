# QuickPay

Fintech digital wallet and QR payment Android application.

## Build Status

- `assembleDebug`: **BUILD SUCCESSFUL**
- `testDebugUnitTest`: **all tests pass**

## Architecture

- **Language**: Java 11
- **Architecture**: MVVM (View - ViewModel - Model)
- **Data Persistence**: Room SQLite
- **Networking**: Retrofit 2 + JSON API
- **Navigation**: Navigation Component
- **Dependencies**: Glide, Material Components, Gson, JUnit

## Features

- Authentication (JWT login/register with encrypted session)
- Wallet balance, add money, send money, receive QR
- Merchant QR payment with backend validation
- Transaction state machine: PENDING → PROCESSING → SUCCESS/FAILED/REVERSED
- Unit tests for validators, QR parsing, payment validation

## Backend

ASP.NET Core API running on port `8080` at `http://10.0.2.2:8080/api/`.