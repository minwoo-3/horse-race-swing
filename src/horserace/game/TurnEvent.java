package horserace.game;

import horserace.model.event.RandomEvent;
import horserace.model.horse.Horse;

/**
 * 한 턴에 발생한 이벤트 기록. 화면이 메시지 출력과 애니메이션에 사용한다.
 * @param user   이벤트를 일으킨 말
 * @param target 효과를 받은 말
 * @param event  발생한 이벤트
 */
public record TurnEvent(Horse user, Horse target, RandomEvent event) {

    public String message() {
        return event.getMessage(user, target);
    }
}
