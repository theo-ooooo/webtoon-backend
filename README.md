# Webtoon Backend

웹툰 열람 플랫폼의 백엔드 API 서버. 인증, 웹툰/에피소드 관리, 코인 결제, 평점, 인기 랭킹 등 플랫폼 운영에 필요한 핵심 기능을 제공한다.

---

## 기술 스택

| 구분 | 기술 |
|------|------|
| Language | Java 21 |
| Framework | Spring Boot 3.5, Spring Security |
| ORM | JPA / Hibernate, QueryDSL 5 |
| Database | MySQL |
| Authentication | JWT (jjwt 0.12) |
| Storage | AWS S3 |
| Build | Gradle |
| etc. | Lombok, Jakarta Validation |

---

## 주요 기능

- **인증/인가** -- 회원가입, 로그인, JWT 기반 인증, 역할별 접근 제어
- **웹툰 CRUD** -- 웹툰 등록/수정/삭제/조회, 장르 분류
- **에피소드 CRUD** -- 에피소드 등록/수정/삭제/조회, 웹툰별 에피소드 목록
- **장르 관리** -- 장르 생성/조회, 웹툰-장르 연결
- **코인 시스템** -- 코인 충전, 잔액 조회, 충전 내역
- **에피소드 구매** -- 코인을 사용한 유료 에피소드 구매, 구매 내역 조회
- **평점** -- 에피소드별 평점 등록/수정/조회
- **인기 랭킹** -- 조회수/평점 기반 웹툰 랭킹
- **열람 기록** -- 사용자별 에피소드 열람 기록 저장/조회
- **이벤트/공지** -- 이벤트 및 공지사항 CRUD
- **파일 업로드** -- AWS S3를 통한 썸네일/이미지 업로드

---

## API 도메인 구조

```
domain/
├── auth          # 인증 (로그인, 회원가입, JWT)
├── user          # 사용자 프로필
├── comic         # 웹툰
├── episode       # 에피소드
├── genre         # 장르
├── coin          # 코인 충전/관리
├── purchase      # 에피소드 구매
├── rating        # 평점
├── ranking       # 인기 랭킹
├── readhistory   # 열람 기록
├── event         # 이벤트
├── notice        # 공지사항
└── upload        # S3 파일 업로드
```

---

## 프로젝트 구조

```
src/main/java/com/webtoon/
├── WebtoonBackendApplication.java
├── domain/                     # 도메인별 패키지
│   └── {도메인}/
│       ├── controller/         # REST Controller
│       ├── dto/                # 요청/응답 DTO
│       ├── entity/             # JPA Entity
│       ├── repository/         # Repository (JPA + QueryDSL)
│       └── service/            # 비즈니스 로직
└── global/                     # 공통 모듈
    ├── config/                 # Spring 설정 (Security, S3, CORS 등)
    ├── security/               # JWT 필터, 인증 처리
    ├── exception/              # 전역 예외 처리
    ├── response/               # 공통 응답 포맷
    ├── enums/                  # 공통 Enum
    ├── batch/                  # 배치 작업
    └── init/                   # 초기 데이터 설정
```

---

## 실행 방법

### 사전 준비

- Java 21
- MySQL 실행 중 (기본: `localhost:3306`)
- `.env` 파일 생성 (`.env.example` 참고)

### 환경변수 설정

프로젝트 루트에 `.env` 파일을 생성한다.

```dotenv
DB_HOST=localhost
DB_PORT=3306
DB_NAME=webtoon
DB_USERNAME=root
DB_PASSWORD=

JWT_SECRET=              # 256비트 이상의 시크릿 키
JWT_EXPIRATION=86400000  # 토큰 만료 시간 (ms), 기본 24시간

AWS_S3_BUCKET=           # S3 버킷 이름
AWS_REGION=ap-northeast-2
AWS_PROFILE=default
```

### 서버 실행

```bash
./gradlew bootRun
```

### 테스트

```bash
./gradlew test
```

### QueryDSL Q클래스 생성

```bash
./gradlew compileJava
```

---

## 참고 문서

- `PLAN.md` -- 기획 문서 (전체 스펙, API, 페이지 구성)
- `docs/architecture.md` -- DB 스키마, API 응답 규격, ErrorCode
- `docs/backend.md` -- 패키지 구조, QueryDSL, Security, 코드 스타일
