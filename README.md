# Certi-Folio Backend

Certi-Folio의 백엔드 API 서버입니다.  
사용자의 이력/스펙 관리, 포트폴리오 초안 생성, 커뮤니티, 멘토링, 채팅, 알림, 채용 공고 캘린더 기능을 제공합니다.

## 주요 기능

- OAuth2 기반 소셜 로그인과 JWT 인증
- 자격증, 교육, 경력, 프로젝트, 활동 등 스펙 정보 관리
- Gemini API 기반 포트폴리오 초안 생성 및 수정
- 게시글/댓글/이미지 업로드를 포함한 커뮤니티 기능
- 멘토 신청, 멘토링 신청/승인/거절, 세션 관리
- 1:1 채팅과 그룹 채팅을 위한 WebSocket/STOMP 메시징
- 알림 조회, 읽음 처리, 삭제
- 채용 공고 가져오기 및 캘린더 조회

## 기술 스택

| 분류 | 기술 |
| --- | --- |
| Language | Java 21 |
| Framework | Spring Boot 4.0.0 |
| Build | Gradle Wrapper |
| Database | MySQL |
| ORM | Spring Data JPA, Hibernate |
| Security | Spring Security, OAuth2 Client, JWT |
| Realtime | WebSocket, STOMP, SockJS |
| API Docs | springdoc-openapi, Swagger UI |
| External | AWS S3, Gemini API |
| Utility | Lombok |

## 프로젝트 구조

```text
src/main/java/com/certifolio/server
├── ServerApplication.java
├── domain
│   ├── analytics      # 사용자 스펙 기반 분석
│   ├── community      # 게시글, 댓글, 게시글 이미지
│   ├── form           # 자격증, 교육, 경력, 프로젝트, 활동 등 스펙 관리
│   ├── groupchat      # 그룹 채팅방, 참여자, 메시지
│   ├── jobposting     # 채용 공고 가져오기 및 캘린더
│   ├── mentoring      # 멘토, 멘토링 신청, 세션, 1:1 채팅
│   ├── notification   # 알림 및 자격증 만료 알림 스케줄러
│   ├── portfolio      # 포트폴리오 초안 생성/수정/이미지
│   └── user           # 사용자, 온보딩, 프로필
└── global
    ├── apiPayload     # 공통 응답/예외 모델
    ├── common         # 공통 엔티티, 유틸, 외부 API 서비스
    ├── config         # JPA, Swagger, S3, Web, WebSocket 설정
    ├── dev            # 개발용 토큰 발급 API
    ├── jwt            # JWT 필터, 토큰 발급/검증, STOMP 인증
    ├── security       # Security/OAuth2 설정 및 핸들러
    └── service        # S3 업로드 서비스
```

## 시작하기

### 요구 사항

- JDK 21
- MySQL 8.x 권장
- Gradle은 별도 설치 없이 프로젝트의 Gradle Wrapper를 사용합니다.

### 환경 변수 및 시크릿 설정

`src/main/resources/application.yaml`은 `application-secret.yaml`을 선택적으로 불러옵니다.  
로컬에서는 프로젝트 루트 또는 `src/main/resources` 아래에 `application-secret.yaml`을 만들고 아래 값을 채워주세요.

```yaml
spring:
  datasource:
    password: YOUR_DB_PASSWORD
  security:
    oauth2:
      client:
        registration:
          google:
            client-id: YOUR_GOOGLE_CLIENT_ID
            client-secret: YOUR_GOOGLE_CLIENT_SECRET
          naver:
            client-id: YOUR_NAVER_CLIENT_ID
            client-secret: YOUR_NAVER_CLIENT_SECRET
          kakao:
            client-id: YOUR_KAKAO_CLIENT_ID
            client-secret: YOUR_KAKAO_CLIENT_SECRET

jwt:
  secret: YOUR_BASE64_ENCODED_JWT_SECRET

gemini:
  api:
    key: YOUR_GEMINI_API_KEY

cloud:
  aws:
    credentials:
      access-key: YOUR_AWS_ACCESS_KEY
      secret-key: YOUR_AWS_SECRET_KEY
```

환경 변수로 주입해도 됩니다.

```bash
export DB_PASSWORD=YOUR_DB_PASSWORD
export GEMINI_API_KEY=YOUR_GEMINI_API_KEY
```

> `application-secret.yaml`과 실제 인증 키는 커밋하지 마세요.

### 데이터베이스 준비

기본 datasource는 아래 MySQL을 사용합니다.

```text
URL      jdbc:mysql://localhost:3306/CertiFolio?useSSL=false&serverTimezone=Asia/Seoul&characterEncoding=UTF-8
Username root
Password ${DB_PASSWORD}
```

로컬 MySQL에 데이터베이스를 생성합니다.

```sql
CREATE DATABASE CertiFolio
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
```

JPA 설정은 `ddl-auto: update`입니다. 개발 환경에서는 애플리케이션 실행 시 엔티티 기준으로 테이블이 갱신됩니다.

## 실행

```bash
./gradlew bootRun
```

Windows에서는 아래 명령을 사용합니다.

```bash
gradlew.bat bootRun
```

서버 기본 포트는 `8080`입니다.

## 테스트 및 빌드

```bash
./gradlew test
./gradlew bootJar
```

Docker 이미지로 빌드할 수도 있습니다.

```bash
docker build -t certifolio-backend .
docker run -p 8080:8080 certifolio-backend
```

## API 문서

서버 실행 후 Swagger UI에서 API를 확인할 수 있습니다.

- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

JWT가 필요한 API는 `Authorization: Bearer <access-token>` 헤더를 사용합니다.

## 인증

- OAuth2 로그인 지원: Google, Naver, Kakao
- OAuth2 성공 시 서버가 JWT를 발급하고 프론트엔드 콜백 URI로 전달합니다.
- 기본 운영 콜백 설정은 `https://certifolio.shop/auth/callback`입니다.
- 로컬 프론트엔드와 연동할 때는 `app.oauth2.redirect-uri`와 각 OAuth2 provider의 `redirect-uri`를 로컬 주소로 오버라이드하세요.

개발 중에는 `/dev/token`으로 개발용 토큰을 발급할 수 있습니다. 이 엔드포인트는 보안 설정에서 permitAll로 열려 있습니다.

## 주요 REST API

| 도메인 | 메서드 | 경로 |
| --- | --- | --- |
| 사용자 | GET | `/api/users/me` |
| 사용자 온보딩 | POST | `/api/users/me/onboarding` |
| 사용자 수정 | PATCH | `/api/users/me` |
| 프로필 이미지 | POST | `/api/users/me/profile-image` |
| 전체 스펙 조회 | GET | `/api/specs/all` |
| 자격증 | GET/POST/PATCH/DELETE | `/api/specs/certificates` |
| 교육 | GET/POST/PATCH/DELETE | `/api/specs/educations` |
| 경력 | GET/POST/PATCH/DELETE | `/api/specs/careers` |
| 프로젝트 | GET/POST/PATCH/DELETE | `/api/specs/projects` |
| 활동 | GET/POST/PATCH/DELETE | `/api/specs/activities` |
| 분석 | GET/POST | `/api/analytics` |
| 분석 히스토리 | GET | `/api/analytics/history` |
| 포트폴리오 초안 생성 | POST | `/api/portfolio/draft/generate` |
| 최신 포트폴리오 초안 | GET | `/api/portfolio/draft/latest` |
| 포트폴리오 초안 수정/삭제 | PATCH/DELETE | `/api/portfolio/draft/{id}` |
| 포트폴리오 이미지 | POST | `/api/portfolio/draft/{id}/image` |
| 게시글 | GET/PATCH/DELETE | `/api/posts`, `/api/posts/{postId}` |
| 게시글 작성 | POST | `/api/posts/create` |
| 게시글 이미지 | POST | `/api/posts/images` |
| 댓글 작성 | POST | `/api/comments/create` |
| 댓글 수정/삭제 | PATCH/DELETE | `/api/comments/{commentId}` |
| 멘토 목록/상세 | GET | `/api/mentors`, `/api/mentors/{mentorId}` |
| 멘토 지원 | POST | `/api/mentors/apply` |
| 내 멘토 정보 | GET/PUT | `/api/mentors/me` |
| 관리자 멘토 목록 | GET | `/api/admin/mentors` |
| 관리자 멘토 승인/거절 | PATCH | `/api/admin/mentors/{mentorId}/approve`, `/api/admin/mentors/{mentorId}/reject` |
| 멘토링 신청 | POST/GET | `/api/mentoring-applications` |
| 멘토링 세션 | GET/POST/PATCH | `/api/mentoring/sessions` |
| 1:1 채팅방 | GET/POST | `/api/chat/rooms` |
| 1:1 채팅 메시지 | GET/POST | `/api/chat/rooms/{chatRoomId}/messages`, `/api/chat/rooms/{chatRoomId}/send` |
| 그룹 채팅방 | GET/POST | `/api/group-chat/rooms` |
| 내 그룹 채팅방 | GET | `/api/group-chat/rooms/my` |
| 그룹 채팅 참여/퇴장 | POST/DELETE | `/api/group-chat/rooms/{roomId}/join`, `/api/group-chat/rooms/{roomId}/leave` |
| 그룹 채팅 메시지 | GET/POST | `/api/group-chat/rooms/{roomId}/messages`, `/api/group-chat/rooms/{roomId}/send` |
| 알림 목록 | GET | `/api/notifications` |
| 최근 알림 | GET | `/api/notifications/recent` |
| 알림 읽음 처리 | PATCH | `/api/notifications/{id}/read`, `/api/notifications/read-all` |
| 알림 삭제 | DELETE | `/api/notifications/{id}`, `/api/notifications/all` |
| 채용 공고 가져오기 | POST | `/api/job-postings/import` |
| 채용 공고 캘린더 | GET | `/api/job-postings/calendar` |

## WebSocket

WebSocket/STOMP 연결 엔드포인트는 `/ws`이며 SockJS를 지원합니다.

```text
Endpoint                /ws
Application prefix      /app
Broker prefixes         /topic, /queue
```

1:1 채팅:

```text
SEND      /app/chat.send/{chatRoomId}
SEND      /app/chat.join/{chatRoomId}
SUBSCRIBE /topic/chat.{chatRoomId}
```

그룹 채팅:

```text
SEND      /app/group-chat.send/{roomId}
SEND      /app/group-chat.join/{roomId}
SUBSCRIBE /topic/group-chat.{roomId}
```

STOMP inbound 채널은 JWT 인증 인터셉터를 사용합니다.

## 공통 응답

API 응답은 `ApiResponse` 래퍼를 통해 일관된 형식으로 반환됩니다.  
비즈니스 예외는 `BusinessException`과 `GlobalExceptionHandler`에서 처리합니다.

## 관련 레포지토리

- Frontend: https://github.com/Jong0128/Certi-Folio-Front_v3.git

## 팀

| 이름 |
| --- |
| 임종훈 |
| 남윤성 |
