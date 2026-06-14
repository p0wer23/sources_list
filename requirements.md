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
- `SourceEntity(sourceId, url, title, bracket, seriousGroupId, priorityRank, isDone, createdAt, updatedAt)`
- `SeriousGroupEntity(groupId, name, normalizedName, groupPriorityRank, isBuiltIn, createdAt, updatedAt)`
- `BracketType`: `UNCLASSIFIED`, `CASUAL`, `SERIOUS`

Behavior:
- home shows only `Sources List`, `Unclassified`, `Casual`, `Serious`
- home has a visible `+`
- tapping `Unclassified` or `Casual` opens that bracket page
- tapping `Serious` opens a Serious-groups page
- new URLs save into `Unclassified`
- add screen validates blank/invalid URLs inline
- duplicate saves warn and stay on add screen until confirm/cancel
- bracket pages allow open, copy, move, delete
- Substack URLs open in the Substack Android app when installed, using `open.substack.com` app links
- `Casual` and `Serious` support active/completed, done, restore
- `Serious` has a built-in `Ungrouped` group and custom groups
- `Serious` groups support ranked `P1`/`P2`/`P3` priorities
- active `Casual` sources support ranked `P1`/`P2`/`P3` priorities
- active links inside each `Serious` group support ranked `P1`/`P2`/`P3` priorities
- priority actions use compact `P1`/`P2`/`P3` and `Clear` chips
- completed URLs cannot move to another bracket
- source actions are inline on the card, not in a popup menu
- deleting a custom Serious group moves its links to app-level `Unclassified` as active links
- moving a source into `Serious` requires selecting a Serious group
- active `Casual` lists and active links inside each `Serious` group are ordered by priority first, then `createdAt` ascending
- active `Casual` lists and active links inside each `Serious` group show a divider between priority items and the rest when both groups exist
- `Unclassified` and completed lists are ordered by `createdAt` ascending
- all data stays local in Room
