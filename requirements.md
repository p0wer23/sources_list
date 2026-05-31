# Sources List App - Requirements

## 1. Overview
This app is a personal Android app for storing and organizing reading and viewing sources. Each source is a link. The app groups every saved link into one of three brackets:
- `Unclassified`
- `Casual`
- `Serious`

The app should use the same stack and tooling as `../GOAL_app`.

## 2. Tech Stack and Tooling
Mirror the implementation approach used in `../GOAL_app`:
- Language: Kotlin
- UI: Jetpack Compose with Material 3
- Architecture: MVVM
- Local persistence: Room database
- Navigation: Navigation Compose
- Build system: Gradle
- Annotation processing: KSP
- Android Gradle Plugin: same family as `../GOAL_app`

Reference implementation observed in `../GOAL_app`:
- Kotlin Compose plugin enabled
- Room schema export configured through KSP
- AndroidX-based project
- Version catalog managed with `gradle/libs.versions.toml`

Use latest stable versions instead of copying the older pinned versions from `../GOAL_app`.

Current stable baseline confirmed on May 31, 2026:
- AGP: `9.2.0`
- Gradle: `9.4.1`
- JDK: `17`
- Kotlin: `2.3.21`
- KSP: `2.3.9`
- Compose BOM: `2026.05.00`
- Navigation Compose: `2.9.8`
- Room: `2.8.4`
- Activity Compose: `1.13.0`
- Lifecycle ViewModel Compose: `2.10.0`

Versioning guidance:
- Use the Compose BOM instead of manually pinning individual Compose UI and Material 3 versions.
- Re-check versions at implementation start if setup happens later, because these dependencies change frequently.

## 3. Target User
- Single personal user
- Uses the app to manage blogs, articles, videos, and similar content links

## 4. Core Entities

### 4.1 Source
A source record should contain at least:
- unique id
- link URL
- bracket/status
- title or label
  - optional in V1
  - if title is absent, show the URL in the UI
- created timestamp
- updated timestamp
- completion state

## 5. Brackets

### 5.1 Unclassified
Purpose:
- temporary inbox for newly added links

Allowed actions:
- copy link
- open link
- delete link
- move to `Casual`
- move to `Serious`

### 5.2 Casual
Purpose:
- content that can be consumed whenever convenient

Allowed actions:
- copy link
- open link
- delete link
- move to `Unclassified` if needed
- move to `Serious`
- mark as done

### 5.3 Serious
Purpose:
- content that requires dedicated focus and time

Allowed actions:
- copy link
- open link
- delete link
- move to `Unclassified` if needed
- move to `Casual`
- mark as done

## 6. Functional Requirements

### 6.1 Add Source
- User must be able to add a new source link into the app.
- By default, a newly added source should go into `Unclassified`.
- The add flow should require the URL.
- The add flow should optionally allow a custom title/label in V1.
- If no title is provided, the app should display the raw URL.

### 6.2 View Sources by Bracket
- User must land on a home screen that presents three selectors:
  - `Unclassified`
  - `Casual`
  - `Serious`
- Tapping a selector must navigate to a dedicated page for that bracket.
- The home screen must not expand or preview the URLs inline under those selectors.
- Each bracket page must show the list for that bracket only.
- Each list item should clearly show enough information to identify the source.
- The bracket of a source must be visually clear.

### 6.3 Move Source Between Brackets
- User must be able to move a source from one bracket to another.
- Movement must update the stored state immediately.
- The source must disappear from the old bracket and appear in the new bracket.

### 6.4 Copy Link
- User must be able to copy any stored link to the clipboard from any bracket.

### 6.5 Open Link
- User must be able to open any stored link using the appropriate external app or browser.
- YouTube links should open in the YouTube app when available.
- Other web links should open in Chrome.

### 6.6 Delete Source
- User must be able to delete a source from any bracket.
- Deletion should remove the source from storage.
- A confirmation step is recommended to prevent accidental deletion.

### 6.7 Mark as Done
- User must be able to mark sources in `Casual` and `Serious` as done.
- Done items should be clearly distinguishable from active items.

### 6.8 Done Item Handling
- Done items from `Casual` must appear in a completed view scoped to `Casual`.
- Done items from `Serious` must appear in a completed view scoped to `Serious`.
- The `Casual` bracket page must provide an option to show only read URLs.
- The `Serious` bracket page must provide an option to show only read URLs.
- Active and completed items should not be mixed in the default list view.
- Keep the data model simple by storing a boolean completion flag and bracket value.

### 6.9 Duplicate Detection
- The app should warn the user when they try to save a duplicate URL.
- Warning is required, but V1 does not have to hard-block the save unless that is chosen later.
- Duplicate comparison should be based on the stored URL string in V1.

## 7. UX Requirements
- The existing app name/header can remain as-is.
- The home screen should expose a clear `+` action for adding a URL.
- Saving a link should be quick and require few taps.
- Moving a link should be simple from the list item itself.
- Copy and delete actions should be easy to access.
- The app should feel lightweight and focused, not overloaded with options.
- The UI should prioritize clarity over decoration.

## 8. Data and Persistence Requirements
- All data should be stored locally on device in Room.
- The app should work without requiring login or internet-based account setup.
- Data should persist across app restarts.

## 9. Suggested Information Architecture
- Home screen with three bracket selections:
  - `Unclassified`
  - `Casual`
  - `Serious`
- The home screen should act as a navigation hub only.
- A prominent `+` action on the home screen should open the add-source flow.
- Selecting a bracket should navigate to a separate bracket page.
- When the user is inside one bracket view, the app should expose a left navigation drawer for moving between views.
- Separate add-source flow
- Each of `Casual` and `Serious` should also provide access to its own completed/read-only view.
- Simple source item actions:
  - open
  - copy
  - move
  - delete
  - mark done where applicable

## 10. Suggested MVVM Structure
- `data/`
  - Room database
  - entity
  - dao
- `ui/unclassified/`
- `ui/casual/`
- `ui/serious/`
- `ui/common/`
- `ui/theme/`

Recommended entities and enums:
- `SourceEntity`
- `BracketType` with `UNCLASSIFIED`, `CASUAL`, `SERIOUS`

Recommended fields for `SourceEntity`:
- `sourceId`
- `url`
- `title`
- `bracket`
- `isDone`
- `createdAt`
- `updatedAt`

Recommended supporting queries:
- active sources by bracket
- completed sources by bracket
- duplicate lookup by URL

## 11. Non-Functional Requirements
- App should remain usable with a growing list of links.
- UI state should update reactively when data changes.
- Code should stay simple and maintainable for a beginner-friendly Android project.

## 12. Out of Scope for V1
- user accounts
- cloud sync
- tags
- folders beyond the three brackets
- AI summarization
- recommendations
- reminders and scheduling
- rich note-taking

## 13. Open Product Decisions
- Whether `Unclassified` should allow mark-as-done
  - recommended answer: no, because it is an inbox bucket rather than a consumption state
- Whether duplicate warning should still allow the user to save after confirmation
- Whether URL normalization should be introduced later for stronger duplicate detection

## 14. Summary of MVP
The MVP is an Android app where a user can:
- save a link
- see it in `Unclassified`
- move it to `Casual` or `Serious`
- copy it
- open it in Chrome or YouTube
- delete it
- mark `Casual` and `Serious` items as done
- view completed `Casual` and completed `Serious` items separately
- get warned about duplicate URLs

This should be built with the same core Android stack already used in `../GOAL_app`.
