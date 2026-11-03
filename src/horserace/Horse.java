package horserace;

/** 출전 말 한 마리. 이름, 배당률, 현재 위치를 가진다. */
public class Horse {
    private final int number;
    private final String name;
    private final double odds;
    private int position;

    public Horse(int number, String name, double odds) {
        this.number = number;
        this.name = name;
        this.odds = odds;
    }

    public void move(int steps) {
        position += steps;
    }

    public void reset() {
        position = 0;
    }

    public int getNumber() { return number; }
    public String getName() { return name; }
    public double getOdds() { return odds; }
    public int getPosition() { return position; }
}
