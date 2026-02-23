#  Community Board

> 이스트소프트 백엔드 과정 **2차 팀 프로젝트**
> Spring Framework 기반 커뮤니티 게시판 웹 애플리케이션

---

## 👥 팀 구성

| 역할 | 이름 |
|------|------|
| 팀장 | 정확 🌟 |
| 팀원 | 김재웅 |
| 팀원 | 박민성 |
| 팀원 | 최서윤 |
| 팀원 | 김정인 |

---

## 🛠 기술 스택

| 분류 | 기술 |
|------|------|
| Language | Java |
| Framework | Spring MVC, Spring Security |
| Database | H2 |
| ORM | JPA / Hibernate |
| Build | Gradle |
| Template Engine | Thymeleaf |
| 암호화 | BCryptPasswordEncoder |
| 이메일 | JavaMailSender |
| 소셜 로그인 | OAuth2 (Google) |

---

##  프로젝트 목표

커뮤니티 게시판 웹 애플리케이션의 **도메인 모델링, 데이터베이스 설계, 기능 구현**을 목표로 합니다.

- 요구사항 명세 및 엔티티 간 관계 정의
- Spring Security 기반 인증/인가
- 게시판 / 게시글 / 댓글 / 쪽지 / 마이페이지 / 관리자 기능 구현

---

##  프로젝트 구조

```
com.example.community
├── controller
│   ├── MainController.java          # 메인 페이지
│   ├── UserController.java          # 회원가입 / 로그인
│   ├── UserApiController.java       # 비밀번호 재설정 API
│   ├── BoardController.java         # 게시판 CRUD
│   ├── PostController.java          # 게시글 CRUD + 좋아요
│   ├── CommentController.java       # 댓글 REST API
│   ├── MessageController.java       # 쪽지함
│   ├── MypageController.java        # 마이페이지
│   ├── AdminController.java         # 관리자
│   └── EmailController.java         # 이메일 인증
│
├── service
│   ├── UserService / UserServiceImpl
│   ├── BoardService / BoardServiceImpl
│   ├── PostService / PostServiceImpl
│   ├── CommentService / CommentServiceImpl
│   ├── MessageService / MessageServiceImpl
│   ├── MypageService / MypageServiceImpl
│   ├── AdminService / AdminServiceImpl
│   └── EmailService
│
├── domain
│   ├── user
│   │   ├── UserEntity.java
│   │   ├── UserDto.java
│   │   └── UserRole.java            (USER, ADMIN)
│   ├── board
│   │   ├── BoardEntity.java
│   │   └── BoardDto.java
│   ├── post
│   │   ├── PostEntity.java
│   │   ├── PostDto.java
│   │   ├── PostLikeEntity.java
│   │   ├── Criteria.java
│   │   └── ResultDto.java
│   ├── comment
│   │   ├── CommentEntity.java
│   │   └── CommentDto.java
│   └── message
│       ├── MessageEntity.java
│       └── MessageDto.java
│
└── security
    ├── SecurityConfiguration.java
    ├── CustomUserDetails.java
    ├── CustomUserDetailsService.java
    ├── CustomOAuth2UserService.java
    └── PasswordEncoderConfig.java
```

---

##  ERD (엔티티 관계)

```
users (회원)
 ├── user_id (PK)
 ├── username          UNIQUE | 4~50자 | 영문/숫자/_
 ├── password          BCrypt 암호화
 ├── name
 ├── nickname          UNIQUE
 ├── email             UNIQUE
 ├── email_verified    이메일 인증 여부
 ├── role              USER / ADMIN
 ├── active            soft delete (탈퇴 시 false)
 ├── created_at
 └── updated_at

boards (게시판)
 ├── id (PK)
 ├── title             UNIQUE | 2~50자
 ├── category          NOTICE / FREE / 기타
 ├── created_at
 └── updated_at

posts (게시글)
 ├── id (PK)
 ├── user_id           FK → users
 ├── board_id          FK → boards
 ├── post_title        최대 100자
 ├── content           TEXT
 ├── view_count
 ├── comments_count
 ├── likes_count
 ├── post_type         공지 / 일반
 ├── fixed             0: 일반 / 1: 고정글
 ├── created_at
 └── updated_at

comments (댓글)
 ├── id (PK)
 ├── user_id           FK → users
 ├── post_id           FK → posts, CASCADE DELETE
 ├── r_content         1~200자
 ├── created_at
 └── updated_at

post_likes (게시글 좋아요)
 ├── id (PK)
 ├── post_id           FK → posts
 ├── user_id           FK → users
 ├── created_at
 └── UNIQUE (post_id, user_id)  중복 좋아요 방지

messages (쪽지)
 ├── id (PK)
 ├── sender_id             FK → users (EAGER)
 ├── receiver_id           FK → users (EAGER)
 ├── m_title               1~50자
 ├── content               TEXT, 1~1000자
 ├── is_read               0: 미읽음 / 1: 읽음
 ├── readed_at
 ├── sender_delete_state   0: 유지 / 1: 휴지통 / 2: 영구삭제
 ├── receiver_delete_state 0: 유지 / 1: 휴지통 / 2: 영구삭제
 └── created_at
```

>  쪽지는 발신자·수신자 양쪽 모두 삭제 상태가 2일 때만 DB에서 물리 삭제됩니다.

---

##  보안 및 인증

### 인증 방식
- **일반 로그인**: `username` + `password` (Spring Security Form Login)
- **소셜 로그인**: Google OAuth2 (`CustomOAuth2UserService`)
  - 최초 로그인 시 자동 회원가입 (`google_{googleId}`)
  - 이미 등록된 이메일이면 기존 계정으로 로그인
- **비밀번호 암호화**: `BCryptPasswordEncoder`
- **인증 객체**: `CustomUserDetails` (UserDetails + OAuth2User 동시 구현)

### 권한 정책 (Security Filter Chain)

| 접근 범위 | 허용 대상 |
|-----------|-----------|
| `/`, `/login`, `/signup`, `/findpassword` | 모두 허용 |
| `/api/email/**`, `/api/user/reset-password` | 모두 허용 |
| `/board/**` GET | 모두 허용 |
| `/board/**` POST/PUT/DELETE | USER, ADMIN |
| `/api/posts/*/comments` GET | 모두 허용 |
| `/api/posts/*/comments` POST/PUT/DELETE | USER, ADMIN |
| `/mypage/**` | USER, ADMIN |
| `/admin/**` | ADMIN 전용 |
| `/h2-console/**`, `/static/**` | Security 제외 |

### 권한 역할 비교

| 기능 | USER | ADMIN |
|------|:----:|:-----:|
| 게시글 읽기 / 댓글 보기 | ✅ | ✅ |
| 게시글 / 댓글 작성 | ✅ | ✅ |
| 본인 게시글 / 댓글 수정·삭제 | ✅ | ✅ |
| 타인 게시글 / 댓글 삭제 | ❌ | ✅ |
| 공지사항(NOTICE) 작성 | ❌ | ✅ |
| 쪽지 발송 / 수신 | ✅ | ✅ |
| 유저 권한 변경 / 정지 / 활성화 | ❌ | ✅ |
| 게시판 생성 / 수정 / 삭제 | ❌ | ✅ |

---

##  주요 기능 및 API

### 메인 / 회원

| Method | URL | 설명 |
|--------|-----|------|
| GET | `/` | 메인 페이지 (공지, 게시글 목록, 인기글 TOP 10) |
| GET | `/login` | 로그인 페이지 (일반 + Google OAuth2) |
| GET | `/signup` | 회원가입 페이지 |
| POST | `/signup` | 회원가입 처리 |
| GET | `/findpassword` | 비밀번호 찾기 |
| GET | `/check-username` | 아이디 중복 체크 |
| GET | `/check-nickname` | 닉네임 중복 체크 |
| POST | `/api/user/reset-password` | 비밀번호 재설정 (이메일 인증 후) |

### 이메일 인증

| Method | URL | 설명 |
|--------|-----|------|
| GET | `/api/email/send-verification` | 6자리 인증번호 발송 (유효시간 5분) |
| GET | `/api/email/verify-code` | 인증번호 확인 |

### 게시판 / 게시글

| Method | URL | 설명 |
|--------|-----|------|
| GET | `/board` | 게시판 목록 |
| GET | `/board/{boardId}/post/list` | 게시글 목록 (검색, 페이징) |
| GET | `/board/{boardId}/post/read` | 게시글 상세 (이전/다음 글, 좋아요 여부 포함) |
| GET | `/board/{boardId}/post/write` | 게시글 작성 폼 |
| POST | `/board/{boardId}/post/write` | 게시글 작성 처리 |
| GET/POST | `/board/{boardId}/post/update` | 게시글 수정 (본인만) |
| POST | `/board/{boardId}/post/delete` | 게시글 삭제 (본인 또는 관리자) |
| POST | `/board/{boardId}/post/like/{postId}` | 좋아요 토글 |

### 댓글 (REST API)

| Method | URL | 설명 |
|--------|-----|------|
| GET | `/api/posts/{postId}/comments` | 댓글 전체 조회 |
| POST | `/api/posts/{postId}/comments` | 댓글 작성 (로그인 필수) |
| GET | `/api/posts/{postId}/comments/{id}` | 댓글 단건 조회 |
| PUT | `/api/posts/{postId}/comments/{id}` | 댓글 수정 (작성자 본인) |
| DELETE | `/api/posts/{postId}/comments/{id}` | 댓글 삭제 (본인 또는 관리자) |

### 쪽지

| Method | URL | 설명 |
|--------|-----|------|
| GET | `/messages/all` \| `/received` \| `/sent` \| `/trash` | 쪽지함 조회 |
| GET | `/messages/api/unread-count` | 읽지 않은 쪽지 수 |
| GET | `/messages/api/read` | 쪽지 상세 (수신자 읽음 처리 포함) |
| POST | `/messages/api/write` | 쪽지 발송 (닉네임 기준 수신자 검색) |
| POST | `/messages/api/trash` | 휴지통 이동 |
| POST | `/messages/api/restore` | 복구 |
| POST | `/messages/api/delete` | 영구 삭제 (양측 모두 삭제 시 물리 삭제) |
| POST | `/messages/api/trash/bulk` | 선택 휴지통 이동 |
| POST | `/messages/api/delete/bulk` | 선택 영구 삭제 |

### 마이페이지

| Method | URL | 설명 |
|--------|-----|------|
| GET | `/mypage` | 마이페이지 (정보, 최근 게시글/댓글 10개) |
| POST | `/mypage/nickname` | 닉네임 변경 |
| GET | `/mypage/nickname/check` | 닉네임 중복 체크 |
| POST | `/mypage/password/verify` | 현재 비밀번호 확인 |
| POST | `/mypage/password` | 비밀번호 변경 |
| GET | `/mypage/posts` | 내가 쓴 게시글 목록 |
| GET | `/mypage/comments` | 내가 쓴 댓글 목록 |
| POST | `/mypage/removeAccount` | 회원 탈퇴 (DB에서 실제 삭제) |

### 관리자

| Method | URL | 설명 |
|--------|-----|------|
| GET | `/admin/dashboard` | 관리자 대시보드 |
| GET | `/admin/users` | 유저 목록 (페이징, 역할 필터, 검색) |
| GET | `/admin/users/{userId}` | 유저 상세 조회 |
| PATCH | `/admin/users/{userId}/role` | 유저 권한 변경 (자기 자신 변경 불가, 최소 1명 ADMIN 유지) |
| POST | `/admin/users/{userId}/ban` | 유저 정지 (active=false) |
| POST | `/admin/users/{userId}/activate` | 유저 활성화 |
| POST | `/admin/users/{userId}/deactivate` | 유저 비활성화 |
| GET/POST | `/admin/boards` | 게시판 조회 / 생성 |
| PUT | `/admin/boards/{boardId}` | 게시판 수정 |
| DELETE | `/admin/boards/{boardId}` | 게시판 삭제 |
| GET | `/admin/posts` | 게시글 전체 조회 (페이징) |
| DELETE | `/admin/posts/{postId}` | 게시글 삭제 |
| GET | `/admin/comments` | 댓글 전체 조회 (페이징) |
| DELETE | `/admin/comments/{commentId}` | 댓글 삭제 |


##  도메인별 유효성 검증

| 도메인 | 필드 | 조건 |
|--------|------|------|
| User | username | 4~50자, 영문/숫자/_ |
| User | name | 2~50자 |
| User | nickname | 2~50자, UNIQUE |
| User | email | 이메일 형식, UNIQUE |
| User | password | 재설정 시 8~20자 |
| Board | title | 2~50자, UNIQUE |
| Comment | content | 1~200자 |
| Message | title | 1~50자 |
| Message | content | 1~1000자 |

---

## 서비스 주요 로직

**UserService**
- 회원가입 시 username / nickname / email 중복 체크 후 BCrypt 암호화 저장
- 비밀번호 변경 시 현재 비밀번호 일치, 새 비밀번호 확인, 기존 비밀번호와 동일 여부 검증

**PostService**
- 게시글 수정: 작성자 본인만 가능
- 게시글 삭제: 작성자 또는 관리자 가능
- 좋아요: `post_likes` 테이블 UNIQUE 제약으로 중복 방지, 토글 방식

**CommentService**
- 댓글 작성 시 `posts.comments_count` 자동 증가
- 댓글 삭제 시 `posts.comments_count` 자동 감소
- 삭제: 작성자 본인 또는 관리자 가능

**MessageService**
- 수신자는 닉네임으로 검색
- 자기 자신에게 쪽지 발송 불가
- 삭제 상태: `0(유지) → 1(휴지통) → 2(영구삭제)`, 양측 모두 2일 때 물리 삭제

**AdminService**
- 권한 변경 시 자기 자신 변경 불가
- ADMIN → 다른 역할 변경 시 최소 1명의 ADMIN 유지 검증

**EmailService**
- 6자리 난수 인증번호 생성
- `ConcurrentHashMap`으로 인메모리 관리, 유효시간 5분

---
