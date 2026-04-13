# FastMart 🛒

A feature-rich Android e-commerce application built with Java as part of the Software for Mobile Devices (SMD) course at FAST-NUCES Lahore.

## 📱 Screenshots

> Splash • Onboarding • Login • SignUp • Home • Search • Favourites • Cart • Account

## ✨ Features

- **Onboarding** — shown only on first launch using SharedPreferences
- **Authentication** — Sign up and login with input validation and persistent session
- **Home** — Deals of the Day (horizontal ListView) + Recommended products (2-column RecyclerView grid)
- **Favourites** — Heart products from home, manage your wishlist
- **Search** — Search with history, clear all, and keyboard management
- **Cart** — Add products, adjust quantities, dynamic total, SMS checkout
- **Account** — User profile display

## 🏗️ Tech Stack

- **Language:** Java
- **Min SDK:** API 24
- **Architecture:** Activity + Fragment based
- **Storage:** SharedPreferences
- **UI Components:** RecyclerView, ListView, ViewPager2, TabLayout, BottomNavigationView
- **Libraries:** Material Design, AndroidX

## 📁 Project Structure
app/src/main/java/
├── activities/
│   ├── SplashActivity.java
│   ├── OnboardingActivity.java
│   └── LoginActivity.java
├── fragments/
│   ├── LoginFragment.java
│   ├── SignUpFragment.java
│   ├── HomeFragment.java
│   ├── SearchFragment.java
│   ├── FavouritesFragment.java
│   ├── CartFragment.java
│   └── ProfileFragment.java
├── adapters/
│   ├── LoginPagerAdapter.java
│   ├── DealsAdapter.java
│   ├── ProductAdapter.java
│   ├── FavouritesAdapter.java
│   └── CartAdapter.java
├── models/
│   └── Product.java
└── MainActivity.java
res/
├── layout/
│   ├── activity_splash.xml
│   ├── activity_onboarding.xml
│   ├── activity_login.xml
│   ├── activity_main.xml
│   ├── fragment_home.xml
│   ├── fragment_search.xml
│   ├── fragment_favourites.xml
│   ├── fragment_cart.xml
│   ├── fragment_profile.xml
│   ├── item_deal.xml
│   ├── item_product.xml
│   ├── item_favourite.xml
│   ├── item_cart.xml
│   └── item_recent_search.xml
├── drawable/
│   ├── rounded_top_white.xml
│   └── input_background.xml
├── menu/
│   └── bottom_nav_menu.xml
└── values/
├── colors.xml
└── strings.xml

## 🚀 Getting Started

### Prerequisites
- Android Studio Hedgehog or later
- JDK 11+
- Android device or emulator running API 24+

### Installation
1. Clone the repository
```bash
   git clone https://github.com/yourusername/FastMart.git
```
2. Open the project in Android Studio
3. Let Gradle sync complete
4. Run on emulator or physical device

## 🗝️ SharedPreferences Schema

All data is stored in a single file `app.settings` using dot-notation keys:

| Key | Type | Purpose |
|-----|------|---------|
| `onboarding.shown` | boolean | First launch check |
| `user.email` | String | Registered email |
| `user.password` | String | Registered password |
| `user.isLoggedIn` | boolean | Session persistence |
| `fav.{i}` | boolean | Product favourited state |
| `fav.name.{i}` | String | Favourite product name |
| `fav.price.{i}` | String | Favourite product price |
| `fav.desc.{i}` | String | Favourite product description |
| `favList.favourites` | String | Reserved key |
| `cart.{i}` | boolean | Product in cart state |
| `cart.name.{i}` | String | Cart product name |
| `cart.price.{i}` | String | Cart product price |
| `cart.desc.{i}` | String | Cart product description |
| `cart.qty.{i}` | int | Cart product quantity |
| `search.history` | String | Comma-separated search history |

## 📋 Permissions

```xml
<uses-permission android:name="android.permission.SEND_SMS"/>
<uses-feature android:name="android.hardware.telephony" android:required="false"/>
```

## 🎨 Design Reference

UI designed in Figma — [View Design](https://www.figma.com/design/YClmNVQ4S3UQKE7MvZu0tD/SMD-Assignment-1?node-id=7-1896)

## 👨‍💻 Author

**Bissam** — FAST-NUCES Lahore, BSCS Semester 6

---

*Submitted for SMD Assignment 2 — Spring 2026*
