# FinGenie - Personal Expense Tracker  dépenses

FinGenie is a modern Android application designed to help users track their daily expenses effortlessly, providing insights into their spending habits through a clean, intuitive interface and visual charts.

## ✨ About The Project

FinGenie aims to simplify personal finance management by offering a seamless way to record expenses, categorize them, and visualize spending patterns. Built entirely with Kotlin and Jetpack Compose, it leverages modern Android development practices and Firebase for backend services.

**Key Motivations:**
* To provide a user-friendly and visually appealing expense tracking experience.
* To learn and implement modern Android technologies like Jetpack Compose, Kotlin Flows, Hilt, and Firebase.
* To create a portfolio project showcasing full-stack mobile app development skills.

---

## 📸 Screenshots
## 📸 Screenshots

<table>
  <tr>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/40bf8ae5-1d2f-4e6b-8917-f5831b8f83f7" alt="FinGenie App Screenshot 1" width="270"/>
      <br/>
      <em></em>
    </td>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/eda648f7-cf52-4ce7-a0a6-edbf371419c2" alt="FinGenie App Screenshot 2" width="270"/>
      <br/>
      <em></em>
    </td>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/0c99f09d-f8cb-4ea7-858b-533223424e8b" alt="FinGenie App Screenshot 3" width="270"/>
      <br/>
      <em></em>
    </td>
  </tr>
  <tr>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/5d2958d8-4877-42c6-b245-a8b42ec219f1" alt="FinGenie App Screenshot 4" width="270"/>
      <br/>
      <em></em>
    </td>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/9bc17bd8-7e8c-43a6-b4bf-df8a187e7ede" alt="FinGenie App Screenshot 5" width="270"/>
      <br/>
      <em></em>
    </td>
    <td align="center">
      </td>
  </tr>
</table>

---

## 🚀 Features

* **User Authentication:** Secure sign-up and login using Phone Number and OTP verification (Firebase Auth).
* **Profile Management:**
    * Users can set up their profile with name, DOB after initial login.
    * View profile information.
    * Logout functionality.
* **Expense Tracking (CRUD):**
    * **Add:** Easily add new expenses with description, amount, category, and date.
    * **View:** Display a comprehensive list of all expenses, grouped by date with daily totals.
    * **Edit:** Modify existing expense details.
    * **Delete:** Remove unwanted expense entries with confirmation.
* **Dashboard Overview:**
    * Display total spending for selected periods (This Month, Last Month, All Time).
    * Animated total spending amount.
    * Quick statistics card (total transactions, average spending, top category).
    * **Category Breakdown:** Pie chart showing expense distribution by category.
    * **Spending Trend:** Bar chart visualizing spending patterns over recent days.
    * List of recent transactions with navigation to edit.
* **Data Filtering & Search:**
    * Filter expenses by time period (This Month, All Time) on the expenses list and dashboard.
    * Search functionality within the expenses list.
* **Modern UI/UX:**
    * Built entirely with Jetpack Compose and Material 3 components.
    * Custom theming (e.g., lime green accents, blue gradient cards on the dashboard).
    * Responsive and intuitive user interface.
* **Real-time Data Sync:** Expenses are stored and synced with Cloud Firestore.

---

## 🛠️ Tech Stack & Architecture

* **Language:** [Kotlin](https://kotlinlang.org/)
* **UI Toolkit:** [Jetpack Compose](https://developer.android.com/jetpack/compose) (with Material 3)
* **Architecture:** MVVM (Model-View-ViewModel)
* **Asynchronous Programming:** Kotlin Coroutines & Flows (StateFlow, `collectAsState`, `LaunchedEffect`)
* **Navigation:** Jetpack Navigation Compose
* **Dependency Injection:** [Hilt](https://developer.android.com/training/dependency-injection/hilt-android)
* **Backend & Database (Firebase):**
    * [Firebase Authentication](https://firebase.google.com/docs/auth) (Phone OTP)
    * [Cloud Firestore](https://firebase.google.com/docs/firestore) (NoSQL Document Database)
* **Charting Library:** [MPAndroidChart](https://github.com/PhilJay/MPAndroidChart) (Integrated via `AndroidView` for Compose)
* **Android Jetpack Components:** ViewModel, Lifecycle, Activity
* **Date/Time Handling:** `java.util.Date`, `java.util.Calendar`, `java.text.SimpleDateFormat`
* **Build System:** Gradle

---

## 🏁 Getting Started

To get a local copy up and running, follow these simple steps.

### Prerequisites

* Android Studio (latest stable version recommended, e.g., Koala or newer)
* Android SDK (minSdkVersion specified in the project, e.g., API 24)
* A Firebase project set up with:
    * Authentication (Phone provider enabled)
    * Cloud Firestore (database created, appropriate security rules, and necessary indexes for queries)
    * Billing enabled (for Phone Auth SMS beyond free tier and other Firebase services if scaled).

### Installation

1.  **Clone the repo:**
    ```sh
    git clone [https://github.com/YOUR_USERNAME/FinGenie.git](https://github.com/YOUR_USERNAME/FinGenie.git)
    ```
2.  **Firebase Setup:**
    * Create a Firebase project at [console.firebase.google.com](https://console.firebase.google.com/).
    * Add an Android app to your Firebase project with your app's package name (e.g., `com.shubhanya.fingenienxt`).
    * Download the `google-services.json` file from your Firebase project settings and place it in the `app/` directory of your Android project.
    * Enable **Phone Authentication** in the Firebase console (Authentication > Sign-in method).
    * Set up **Cloud Firestore** and ensure your security rules allow authenticated users to read/write their data. Create any necessary composite indexes as indicated by Firestore error messages in Logcat during development.
3.  **Open in Android Studio:**
    * Open the cloned project in Android Studio.
    * Let Gradle sync and download dependencies.
4.  **Build and Run:**
    * Build the project (Build > Make Project).
    * Run the app on an emulator or a physical Android device.

---

## 🗺️ Roadmap (Potential Future Enhancements)

* [ ] Income Tracking: Allow users to add and categorize income.
* [ ] Budget Management:
    * Create and manage monthly/custom budgets per category.
    * Visual progress bars for budgets on the dashboard and a dedicated budget screen.
* [ ] Detailed Reports & Analytics:
    * More advanced filtering and sorting options.
    * Export data (CSV/PDF).
    * Comparison reports (e.g., month-over-month spending).
* [ ] Financial Insights: More sophisticated, personalized financial tips and observations.
* [ ] Multiple Accounts: Support for tracking expenses across different accounts (cash, bank, credit card).
* [ ] Recurring Transactions: Ability to set up recurring expenses/income.
* [ ] User Profile Customization: Avatar upload, more profile fields.
* [ ] Enhanced UI Animations & Transitions.
* [ ] Tablet Layout Support.
* [ ] Dark/Light Theme Customization within the app.

---

## 🤝 Contributing

Contributions are what make the open-source community such an amazing place to learn, inspire, and create. Any contributions you make are **greatly appreciated**.

If you have a suggestion that would make this better, please fork the repo and create a pull request. You can also simply open an issue with the tag "enhancement".
Don't forget to give the project a star! Thanks again!

1.  Fork the Project
2.  Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3.  Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4.  Push to the Branch (`git push origin feature/AmazingFeature`)
5.  Open a Pull Request

---

## 📜 License

---

## 📧 Contact

Shubhankar Das 

Project Link: [https://github.com/YOUR_USERNAME/FinGenie](https://github.com/YOUR_USERNAME/FinGenie)

---

## 🙏 Acknowledgements (Optional)

* Mention any libraries or resources that were particularly helpful.
* Any design inspirations.
