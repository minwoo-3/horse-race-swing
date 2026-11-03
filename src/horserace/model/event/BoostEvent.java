package horserace.model.event;

import horserace.model.horse.Horse;

/** 스킬: 부스터로 크게 전진한다. */
public class BoostEvent implements RandomEvent {
    private static final int BOOST_STEPS = 5;

    @Override
    public void apply(Horse target) {
        target.boost(BOOST_STEPS);
    }

    @Override
    public String getMessage(Horse user, Horse target) {
        return "⚡ " + target.getDisplayName() + " 부스터 발동! (+" + BOOST_STEPS + "칸)";
    }
}
