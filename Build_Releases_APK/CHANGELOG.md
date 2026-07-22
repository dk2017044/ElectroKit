# ElectroKit Release Changelog & Version History

---

## Version 2.0.2 (Build Code: 11) — Current Release
- **Release Date**: 2026-07-23
- **What's New**:
  - **Theme Persistence**: Light and Dark theme mode choices are now stored in `SharedPreferences`, restoring the user's preferred visual style automatically across app launches.
  - **Archiving Architecture Fix**: Corrected Gradle archiving tasks to support release variants (`assembleRelease`), archiving release-ready binaries automatically into `Build_Releases_APK/`.

---

## Version 2.0.1 (Build Code: 10)
- **Release Date**: 2026-07-22
- **What's New**:
  - **Input Text Colors Fixed**: Standardized OutlinedTextField style parameters via `electroKitTextFieldColors` ensuring high-contrast visibility for hint labels, text inputs, borders, and errors in both Light and Dark themes.
  - **Premium UI Tweaks**: Polished overall layout alignments across calculator screens and optimized Support links.

---

## Version 2.0.0 (Build Code: 9)
- **Release Date**: 2026-07-22
- **What's New**:
  - **Major Version Upgrade**: Transitioned the app to Version 2.0.0 with highly interactive Jetpack Compose animations.
  - **PCB Background Animator**: Deployed a dynamic, low-opacity circuit board background (`PcbBackground`) drawing rotating cyan/white electrons on canvas traces.
  - **Ohms Law & LED Glows**: Integrated animated glowing paths and breathing yellow/green diode highlights representing circuit status.
  - **Shimmer and Base Transitions**: Custom dropdown color band shimmers and slide-fade digit animations for a premium fluid experience.

---

## Version 1.0.7 (Build Code: 8)
- **Release Date**: 2026-07-22
- **What's New**:
  - Search Back Handler: Pressing back clears search query first instead of closing the screen.
  - Search Clear Button: One-click clear icon added to search text field.

---

## Version 1.0.6 (Build Code: 7)
- **Release Date**: 2026-07-22
- **What's New**:
  - Components Database Upgrade: Integrated 111 new components, upgrading total active database entries to 400.

---

## Version 1.0.5 (Build Code: 6)
- **Release Date**: 2026-07-22
- **What's New**:
  - Home Screen Declutter: Removed "289" component count subtitle from Components DB card.
  - References Removed: Deleted unused "References" chip from the horizontal categories list.
  - Edge-to-Edge Layout Fix: Added statusBarsPadding to Home screen and rewrote Ohm's Law to use Scaffold/TopAppBar, completely resolving status bar overlapping issues.

---

## Version 1.0.4 (Build Code: 5) — Current Release
- **Release Date**: 2026-07-22
- **What's New**:
  - 🔄 **Back Confirmation**: Quit dialog added to Home screen to prevent accidental exits.
  - 🔍 **Unified Search & Persistence**: Home page search directly carries query to Components, which retains the last search query and results.
  - 🚀 **Performance Optimization**: Pagination (lazy loading 20 items at a time) added to the Component Library list; component count removed for clean UX.
  - ❤️ **Component Favoriting**: Users can favorite components from Detail view and access them on the Favorites screen.
  - 🎨 **Canvas Social Icons**: High-fidelity custom Instagram and YouTube logos drawn using Compose Canvas.
  - 🧮 **Ohm's Law Target Mode**: Locks and auto-calculates target parameter (Voltage, Current, or Resistance) properly.

---

## Version 1.0.3 (Build Code: 4)
- **Release Date**: 2026-07-21
- **What's New**:
  - 🎨 **Custom ElectroKit Launcher Icon**: Completely removed default Android robot icon. Replaced with custom Hexagon PCB 'E' logo with Amber lightning bolt (⚡).
  - 📱 **Multi-Density & Adaptive Icon Support**: Full support for mdpi, hdpi, xhdpi, xxhdpi, xxxhdpi and Android 8+ Adaptive Icons.
  - 🌟 **Splash Screen Branding**: Integrated high-resolution logo into Splash Screen with smooth scale animation.

---

## Version 1.0.2 (Build Code: 3)
- **Release Date**: 2026-07-21
- **What's New**:
  - Redesigned Check for Updates Card: Balanced, compact Material Design 3 layout with a dedicated 'Check Now' button opening the Google Drive update folder.
  - Instagram & YouTube Buttons: Clickable social media icon buttons beside Developer Name (Dilip Kumar):
    - Instagram: @di7xu (https://instagram.com/di7xu)
    - YouTube: @m.dilip07 (https://youtube.com/@m.dilip07)
  - Automated Gradle Archiving: 'archiveVersionedApk' task auto-saves versioned APKs into Build_Releases_APK/.

---

## Version 1.0.1 (Build Code: 2)
- **Release Date**: 2026-07-21
- **What's New**:
  - Profile Picture: Integrated developer avatar (profile.png) into Developer Info card.
  - Direct Named APK Sharing: Share option now sends clean named ElectroKit_v1.0.1.apk instead of base.apk.
  - Launch Stability: Fixed splash screen exit bug and resolved Samsung Android 12 compatibility issues.

---

## Version 1.0.0 (Build Code: 1)
- **Release Date**: 2026-07-20
- **What's New**:
  - Initial Release: Complete offline Electronics Engineering Toolkit.
  - 6 Calculators & Converters: Ohm's Law, LED Resistor, Resistor Color, SMD Code, Series/Parallel, Number System Converter.
  - 289 Component Database: Offline local SQLite database with 16-section detailed view.
