package horserace.model.event;

import horserace.model.horse.Horse;

/** 공통 이벤트: 넘어져서 이번 턴 이동하지 못한다. */
public class FallEvent implements RandomEvent {

    @Override
    public void apply(Horse target) {
        target.stun();
    }

    @Override
    public String getMessage(Horse user, Horse target) {
        return "💥 " + target.getDisplayName() + " 넘어짐!";
    }
}
