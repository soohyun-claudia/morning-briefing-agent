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
| 이메일 | Gmail SMTP (JavaMailSender) |
| 스케줄링 | Spring `@Scheduled` (cron) |
| 뉴스 | NewsAPI (예정) |
| 배포 | AWS EC2 (예정) |
| CI/CD | GitHub Actions (예정) |

---

## 📁 프로젝트 구조

```
src/main/java/com/briefing
├── agent
│   ├── dto
│   │   └── CalendarEvent        # 캘린더 일정 DTO ✅
│   ├── BriefingAgent            # 전체 흐름 조율 (Orchestrator) ✅
│   ├── CalendarAgent            # 캘린더 수집 ✅
│   ├── NewsAgent                # 뉴스 수집 (예정)
│   └── DeliveryAgent            # 브리핑 전달 (예정)
├── service
│   ├── BriefingGeneratorService # Claude API 호출 ✅
│   └── MailService              # 이메일 발송 ✅
├── scheduler
│   └── BriefingScheduler        # 매일 05:30 cron 자동 실행 ✅
└── config
```

---

## 🗺 로드맵

### 1단계 — Single Agent 구축
- [x] Google Calendar API 연동
- [x] Claude API 연동 + 브리핑 생성
- [x] 이메일 자동 발송 (Gmail SMTP)
- [x] 매일 아침 cron 자동 실행 (05:30)
- [x] BriefingAgent Orchestrator 구현
- [ ] AWS EC2 배포
- [ ] NewsAPI 연동 + 뉴스 요약 (예정)

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

### 사전 준비
- Java 21
- Google Cloud Console 프로젝트 생성 + Calendar API 활성화
- Google OAuth 2.0 자격증명 발급 (`credentials.json`)
- Anthropic API 키 발급
- Gmail 앱 비밀번호 발급

### 환경 설정

`src/main/resources/application.yml` 생성 후 아래 내용 입력:

```yaml
spring:
  application:
    name: morning-briefing-agent
  mail:
    host: smtp.gmail.com
    port: 587
    username: YOUR_GMAIL
    password: YOUR_APP_PASSWORD
    default-encoding: UTF-8
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true

google:
  calendar:
    credentials-path: classpath:credentials.json
    tokens-path: tokens
    calendar-id: YOUR_CALENDAR_ID

anthropic:
  api-key: YOUR_CLAUDE_API_KEY

mail:
  to: YOUR_EMAIL
```

`src/main/resources/credentials.json` 에 Google OAuth 자격증명 파일 위치

### 실행

```bash
./gradlew bootRun
```

처음 실행 시 브라우저에서 Google 로그인 필요 (이후 자동 인증)

> 매일 05:30에 자동으로 브리핑 이메일이 발송됩니다.

---

## 📝 개발 기록

| 날짜 | 내용 | 상태 |
|---|---|---|
| 3/17 | 프로젝트 세팅 + Google Calendar 연동 | ✅ |
| 4/26 | Claude API 연동 + 브리핑 생성 | ✅ |
| 4/26 | Gmail SMTP 이메일 발송 구현 | ✅ |
| 4/26 | BriefingAgent + Scheduler 구현 | ✅ |
| 4/27~ | AWS EC2 배포 | 🔄 진행 중 |

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