---
name: pr
description: Pull Request를 생성합니다
---

pr-creator 에이전트를 사용하여 Pull Request를 생성합니다.

Task tool을 사용하여 subagent_type=pr-creator로 에이전트를 호출하세요.

에이전트가 자동으로:
1. 현재 브랜치 상태 확인
2. base 브랜치 대비 변경사항 분석
3. 커밋 메시지 패턴 분석
4. .github/PULL_REQUEST_TEMPLATE.md 템플릿에 맞춰 PR 본문 작성
5. gh pr create로 PR 생성

PR 생성 규칙:
- 제목에 이모지 금지 (✨, ♻️ 등)
- 간결하고 명확한 제목
- 템플릿 형식 준수
- Claude Code 서명 금지
- base 브랜치 기본값: develop
