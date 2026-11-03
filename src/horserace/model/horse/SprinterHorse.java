package horserace.model.horse;

import java.util.Random;

/** 질주형: 매 턴 0~5칸 랜덤 이동. 기복이 크다. 배당 2배. */
public class SprinterHorse extends Horse {
    private static final Random RANDOM = new Random();

    public SprinterHorse(int number, String name) {
        super(number, name, 2.0);
    }

    @Override
    public void run() {
        move(RANDOM.nextInt(6)); // 0~5
    }

    @Override
    public String getType() {
        return "질주형";
    }
}
