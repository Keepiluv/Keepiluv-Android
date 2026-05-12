---
name: commit
description: 변경사항을 커밋합니다
---

committer 에이전트를 사용하여 변경사항을 커밋합니다.

Task tool을 사용하여 subagent_type=committer로 에이전트를 호출하세요.

에이전트가 자동으로:
1. git status, git diff로 변경사항 분석
2. git log로 기존 커밋 패턴 확인
3. 프로젝트 커밋 규칙에 맞는 메시지 작성
4. 커밋 생성

커밋 규칙:
- {emoji} {Type}: {제목} 형식
- 한 줄만 작성 (본문 없음)
- Claude Code 서명 금지
- 한글로 작성
