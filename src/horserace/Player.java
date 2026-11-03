package horserace;

/** 플레이어 잔액 관리. */
public class Player {
    public static final int START_MONEY = 10000;

    private int balance = START_MONEY;

    public boolean canBet(int amount) {
        return amount > 0 && amount <= balance;
    }

    /** 베팅 금액을 잔액에서 차감한다. */
    public void placeBet(int amount) {
        balance -= amount;
    }

    /** 당첨금을 잔액에 더한다. */
    public void receive(int amount) {
        balance += amount;
    }

    public boolean isBankrupt() {
        return balance <= 0;
    }

    public int getBalance() { return balance; }
}
