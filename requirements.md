# Sources List Requirements

Tech stack:
- Kotlin
- Jetpack Compose + Material 3
- MVVM
- Room
- Navigation Compose
- Gradle + KSP
- same general setup as `../GOAL_app`

Core data:
- `SourceEntity`
- fields: `sourceId`, `url`, `title`, `bracket`, `isDone`, `createdAt`, `updatedAt`
- `BracketType`: `UNCLASSIFIED`, `CASUAL`, `SERIOUS`

Current UI flow:
- home shows only `Unclassified`, `Casual`, `Serious`
- home has a visible `+` action for add-source
- tapping a selector opens that bracket page
- home does not preview URLs inline
- `Casual` and `Serious` provide active/completed views

V1 behavior:
- new URLs save into `Unclassified`
- add screen validates blank/invalid URLs inline
- bracket pages allow open, copy, move, delete
- `Casual` and `Serious` allow mark-done and restore
- duplicate URL save should warn before adding again and keep add-screen context until confirmed or canceled
- all data stays local in Room
