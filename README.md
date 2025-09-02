<img width="1440" height="510" alt="Surface Pro 8 - 6" src="https://github.com/user-attachments/assets/e4633938-9b03-49aa-93e5-894f3ab9a9ac" />
# 프로젝트 소개

> 한 장씩 넘기며 기록하는 나의 독서 노트 **구름한장☁️**

책을 한 장씩 읽어나가는 경험을 구름처럼 가볍고 편안하게
> 

# 개발 기간

**🗓️ 2025.07.28 ~ 2025.09.02**

# 아키텍처

### 시스템 구조도
<img width="1531" height="1042" alt="Frame 120" src="https://github.com/user-attachments/assets/03cf276a-ac97-49fc-94e6-f0de09ac10ed" />


### CA + MVVM
<img width="2434" height="524" alt="Frame 121" src="https://github.com/user-attachments/assets/d8e5a821-5d8f-4f3d-bb35-32e13af2126f" />


- MVVM 으로 UI 와 비즈니스 로직을 분리
- Clean Architecture 로 계층간 의존성을 줄여 유지보수성과 테스트 용이성 높임

# 주요기능

- Firebase Auth 를 사용한 회원가입 및 로그인
    - 네이버, 카카오, 구글
- 홈
    - 현재 읽고있는 책 목록을 제공
    - 책 카드에 책 정보(제목, 저자, 표지) 및 현재 읽고있는 페이지, 누적 독서 시간 등 핵심 정보를 표시
    - 하루 독서 목표를 정하고 도넛 차트로 달성도를 확인
    - 사용자가 작성한 필사 문장을 랜덤으로 표시
    - 책을 검색할 수 있는 서치바 제공
- 도서 검색
    - 알라딘 API 기반 검색
    - 책 제목, 저자, 출판사, isbn 등의 키워드를 통해 책 검색 기능 제공
    - 검색 결과는 책 표지, 제목, 저자, 페이지 수 등 상세 정보 표시
    - ‘읽을 책, 읽는 중, 읽은 책에 추가’ 기능 제공
- 서재
    - ‘읽을 책, 읽는 중, 읽은 책’ 상태 별 목록 관리
    - 각 책 클릭 시 해당 책의 상세 페이지로 이동
- 책 상세 화면
    - 해당 책의 정보 제공
    - 독서 진행도, 독서 기간, 누적 독서 시간, 하루 평균 독서 시간 제공
    - 해당 책의 상세 정보, 작성한 필사, 독서 기록 목록들을 탭으로 제공
    - 다 읽은 해당 책에 대한 평점과 후기를 작성하고 수정할 수 있는 기능 제공
    - 독서를 기록할 때 타이머나 직접 기입 기능을 제공
    - 책 당 1개의 마인드맵을 그릴 수 있는 기능 제공
    - 책에서 인상깊은 문장을 작성할 수 있는 필사 기능 제공
- 독서 타이머
    - 책 상세 화면 또는 위젯에서 타이머 화면으로 이동해 시작, 정지, 재개 기능 제공
    - 타이머가 동작하는 중에도 인상 깊은 문장 추가할 수 있는 기능 제공
    - 타이머 동작 중 플로팅 윈도우 기능 제공
    - 타이머 종료 시 시간 저장 및 누적 시간을 자동으로 반영
- 인상깊은 문장
    - 타이머 중 또는 책 상세 화면에서 인상깊은 문장 작성
    - 책 상세 화면에서 필사 수정/삭제 기능
    - 앱 내 전체 필사 목록에서 책 구분 없이 열람 가능
- 마인드 맵
    - 각 책 별로 1개의 마인드맵 생성 가능
    - 독서 중에 아이디어나 개념, 인물, 사건의 흐름 등을 시각화
    - 노드의 추가와 삭제, 위치 조정, 편집 기능 등 제공
- 통계
    - 무슨 장르의 책을 주로 읽는지 읽은 책의 장르를 도넛 차트로 표시
    - 책을 주로 읽는 시간대와, 요일 별 읽은 페이지 수를 바 차트와 라인 차트로 제공
    - 주간, 월간, 연간 필터링 기능 제공
- 마이페이지
    - 닉네임 변경
    - 사용자의 총 독서 통계를 간단하게 제공
    - 독서 잔디 캘린더 제공
    - 평가하기 - 구글 플레이 평점 남기기
    - 문의하기 - 메인 개발자 이메일 보내기로 바로가기 연결
    - 앱 내에서 사용된 오픈소스 라이선스 정보를 리스트로 표시
    - 라이트 모드, 다크 모드를 변경할 수 있는 테마 기능 제공
    - 로그아웃
    - 탈퇴
- 온보딩
    - 사용자가 앱에서 사용할 닉네임을 결정
    - 앱 사용 목적을 조사하고 앱에 대한 설명을 제공
    - 사용자가 앱에서 사용할 테마를 결정

# 주요 화면 스크린샷
<table>
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



# 팀원 소개
| 팀장 | 부팀장 | 팀원 | 팀원 |
|:----:|:----:|:----:|:----:|
|<img width=150 src="https://avatars.githubusercontent.com/u/107612802?v=4"/>|<img width=150 src="https://avatars.githubusercontent.com/u/125240447?v=4"/>|<img width=150 src="https://avatars.githubusercontent.com/u/207657876?v=4">|<img width=150 src="https://avatars.githubusercontent.com/u/57650484?v=4">|
|이소희|김의현|김학록|홍의정|

![Group 389.png](attachment:80f53b4c-6ecd-4a9d-a729-609a5293fd9e:Group_389.png)

![Group 387.png](attachment:ebd058a8-13d8-4977-9062-b2b19baf6f40:Group_387.png)
