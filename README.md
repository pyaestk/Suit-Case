# 🎒 Suit-Case

**Suit-Case** is a modern Android application that helps you plan and manage your travel packing with ease. Whether you're going on a business trip, vacation, or weekend getaway, Suit-Case ensures you never forget your essentials.

<img width="1512" height="982" alt="Suitcase 1" src="https://github.com/user-attachments/assets/00f24489-2780-4891-9d26-22171be278ff" />
<img width="1512" height="982" alt="Suitcase 2" src="https://github.com/user-attachments/assets/2f84b6e0-c13e-438e-bc97-564fce96f9b6" />

## ✨ Features

- ✅ **Secure Sign-Up & Log-In**  
  Access your personal packing lists securely from anywhere.

- 🧳 **Create and Manage Packing Lists**  
  Add trips and manage separate packing lists for each one.

- ✅ **Mark Items as Packed**  
  Check off items as you pack to stay organized.

- 📸 **Add Photos to Items**  
  Upload item photos for easy recognition.

- ☁️ **Cloud Sync and Backup**  
  Automatically sync your packing lists across devices and keep them safe in the cloud.

- 🎯 **Gesture Controls**  
  - Swipe **left** to share an item  
  - Swipe **right** to delete an item  
  - **Shake your device** to mark all items as packed




## 🛠️ Tech Stack 

- **Kotlin** – Primary programming language
- **MVVM Architecture** – Clean separation between UI and logic
- **Firebase**  
  - Authentication – For user login/signup  
  - Firestore – For storing and syncing packing data  
  - Storage – For uploading and retrieving item photos
- **Koin** – Dependency injection framework
- **Kotlin Coroutines** – For smooth background task handling
- **Jetpack libraries** – ViewModel, LiveData, Navigation, etc.

## 🚀 Getting Started

### Prerequisites

- Android Studio
- A Firebase project with:
  - Authentication enabled
  - Firestore database configured
  - Storage enabled

### Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/your-username/suit-case.git

2. Open the project in Android Studio.

3. Connect your Firebase project:
   - Download google-services.json from your Firebase console.
   - Place it in the app/ directory.
4. Build and run the app on an emulator or Android device.
