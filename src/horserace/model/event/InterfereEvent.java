package horserace.model.event;

import horserace.model.horse.Horse;

/** 스킬: 다른 말을 방해해서 이번 턴 이동하지 못하게 한다. */
public class InterfereEvent implements RandomEvent {

    @Override
    public void apply(Horse target) {
        target.stun();
    }

    @Override
    public boolean targetsOthers() {
        return true;
    }

    @Override
    public String getMessage(Horse user, Horse target) {
        return "🎯 " + user.getDisplayName() + "의 방해! " + target.getDisplayName() + " 이번 턴 정지";
    }
}
