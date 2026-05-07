# 🎮 컴공서버 대시보드

> 순천향대학교 컴퓨터공학과 2학년 주민식이 개발한 Minecraft 서버 실시간 모니터링 대시보드

![대시보드 미리보기](https://i.imgur.com/placeholder.png)

## 📌 프로젝트 소개

친구들과 운영 중인 Minecraft Java Edition 서버(`minsic.feathermc.gg`)를 웹에서 편하게 모니터링하기 위해 만든 대시보드입니다.
서버 상태, 접속 중인 플레이어, 플레이어별 통계를 실시간으로 확인할 수 있습니다.

## ✨ 주요 기능

- ⚡ **서버 상태 모니터링** — 포트, 주소, 버전 실시간 표시
- 👥 **접속 중인 플레이어 목록** — RCON으로 실시간 조회
- 📊 **플레이어 통계** — 서버 로그 파싱으로 마지막 접속일, 총 플레이 시간, 접속 횟수 표시
- 🔄 **30초마다 자동 새로고침**

## 🛠 기술 스택

| 분류 | 기술 |
|------|------|
| Backend | Java 21, Spring Boot 3.5 |
| Frontend | Thymeleaf, HTML/CSS |
| 서버 연동 | RCON Protocol |
| 데이터 수집 | Minecraft 서버 로그 파싱 (.log, .log.gz) |
| 빌드 도구 | Gradle |

## 🖥 실행 방법

### 사전 준비
- Java 21 이상
- Minecraft 서버에서 RCON 활성화 필요

```properties
enable-rcon=true
rcon.port=25575
rcon.password=your_password
```

### 설정
`src/main/resources/application.properties` 수정:

```properties
spring.application.name=mcserver-dashboard
minecraft.log.path=서버_로그_폴더_경로
```

### 실행

```bash
./gradlew bootRun
```

브라우저에서 `http://localhost:8080` 접속

## 📁 프로젝트 구조
src/main/java/com/minsic/mcserver_dashboard/
├── McserverDashboardApplication.java  # 메인 클래스
├── HomeController.java                # 라우팅
├── RconService.java                   # RCON 서버 연동
├── LogParserService.java              # 로그 파싱
└── PlayerStats.java                   # 플레이어 데이터 모델

## 👨‍💻 개발자

**주민식** — 순천향대학교 컴퓨터공학과 2학년  
GitHub: [@m1ns1c](https://github.com/m1ns1c)