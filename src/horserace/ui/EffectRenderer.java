package horserace.ui;

import horserace.model.event.BoostEvent;
import horserace.model.event.FallEvent;
import horserace.model.event.InterfereEvent;
import horserace.model.event.JumpEvent;
import horserace.model.event.RandomEvent;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.Random;

/**
 * 이벤트 효과를 도트로 그린다. 어떤 효과를 그릴지는 이벤트 종류로 정한다.
 * t는 효과 진행도(0.0 ~ 1.0), 좌표는 말 중심의 도트 좌표.
 */
final class EffectRenderer {
    private static final double PROJECTILE_TIME = 0.45; // 방해 구슬이 날아가는 구간 (전체 진행도 중)

    private EffectRenderer() {
    }

    /** 효과를 받는 동안 말이 얼마나 움직여 보일지 (점프는 위로, 넘어짐/방해는 좌우로 흔들림). */
    static Point horseOffset(Effect effect, double t) {
        RandomEvent event = effect.event();
        if (event instanceof JumpEvent) {
            return new Point(0, -(int) (Math.sin(Math.PI * t) * 16));
        }
        boolean hit = event instanceof FallEvent || event instanceof InterfereEvent && t > PROJECTILE_TIME;
        if (hit && t < 0.8) {
            return new Point((int) (t * 20) % 2 == 0 ? 2 : -2, 0);
        }
        return new Point(0, 0);
    }

    /** 말 뒤에 깔리는 효과 (부스터 불꽃). */
    static void drawBehind(Graphics2D c, Effect effect, double t, Point target) {
        if (effect.event() instanceof BoostEvent) {
            drawBoostFlame(c, target, t, effect.target().getNumber());
        }
    }

    /**
     * 말 위에 그려지는 효과 (글자, 파편, 투사체).
     * @param textLift 같은 말에 효과가 겹칠 때 글자를 위로 올리는 양
     */
    static void drawFront(Graphics2D c, Effect effect, double t, Point target, Point user, int textLift) {
        RandomEvent event = effect.event();
        int x = target.x;
        int y = target.y;

        if (event instanceof BoostEvent) {
            popText(c, "BOOST!", x, y - 20 - textLift, PixelStyle.YELLOW, t);
        } else if (event instanceof JumpEvent) {
            drawLandingDust(c, x, y, t);
            popText(c, "JUMP!", x, y - 28 - textLift, PixelStyle.SKY, t);
        } else if (event instanceof FallEvent) {
            pixelBurst(c, x, y, t, PixelStyle.RED, PixelStyle.YELLOW);
            popText(c, "넘어짐!", x, y - 20 - textLift, PixelStyle.WHITE, t);
        } else if (event instanceof InterfereEvent) {
            if (t < PROJECTILE_TIME) {
                drawProjectile(c, user, target, t / PROJECTILE_TIME);
            } else {
                double hitT = (t - PROJECTILE_TIME) / (1 - PROJECTILE_TIME);
                pixelBurst(c, x, y, hitT, PixelStyle.VIOLET, PixelStyle.WHITE);
                popText(c, "방해!", x, y - 20 - textLift, PixelStyle.LAVENDER, hitT);
            }
        } else {
            drawGenericRing(c, x, y, t); // 새로 추가한 스킬도 기본 효과는 보이게
        }
    }

    /** 넘어져 있는 동안 머리 위로 별이 빙글빙글. */
    static void drawDizzyStars(Graphics2D c, int centerX, int headY, long now) {
        c.setColor(PixelStyle.YELLOW);
        for (int k = 0; k < 3; k++) {
            double angle = now / 150.0 + k * Math.PI * 2 / 3;
            int x = centerX + 10 + (int) (Math.cos(angle) * 8);
            int y = headY - 4 + (int) (Math.sin(angle) * 3);
            c.fillRect(x, y - 1, 1, 3);
            c.fillRect(x - 1, y, 3, 1);
        }
    }

    // ---------- 개별 효과 ----------

    private static void drawBoostFlame(Graphics2D c, Point horse, double t, int seed) {
        int x = horse.x - HorseSprite.WIDTH / 2 + 2;
        int y = horse.y + 2;
        Random flicker = new Random(System.currentTimeMillis() / 50 + seed); // 50ms마다 모양이 바뀜

        for (int row = -2; row <= 2; row++) {
            int length = (int) ((10 + flicker.nextInt(10)) * (1 - t * 0.6)) - Math.abs(row) * 3;
            c.setColor(row == 0 ? PixelStyle.WHITE : Math.abs(row) == 1 ? PixelStyle.YELLOW : PixelStyle.ORANGE);
            c.fillRect(x - length, y + row * 2, Math.max(0, length), 2);
        }
        c.setColor(PixelStyle.WHITE); // 속도선
        for (int k = 0; k < 3; k++) {
            c.fillRect(x - 28 - flicker.nextInt(10), y - 9 + k * 9, 8, 1);
        }
    }

    private static void drawLandingDust(Graphics2D c, int x, int y, double t) {
        if (t <= 0.75) {
            return;
        }
        int spread = (int) ((t - 0.75) * 40);
        c.setColor(PixelStyle.DIRT_LIGHT);
        c.fillRect(x - 10 - spread, y + 12, 3, 2);
        c.fillRect(x + 8 + spread, y + 12, 3, 2);
        c.fillRect(x - 5 - spread / 2, y + 10, 2, 2);
        c.fillRect(x + 4 + spread / 2, y + 10, 2, 2);
    }

    /** 사용자 말에서 대상 말까지 포물선으로 날아가는 보라 구슬. p: 0.0 ~ 1.0 */
    private static void drawProjectile(Graphics2D c, Point from, Point to, double p) {
        int x = (int) (from.x + (to.x - from.x) * p);
        int y = (int) (from.y + (to.y - from.y) * p - Math.sin(Math.PI * p) * 20);
        c.setColor(PixelStyle.VIOLET);
        c.fillRect(x - 3, y - 2, 6, 4);
        c.fillRect(x - 2, y - 3, 4, 6);
        c.setColor(PixelStyle.WHITE);
        c.fillRect(x - 1, y - 1, 2, 2);
    }

    private static void drawGenericRing(Graphics2D c, int x, int y, double t) {
        int radius = 6 + (int) (t * 12);
        c.setColor(PixelStyle.YELLOW);
        for (int k = 0; k < 12; k++) {
            double angle = Math.PI * 2 * k / 12;
            c.fillRect(x + (int) (Math.cos(angle) * radius), y + (int) (Math.sin(angle) * radius), 2, 2);
        }
    }

    /** 사방으로 튀는 도트 파편. */
    private static void pixelBurst(Graphics2D c, int x, int y, double t, Color main, Color spark) {
        int radius = 4 + (int) (t * 14);
        int size = t < 0.6 ? 3 : 2;
        for (int k = 0; k < 8; k++) {
            double angle = Math.PI * 2 * k / 8 + Math.PI / 8;
            c.setColor(k % 2 == 0 ? main : spark);
            c.fillRect(x + (int) (Math.cos(angle) * radius) - 1, y + (int) (Math.sin(angle) * radius) - 1, size, size);
        }
        if (t < 0.3) { // 처음에 번쩍
            c.setColor(spark);
            c.fillRect(x - 3, y - 1, 6, 2);
            c.fillRect(x - 1, y - 3, 2, 6);
        }
    }

    /** 튀어 올랐다가 끝에 깜빡이며 사라지는 도트 글자. */
    private static void popText(Graphics2D c, String text, int x, int y, Color color, double t) {
        boolean blinkOff = t > 0.7 && (int) (t * 30) % 2 == 0;
        if (blinkOff) {
            return;
        }
        int bounce = t < 0.15 ? (int) ((0.15 - t) * 40) : 0;
        int rise = (int) (t * 8);
        c.setFont(PixelStyle.boldFont());
        int width = c.getFontMetrics().stringWidth(text);
        PixelStyle.outlinedText(c, text, x - width / 2, y - rise + bounce, color, PixelStyle.INK, 1);
    }
}
