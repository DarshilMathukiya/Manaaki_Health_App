Manaaki Home - Frontend Prototype

Overview
Manaaki Home is a frontend Android application prototype developed for academic evaluation. The project follows a strict UI/UX design and frontend evaluation pathway, focusing on elderly accessibility, cognitive load reduction, and usability standards. 

This repository contains the native Android codebase used to conduct proxy user testing and evaluate the interface against specific accessibility metrics, rather than relying on static wireframes.

Design & Accessibility Objectives
This prototype was iteratively refined to meet the following criteria:
WCAG 2.2 AAA Compliance: High-contrast text and UI elements to support visually impaired users.
Motor Accessibility: Enforced minimum 48x48 dp touch targets across all interactive elements (navigation, buttons, and alerts) to accommodate reduced fine motor control.
Cognitive Load Reduction: Simplified navigation and decluttered dashboard views to focus exclusively on immediate health actions (medication reminders, core vitals, and emergency SOS).

Technical Stack
Platform: Android
Language: Kotlin
UI Toolkit: Jetpack Compose (Material Design 3)
Architecture: Hardcoded mock data architecture to facilitate immediate UI/UX testing without backend dependencies.

Running the Project Locally
1. Clone this repository to your local machine.
2. Open the project in Android Studio.
3. Allow Gradle to sync and download the necessary Jetpack Compose dependencies.
4. Select an emulator or physical Android device and click Run.
