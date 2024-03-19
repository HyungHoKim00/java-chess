# java-chess

체스 미션 저장소

## 우아한테크코스 코드리뷰

- [온라인 코드 리뷰 과정](https://github.com/woowacourse/woowacourse-docs/blob/master/maincourse/README.md)

## 기능 구현 목록

### 입력

> 게임 시작 : start
> 게임 종료 : end
> 게임 이동 : move source위치 target위치 - 예. move b2 b3
- [x] start 입력 시 게임을 시작한다.
  - [x] start 입력 후 다시 start를 입력하는 경우 예외를 발생시킨다.
- [x] end 입력 시 게임을 종료한다.
- [ ] move 입력 시 source위치에 있는 piece가 target위치로 움직인다.
  - [ ] source 위치에 piece가 없는 경우 예외를 발생시킨다.
  - [ ] 움직이지 못하는 경우 예외를 발생시킨다.

### 출력
- [x] 판을 출력한다.
    - [x] 가로는 왼쪽부터 a-h, 세로는 아래부터 1-8 이다.
    - [x] 검은 말은 대문자로, 흰 말은 소문자로 출력한다.

### board
- [x] piece들을 가진다.

### piece
- [x] 위치를 가진다.
- [x] 팀을 가진다.
- [x] 자신의 타입을 반환할 수 있다.
- [ ] 움직일 수 있다.
  - [ ] (보류) 이동한 이후 왕이 공격받는 상태가 되도록 이동할 수 없다.

### position
- [x] 이차원 위치 값을 가지고 있는다.
  - [x] 1-8 사이의 값이어야한다.

### team(enum)
- [x] BLACK, WHITE

### kind(enum)

### pawn
- [x] piece를 상속한다.
- [ ] 이동할 수 있다.
  - [ ] 앞으로 한 칸 이동할 수 있다.
  - [ ] white는 2열일 시, black은 7열일 시 2칸 이동 가능하다.
  - [ ] 상대 piece를 공격할 시, 대각선으로만 공격 가능하다. 
  - [ ] (보류) 앙파상: 상대 폰이 두칸 전진하고 자신의 왼쪽 혹은 오른쪽에 있는 경우, 그 방향으로 공격하고 대각선으로 나아갈 수 있다.

### knight
- [x] piece를 상속한다.
- [ ] 이동할 수 있다.
  - [ ] 직선 방향으로 한칸, 대각선 방향으로 한칸 움직일 수 있다(날 일)
  - [ ] piece를 넘어갈 수 있다.

### bishop
- [x] piece를 상속한다.
- [ ] 이동할 수 있다.
  - [ ] 대각선 전 범위로 이동할 수 있다.

### rook
- [x] piece를 상속한다.
- [ ] 이동할 수 있다.
  - [ ] 직선 전 범위로 이동할 수 있다.

### queen
- [x] piece를 상속한다.
- [ ] 이동할 수 있다.
  - [ ] 직선 및 대각선 전 범위로 이동할 수 있다.

### king
- [x] piece를 상속한다.
- [ ] 이동할 수 있다.
  - [ ] 전 방향 한 칸씩 이동할 수 있다.
  - [ ] (보류) 이동하고자 하는 위치가 공격받고 있는 경우, 이동할 수 없다.
  - [ ] (보류) 캐슬링: 왕과 타겟 룩이 한번도 이동하지 않았으며, king부터 rook까지 piece가 존재하지 않고 공격받고 있지 않은 경우, king을 rook 방향으로 두 칸 움직이고 rook을 king 반대 방향 바로 옆으로 이동할 수 있다.


### 출력 예시

```
> 체스 게임을 시작합니다.
> 게임 시작 : start
> 게임 종료 : end
> 게임 이동 : move source위치 target위치 - 예. move b2 b3
start
RNBQKBNR
PPPPPPPP
........
........
........
........
pppppppp
rnbqkbnr

move b2 b3
RNBQKBNR
PPPPPPPP
........
........
........
.p......
p.pppppp
rnbqkbnr

```