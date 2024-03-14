# RoleAuth

역할을 자동 부여하기 위한 봇

## 필수 권한
- MANAGER_ROLES
- KICK_MAMBERS
- MANAGE_NICKNAME

## 설정
1. 최신 Release에서 `RoleAuth.jar` 파일을 받는다.
2. 같은 경로에 .env 파일을 만들고 아래 예시와 같이 입력한다.
    ```bash
   TOKEN=YOUR_DISCORD_TOKEN
    DB_PASSWORD=YOUR_DB_PASSWORD
    DB_NAME=YOUR_DB_NAME
   ```
3. docker-compose.prod.yml 파일을 실행시킨다.
   ```bash
   docker-compose -f docker-compose.prod.yml up -d
    ```

## 명령어
|  명령어   |  필요권한   |
|:------:|:-------:|
|  유저설정  | 역할 관리하기 |
|  유저제거  | 멤버 추방하기 |
| 인증버튼생성 | 역할 관리하기 |

## CSV를 통한 유저 등록
**학번, 이름, 역할이름 순의 데이터 여야 합니다. [예제](example/exmaple.csv)**

**무조건 CSV 파일 하나만 업로드 해야합니다**

```text
<유저전체추가>
```
순수히 역할만 추가합니다

<br/>

```text
<유저전체추가>(동기화)
```
역할을 추가하고 기존에 있는 유저일 경우 역할을 수정합니다.

## 주의사항
- 모든 유저에게 닉네임 변환 권한이 없어야 합니다.
- CSV 파일을 보낸 채널에 봇이 볼 수 있는 권한이 없으면 작동하지 않을 수 있습니다.