package horserace.model.horse;

/**
 * 모든 경주마의 공통 부모 클래스.
 * 이름, 위치, 배당률 같은 공통 상태는 여기서 관리하고,
 * 매 턴 얼마나 움직일지는 하위 클래스가 run()을 오버라이딩해서 정한다.
 */
public abstract class Horse {
    private final int number;
    private final String name;
    private final double odds;
    private int position;
    private boolean stunned; // 넘어짐 이벤트로 이번 턴 이동 불가 상태

    protected Horse(int number, String name, double odds) {
        this.number = number;
        this.name = name;
        this.odds = odds;
    }

    /** 한 턴 동안 이동한다. 말 종류마다 이동 방식이 다르다. */
    public abstract void run();

    /** 화면에 표시할 말 종류 이름 (예: "질주형"). */
    public abstract String getType();

    /** 하위 클래스가 run() 안에서 호출하는 이동 메서드. 넘어진 상태면 이번 턴은 움직이지 않는다. */
    protected void move(int steps) {
        if (stunned) {
            stunned = false;
            return;
        }
        position += steps;
    }

    /** 넘어짐 이벤트: 다음 run()에서 이동하지 않는다. */
    public void stun() {
        stunned = true;
    }

    /** 부스터 이벤트: 추가로 전진한다. */
    public void boost(int steps) {
        position += steps;
    }

    /** 새 레이스 시작 전 상태 초기화. */
    public void reset() {
        position = 0;
        stunned = false;
    }

    public int getNumber() { return number; }
    public String getName() { return name; }
    public double getOdds() { return odds; }
    public int getPosition() { return position; }
    public boolean isStunned() { return stunned; }
}
