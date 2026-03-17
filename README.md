# 🤖 Morning Briefing Agent

> 매일 아침, AI가 하루를 브리핑해주는 자동화 Agent 서비스

---

## 📌 프로젝트 소개

단일 Agent로 시작해 Multi-Agent 시스템으로 확장해가는 AI 자동화 프로젝트입니다.
Google Calendar 일정과 뉴스를 수집해 매일 아침 브리핑을 전달하는 서비스를 시작점으로,
Agent 설계 패턴과 AI 개발 워크플로우를 직접 연구합니다.

---

## 🛠 기술 스택

| 영역 | 기술 |
|---|---|
| 백엔드 | Spring Boot 3.5.1 / Java 21 |
| AI | Claude API (Anthropic) |
| 캘린더 | Google Calendar API |
| 뉴스 | NewsAPI |
| 이메일 | Gmail SMTP (JavaMailSender) |
| 스케줄링 | Spring `@Scheduled` (cron) |
| 배포 | AWS EC2 (예정) |
| CI/CD | GitHub Actions (예정) |

---

## 📁 프로젝트 구조
```
src/main/java/com/briefing
├── agent
│   ├── BriefingAgent        # 전체 흐름 조율 (Orchestrator)
│   ├── CalendarAgent        # 캘린더 수집
│   ├── NewsAgent            # 뉴스 수집
│   └── DeliveryAgent        # 브리핑 전달
├── service
│   ├── ClaudeService        # Claude API 호출
│   └── MailService
├── client                   # 외부 API 호출
├── scheduler                # cron 실행
└── config
```

---

## 🗺 로드맵

### 1단계 — Single Agent 구축
- [ ] Google Calendar API 연동
- [ ] NewsAPI 연동 + 뉴스 요약
- [ ] Claude API 연동 + 브리핑 생성
- [ ] 이메일 자동 발송
- [ ] 매일 아침 cron 자동 실행

### 2단계 — Multi-Agent 확장
- [ ] Agent 역할별 분리
- [ ] Agent 간 메시지 전달 구조 설계
- [ ] CI/CD 구축 (GitHub Actions + AWS EC2)

### 3단계 — AI 개발 워크플로우 연구
- [ ] Agent Orchestration 패턴 적용
- [ ] 투자 시스템 Agent 확장
- [ ] 블로그 / 링크드인 시리즈 포스팅

---

## 🚀 실행 방법

### 환경 변수 설정
```yaml
# application.yml
anthropic:
  api-key: YOUR_CLAUDE_API_KEY

google:
  calendar:
    credentials-path: /path/to/credentials.json

news:
  api-key: YOUR_NEWS_API_KEY

spring:
  mail:
    username: YOUR_GMAIL
    password: YOUR_APP_PASSWORD
```

### 실행
```bash
./gradlew bootRun
```

---

## 📝 개발 기록

| 주차 | 내용 |
|---|---|
| 1주차 | 프로젝트 세팅 + Google Calendar 브리핑 |
| 2주차 | 뉴스 수집 + 요약 추가 |
| 3주차 | 브리핑 품질 고도화 |
| 4주차 | 전달 채널 확장 + 1단계 마무리 |

---

## 🌿 브랜치 전략

혼자 작업하는 개인 프로젝트로, 단순하게 유지합니다.

### 브랜치 구조
```
main        ← 완성된 버전만 (언제든 보여줄 수 있는 상태)
develop     ← 매일 작업하는 브랜치
feature/*   ← 기능이 크거나 실험적인 작업일 때만
```

### 작업 흐름
```
develop에서 매일 작업 + 커밋
       ↓
기능 하나 완성되면 main에 merge
```

### feature 브랜치 예시
```
develop
├── feature/claude-api
├── feature/email-delivery
└── feature/news-api
```