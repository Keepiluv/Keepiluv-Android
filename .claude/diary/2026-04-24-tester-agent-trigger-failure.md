# 2026-04-24: tester 에이전트 트리거 실패 - 한/영 키워드 문제

## 작업 내용
CertificationTime 도메인 모델의 테스트 코드 작성

## 문제 발생

### 사용자 요청
```
CertificationTime Test Code 추가
```

### 기대한 동작
tester 에이전트가 자동으로 활성화되어 테스트 코드 작성

### 실제 동작
- ❌ tester 에이전트 활성화 안됨
- Claude가 직접 테스트 코드 작성
- tester 에이전트 규칙 미준수:
  - Given-When-Then 주석 누락
  - 백틱 네이밍 규칙 부분적용
  - 명확한 assertion 부족

## 원인 분석

### tester.md의 description
```markdown
description: 테스트 코드를 작성하는 에이전트.
"테스트 작성해줘", "테스트 코드 만들어줘", "테스트해줘" 등의 요청 시 활성화.
```

**문제점:**
- ✅ 한글 키워드만 등록: "테스트 작성해줘"
- ❌ 영어 키워드 미등록: "Test Code", "Add Test"

### 매칭 실패
```
사용자: "CertificationTime Test Code 추가"
         ^^^^^^^^^^^^^^^^^ ^^^^^^^^^ ^^^
         (도메인)          (영어!)   (한글)

트리거: "테스트 작성해줘", "테스트 코드 만들어줘", "테스트해줘"
        ^^^^^^^^ (한글만)

→ 매칭 실패!
```

## 해결 방법

### tester.md description 수정

**Before:**
```markdown
description: 테스트 코드를 작성하는 에이전트.
"테스트 작성해줘", "테스트 코드 만들어줘", "테스트해줘" 등의 요청 시 활성화.
```

**After:**
```markdown
description: 테스트 코드를 작성하는 에이전트.
"테스트 작성해줘", "테스트 코드 만들어줘", "테스트해줘",
"Test 추가", "Test Code 추가", "Add Test" 등의 요청 시 활성화.
```

## 핵심 교훈

### 1. 에이전트 트리거 키워드는 한/영 모두 등록
한국어 프로젝트라도 개발자는 영어 용어를 자연스럽게 섞어 씀:
- "Test Code 추가" ✅ 자연스러움
- "테스트 코드 추가" ✅ 자연스러움
- 둘 다 지원해야 함!

### 2. 일반적인 영어 개발 용어 패턴
```
Test → 테스트
Code → 코드
Add → 추가
Create → 생성
Fix → 수정
Refactor → 리팩토링
```

### 3. description 작성 가이드라인
에이전트 description에는 다음을 모두 포함:
- ✅ 순수 한글: "테스트 작성해줘"
- ✅ 순수 영어: "Add Test"
- ✅ 한영 혼용: "Test Code 추가"

### 4. Claude의 판단 능력
Claude는 description의 키워드 매칭에 의존함:
- **자동 매칭**: description의 키워드와 직접 비교
- **의미 해석**: "Test Code" ≈ "테스트 코드" 자동 인식 안됨
- → **명시적 키워드 필요!**

## 후속 조치

### 1. 다른 에이전트도 확인 필요
- implementer.md
- committer.md
- pr-creator.md
- performance-optimizer.md

모두 한/영 혼용 패턴 확인 및 추가

### 2. 에이전트 description 작성 템플릿
```markdown
description: {에이전트 설명}.
"{한글 키워드1}", "{한글 키워드2}",
"{영어 키워드1}", "{한영 혼용 키워드1}" 등의 요청 시 활성화.
```

## 회고

처음에는 "왜 규칙을 안 지켰지?"라고 생각했지만,
실제로는 **에이전트가 활성화조차 안 된** 것이었다.

영어/한글을 자연스럽게 섞어 쓰는 한국 개발자의 습관을
에이전트 설정에 반영하지 못한 것이 원인이었다.

앞으로는 description 작성 시:
1. 한글 표현
2. 영어 표현
3. 한영 혼용 표현

모두 고려하여 작성해야겠다!

---

## 개선 완료 (2026-04-24)

### 모든 에이전트 description 업데이트 완료 ✅

사용자의 질문("변경해줘, 수정해줘 같은 부탁을 하면 implementer 에이전트가 작동하지 않을까?")을 계기로
전체 에이전트를 점검하고 한/영 혼용 패턴을 모두 추가했습니다.

#### 개선된 에이전트 목록

1. **tester.md**
   - 추가: "Test 추가", "Test Code 추가", "Add Test"

2. **implementer.md**
   - 추가: "개발해줘", "작성해줘", "추가해줘", "생성해줘", "변경해줘", "수정해줘"
   - 추가: "implement", "write code", "create", "develop", "add feature", "modify", "change"

3. **committer.md**
   - 추가: "커밋 작성해줘", "커밋 생성해줘", "git 커밋"
   - 추가: "commit", "make commit", "create commit"

4. **pr-creator.md**
   - 추가: "PR 생성해줘", "PR 올려줘", "풀리퀘 만들어줘", "풀리퀘스트"
   - 추가: "create PR", "create pull request", "make PR"

5. **performance-optimizer.md**
   - 추가: "성능 개선", "최적화", "메모리 최적화", "렌더링 최적화", "시작 시간 최적화", "프로파일링", "벤치마크"
   - 추가: "optimize performance", "reduce recomposition", "find memory leak", "profiling", "benchmark"

### 기대 효과

이제 다음과 같은 다양한 표현이 모두 작동합니다:

```
✅ "변경해줘" → implementer
✅ "수정해줘" → implementer
✅ "commit" → committer
✅ "PR 올려줘" → pr-creator
✅ "최적화" → performance-optimizer
✅ "Test Code 추가" → tester
```

---

**참고:** 이 경험을 바탕으로 모든 에이전트 description을
한/영 혼용 패턴으로 개선 완료!
