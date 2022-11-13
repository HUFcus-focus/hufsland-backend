<p align="center"><img src="https://user-images.githubusercontent.com/61545957/198277755-ca09e2b5-8b53-45b0-ba01-1f68e45ab2a4.png" width="300px"></p>

<p align="center"><i>외대인을 위한 맞춤 졸업관리 서비스, <b>외딴섬</b></i></p>

## Getting Started

데이터베이스 연동을 위해 application.yml 혹은 application.properties 파일을 추가해주세요

다음과 같이 설정파일을 구성하여 데이터베이스를 연동해주세요
```
# DB 연동 소스
spring:
  datasource:
    driver-class-name: #연동할 DB 드라이버 소스 적용
    url: #연동할 DB URL 소스 적용
    username: #DB 이름
    password: #DB 비밀번호
    hikari:
      minimum-idle: 
      maximum-pool-size:  
```
```
# JPA 연동 소스
spring:
  jpa:
    hibernate:
      ddl-auto: #create update none
    properties:
      hibernate:
        format_sql: 
        show_sql: 
```

다음 명령어로 `http://localhost:8080`에 개발 환경을 실행할 수 있습니다


> 코드 컨벤션을 위해 `ESLint`와 `Prettier`, 커밋 메시지 컨벤션을 위해 `gitmoji-cli`를 적용해주세요!

## Contributions

개선사항 및 기타 의견은 `Issues` 탭에 등록해주세요! 협업 문의는 `hgene0921@gmail.com`으로 연락 바랍니다 :)
