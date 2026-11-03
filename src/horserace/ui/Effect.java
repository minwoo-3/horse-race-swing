package horserace.ui;

import horserace.game.TurnEvent;
import horserace.model.event.RandomEvent;
import horserace.model.horse.Horse;

/** 화면에서 재생 중인 이벤트 효과 하나. */
final class Effect {
    private static final long DURATION_MS = 800;

    private final TurnEvent event;
    private final long startedAt;

    Effect(TurnEvent event, long startedAt) {
        this.event = event;
        this.startedAt = startedAt;
    }

    /** 진행도: 0.0(시작) ~ 1.0(끝). */
    double progress(long now) {
        return Math.min(1.0, (now - startedAt) / (double) DURATION_MS);
    }

    boolean isFinished(long now) {
        return progress(now) >= 1.0;
    }

    RandomEvent event() {
        return event.event();
    }

    Horse user() {
        return event.user();
    }

    Horse target() {
        return event.target();
    }
}
