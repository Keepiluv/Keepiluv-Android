# 2026-04-23: RelativeTime 리팩토링 - Clean Architecture 적용기

## 작업 내용
RelativeTimeFormatter를 Clean Architecture 원칙에 맞게 리팩토링

### 초기 요구사항
- RelativeTimeFormatter의 시간 표시 로직 변경
  - 0~10분: "방금 전"
  - 11~59분: 분 단위
  - 1~23시간: 시간 단위 (80분도 1시간 전)
  - 24시간+: 일 단위
- 다국어 지원을 위한 strings.xml 분리

## 소통 과정에서의 시행착오

### 1차 시도: Context를 Singleton에 전달 ❌
**문제점:**
- `RelativeTimeFormatter` object에 Context를 파라미터로 전달
- 메모리 누수 위험

**배운 점:**
> Singleton 객체에 Context를 전달하지 말 것!

### 2차 시도: util 모듈에 계산 로직 ❌
**문제점:**
- `core/util` 모듈에 `RelativeTimeCalculator` 구현
- 비즈니스 로직이 util에 위치

**사용자 피드백:**
> "domain layer에 해당하는 domain 모듈에 만드는 걸 원한 거야"

**배운 점:**
- util 모듈: 범용 유틸리티만
- domain 모듈: 비즈니스 로직

### 3차 시도: 팩토리 메서드 패턴 적용 ✅
**사용자 피드백:**
> "계산 로직은 RelativeTime의 팩토리 메서드로 구현"

**개선:**
```kotlin
// Before
object RelativeTimeCalculator {
    fun calculate(uploadedAt: String): RelativeTime?
}

// After
sealed class RelativeTime {
    companion object {
        fun from(uploadedAt: String): RelativeTime
    }
}
```

### 4차 시도: core/ui 모듈에 포맷팅 extension ❌
**큰 오해 발생!**

**나의 이해:**
- "UI의 책임" = `core/ui` 모듈
- Extension 함수로 `RelativeTime.format()` 구현

**사용자의 진짜 의도:**
> "ui 모듈이 아니라 Ui layer를 말한 거였어"
> "PhotologCardContent에서 포맷팅 하는걸 부탁한거야"

**배운 점:**
- **"UI Layer"** = `feature` 모듈의 실제 Composable 컴포넌트
- **"UI 모듈"** = `core/ui` 모듈 (공통 유틸리티)
- 용어 구분이 중요!

### 최종 해결: 실제 UI 컴포넌트에서 포맷팅 ✅

**최종 구조:**
```kotlin
// Domain (domain 모듈) - 비즈니스 로직
sealed class RelativeTime {
    companion object {
        fun from(uploadedAt: String): RelativeTime
    }
}

// UI Layer (feature 모듈) - 포맷팅
@Composable
private fun formatRelativeTime(relativeTime: RelativeTime?): String =
    when (relativeTime) {
        is RelativeTime.JustNow -> stringResource(R.string.time_just_now)
        is RelativeTime.Minutes -> stringResource(R.string.time_minutes_ago, relativeTime.value)
        // ...
    }
```

## 핵심 교훈

### 1. 레이어별 책임 분리
- **Domain**: 비즈니스 로직 (시간 계산)
- **UI Layer**: 표현 로직 (포맷팅)
- Context는 Composable에서만 안전하게 사용

### 2. 팩토리 메서드 패턴
- Sealed class의 companion object에 `from()` 구현
- 깔끔한 API: `RelativeTime.from(uploadedAt)`

### 3. 용어의 명확성
- "모듈"과 "레이어"는 다르다
- 소통 시 구체적으로 명시할 것

### 4. 메모리 안전성
- Singleton에 Context 전달 금지
- Composable 스코프에서만 Context 사용

## 개선 사항

### implementer.md 에이전트 문서 업데이트
새로운 섹션 추가:
- 레이어별 책임 명확화
- 용어 명확화 (UI Layer vs UI 모듈)
- 절대 금지 사항 명시

## 회고
처음에는 서로 다른 해석으로 여러 번 수정했지만,
각 시행착오를 통해 Clean Architecture의 본질과
정확한 용어 사용의 중요성을 배웠다.

특히 "UI의 책임"이라는 표현이
- 나: core/ui 모듈
- 사용자: feature의 실제 Composable

로 다르게 해석되었던 점이 재미있었다 ㅋㅋㅋ

앞으로는 이런 용어 혼동이 없도록
구체적인 모듈명과 파일명으로 소통하면 좋을 것 같다.
