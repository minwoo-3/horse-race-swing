package horserace.model.event;

import horserace.model.horse.Horse;

/**
 * 레이스 중 일어나는 돌발 이벤트.
 * 공통 이벤트(Race가 가짐)와 캐릭터 스킬(각 Horse가 가짐) 모두 이 인터페이스를 구현한다.
 * Race는 이벤트 종류를 몰라도 apply()만 호출하면 된다.
 */
public interface RandomEvent {

    /** 대상 말에게 효과를 적용한다. */
    void apply(Horse target);

    /**
     * 화면에 표시할 메시지.
     * @param user   이벤트를 일으킨 말 (공통 이벤트나 자기 자신에게 거는 스킬이면 target과 같음)
     * @param target 효과를 받는 말
     */
    String getMessage(Horse user, Horse target);

    /** true면 자기 자신이 아니라 다른 말에게 효과를 건다 (방해 스킬). */
    default boolean targetsOthers() {
        return false;
    }
}
