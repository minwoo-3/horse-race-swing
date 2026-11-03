package horserace.ui;

import horserace.game.Race;

/**
 * 트랙 화면의 크기와 좌표 계산. 단위는 "도트"(화면 픽셀 ÷ SCALE).
 * TrackPanel, TrackBackground가 같은 기준을 쓰도록 한곳에 모았다.
 */
final class TrackLayout {
    static final int SCALE = 2;              // 도트 1칸 = 화면 2픽셀
    static final int TRACK_WIDTH = 440;      // 트랙 전체 너비 (WIDTH는 Swing 상수와 이름이 겹쳐서 피함)
    static final int TOP = 34;               // 위쪽 안내 문구 영역 높이
    static final int LANE_HEIGHT = 40;
    static final int LABEL_WIDTH = 94;       // 왼쪽 이름표 영역 너비
    static final int BOTTOM = 8;
    static final int FINISH_X = TRACK_WIDTH - 16;  // 결승선 x

    private TrackLayout() {
    }

    static int height(int laneCount) {
        return TOP + LANE_HEIGHT * laneCount + BOTTOM;
    }

    static int laneTop(int lane) {
        return TOP + lane * LANE_HEIGHT;
    }

    static int laneCenterY(int lane) {
        return laneTop(lane) + LANE_HEIGHT / 2;
    }

    /** y 좌표가 몇 번째 레인인지. 레인 밖이면 -1. */
    static int laneAt(int y, int laneCount) {
        if (y < TOP) {
            return -1;
        }
        int lane = (y - TOP) / LANE_HEIGHT;
        return lane < laneCount ? lane : -1;
    }

    /** 레이스 위치(0 ~ FINISH_LINE 칸)를 말 중심의 x 좌표로 바꾼다. */
    static int positionToX(double position) {
        int start = LABEL_WIDTH + 14 + HorseSprite.WIDTH / 2;
        int end = FINISH_X - HorseSprite.WIDTH / 2 + 6;
        double clamped = Math.min(position, Race.FINISH_LINE);
        return start + (int) ((end - start) * clamped / Race.FINISH_LINE);
    }
}
