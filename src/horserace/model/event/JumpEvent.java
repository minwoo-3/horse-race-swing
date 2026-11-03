package horserace.model.event;

import horserace.model.horse.Horse;

/** 스킬: 점프로 장애물을 넘는다. 넘어진 상태에서 회복하고 조금 전진한다. */
public class JumpEvent implements RandomEvent {
    private static final int JUMP_STEPS = 6;

    @Override
    public void apply(Horse target) {
        target.recover();
        target.boost(JUMP_STEPS);
    }

    @Override
    public String getMessage(Horse user, Horse target) {
        return "🦘 " + target.getDisplayName() + " 점프! (+" + JUMP_STEPS + "칸, 넘어짐 회복)";
    }
}
