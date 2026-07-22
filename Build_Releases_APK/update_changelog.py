import os

releases_dir = r"c:\MyProject\Mycreate\Electronics app\Build_Releases_APK"
os.makedirs(releases_dir, exist_ok=True)

changelog_md = """# ElectroKit Release Changelog & Version History

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

## Version 1.0.4 (Build Code: 5)
- **Release Date**: 2026-07-22
- **What's New**:
  - Back Confirmation: Quit dialog added to Home screen to prevent accidental exits.
  - Unified Search & Persistence: Home page search directly carries query to Components, which retains the last search query and results.
  - Performance Optimization: Pagination (lazy loading 20 items at a time) added to the Component Library list.
  - Component Favoriting: Users can favorite components from Detail view and access them on the Favorites screen.
  - Canvas Social Icons: High-fidelity custom Instagram and YouTube logos drawn using Compose Canvas.
  - Ohm's Law Target Mode: Locks and auto-calculates target parameter (Voltage, Current, or Resistance) properly.

---

## Version 1.0.3 (Build Code: 4)
- **Release Date**: 2026-07-21
- **What's New**:
  - Custom ElectroKit Launcher Icon: Completely removed default Android robot icon. Replaced with custom Hexagon PCB 'E' logo with Amber lightning bolt (⚡).
  - Multi-density Launcher Icons: Full support for mdpi, hdpi, xhdpi, xxhdpi, xxxhdpi, and Android 8+ Adaptive Icons.
  - Splash Screen Branding: Integrated high-resolution logo into Splash Screen with smooth scale animation.

---

## Version 1.0.2 (Build Code: 3)
- **Release Date**: 2026-07-21
- **What's New**:
  - Redesigned Check for Updates Card: Balanced, compact Material Design 3 layout with a dedicated 'Check Now' button opening the Google Drive update folder.
  - Instagram & YouTube Buttons: Clickable social media icon buttons beside Developer Name (Dilip Kumar).
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
"""

v202_notes = """ElectroKit Version 2.0.2 Release Notes:
- Theme Persistence: Light and Dark theme mode preferences are now stored in SharedPreferences, automatically restoring your theme across app restarts.
- Gradle Archiving Configuration: Updated build tasks to properly copy production release APKs to the Build_Releases_APK/ folder when assembleRelease is run.
"""

v201_notes = """ElectroKit Version 2.0.1 Release Notes:
- Input Text Colors Fixed: Standardized OutlinedTextField style parameters via electroKitTextFieldColors ensuring high-contrast visibility for hint labels, text inputs, borders, and errors in both Light and Dark themes.
- Premium UI Tweaks: Polished overall layout alignments across calculator screens and optimized Support links.
"""

v200_notes = """ElectroKit Version 2.0.0 (Major Release) Release Notes:
- Interactive Engineering Animations: Added rich, responsive animations to all core calculator & reference screens:
  1. Ohm's Law Screen: Current flow animation showing blue/white glowing paths on calculation trigger.
  2. LED Resistor Screen: Dynamic breathing LED bulb animation in yellow/green.
  3. Resistor Color Screen: Shimmer glow highlighting resistor bands briefly on color dropdown changes.
  4. Series/Parallel Screen: Dotted/glowing flow animation moving along a circuit diagram showing parallel branch current splits.
  5. Number System Converter Screen: Digits transition smoothly with slide-fade animation on conversion.
  6. Component Details Screen: Dynamic Interactive Datasheet Explorer with magnifying glass zoom-in/out animation on interaction.
"""

v107_notes = """ElectroKit Version 1.0.7 Release Notes:
- Search Back Handler: Pressing back clears search query first instead of closing the screen.
- Search Clear Button: One-click clear icon added to search text field.
"""

v106_notes = """ElectroKit Version 1.0.6 Release Notes:
- Components Database Upgrade: Integrated 111 new components, upgrading total active database entries to 400.
"""

v105_notes = """ElectroKit Version 1.0.5 Release Notes:
- Home Screen Declutter: Removed "289" component count subtitle from Components DB card.
- References Removed: Deleted unused "References" chip from the horizontal categories list.
- Edge-to-Edge Layout Fix: Added statusBarsPadding to Home screen and rewrote Ohm's Law to use Scaffold/TopAppBar, completely resolving status bar overlapping issues.
"""

v104_notes = """ElectroKit Version 1.0.4 Release Notes:
- Back Confirmation: Quit dialog added to Home screen to prevent accidental exits.
- Unified Search & Persistence: Home page search directly carries query to Components, which retains the last search query and results.
- Performance Optimization: Pagination (lazy loading 20 items at a time) added to the Component Library list.
- Component Favoriting: Users can favorite components from Detail view and access them on the Favorites screen.
- Canvas Social Icons: High-fidelity custom Instagram and YouTube logos drawn using Compose Canvas.
- Ohm's Law Target Mode: Locks and auto-calculates target parameter (Voltage, Current, or Resistance) properly.
"""

v103_notes = """ElectroKit Version 1.0.3 Release Notes:
- Custom ElectroKit Launcher Icon: Completely removed default Android robot icon. Replaced with custom Hexagon PCB 'E' logo with Amber lightning bolt (⚡).
- Multi-density Launcher Icons: Full support for mdpi, hdpi, xhdpi, xxhdpi, xxxhdpi, and Android 8+ Adaptive Icons.
- Splash Screen Branding: Integrated high-resolution logo into Splash Screen with smooth scale animation.
"""

v102_notes = """ElectroKit Version 1.0.2 Release Notes:
- Redesigned 'Check for Updates' Material Design 3 card with compact layout and 'Check Now' button.
- Added clickable Instagram (@di7xu) and YouTube (@m.dilip07) social media buttons beside developer name (Dilip Kumar).
- Automated Gradle build task 'archiveVersionedApk' to auto-save versioned APKs to Build_Releases_APK/.
- Fixed share filename to 'ElectroKit_v1.0.2.apk'.
"""

v101_notes = """ElectroKit Version 1.0.1 Release Notes:
- Added Developer Profile Picture (profile.png) to Developer Info card.
- Updated APK sharing mechanism to share clean named 'ElectroKit_v1.0.1.apk' file via FileProvider.
- Fixed splash screen back navigation exit bug.
- Converted Room database layer to native crash-free SQLiteOpenHelper.
- Set targetSdk=34 for Samsung Android 12+ OS compatibility.
"""

v100_notes = """ElectroKit Version 1.0.0 Release Notes:
- Initial release of ElectroKit Offline Electronics Toolkit.
- Included 6 core calculators: Ohm's Law, LED Resistor, Resistor Color Code, SMD Code, Series/Parallel Math, Number System Converter.
- Offline 289 Component JSON dataset loaded into native SQLite database.
- 16-section Component Detail Screen.
"""

with open(os.path.join(releases_dir, 'CHANGELOG.md'), 'w', encoding='utf-8') as f:
    f.write(changelog_md)

with open(os.path.join(releases_dir, 'ElectroKit_v2.0.2_release_notes.txt'), 'w', encoding='utf-8') as f:
    f.write(v202_notes)

with open(os.path.join(releases_dir, 'ElectroKit_v2.0.1_release_notes.txt'), 'w', encoding='utf-8') as f:
    f.write(v201_notes)

with open(os.path.join(releases_dir, 'ElectroKit_v2.0.0_release_notes.txt'), 'w', encoding='utf-8') as f:
    f.write(v200_notes)

with open(os.path.join(releases_dir, 'ElectroKit_v1.0.7_release_notes.txt'), 'w', encoding='utf-8') as f:
    f.write(v107_notes)

with open(os.path.join(releases_dir, 'ElectroKit_v1.0.6_release_notes.txt'), 'w', encoding='utf-8') as f:
    f.write(v106_notes)

with open(os.path.join(releases_dir, 'ElectroKit_v1.0.5_release_notes.txt'), 'w', encoding='utf-8') as f:
    f.write(v105_notes)

with open(os.path.join(releases_dir, 'ElectroKit_v1.0.4_release_notes.txt'), 'w', encoding='utf-8') as f:
    f.write(v104_notes)

with open(os.path.join(releases_dir, 'ElectroKit_v1.0.3_release_notes.txt'), 'w', encoding='utf-8') as f:
    f.write(v103_notes)

with open(os.path.join(releases_dir, 'ElectroKit_v1.0.2_release_notes.txt'), 'w', encoding='utf-8') as f:
    f.write(v102_notes)

with open(os.path.join(releases_dir, 'ElectroKit_v1.0.1_release_notes.txt'), 'w', encoding='utf-8') as f:
    f.write(v101_notes)

with open(os.path.join(releases_dir, 'ElectroKit_v1.0.0_release_notes.txt'), 'w', encoding='utf-8') as f:
    f.write(v100_notes)

print('Changelog and Version Release Notes Saved Successfully in:', releases_dir)
