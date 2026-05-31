# Sources List Requirements

Stack:
- Kotlin
- Jetpack Compose + Material 3
- MVVM
- Room
- Navigation Compose
- Gradle + KSP
- same general setup as `../GOAL_app`

Data:
- `SourceEntity(sourceId, url, title, bracket, isDone, createdAt, updatedAt)`
- `BracketType`: `UNCLASSIFIED`, `CASUAL`, `SERIOUS`

Behavior:
- home shows only `Sources List`, `Unclassified`, `Casual`, `Serious`
- home has a visible `+`
- tapping a selector opens that bracket page
- new URLs save into `Unclassified`
- add screen validates blank/invalid URLs inline
- duplicate saves warn and stay on add screen until confirm/cancel
- bracket pages allow open, copy, move, delete
- `Casual` and `Serious` support active/completed, done, restore
- lists are ordered by `createdAt` ascending
- all data stays local in Room
