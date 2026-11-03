package horserace.model.player;

import horserace.model.horse.Horse;

/**
 * 플레이어: 이름, 잔액, 이번 베팅 정보.
 * 잔액은 setter 없이 bet()과 win()으로만 바뀐다.
 */
public class Player {
    public static final int START_MONEY = 10000;

    private final String name;
    private int balance = START_MONEY;
    private Horse bettingHorse;
    private int betAmount;

    public Player(String name) {
        this.name = name;
    }

    /** 베팅 가능한 금액인지: 1원 이상, 잔액 이하. */
    public boolean canBet(int amount) {
        return amount >= 1000 && amount <= balance;
    }

    /** 베팅: 잔액에서 금액을 빼고 어떤 말에 걸었는지 기억한다. */
    public void bet(Horse horse, int amount) {
        if (!canBet(amount)) {
            throw new IllegalArgumentException("잘못된 금액입니다.");
        }
        balance -= amount;
        bettingHorse = horse;
        betAmount = amount;
    }

    /**
     * 당첨: 베팅 금액 × 배당률을 잔액에 더한다.
     * @return 받은 당첨금
     */
    public int win() {
        if (bettingHorse == null) {
            throw new IllegalStateException("베팅 내역이 없습니다.");
        }
        int prize = (int) (betAmount * bettingHorse.getOdds());
        balance += prize;
        return prize;
    }

    public boolean isBankrupt() {
        return balance <= 0;
    }

    public String getName() {
        return name;
    }

    public int getBalance() {
        return balance;
    }

    public Horse getBettingHorse() {
        return bettingHorse;
    }

    public int getBetAmount() {
        return betAmount;
    }
}
