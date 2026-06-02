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
- `SourceListScreen.kt` renders bracket items with inline actions
- `SourceViewModel.kt` exposes Room-backed state and actions
- `ExternalActions.kt` routes outbound links, including Substack app opens

Other:
- `app/app/schemas/` stores Room schema output
- `app/app/build/` is generated output
