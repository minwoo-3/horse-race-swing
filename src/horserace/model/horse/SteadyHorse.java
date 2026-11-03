package horserace.model.horse;

import java.util.Random;

/** 안정형: 매 턴 항상 2~3칸 이동. 배당 1.5배. */
public class SteadyHorse extends Horse {
    private static final Random RANDOM = new Random();

    public SteadyHorse(int number, String name) {
        super(number, name, 1.5);
    }

    @Override
    public void run() {
        move(2 + RANDOM.nextInt(2)); // 2~3
    }

    @Override
    public String getType() {
        return "안정형";
    }
}
