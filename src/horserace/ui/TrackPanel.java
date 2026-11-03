package horserace.ui;

import horserace.game.Race;
import horserace.game.TurnEvent;
import horserace.model.horse.Horse;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static horserace.ui.TrackLayout.FINISH_X;
import static horserace.ui.TrackLayout.LABEL_WIDTH;
import static horserace.ui.TrackLayout.LANE_HEIGHT;
import static horserace.ui.TrackLayout.SCALE;
import static horserace.ui.TrackLayout.TOP;
import static horserace.ui.TrackLayout.TRACK_WIDTH;

/**
 * 경주 트랙 화면. 말 위치는 Race의 getter로만 읽는다.
 *
 * 그리는 방법: 작은 해상도(도트)의 캔버스에 안티앨리어싱 없이 그린 뒤,
 * 픽셀을 그대로 SCALE배 키워서 화면에 붙인다. 그래서 모든 것이 도트처럼 보인다.
 * 그림 세부는 TrackBackground(배경), HorseSprite(말), EffectRenderer(효과)가 맡는다.
 */
public class TrackPanel extends JPanel {
    private static final int FRAME_MS = 16;          // 약 60fps
    private static final int LEG_FRAME_MS = 110;     // 다리 그림이 바뀌는 간격
    private static final String IMAGE_DIR = "images";
    private static final String[] IMAGE_EXTENSIONS = {"png", "gif", "jpg", "jpeg"};

    private final Race race;
    private final Map<Horse, Image> photos = new HashMap<>();
    private final List<Effect> effects = new ArrayList<>();

    // 부드러운 이동: 턴마다 moveFrom -> 실제 위치까지 moveDurationMs 동안 일정한 속도로 이동
    private final Map<Horse, Double> displayPositions = new HashMap<>();
    private final Map<Horse, Double> moveFrom = new HashMap<>();
    private long moveStartedAt;
    private int moveDurationMs = 1;

    private String banner = "";
    private Horse selectedHorse;
    private BufferedImage canvas;
    private BufferedImage background;

    public TrackPanel(Race race, Consumer<Horse> onHorseClick) {
        this.race = race;
        for (Horse horse : race.getHorses()) {
            loadPhoto(horse);
        }
        resetDisplay();
        setPreferredSize(new Dimension(TRACK_WIDTH * SCALE, TrackLayout.height(laneCount()) * SCALE));
        setBackground(PixelStyle.GRASS);
        addLaneClickListener(onHorseClick);
        new Timer(FRAME_MS, e -> animate()).start();
    }

    // ---------- GameFrame이 호출하는 메서드 ----------

    /** 새 레이스 시작 시: 화면 위치를 실제 위치로 바로 맞추고 효과를 지운다. */
    public void resetDisplay() {
        for (Horse horse : race.getHorses()) {
            displayPositions.put(horse, (double) horse.getPosition());
            moveFrom.put(horse, (double) horse.getPosition());
        }
        moveStartedAt = 0;
        effects.clear();
        repaint();
    }

    /**
     * 턴이 진행된 직후 호출: 지금 화면 위치에서 실제 위치까지 durationMs 동안 일정한 속도로 이동한다.
     * 다음 턴은 이동이 끝나는 순간 시작되므로 말이 멈추지 않고 계속 달린다.
     */
    public void moveToCurrent(int durationMs) {
        for (Horse horse : race.getHorses()) {
            moveFrom.put(horse, displayPositions.get(horse));
        }
        moveStartedAt = System.currentTimeMillis();
        moveDurationMs = Math.max(1, durationMs);
    }

    public void playEffect(TurnEvent event) {
        effects.add(new Effect(event, System.currentTimeMillis()));
    }

    public void setSelectedHorse(Horse horse) {
        selectedHorse = horse;
        repaint();
    }

    /** 트랙 위쪽 안내 문구. */
    public void setBanner(String text) {
        banner = text == null ? "" : text;
        repaint();
    }

    // ---------- 애니메이션 ----------

    private void animate() {
        long now = System.currentTimeMillis();
        double t = Math.min(1.0, (now - moveStartedAt) / (double) moveDurationMs);
        for (Horse horse : race.getHorses()) {
            double from = moveFrom.get(horse);
            displayPositions.put(horse, from + (horse.getPosition() - from) * t);
        }
        effects.removeIf(effect -> effect.isFinished(now));
        repaint();
    }

    /** 다리를 움직일지: 결승 전이고 이동 중이면 true. */
    private boolean isRunning(Horse horse, long now) {
        double shown = displayPositions.get(horse);
        boolean moving = Math.abs(horse.getPosition() - shown) > 0.01
                || now - moveStartedAt < moveDurationMs + 50;
        return shown < Race.FINISH_LINE && moving && !horse.isStunned();
    }

    // ---------- 그리기 ----------

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int height = Math.max(1, getHeight() / SCALE);
        if (canvas == null || canvas.getHeight() != height) {
            canvas = new BufferedImage(TRACK_WIDTH, height, BufferedImage.TYPE_INT_RGB);
            background = TrackBackground.create(TRACK_WIDTH, height, laneCount());
        }

        Graphics2D c = canvas.createGraphics();
        PixelStyle.crisp(c);
        c.drawImage(background, 0, 0, null);
        drawScene(c, System.currentTimeMillis());
        c.dispose();

        Graphics2D g2 = (Graphics2D) g.create();
        PixelStyle.crisp(g2);
        int x = (getWidth() - TRACK_WIDTH * SCALE) / 2;
        g2.drawImage(canvas, x, 0, TRACK_WIDTH * SCALE, height * SCALE, null); // 픽셀 그대로 키우기
        g2.dispose();
    }

    /** 뒤에서 앞 순서로: 글자 -> 레인 -> 말 뒤 효과 -> 말 -> 말 위 효과. */
    private void drawScene(Graphics2D c, long now) {
        drawHeader(c);
        List<Horse> horses = race.getHorses();
        for (int lane = 0; lane < horses.size(); lane++) {
            drawLane(c, horses.get(lane), lane, now);
        }
        for (Effect effect : effects) {
            EffectRenderer.drawBehind(c, effect, effect.progress(now), horsePoint(effect.target()));
        }
        for (Horse horse : horses) {
            drawHorse(c, horse, now);
        }
        drawEffectsInFront(c, now);
    }

    private void drawHeader(Graphics2D c) {
        if (!banner.isEmpty()) {
            c.setFont(PixelStyle.boldFont());
            PixelStyle.outlinedText(c, banner, 8, 15, PixelStyle.WHITE, PixelStyle.INK, 1);
        }
        c.setFont(PixelStyle.font());
        PixelStyle.outlinedText(c, "FINISH", FINISH_X - 38, TOP - 4, PixelStyle.YELLOW, PixelStyle.INK, 1);
    }

    /** 레인 하나: 선택 표시, 이름표, 레인 번호, 지나온 자국. */
    private void drawLane(Graphics2D c, Horse horse, int lane, long now) {
        int top = TrackLayout.laneTop(lane);

        if (horse == selectedHorse) {
            drawSelection(c, top, now);
        }

        PixelStyle.fillPixelBox(c, 7, top + 4, LABEL_WIDTH - 9, LANE_HEIGHT - 8, 1,
                PixelStyle.INK, HorseSprite.colorOf(horse));
        c.setFont(PixelStyle.boldFont());
        c.setColor(PixelStyle.WHITE);
        c.drawString(horse.getNumber() + "." + horse.getName(), 11, top + 18);
        c.setFont(PixelStyle.font());
        c.setColor(PixelStyle.GRAY);
        c.drawString(horse.getType() + " " + Formats.shortOdds(horse.getOdds()), 11, top + 31);

        c.setColor(PixelStyle.DIRT_DARK);
        c.drawString(String.valueOf(lane + 1), LABEL_WIDTH + 3, top + 25);

        // 지나온 자국 (콘솔 버전의 "-")
        int horseLeft = HorseSprite.left(horsePoint(horse).x);
        int trailY = TrackLayout.laneCenterY(lane) + 10;
        c.setColor(PixelStyle.DIRT_LIGHT);
        for (int x = LABEL_WIDTH + 14; x < horseLeft; x += 6) {
            c.fillRect(x, trailY, 3, 1);
        }
    }

    /** 선택된 레인: 빙글빙글 도는 점선 테두리 + 깜빡이는 화살표. */
    private void drawSelection(Graphics2D c, int top, long now) {
        int x = 1;
        int y = top + 1;
        int w = FINISH_X - 2;
        int h = LANE_HEIGHT - 1;
        int phase = (int) (now / 80) % 6;

        c.setColor(PixelStyle.YELLOW);
        for (int i = 0; i < w; i++) {
            if ((i + phase) % 6 < 3) {
                c.fillRect(x + i, y, 1, 1);
                c.fillRect(x + w - 1 - i, y + h - 1, 1, 1);
            }
        }
        for (int i = 0; i < h; i++) {
            if ((i + phase) % 6 < 3) {
                c.fillRect(x + w - 1, y + i, 1, 1);
                c.fillRect(x, y + h - 1 - i, 1, 1);
            }
        }
        boolean arrowVisible = (now / 300) % 2 == 0;
        if (arrowVisible) {
            c.fillRect(2, top + 16, 2, 8);
            c.fillRect(4, top + 18, 2, 4);
        }
    }

    /** 말 + (사진이 있으면) 등에 탄 기수 얼굴 + 번호 + 넘어짐 별. */
    private void drawHorse(Graphics2D c, Horse horse, long now) {
        Point center = horsePoint(horse);
        for (Effect effect : effects) {
            if (effect.target() == horse) {
                Point offset = EffectRenderer.horseOffset(effect, effect.progress(now));
                center.translate(offset.x, offset.y);
            }
        }

        boolean stride = isRunning(horse, now) && (now / LEG_FRAME_MS + horse.getNumber()) % 2 == 0;
        Color body = horse.isStunned() ? PixelStyle.GRAY : HorseSprite.colorOf(horse);
        HorseSprite.draw(c, center.x, center.y, body, stride);

        int left = HorseSprite.left(center.x);
        int top = HorseSprite.top(center.y);
        int numberY = top + 4;

        Image photo = photos.get(horse);
        if (photo != null) {
            int size = 16;
            int photoX = left + 9;
            int photoY = top - size + 8;
            c.setColor(PixelStyle.INK);
            c.fillRect(photoX - 1, photoY - 1, size + 2, size + 2);
            c.drawImage(photo, photoX, photoY, size, size, this);
            numberY = photoY + 6;
        }

        c.setFont(PixelStyle.boldFont());
        PixelStyle.outlinedText(c, String.valueOf(horse.getNumber()), left - 6, numberY,
                PixelStyle.WHITE, HorseSprite.colorOf(horse).darker(), 1);

        if (horse.isStunned()) {
            EffectRenderer.drawDizzyStars(c, center.x, top, now);
        }
    }

    /** 같은 말에 효과가 겹치면 글자를 한 줄씩 위로 쌓는다. */
    private void drawEffectsInFront(Graphics2D c, long now) {
        Map<Horse, Integer> stackCount = new HashMap<>();
        for (Effect effect : effects) {
            int level = stackCount.merge(effect.target(), 1, Integer::sum) - 1;
            EffectRenderer.drawFront(c, effect, effect.progress(now),
                    horsePoint(effect.target()), horsePoint(effect.user()), level * 13);
        }
    }

    // ---------- 도우미 ----------

    private int laneCount() {
        return race.getHorses().size();
    }

    /** 말 중심의 도트 좌표 (화면에 보이는 위치 기준). */
    private Point horsePoint(Horse horse) {
        int lane = race.getHorses().indexOf(horse);
        return new Point(TrackLayout.positionToX(displayPositions.get(horse)), TrackLayout.laneCenterY(lane));
    }

    /** 레인을 클릭하면 그 레인의 말을 선택. 레인 위에서는 손가락 커서. */
    private void addLaneClickListener(Consumer<Horse> onHorseClick) {
        MouseAdapter mouse = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Horse horse = horseAt(e.getY());
                if (horse != null) {
                    onHorseClick.accept(horse);
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                int cursor = horseAt(e.getY()) != null ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR;
                setCursor(Cursor.getPredefinedCursor(cursor));
            }
        };
        addMouseListener(mouse);
        addMouseMotionListener(mouse);
    }

    /** 화면 y 좌표에 있는 레인의 말. 레인 밖이면 null. */
    private Horse horseAt(int screenY) {
        int lane = TrackLayout.laneAt(screenY / SCALE, laneCount());
        return lane < 0 ? null : race.getHorses().get(lane);
    }

    /** images/horse{번호}.png 를 먼저, 없으면 공용 images/horse.png 를 찾는다. 둘 다 없으면 사진 없이 그린다. */
    private void loadPhoto(Horse horse) {
        for (String baseName : new String[]{"horse" + horse.getNumber(), "horse"}) {
            for (String extension : IMAGE_EXTENSIONS) {
                File file = new File(IMAGE_DIR, baseName + "." + extension);
                Image image = file.isFile() ? readImage(file) : null;
                if (image != null) {
                    photos.put(horse, image);
                    return;
                }
            }
        }
    }

    private static Image readImage(File file) {
        if (file.getName().endsWith(".gif")) {
            return new ImageIcon(file.getPath()).getImage(); // ImageIcon은 GIF 애니메이션을 지원
        }
        try {
            return ImageIO.read(file);
        } catch (IOException e) {
            return null; // 읽을 수 없는 파일은 건너뛴다
        }
    }
}
