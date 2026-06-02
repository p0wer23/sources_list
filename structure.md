# Structure

`app/`
- Gradle project root

`app/app/`
- Android app module

`app/app/src/main/java/com/example/sourceslist/`
- `MainActivity.kt`
- `data/`
- `ui/sources/`
- `ui/common/`
- `ui/theme/`

UI:
- `SourcesApp.kt` owns app navigation
- `AddSourceScreen.kt` handles add-source flow
- `SourceListScreen.kt` renders bracket items with inline actions, priority controls, and the priority/backlog divider
- `SourceViewModel.kt` exposes Room-backed state, actions, and priority updates
- `ExternalActions.kt` routes outbound links, including Substack app opens

Other:
- `app/app/schemas/` stores Room schema output for Room migrations
- `app/app/build/` is generated output
