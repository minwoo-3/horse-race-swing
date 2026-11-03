package horserace.ui;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Random;

import static horserace.ui.PixelStyle.DIRT;
import static horserace.ui.PixelStyle.DIRT_DARK;
import static horserace.ui.PixelStyle.DIRT_LIGHT;
import static horserace.ui.PixelStyle.GRASS;
import static horserace.ui.PixelStyle.GRASS_DARK;
import static horserace.ui.TrackLayout.FINISH_X;
import static horserace.ui.TrackLayout.LABEL_WIDTH;
import static horserace.ui.TrackLayout.TOP;

/** 움직이지 않는 배경(잔디, 흙길, 울타리, 결승선). 한 번 그려 두고 매 프레임 그대로 붙인다. */
final class TrackBackground {
    private static final int GRAVEL_COUNT = 900;
    private static final int GRASS_STRIPE = 8;
    private static final int FENCE_POST_GAP = 16;

    private TrackBackground() {
    }

    static BufferedImage create(int width, int height, int laneCount) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D c = image.createGraphics();
        PixelStyle.crisp(c);

        int trackBottom = TrackLayout.laneTop(laneCount);
        drawGrass(c, width, height);
        drawDirt(c, width, trackBottom);
        drawFences(c, width, laneCount);
        drawFinishLine(c, trackBottom);

        c.dispose();
        return image;
    }

    /** 줄무늬로 깎은 잔디. */
    private static void drawGrass(Graphics2D c, int width, int height) {
        for (int x = 0; x < width; x += GRASS_STRIPE) {
            c.setColor((x / GRASS_STRIPE) % 2 == 0 ? GRASS : GRASS_DARK);
            c.fillRect(x, 0, GRASS_STRIPE, height);
        }
    }

    /** 흙길 + 자갈 점 (항상 같은 무늬가 나오게 seed 고정). */
    private static void drawDirt(Graphics2D c, int width, int trackBottom) {
        int trackWidth = width - LABEL_WIDTH;
        int trackHeight = trackBottom - TOP;
        c.setColor(DIRT);
        c.fillRect(LABEL_WIDTH, TOP, trackWidth, trackHeight);

        Random gravel = new Random(7);
        for (int k = 0; k < GRAVEL_COUNT; k++) {
            c.setColor(gravel.nextBoolean() ? DIRT_DARK : DIRT_LIGHT);
            c.fillRect(LABEL_WIDTH + gravel.nextInt(trackWidth), TOP + gravel.nextInt(trackHeight), 1, 1);
        }
    }

    /** 레인 구분 흰 울타리. */
    private static void drawFences(Graphics2D c, int width, int laneCount) {
        for (int lane = 0; lane <= laneCount; lane++) {
            int y = TrackLayout.laneTop(lane);
            c.setColor(PixelStyle.WHITE);
            c.fillRect(LABEL_WIDTH, y, width - LABEL_WIDTH, 1);
            for (int x = LABEL_WIDTH; x < width; x += FENCE_POST_GAP) {
                c.fillRect(x, y - 1, 2, 3);
            }
            c.setColor(DIRT_DARK);
            c.fillRect(LABEL_WIDTH, y + 1, width - LABEL_WIDTH, 1);
        }
    }

    /** 체크무늬 결승선 + 깃발. */
    private static void drawFinishLine(Graphics2D c, int trackBottom) {
        for (int y = TOP; y < trackBottom; y += 2) {
            for (int col = 0; col < 3; col++) {
                c.setColor(((y / 2) + col) % 2 == 0 ? PixelStyle.WHITE : PixelStyle.INK);
                c.fillRect(FINISH_X + col * 2, y, 2, 2);
            }
        }
        c.setColor(PixelStyle.WHITE);
        c.fillRect(FINISH_X + 2, TOP - 12, 1, 12);
        c.setColor(PixelStyle.RED);
        c.fillRect(FINISH_X + 3, TOP - 12, 6, 4);
    }
}
