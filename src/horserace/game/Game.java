package horserace.game;

import horserace.model.horse.Horse;
import horserace.model.horse.LuckyHorse;
import horserace.model.horse.SprinterHorse;
import horserace.model.horse.SteadyHorse;
import horserace.model.player.Player;

import java.util.List;
import java.util.Scanner;

/** 전체 게임 흐름: 베팅 -> 레이스 -> 정산 -> 반복. */
public class Game {
    private final Scanner scanner = new Scanner(System.in);
    private final Player player = new Player("플레이어");
    private final List<Horse> horses = List.of(
            new SprinterHorse(1, "번개"),
            new SteadyHorse(2, "꾸준이"),
            new LuckyHorse(3, "럭키")
    );

    public void start() {
        System.out.println("=== 경마 게임 ===");
        System.out.println("시작 자금: " + player.getBalance() + "원");

        // TODO: 게임 루프
        //   1. printHorses()
        //   2. selectHorse(), inputBetAmount()
        //   3. new Race(horses).run()
        //   4. settle()
        //   5. 파산 체크 -> 종료 / askContinue()
    }

    private void printHorses() {
        // TODO: 말 번호, 이름, 배당률 출력
    }

    private Horse selectHorse() {
        // TODO: 잘못된 번호/숫자 아닌 입력이면 다시 입력받기
        return null;
    }

    private int inputBetAmount() {
        // TODO: player.canBet() 검사, 잘못된 입력이면 다시 입력받기
        return 0;
    }

    private void settle(Horse picked, int bet, Horse winner) {
        // TODO: 이기면 bet * odds 지급, 지면 베팅 금액 잃음. 잔액 출력
    }

    private boolean askContinue() {
        // TODO: y/n 입력 (그 외 입력은 다시)
        return false;
    }
}
