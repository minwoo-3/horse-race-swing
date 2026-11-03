package horserace.ui;

import horserace.model.horse.Horse;

import java.awt.Color;
import java.awt.Graphics2D;

/** 도트로 그린 말 그림. 16 x 12칸 그림을 한 칸당 2도트로 그린다. */
final class HorseSprite {
    private static final int ART_PIXEL = 2;
    static final int WIDTH = 16 * ART_PIXEL;
    static final int HEIGHT = 12 * ART_PIXEL;

    /** 말 번호별 고정 색 (레인이 바뀌어도 같은 색). */
    private static final Color[] HORSE_COLORS = {
            new Color(0xB13E53), new Color(0x3B5DC9), new Color(0x38B764),
            new Color(0xEF7D57), new Color(0x8E4EC6), new Color(0x41A6F6)
    };
    private static final Color MANE = new Color(0x3B2416);
    private static final Color HOOF = new Color(0x333C57);

    /*
     * 오른쪽을 보는 말. 글자 하나 = 한 칸.
     * B: 몸 색, D: 몸 그림자, M: 갈기/꼬리, K: 눈, H: 발굽, .: 투명
     */
    private static final String[] BODY = {
            "............BB..",
            "...........BBBB.",
            "..........MBBKBB",
            "..........MBBBBB",
            ".........MBBB...",
            "M.BBBBBBBBBBB...",
            "MMBBBBBBBBBBB...",
            "M.DBBBBBBBBBD...",
            "..DDBBBBBBBD....",
    };
    private static final String[] LEGS_STRIDE = {   // 달리기 1: 다리를 벌림
            "..B.B.....B.B...",
            ".B...B...B...B..",
            "H.....H.H.....H.",
    };
    private static final String[] LEGS_TUCK = {     // 달리기 2 / 서 있기: 다리를 모음
            "...BB.....BB....",
            "...BB.....BB....",
            "...HH.....HH....",
    };

    private HorseSprite() {
    }

    static Color colorOf(Horse horse) {
        return HORSE_COLORS[(horse.getNumber() - 1) % HORSE_COLORS.length];
    }

    /**
     * 말 한 마리를 그린다.
     * @param stride true면 다리를 벌린 프레임 (달리는 중에 번갈아 바꾼다)
     */
    static void draw(Graphics2D c, int centerX, int centerY, Color body, boolean stride) {
        int left = left(centerX);
        int top = top(centerY);

        c.setColor(PixelStyle.DIRT_DARK); // 그림자
        c.fillRect(left + 4, top + HEIGHT, WIDTH - 10, 2);

        drawRows(c, BODY, left, top, body);
        drawRows(c, stride ? LEGS_STRIDE : LEGS_TUCK, left, top + BODY.length * ART_PIXEL, body);
    }

    static int left(int centerX) {
        return centerX - WIDTH / 2;
    }

    static int top(int centerY) {
        return centerY - HEIGHT / 2 + 2;
    }

    private static void drawRows(Graphics2D c, String[] rows, int left, int top, Color body) {
        Color shade = body.darker();
        for (int row = 0; row < rows.length; row++) {
            for (int col = 0; col < rows[row].length(); col++) {
                Color color = switch (rows[row].charAt(col)) {
                    case 'B' -> body;
                    case 'D' -> shade;
                    case 'M' -> MANE;
                    case 'K' -> PixelStyle.INK;
                    case 'H' -> HOOF;
                    default -> null;
                };
                if (color != null) {
                    c.setColor(color);
                    c.fillRect(left + col * ART_PIXEL, top + row * ART_PIXEL, ART_PIXEL, ART_PIXEL);
                }
            }
        }
    }
}
