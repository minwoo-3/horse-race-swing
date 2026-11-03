package horserace.model.horse;

import java.util.Random;

/** 행운형: 평소 1~3칸, 낮은 확률(10%)로 6~8칸 크게 전진. 배당 4배. */
public class LuckyHorse extends Horse {
    private static final Random RANDOM = new Random();
    private static final double LUCKY_CHANCE = 0.1;

    public LuckyHorse(int number, String name) {
        super(number, name, 4.0);
    }

    @Override
    public void run() {
        if (RANDOM.nextDouble() < LUCKY_CHANCE) {
            move(6 + RANDOM.nextInt(3)); // 6~8
        } else {
            move(1 + RANDOM.nextInt(3)); // 1~3
        }
    }

    @Override
    public String getType() {
        return "행운형";
    }
}
