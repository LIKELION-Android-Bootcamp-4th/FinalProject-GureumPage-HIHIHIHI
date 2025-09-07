<img width="1440" height="510" alt="Surface Pro 8 - 6" src="https://github.com/user-attachments/assets/e4633938-9b03-49aa-93e5-894f3ab9a9ac" />

# 프로젝트 소개

> 한 장씩 넘기며 기록하는 나의 독서 노트 **구름한장☁️**
<br>책을 한 장씩 읽어나가는 경험을 구름처럼 가볍고 편안하게

<!-- TODO 기획 의도나 앱 개발 목적 추가 -->

# 개발 기간

**🗓️ 2025.07.28 ~ 2025.09.02**

<!-- # 기술 스택 -->
<!-- 넣는다면 아마 표 형식으로 할 듯? -->

# 아키텍처

### 시스템 구조도
<img width="1531" height="1042" alt="Frame 120" src="https://github.com/user-attachments/assets/03cf276a-ac97-49fc-94e6-f0de09ac10ed" />


### CA + MVVM
<img width="2434" height="524" alt="Frame 121" src="https://github.com/user-attachments/assets/d8e5a821-5d8f-4f3d-bb35-32e13af2126f" />


- MVVM 으로 UI 와 비즈니스 로직을 분리
- Clean Architecture 로 계층간 의존성을 줄여 유지보수성과 테스트 용이성 높임

<!-- # 패키지 구조 -->

<!-- ```
🗂️ GureumPage
├─📦 data
│  └─📁 kotlin+java
│     └─📦 com.hihihihi.data
│        ├─📁 common.util          // data 계층 공용 유틸
│        ├─📁 di                   // Data용 Hilt 모듈 (파이어베이스, Repository 등)
│        ├─📁 local                // DataStore 등 로컬 소스
│        ├─📁 mapper               // DTO ↔ Domain 변환
│        ├─📁 notification         // FCM 토큰 업로드
│        ├─📁 remote               // Firestore/Functions등 service, DataSource, DTO
│        └─📁 repositoryimpl       // Domain Repository 구현체
│
├─📦 domain
│  └─📁 kotlin+java
│     └─📦 com.hihihihi.domain
│        ├─📁 model                // 도메인 엔티티 등
│        ├─📁 notification         // 도메인 FCM 이벤트 인터페이스
│        ├─📁 operation            // 마인드맵 노드 편집 연산자
│        ├─📁 repository           // Repository 인터페이스
│        └─📁 usecase              // UseCase
│
└─📦 presentation
   └─📁 kotlin+java
      └─📦 com.hihihihi.gureumpage
         ├─📁 common.utils         // UI 공용 유틸/확장
         ├─📁 designsystem         // 공통 컴포넌트/테마
         ├─📁 di                   // UI 관련 모듈
         ├─📁 navigation           // NavGraph, Destinations
         ├─📁 notification         // FCM 수신/알림 채널
         ├─📁 ui                   // feature별 화면/컴포넌트/뷰모델
         ├─📁 widgets              // Glance 위젯
         ├─📄 GureumApp.kt
         └─📄 MainActivity.kt
``` -->


# 주요 기능

| 섹션 | 주요 기능 |
|---|---|
| 인증 (Firebase Auth) | • 소셜 로그인: 네이버 / 카카오 / 구글 |
| 홈 | • 현재 읽는 중 책 목록 제공<br>• 책 카드: 제목·저자·표지, 현재 페이지, 누적 독서 시간 표시<br>• 하루 독서 목표 설정 & 도넛 차트로 달성도 확인<br>• 작성한 필사 문장 랜덤 노출<br>• 책 검색 서치바 제공 |
| 도서 검색 | • 알라딘 API 기반 검색<br>• 키워드: 제목 / 저자 / 출판사 / ISBN<br>• 결과: 표지·제목·저자·페이지 수 등 상세 정보 표시<br>• 상태 변경: 읽을 책 / 읽는 중 / 읽은 책에 바로 추가 |
| 서재 | • 상태별 목록 관리: 읽을 책 / 읽는 중 / 읽은 책<br>• 항목 클릭 시 책 상세 화면으로 이동 |
| 책 상세 화면 | • 책 기본 정보 제공<br>• 독서 진행도·독서 기간·누적 시간·하루 평균 시간<br>• 탭: 상세 정보 / 필사 / 독서 기록<br>• 완독 후 평점·후기 작성/수정<br>• 기록: 타이머 또는 직접 기입<br>• 책당 1개의 마인드맵 생성·관리<br>• 필사(인상 깊은 문장) 작성 |
| 독서 타이머 | • 책 상세/위젯에서 진입, 시작·정지·재개<br>• 동작 중 필사 추가 가능<br>• 플로팅 윈도우 제공(드래그/스와이프 조절, 다른 앱 위 동작)<br>• 종료 시 시간 저장 및 누적 반영 자동 |
| 인상 깊은 문장(필사) | • 타이머 중 또는 책 상세에서 작성<br>• 책 상세에서 수정/삭제<br>• 앱 내 전체 필사 목록에서 도서 구분 없이 열람 |
| 마인드맵 | • 책별 1개 마인드맵 생성<br>• 아이디어/개념/인물/사건 흐름 시각화<br>• 노드 추가·삭제·이동·편집<br>• 스냅샷 기반 undo/redo 등 |
| 통계 | • 읽은 책 장르: 도넛 차트<br>• 시간대/요일별 읽은 페이지: 바/라인 차트<br>• 주간·월간·연간 필터 |
| 마이페이지 | • 닉네임 변경<br>• 총 독서 통계 요약<br>• 독서 잔디 캘린더<br>• 평가하기(Play 평점), 문의하기(이메일 연결)<br>• 오픈소스 라이선스 목록<br>• 라이트/다크 테마 전환<br>• 로그아웃, 탈퇴 |
| 온보딩 | • 닉네임 설정<br>• 앱 사용 목적 조사 & 앱 소개<br>• 테마(라이트/다크) 선택 |


# 주요 화면 스크린샷
<table>
  <tr>
    <td align="center">
      홈 화면
    </td>
    <td align="center">
      책 상세
    </td>
    <td align="center">
      필사 추가
    </td>
  </tr>
  <tr>
    <td align="center">
      <img width="454" height="936" alt="Group 392" src="https://github.com/user-attachments/assets/5aee2309-75cf-48e0-b1d1-b42ed4e03a21" />
    </td>
    <td align="center">
      <img width="454" height="936" alt="Group 391" src="https://github.com/user-attachments/assets/4e1887e5-36e5-45e0-a7f7-5f63ebfebf3d" />
    </td>
    <td align="center">
      <img width="454" height="936" alt="Group 390" src="https://github.com/user-attachments/assets/c936b1ca-af8b-41ca-97d4-1d4d5329e4b6" />
    </td>
  </tr>
  <tr>
    <td align="center">
      마인드맵
    </td>
    <td align="center">
      스톱워치
    </td>
    <td align="center">
      통계
    </td>
  </tr>
  <tr>
    <td align="center">
      <img width="454" height="936" alt="Group 389" src="https://github.com/user-attachments/assets/4a833334-74a8-44f4-8bc7-8ababdefc7c3" />
    </td>
    <td align="center">
      <img width="454" height="936" alt="Group 388" src="https://github.com/user-attachments/assets/6ae474f8-f8c2-4378-9a4b-f5dc9d9dd218" />
    </td>
    <td align="center">
      <img width="454" height="936" alt="Group 387" src="https://github.com/user-attachments/assets/fc7d645d-1ea2-4e73-ae9b-507e24d1503e" />
    </td>
  </tr>
</table>

<!-- 구글 드라이브로 시연연상 링크 달기? -->

# 팀원 소개
| 팀장 | 부팀장 | 팀원 | 팀원 |
|:----:|:----:|:----:|:----:|
|<img width=150 src="https://avatars.githubusercontent.com/u/107612802?v=4"/>|<img width=150 src="https://avatars.githubusercontent.com/u/125240447?v=4"/>|<img width=150 src="https://avatars.githubusercontent.com/u/207657876?v=4">|<img width=150 src="https://avatars.githubusercontent.com/u/57650484?v=4">|
|[이소희](https://github.com/see-ho)|[김의현](https://github.com/UiHyeon-Kim)|[김학록](https://github.com/hakrok)|[홍의정](https://github.com/aabc88)|

<!-- 각 팀원 역할 넣을까 **말까**.. -->
