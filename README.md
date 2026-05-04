# Cartify - Grocery & Shopping Android App

Cartify aik modern Grocery aur Shopping application hai jo **Jetpack Compose** ke saath bani hai. Yeh app users ko asani se products dhoondne, stores explore karne, aur orders track karne ki sahulat deti hai.

## 🚀 Features

- **Onboarding & Auth:** SplashScreen, Onboarding, Login, aur Signup screens.
- **Home & Explore:** Categories, Featured Stores, aur Products ka display.
- **Detailed Views:** Store aur Product ki mukammal tafseelat (Details).
- **Shopping Experience:** Cart management aur Checkout process.
- **Real-time Tracking:** Orders ki live tracking aur history.
- **Communication:** Vendors ke saath direct chat ka system.
- **Personalization:** Favorites, Address Management, aur Notifications.
- **Search:** Powerfull search functionality products dhoondne ke liye.

## 🛠 Tech Stack

- **UI:** Jetpack Compose (Modern Toolkit)
- **Language:** Kotlin
- **Architecture:** MVVM (Model-View-ViewModel) with Repository Pattern
- **Networking:** Retrofit & Gson (Backend API integration ke liye)
- **Navigation:** Jetpack Navigation Component
- **Asynchronous Work:** Kotlin Coroutines & Flow
- **Dependency Management:** Gradle Version Catalog (libs.versions.toml)

## 📂 Project Structure

- `ui/`: Saari screens aur theme ki files.
- `data/`: Models, Repositories, aur API service interfaces.
- `navigation/`: App ki routing aur screens ke raste.

## ⚙️ Setup & Installation

1. **Clone the repository:**
   ```bash
   git clone https://github.com/sheezaarshad789-coder/Cartify_app.git
   ```

2. **Open in Android Studio:**
   Project ko Android Studio (Ladybug ya latest version) mein open karein.

3. **Backend Configuration:**
   - App abhi `FakeData.kt` se data utha rahi hai.
   - Live backend ke liye `CartifyApiService.kt` mein `BASE_URL` ko apne Vercel URL se update karein.
   - `MainActivity.kt` mein `syncFromBackend()` ko uncomment karein.

4. **Run the App:**
   `Shift + F10` dabayein ya 'Run' button par click karein.

## 📄 License
Yeh project Educational purposes ke liye hai.

---
**Developed by Sheeza Arshad**
