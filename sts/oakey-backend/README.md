/*
[User 패키지 설명]

- user 패키지는 사용자 관련 기능을 담당한다.
- 현재 단계에서는 JWT 없이, 카카오 소셜 로그인 + 자체 회원가입 구조만 구현되어 있다.

구성 요약:
- controller
  - UserController: 자체 회원가입, 사용자 관련 API
  - AuthController: 소셜 로그인 성공/실패 확인용 API

- service
  - UserService: 사용자 저장/수정 등 비즈니스 로직
  - CustomOAuth2UserService: 카카오 OAuth2 로그인 사용자 정보 처리

- dto
  - UserSignupRequest: 자체 회원가입 요청 DTO
  - SocialProfileRequest: 소셜 로그인 후 추가 정보 입력용 DTO

- domain
  - User: 자체/소셜 로그인 공통 사용자 엔티티
  - Social: 소셜 로그인 계정 정보 엔티티 (현재 KAKAO만 사용)

- repository
  - UserRepository: User JPA Repository
  - SocialRepository: Social JPA Repository

보안 관련:
- SecurityConfig에서 OAuth2 로그인만 활성화되어 있음
- JWT 발급/검증, API 권한 보호는 아직 구현되지 않음

목적:
- 소셜 로그인 인증 구조 전달 및 이후 JWT 확장을 위한 기반 구조
*/
