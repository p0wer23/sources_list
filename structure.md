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
- `SeriousGroupsScreen.kt` renders Serious group cards, add/rename dialogs, and group-priority actions
- `SourceListScreen.kt` renders source cards, Serious-group move picking, link priorities, and the priority/backlog divider
- `SourceViewModel.kt` exposes Room-backed source and Serious-group state/actions
- `ExternalActions.kt` routes outbound links, including Substack app opens

Other:
- `app/app/schemas/` stores Room schema output for Room migrations
- Room migrations preserve existing links and assign legacy `Serious` links to `Ungrouped`
- `app/app/build/` is generated output
