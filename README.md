# Manaaki Health: Frontend Prototype

A native Android prototype for older adults in New Zealand who live at home. It brings medication reminders, vital-sign records, daily activity tracking, an emergency SOS button and a care contact list into one app.

> **Student prototype for AUT COMP826 Mobile Systems Development (Milestone 2, UI/UX path).**
> It is not a medical device. All names, health details, phone numbers and readings are sample data.

**Author:** Darshilkumar Mathukiya, UI/UX role
**Course:** COMP826 Mobile Systems Development, Auckland University of Technology

---

## Overview

Manaaki Health is a frontend Android prototype developed for academic evaluation. The project follows the UI/UX design and frontend evaluation pathway. It focuses on elderly accessibility, lower cognitive load and usability standards.

This repository contains the native Android code. It was used to run usability tests with proxy users (adult volunteers standing in for older adults) and to check the interface against accessibility measures, rather than relying on static wireframes.

## Design and accessibility objectives

The prototype was refined over several iterations towards these targets:

- **High contrast:** body text meets the WCAG 2.2 AAA contrast ratio of 7:1 (10.3 to 17.4:1 by calculation). Status colours (amber, red, blue) meet 4.5:1 but not 7:1, and card borders are 2.6:1.
- **Motor accessibility:** a minimum 48 x 48 dp touch target on interactive elements (navigation, buttons and alerts), for users with reduced fine motor control.
- **Lower cognitive load:** simpler navigation and a decluttered Home screen that focuses on immediate health actions (medication reminders, core vitals and emergency SOS).
- **Plain wording and large text:** short labels and layouts that still work at larger system font sizes.

## Screens and features

| Screen | What it shows | Requirement |
|---|---|---|
| Welcome, Sign in, Register | Patient / Caregiver switch, show password, biometric option, register and reset password | n/a |
| Home | Next medicine with Take Now and Snooze, low-supply alert, care service tiles | FR1 |
| Medication | Today's schedule, 7-day adherence, critical-medicine tags, refill alert | FR1 |
| Vitals | Readings stored on the phone, in-range summary, blood pressure, glucose, pulse | FR2 |
| Activity | Step ring against a 7-day baseline, walking pace, resting time, demo sensor buttons | FR3, FR7 (simulated) |
| Care | Linked caregivers and GP with call buttons, task centre | FR6 (interface only) |
| Emergency SOS | Hold for 3 seconds to confirm, current location, Medical ID, emergency contacts | FR4 (no real dialling) |
| Nearby care | Clinics with distance, hours, Call, Nav and Book buttons | FR5 |
| Account | Convert report into PDF button and share dialog (the PDF is empty) | FR8 (interface only) |
| Menu and Settings | Profile, caregiver contact, health history, settings, Log out | n/a |

A red SOS button is shown on the main screens so help is always one press away.

## Technical stack

- **Platform:** Android (minSdk 24, targetSdk 36)
- **Language:** Kotlin 2.2
- **UI toolkit:** Jetpack Compose with Material Design 3, Navigation Compose
- **Architecture:** three layers (data, domain, presentation) with ViewModels
- **Data:** sample data in `SampleData.kt`, loaded into a local Room database on first launch. There is no backend, so the screens can be tested straight away.

## Running the project locally

### What you need
- A current version of [Android Studio](https://developer.android.com/studio). The project uses Android Gradle Plugin 9.4.1, so an older Android Studio may ask you to update.
- An Android emulator (API 24 or newer), or a physical Android phone with USB debugging turned on.

### Steps
1. Clone the repository:
   ```bash
   git clone https://github.com/DarshilMathukiya/Manaaki_Health_App.git
   ```
2. Open the cloned folder in Android Studio (**File > Open**).
3. Wait for Gradle to sync and download the dependencies. This can take a few minutes the first time.
4. Choose an emulator or your phone in the device list and click **Run**.
5. Sign in with the demo account below.

### Demo login
**Patient login** (choose **Patient** on the sign-in screen)

| Field | Value |
|---|---|
| Email | `demo@example.com` |
| Password | `demo123Password` |

**Caregiver login** (choose **Caregiver** on the sign-in screen)

| Field | Value |
|---|---|
| Email | `admin` |
| Password | `Admin01234` |

## Project structure

```
.
├── app/src/main/
│   ├── java/com/example/
│   │   ├── MainActivity.kt
│   │   ├── data/            Local database (Room) and repositories
│   │   ├── domain/          Models, sample data (SampleData.kt) and use cases
│   │   ├── presentation/    Screens, shared components, ViewModels
│   │   └── ui/theme/        Colours, typography and theme
│   └── res/                 Icons, strings and themes
├── design/                  Design iteration PDF (before and after screenshots)
├── gradle/libs.versions.toml  Library versions
└── README.md
```

## Design iteration

The `design/` folder contains [`Manaaki_Design_Iteration.pdf`](design/Manaaki_Design_Iteration.pdf). It shows before-and-after screenshots for six changes, each linked to a commit:

| Change | Problem | Commit |
|---|---|---|
| Change 1 | SOS button covered content | `5c7c386` |
| Change 2 | Button labels cut or split across lines | `5e6ac0a` |
| Change 3 | Sign-in screen did not fit the screen | `47498f2` |
| Change 4 | Vitals chip and legend squeezed | `55c3ad9` |
| Change 5 | Consistent names across screens | `7ae64de` |
| Change 6 | Navigation redesign | `60dbb97` |

## Testing

- Screens were checked on a OnePlus Nord 5 (Android 16) at normal and at the largest system font size.
- Usability testing with 5 adult proxy users (aged 22 to 60) gave a mean SUS of 67.5 against a target of 75. A heuristic evaluation and an accessibility audit (WCAG 2.2, Accessibility Scanner) were also carried out. Results are in the Milestone 2 report.

## Known limitations

- Sample data only. There is no backend or login server.
- The Walk and Simulate fall buttons are demonstration controls that stand in for phone sensors.
- The SOS screen does not place a real call.
- Bluetooth devices from Milestone 1 are not built.
- The Account screen has a Convert report into PDF button and share dialog (FR8), but the PDF is empty.
- TalkBack (screen reader) support was not tested.
- Not tested with older adults, because that requires ethics approval.

## Disclaimer

This is a university prototype. It must not be used for real medical decisions or emergencies. In a real emergency, call 111.
