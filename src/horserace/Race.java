package horserace;

import java.util.List;
import java.util.Random;

/** 레이스 한 판 진행. */
public class Race {
    public static final int FINISH_LINE = 30;
    private static final int TURN_DELAY_MS = 300;

    private final List<Horse> horses;
    private final Random random = new Random();

    public Race(List<Horse> horses) {
        this.horses = horses;
    }

    /** 결승선에 먼저 도착한 말을 반환할 때까지 턴을 반복한다. */
    public Horse run() throws InterruptedException {
        horses.forEach(Horse::reset);
        Horse winner = null;
        while (winner == null) {
            winner = playTurn();
            printTrack();
            Thread.sleep(TURN_DELAY_MS);
        }
        return winner;
    }

    /** 한 턴: 각 말 이동 + 돌발 이벤트. 도착한 말이 있으면 반환, 없으면 null. */
    private Horse playTurn() {
        // TODO: 각 말마다 기본 이동 거리 결정 (예: 1~3칸)
        // TODO: 일정 확률로 이벤트 발생
        //       - 넘어짐: 이번 턴 이동 안 함
        //       - 부스터: 추가로 크게 전진
        //       발생 시 메시지 출력
        // TODO: FINISH_LINE 도착한 말 반환 (동시 도착 처리 방법 결정)
        return horses.get(0); // 임시: 컴파일용
    }

    private void printTrack() {
        for (Horse h : horses) {
            int pos = Math.min(h.getPosition(), FINISH_LINE);
            System.out.printf("%d번 %-6s|%s🐎%n", h.getNumber(), h.getName(), "-".repeat(pos));
        }
        System.out.println();
    }
}
