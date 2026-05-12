# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**Twix (Keep it luv / 키피럽)** is a couple's goal-tracking Android application. The app enables couples to set shared goals, track progress together, and maintain motivation through shared accountability.

**Tech Stack**: Kotlin, Jetpack Compose, MVI Architecture, Koin DI, Ktor Client, DataStore, CameraX

## Common Commands

### Build & Run
```bash
# Build the app
./gradlew assembleDebug

# Install and run on connected device
./gradlew installDebug

# Clean build
./gradlew clean
```

### Code Quality
```bash
# Run ktlint code formatting
./gradlew ktlintFormat

# Check code style (without fixing)
./gradlew ktlintCheck

# Run lint checks
./gradlew lint

# Run all verification checks (ktlint + lint)
./gradlew check
```

### Testing
```bash
# Run all unit tests
./gradlew test

# Run tests for specific module
./gradlew :feature:login:test

# Run instrumented tests on connected device
./gradlew connectedAndroidTest
```

### Module Tasks
```bash
# List all modules
./gradlew projects

# Run task for specific module
./gradlew :feature:login:assembleDebug
```

## Code Implementation Rules

> **All code implementation must be delegated to the `implementer` subagent.**
> Do not write code directly.

Implementation requests include:
- "구현해줘", "만들어줘", "추가해줘", "수정해줘", "변경해줘"
- "고쳐줘", "픽스해줘", "버그 수정"
- "리팩터링", "개선해줘"
- Any request that involves writing or modifying code

```
User: "로그인 버튼 클릭 시 로딩 상태 추가해줘"
       ↓
Claude: Task(subagent_type=implementer, ...)  ← Must follow this flow
       ↓
implementer implements following conventions
```

## Agent Roles

| Agent | When to Use |
|-------|-------------|
| `implementer` | All code writing/modification/refactoring tasks |
| `tester` | Writing test code |
| `committer` | Creating git commits |
| `pr-creator` | Creating Pull Requests |
| `performance-optimizer` | Performance optimization |
| `doveletter-reviewer` | Code review |

## Slash Commands

| Command | Action |
|---------|--------|
| `/impl` | Delegate implementation to implementer |
| `/plan-impl` | Plan → Approve → Implement |
| `/commit` | Delegate commit to committer |
| `/pr` | Delegate PR creation to pr-creator |

## Architecture Overview

### Multi-Module Structure

```
Twix/
├── app/                    # Application module (DI orchestration)
├── build-logic/            # Convention plugins for build configuration
├── core/                   # Shared infrastructure modules
│   ├── ui/                # BaseViewModel, LoadableState, MVI base classes
│   ├── navigation/        # AppNavHost, navigation implementation
│   ├── navigation-contract/  # NavRoutes, AppNavigator, NavGraphContributor interfaces
│   ├── network/           # Ktor client, API services
│   ├── result/            # AppResult<T>, AppError types
│   ├── design-system/     # UI components, theme
│   └── [other core modules]
├── domain/                 # Pure Kotlin business logic (no Android deps)
├── data/                   # Repository implementations
└── feature/                # UI layer (ViewModels + Composables)
    ├── login/
    ├── main/
    ├── goal-editor/
    ├── photolog/{capture,detail,editor}/
    └── [other features]
```

**Dependency Flow**: `feature → domain ← data`, all depend on `core/*`

### MVI Architecture

This project uses a **type-safe MVI pattern** via `BaseViewModel<State, Intent, SideEffect>`:

**Location**: `core/ui/src/main/java/com/twix/ui/base/BaseViewModel.kt`

**Key Components**:
- **State**: Immutable UI state exposed via `StateFlow`
- **Intent**: User actions dispatched sequentially via `dispatch(intent)`
- **SideEffect**: One-time events (navigation, toasts) consumed via `Flow`
- **reduce {}**: Immutable state updates
- **emitSideEffect()**: Trigger one-time UI events

**Example**:
```kotlin
// Define your contracts
data class MyUiState(val data: List<Item> = emptyList()) : State
sealed interface MyIntent : Intent {
    data object LoadData : MyIntent
}
sealed interface MySideEffect : SideEffect {
    data class ShowToast(val message: String) : MySideEffect
}

// ViewModel
class MyViewModel(
    private val repository: MyRepository
) : BaseViewModel<MyUiState, MyIntent, MySideEffect>(MyUiState()) {

    override fun handleIntent(intent: MyIntent) {
        when (intent) {
            is LoadData -> loadData()
        }
    }

    private fun loadData() {
        launchResult(
            block = { repository.fetchData() },
            onSuccess = { data -> reduce { copy(data = data) } },
            onError = { error -> emitSideEffect(ShowToast(error.message)) }
        )
    }
}

// UI
@Composable
fun MyScreen(viewModel: MyViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                is ShowToast -> { /* show toast */ }
            }
        }
    }

    Button(onClick = { viewModel.dispatch(MyIntent.LoadData) }) {
        Text("Load")
    }
}
```

### LoadableState Pattern

**Location**: `core/ui/src/main/java/com/twix/ui/base/LoadableState.kt`

**Optional interface** for automatic loading/error state management:

```kotlin
interface LoadableState : State {
    val isLoading: Boolean
    val error: AppError?

    fun copyLoadableState(
        isLoading: Boolean = this.isLoading,
        error: AppError? = this.error
    ): LoadableState
}
```

**When your UiState implements LoadableState**, `BaseViewModel.launchResult()` automatically:
1. Clears previous errors before API calls
2. Sets `isLoading = true` when starting
3. Updates `error` field on failure
4. Sets `isLoading = false` when complete

**Example Implementation**:
```kotlin
data class HomeUiState(
    val goalList: GoalList = GoalList(),
    override val isLoading: Boolean = false,
    override val error: AppError? = null,
) : LoadableState {
    // Computed properties for UI states
    val showLoading get() = isLoading && goalList.goals.isEmpty()
    val showError get() = error != null
    val showEmpty get() = goalList.goals.isEmpty() && !isLoading && error == null

    override fun copyLoadableState(isLoading: Boolean, error: AppError?) =
        copy(isLoading = isLoading, error = error)
}

// In ViewModel - no manual loading/error handling needed!
launchResult(
    block = { goalRepository.fetchGoalList(date) },
    onSuccess = { goalList -> reduce { copy(goalList = goalList) } }
    // isLoading and error are handled automatically!
)
```

**Error Handling Patterns**:
1. **Data Loading** (screen entry): LoadableState auto-updates, show error UI
2. **User Actions** (button clicks): Provide `onError` callback to show toast
3. **Background Refresh**: Omit `onError`, fail silently

### Navigation Architecture

**Pattern**: Each feature module contributes its navigation graph via `NavGraphContributor`

**Location**: `core/navigation-contract/src/main/java/com/twix/navigation/contract/NavGraphContributor.kt`

```kotlin
interface NavGraphContributor {
    val graphRoute: NavRoutes
    val startDestination: String
    val priority: Int get() = 100  // Lower = higher priority (Splash = 1)

    fun NavGraphBuilder.registerGraph(navController: NavHostController)
}
```

**How it works**:
1. Each feature module creates a `NavGraphContributor` object
2. Register it in the feature's Koin module with `named(NavRoutes.YourGraph.route)`
3. `AppNavHost` auto-discovers and registers all graphs by priority

**Type-Safe Routes** (`core/navigation/NavRoutes.kt`):
```kotlin
sealed class NavRoutes(val route: String) {
    object LoginGraph : NavRoutes("login_graph")
    object MainGraph : NavRoutes("main_graph")

    // Routes with arguments
    object PhotologDetailRoute : NavRoutes("photolog_detail/{goalId}/{date}") {
        fun createRoute(goalId: Long, date: LocalDate) =
            "photolog_detail/$goalId/$date"
    }
}
```

**Cross-Feature Navigation**:
- Use `AppNavigator` contract (`core/navigation-contract`) injected via Koin
- Never navigate directly between features
- Implementation lives in `AppNavHost`

### Network Layer

**Tech**: Ktor Client + Ktorfit (Retrofit-like interface generation)

**Location**: `core/network/src/main/java/com/twix/network/HttpClientProvider.kt`

**Key Features**:
1. **Automatic Bearer Token Refresh**: On 401, auto-calls `/auth/refresh` and retries original request
2. **Error Mapping**: All exceptions mapped to `AppResult<T>` via `safeApiCall {}`
3. **Kotlinx Serialization**: JSON parsing with `@Serializable`

**Service Interface Example**:
```kotlin
@Headers("Content-Type: application/json")
interface GoalService {
    @GET("/goals")
    suspend fun fetchGoals(@Query("date") date: String): GoalListResponse

    @POST("/goals")
    suspend fun createGoal(@Body request: CreateGoalRequest): GoalDetailResponse
}
```

**Repository Pattern**:
```kotlin
// Domain interface (pure Kotlin, in domain/)
interface GoalRepository {
    suspend fun fetchGoalList(date: String): AppResult<GoalList>
}

// Data implementation (in data/)
class DefaultGoalRepository(
    private val service: GoalService
) : GoalRepository {
    override suspend fun fetchGoalList(date: String): AppResult<GoalList> =
        safeApiCall { service.fetchGoals(date).toDomain() }
}
```

**Error Types** (`core/result/AppError.kt`):
```kotlin
sealed interface AppError {
    data class Http(val status: Int, val code: String?, val message: String?)
    data class Network(val cause: IOException?)
    data class Timeout(val cause: Throwable?)
    sealed interface Auth : AppError {
        data class Unauthorized(...)
        data class TokenExpired(...)
    }
}
```

### Dependency Injection (Koin)

**Module Organization**:
- Each feature module provides: `ViewModelOf(::YourViewModel)` + `NavGraphContributor`
- `app/di/InitKoin.kt` aggregates all modules
- Use `koinViewModel()` in Composables

**Example Feature Module**:
```kotlin
val settingsModule = module {
    single<NavGraphContributor>(named(NavRoutes.SettingsGraph.route)) {
        SettingsNavGraph
    }
    viewModelOf(::SettingsViewModel)
}
```

### Convention Plugins (build-logic/)

**DRY build configuration** via custom Gradle plugins:

**Available Plugins**:
- `twix.feature` - Composite plugin for feature modules (includes Compose, Koin, common deps)
- `twix.data` - For data layer modules
- `twix.android.library` - Base Android library configuration
- `twix.android.compose` - Compose configuration
- `twix.koin` - Koin dependencies
- `twix.kermit` - Logging dependencies

**Usage in Feature Modules**:
```kotlin
// feature/login/build.gradle.kts
plugins {
    alias(libs.plugins.twix.feature)  // Applies everything needed!
}
```

**FeatureConventionPlugin** automatically adds:
- Jetpack Compose
- Koin DI
- Dependencies on: core/ui, core/navigation, core/design-system, core/result, domain

## Module Dependency Rules

**Allowed**:
- `feature →` domain, core/*
- `data →` domain, core/network, core/result
- `domain →` core/result only
- `core/ui →` domain, core/result
- `core/navigation →` core/navigation-contract

**Forbidden**:
- ❌ Feature → Feature (use AppNavigator instead)
- ❌ Domain → Data
- ❌ Domain → Android framework (pure Kotlin)
- ❌ Core → Feature
- ❌ core/navigation-contract → anything (pure interfaces)

## Adding a New Feature

1. **Create module**: `feature/my-feature/`
2. **Apply plugin** in `build.gradle.kts`:
   ```kotlin
   plugins {
       alias(libs.plugins.twix.feature)
   }
   ```
3. **Define MVI contracts**:
   ```kotlin
   data class MyUiState(...) : State  // optionally: LoadableState
   sealed interface MyIntent : Intent
   sealed interface MySideEffect : SideEffect
   ```
4. **Create ViewModel** extending `BaseViewModel<MyUiState, MyIntent, MySideEffect>`
5. **Create NavGraphContributor**:
   ```kotlin
   object MyNavGraph : NavGraphContributor {
       override val graphRoute = NavRoutes.MyGraph
       override val startDestination = NavRoutes.MyRoute.route

       override fun NavGraphBuilder.registerGraph(navController: NavHostController) {
           navigation(route = graphRoute.route, startDestination = startDestination) {
               composable(NavRoutes.MyRoute.route) {
                   MyRoute(/* navigation callbacks */)
               }
           }
       }
   }
   ```
6. **Create Koin module**:
   ```kotlin
   val myFeatureModule = module {
       single<NavGraphContributor>(named(NavRoutes.MyGraph.route)) { MyNavGraph }
       viewModelOf(::MyViewModel)
   }
   ```
7. **Register in app/di/InitKoin.kt**: Add to `featureModules` list
8. **Add route** to `core/navigation/NavRoutes.kt`

## Code Style

- **Ktlint**: Auto-format with `./gradlew ktlintFormat` before committing
- **Immutability**: Use `data class` for State, `sealed interface` for Intent/SideEffect
- **Korean + English**: Comments/docs can be in Korean, code in English
- **Logging**: Use `logger` property in ViewModels (Kermit), not `Log.d()`

## Testing

- **Unit tests**: ViewModels, UseCases, Repositories
- **Test location**: `src/test/` in each module
- **Mocking**: Use test doubles or mocking library for repositories
- **Run**: `./gradlew test` (all modules) or `./gradlew :feature:login:test` (specific)

## Key Files Reference

| File | Purpose |
|------|---------|
| `core/ui/base/BaseViewModel.kt` | MVI foundation, launchResult helper |
| `core/ui/base/LoadableState.kt` | Auto loading/error state interface |
| `core/navigation/AppNavHost.kt` | Navigation orchestration |
| `core/navigation/NavRoutes.kt` | Type-safe route definitions |
| `core/network/HttpClientProvider.kt` | Ktor client with auto token refresh |
| `core/result/AppResult.kt` | Result wrapper for API calls |
| `core/result/AppError.kt` | Error type hierarchy |
| `app/di/InitKoin.kt` | DI module aggregation |
| `build-logic/convention/` | Build configuration conventions |

## Recent Work

The codebase recently migrated to the **LoadableState pattern** for automatic loading/error state management. Features like `home`, `notification`, and base feature modules now use this pattern to reduce boilerplate in ViewModels.

## Important Notes

- **Domain module is pure Kotlin** - no Android framework dependencies
- **Token refresh is automatic** - Ktor Auth plugin handles 401 responses
- **Navigation is modular** - features never depend on each other directly
- **Lazy AuthService** in network module - breaks circular dependency with HttpClient
- **Error handling** - all API calls return `AppResult<T>`, mapped by `safeApiCall {}`

---

## Claude Code AI 에이전트 활용 가이드

> 이 프로젝트는 Claude Code를 단순 코드 자동완성이 아니라,
> **컨벤션을 지키는 팀원**처럼 활용하기 위한 구조를 갖추고 있습니다.

### 핵심 아이디어

```
"내가 코드를 직접 작성하지 않아도,
 내가 정한 컨벤션과 아키텍처 규칙대로 코드가 만들어진다"
```

이를 위해 세 가지 레이어를 설계했습니다.

```
CLAUDE.md          ← "이 프로젝트에서 어떻게 행동해야 하는가"
     ↓
.claude/agents/    ← "각 역할의 전문가"
     ↓
.claude/commands/  ← "전문가를 호출하는 단축키"
```

---

### 1. CLAUDE.md — 라우팅 규칙

이 파일 자체가 가장 중요한 설정입니다.

Claude Code는 대화를 시작할 때 CLAUDE.md를 **자동으로 읽고 원칙으로 따릅니다**.
별도 지시 없이도 "모든 구현은 implementer 에이전트에게 위임하라"는 규칙이 적용됩니다.

**없으면 어떻게 되나?**
LLM이 간단한 요청은 직접 처리해버립니다. 그러면 아래 에이전트들에 정의한
컨벤션 규칙이 무시됩니다.

---

### 2. `.claude/agents/` — 역할별 전문 에이전트

각 파일은 **특정 역할에 최적화된 시스템 프롬프트**입니다.
Claude Code가 서브에이전트로 호출할 때 해당 파일의 지침만 보고 작업합니다.

```
.claude/agents/
├── implementer.md        # 코드 구현 전문 (아키텍처 원칙, 컨벤션 모두 포함)
├── tester.md             # 테스트 코드 전문 (JUnit5 + Turbine + MockK)
├── committer.md          # 커밋 메시지 전문 (이모지 + 한글 규칙)
├── pr-creator.md         # PR 생성 전문 (템플릿 기반)
├── performance-optimizer.md  # 성능 최적화 전문
└── doveletter-reviewer.md    # 코드 리뷰 전문
```

**핵심 포인트**: 에이전트를 분리하면 각자의 역할에만 집중할 수 있습니다.
implementer는 "올바르게 구현하는 것"만, committer는 "커밋 메시지 규칙"만 신경 씁니다.

**`implementer.md`에 정의된 것들 (일부):**
- MVI + Clean Architecture 레이어 규칙
- 매직 넘버 금지, else 금지, get/set 접두사 금지
- 클래스 50줄 / 메서드 15줄 초과 시 분리
- UseCase 생성 기준 (단순 Repository 위임이면 생성 금지)
- 브랜치 없으면 GitHub Issue → 브랜치 자동 생성 후 구현

---

### 3. `.claude/commands/` — 슬래시 커맨드

에이전트를 쉽게 호출하기 위한 단축키입니다.

```
/impl       → implementer 에이전트에게 구현 위임
/plan-impl  → Plan 에이전트로 계획 수립 → 승인 → implementer로 구현
/commit     → committer 에이전트에게 커밋 위임
/pr         → pr-creator 에이전트에게 PR 생성 위임
```

---

### 4. `.claude/settings.local.json` — 훅과 권한

**훅(Hooks)**: 특정 이벤트 발생 시 자동으로 실행되는 쉘 명령어입니다.
LLM이 아니라 **하네스가 직접 실행**합니다.

```json
"hooks": {
  "PostToolUse": {
    "matcher": "Edit|Write",  // .kt 파일 수정 후
    "command": "ktlintFormat" // 자동 포맷팅
  },
  "PreToolUse": {
    "matcher": "Bash(git commit)",  // 커밋 직전
    "command": "ktlintCheck"        // 스타일 검사
  }
}
```

ktlint 규칙을 "지켜줘"라고 프롬프트에 쓰는 것보다
**툴로 강제하는 게 훨씬 신뢰할 수 있습니다.**

---

### 5. `.claude/diary/` — 학습 루프

시행착오를 기록하고 에이전트를 개선하는 피드백 시스템입니다.

```
diary/
├── 2026-04-23-relative-time-refactoring.md
└── 2026-04-24-tester-agent-trigger-failure.md
```

작업 중 소통이 잘 안 맞았던 부분 → 일기 작성 → `implementer.md` 개선
→ 다음 작업에서는 같은 실수 반복 안 함

**실제 사례**: "UI Layer"라는 표현이 개발자는 `feature` 모듈 Composable을 의미했는데,
에이전트는 `core/ui` 모듈로 해석해서 4번 수정했던 경험 →
`implementer.md`에 용어 정의 추가 → 이후 동일 문제 없음

---

### 전체 워크플로우

```
"홈 화면에 스켈레톤 로딩 추가해줘"
          ↓
  CLAUDE.md 라우팅 규칙 적용
          ↓
  implementer 에이전트 호출
  (MVI 컨벤션, 레이어 규칙 자동 적용)
          ↓
  .kt 파일 수정 감지
          ↓ (훅 자동 실행)
  ktlintFormat 자동 포맷팅
          ↓
  "/commit 해줘"
          ↓
  committer 에이전트 호출
  (✨ Feat: 홈 화면 스켈레톤 로딩 추가)
          ↓
  "/pr 해줘"
          ↓
  pr-creator 에이전트 호출
  (PR 템플릿 기반 자동 작성)
```

---

### 스터디 적용 포인트

자신의 프로젝트에 동일한 구조를 만들고 싶다면:

1. **CLAUDE.md 작성** — 프로젝트 컨텍스트 + "모든 구현은 implementer에게"
2. **`implementer.md` 작성** — 본인 프로젝트의 컨벤션 규칙을 구체적으로 작성
3. **훅 설정** — 포맷터가 있다면 Edit/Write 후 자동 실행
4. **사용하면서 개선** — 에이전트가 실수할 때마다 에이전트 파일 업데이트

> 핵심은 **"한 번 규칙을 잘 정의하면, 이후엔 자연어로 요청만 해도 규칙이 지켜진다"**는 것입니다.
