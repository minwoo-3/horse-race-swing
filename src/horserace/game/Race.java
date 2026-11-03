package horserace.game;

import horserace.model.event.FallEvent;
import horserace.model.event.RandomEvent;
import horserace.model.horse.Horse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * 레이스 한 판을 진행한다.
 * 화면 출력은 하지 않고 상태만 바꾼다. 화면 쪽에서 nextTurn()을 반복 호출해서 사용한다.
 */
public class Race {
    public static final int FINISH_LINE = 150;              // 결승선 거리. 늘리면 레이스가 길어진다
    private static final double COMMON_EVENT_CHANCE = 0.08; // 턴마다 말 한 마리가 공통 이벤트를 겪을 확률
    private static final double SKILL_CHANCE = 0.15;        // 턴마다 말 한 마리가 스킬을 쓸 확률

    private final ArrayList<Horse> horses; // 리스트 순서 = 레인 순서
    private final List<RandomEvent> commonEvents = List.of(new FallEvent());
    private final Random random = new Random();
    private int turn;

    public Race(List<Horse> horses) {
        this.horses = new ArrayList<>(horses);
    }

    /** 레인 배정을 무작위로 섞는다. 베팅 전에 호출해서 이번 레이스의 레인을 보여준다. */
    public void shuffleLanes() {
        Collections.shuffle(horses, random);
    }

    /** 새 레이스 시작: 모든 말 위치와 턴 수를 초기화한다. */
    public void start() {
        for (Horse horse : horses) {
            horse.reset();
        }
        turn = 0;
    }

    /**
     * 한 턴 진행: 이벤트가 먼저 일어나고, 그다음 모든 말이 이동한다.
     * @return 이번 턴에 일어난 이벤트 목록
     */
    public List<TurnEvent> nextTurn() {
        turn++;
        List<TurnEvent> events = new ArrayList<>();
        for (Horse horse : horses) {
            tryCommonEvent(horse, events);
            trySkill(horse, events);
        }
        // 다형성: 말 종류를 몰라도 run()만 호출하면 각자 다르게 움직인다.
        for (Horse horse : horses) {
            horse.run();
        }
        return events;
    }

    public boolean isFinished() {
        for (Horse horse : horses) {
            if (horse.getPosition() >= FINISH_LINE) {
                return true;
            }
        }
        return false;
    }

    /** 가장 멀리 간 말. 같은 턴에 여러 마리가 들어오면 더 멀리 간 말이 우승한다. */
    public Horse getWinner() {
        Horse winner = horses.get(0);
        for (Horse horse : horses) {
            if (horse.getPosition() > winner.getPosition()) {
                winner = horse;
            }
        }
        return winner;
    }

    /** 레인 순서대로 정렬된 말 목록 (읽기 전용). */
    public List<Horse> getHorses() {
        return Collections.unmodifiableList(horses);
    }

    public int getTurn() {
        return turn;
    }

    // ---------- 이벤트 ----------

    /** 공통 이벤트(넘어짐 등): 확률에 걸리면 그 말 자신에게 적용. */
    private void tryCommonEvent(Horse horse, List<TurnEvent> events) {
        if (random.nextDouble() >= COMMON_EVENT_CHANCE) {
            return;
        }
        RandomEvent event = pickRandom(commonEvents);
        event.apply(horse);
        events.add(new TurnEvent(horse, horse, event));
    }

    /** 캐릭터 스킬: 확률에 걸리면 발동. 방해 스킬은 다른 말에게 건다. */
    private void trySkill(Horse horse, List<TurnEvent> events) {
        List<RandomEvent> skills = horse.getSkills();
        if (skills.isEmpty() || random.nextDouble() >= SKILL_CHANCE) {
            return;
        }
        RandomEvent skill = pickRandom(skills);
        Horse target = skill.targetsOthers() ? leaderExcept(horse) : horse;
        skill.apply(target);
        events.add(new TurnEvent(horse, target, skill));
    }

    /** 자기 자신을 뺀 말 중 선두 (방해 스킬 대상). */
    private Horse leaderExcept(Horse self) {
        Horse leader = null;
        for (Horse horse : horses) {
            if (horse != self && (leader == null || horse.getPosition() > leader.getPosition())) {
                leader = horse;
            }
        }
        return leader;
    }

    private RandomEvent pickRandom(List<RandomEvent> events) {
        return events.get(random.nextInt(events.size()));
    }
}
