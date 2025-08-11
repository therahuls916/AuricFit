# 👟 AuricFit - Modern Offline Fitness Tracker
> **Project for Android Development Learning & Portfolio**

| Project Info      | Details                            |
|----------------------|-------------------------------------|
| **Developer Name**             | Rahul Salunke                      |
| **Project**        | AuricFit - Fitness Tracker         |
| **Domain**           | Android Development                |
| **Guidance By**    | AI Assistant (step-by-step development) |

---
### 📸 Screenshots

| Splash Screen | Profile Screen | Home Screen |
| :---: | :---: | :---: |
| ![Splash Screen](https://github.com/user-attachments/assets/e87f7e53-9d54-45fc-bceb-30bf790b9147) | ![Profile Screen](https://github.com/user-attachments/assets/4637b4c7-baed-42bd-9a29-860f31e11e20) | ![Home Screen](https://github.com/user-attachments/assets/5d7ecbb4-a4ac-4996-865b-76d45212eb72) |
| **History (Steps)** | **History (Calories)** | **Notification** |
| :---: | :---: | :---: |
| ![History Steps](https://github.com/user-attachments/assets/3224b86a-2692-46d2-8df7-41710c067380) | ![History Calories](https://github.com/user-attachments/assets/099504c3-260a-4d8b-843d-ebf00a890d17) | ![Notification Control](https://github.com/user-attachments/assets/421de23f-a743-412f-9f61-d800737216b9) |

AuricFit is a modern, offline fitness tracker for Android built with Kotlin and Jetpack Compose. It allows users to track their daily steps in real-time, monitor distance and calories burned, and view their activity history in a beautiful, intuitive interface with a focus on a premium user experience.

---
## 🚀 Features

| Feature | Description |
|--------|-------------|
| 👟 **Real-Time Step Tracking** | Tracks steps accurately using the device's built-in `TYPE_STEP_COUNTER` sensor. |
| 📈 **Accurate Calculations** | Dynamically calculates distance covered (based on user's stride length) and calories burned (using the MET formula and user's weight). |
| ✨ **Modern UI/UX** | Built entirely with Jetpack Compose & Material 3, featuring a dark-first design, a real-time dashboard, and an interactive history screen. |
| 📊 **Data Visualization** | Displays daily, weekly, and monthly activity trends with an interactive bar chart powered by MPAndroidChart. |
| 💾 **Data Persistence** | All user data (profile settings, daily history) is saved locally and persists between app launches using Room (for history) and DataStore (for profile). |
| 🔔 **Background Tracking** | Step counting continues to run seamlessly when the app is in the background via a `ForegroundService`, controllable from a system notification. |
| 🔄 **Reboot Resilience** | Automatically re-calibrates the step count baseline after a device reboot to ensure tracking accuracy is never lost. |
| 📄 **Data Export** | Allows users to export their entire fitness history as a CSV file using Android's Storage Access Framework. |

---

## 🛠 Tech Stack

- **Kotlin** (Primary Language)
- **Jetpack Compose** (Modern UI Toolkit)
- **MVVM Architecture** (Model-View-ViewModel)
- **Kotlin Coroutines & Flow** (For asynchronous operations)
- **Room Database** (Local Persistence for daily history)
- **Jetpack DataStore** (Local Persistence for user profile)
- **Jetpack Navigation Compose** (For screen navigation)
- **MPAndroidChart** (For data visualization)
- **Accompanist Permissions** (For streamlined permission handling)
- **Material 3 Components**

---
## 🔧 Installation
```bash
# Replace with your actual GitHub repository URL
git clone https://github.com/therahuls916/AuricFit-Android.git
cd AuricFit-Android

Open the project in Android Studio, let Gradle sync, and click ▶️ Run.
📂 Folder Structure
code
Code
app/src/main/java/com/rahul/auric/auricfit/
├── MainActivity.kt
├── AuricFitApp.kt
├── data/
│   ├── UserProfile.kt
│   └── UserProfileRepository.kt
├── db/
│   ├── AppDatabase.kt
│   ├── StepDao.kt
│   └── StepData.kt
├── di/
│   └── Graph.kt
├── sensor/
│   ├── RebootReceiver.kt
│   ├── StepCounterService.kt
│   ├── StepDataRepository.kt
│   └── StepSensorManager.kt
├── ui/
│   ├── navigation/
│   │   ├── AppNavigation.kt
│   │   └── BottomNavBar.kt
│   ├── screens/
│   │   ├── history/
│   │   ├── home/
│   │   └── profile/
│   └── theme/
│       └── ... (Color.kt, Theme.kt, Type.kt)
└── util/
    ├── CsvUtils.kt
    ├── DateUtils.kt
    └── PermissionUtils.kt

🔐 Permissions Used
Permission	Reason
ACTIVITY_RECOGNITION	("Physical activity") Required to read the step counter sensor on Android 10 and newer.
POST_NOTIFICATIONS	Required to show the persistent notification for the background tracking service on Android 13 and newer.
FOREGROUND_SERVICE & FOREGROUND_SERVICE_HEALTH	Essential for allowing the step counting service to run reliably when the app is in the background.
RECEIVE_BOOT_COMPLETED	Allows the app to re-baseline the step counter after the user reboots their phone, ensuring accurate tracking.

## 🧠 How It Works
* **UI Layer:** The entire UI is built with Jetpack Compose. Screens are stateless Composables that receive data and events from their ViewModels, creating a reactive and predictable UI.
* **State Management:** `StateFlow` is used to expose data from ViewModels to the UI. The UI observes these flows using `collectAsState` and automatically recomposes when the step count, history, or user profile changes.
* **Sensor & Service Layer:** A `ForegroundService` hosts the `StepSensorManager` to ensure continuous, battery-efficient step detection. A `BroadcastReceiver` handles device reboots to maintain data integrity.
* **Data Layer:** A `StepDataRepository` acts as the single source of truth, orchestrating data from the hardware sensor, the Room database, and the DataStore preferences to provide clean, calculated data to the ViewModels.
* **Database:** Room is used for storing a daily summary of fitness activity. DataStore is used for lightweight storage of the user's profile (weight, stride, goal).

---

## ✅ Planned Features

* [ ] 🎉 Send a notification when the user reaches their daily goal.
* [ ] 📅 Refine date formatting on the History screen to be more user-friendly (e.g., "Today", "Yesterday").
* [ ] ⚙️ Add an "About" section to the Profile screen with app information and links.
* [ ] 🎨 Add UI for light mode theme and allow user to switch in preferences.

---

## 🤝 Contributing
Want to help? Fork this repo, create a new branch, and open a PR with improvements or features.

---

## 📄 License
This project is licensed under the MIT License - see the `LICENSE` file for details.

---

## 🙌 Acknowledgements
* [MPAndroidChart](https://github.com/PhilJay/MPAndroidChart) by Phil Jay
* [Material Design Components](https://m3.material.io/) by Google

---

## 👨‍💻 Developer
**Rahul Salunke**
[GitHub](https://github.com/therahuls916) | [LinkedIn](https://www.linkedin.com/in/rahulasalunke/)