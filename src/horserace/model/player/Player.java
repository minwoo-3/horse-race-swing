package horserace.model.player;

import horserace.model.horse.Horse;

/** 플레이어 잔액 관리. */
public class Player {
    public static final int START_MONEY = 10000;

    private int balance = START_MONEY;
    private final String name;
    private Horse bettingHorse;
    private int betAmount;

    public Player(String name) {
        this.name = name;
    }

    public boolean canBet(int amount) {
        return amount > 0 && amount <= balance;
    }

    public boolean isBankrupt() {
        return balance <= 0;
    }

    public int getBalance() { return balance; }

    public void bet(Horse horse, int amount) {
        if (bettingHorse == null) throw new IllegalStateException("베팅 내역이 없습니다.");
        if (!canBet(amount)) throw new IllegalArgumentException("잘못된 금액입니다.");

        balance -= amount;
        bettingHorse = horse;
        betAmount = amount;
    }

    public int win() {
        double odds = bettingHorse.getOdds();
        int prize = (int) (betAmount * odds);
        balance += prize;
        return prize;
    }

    public Horse getBettingHorse() {
        return bettingHorse;
    }

    public int getBetAmount() {
        return betAmount;
    }

    public String getName() {
        return name;
    }
}
