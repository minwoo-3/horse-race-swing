package horserace.ui;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.metal.MetalLookAndFeel;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.io.File;
import java.io.IOException;

/**
 * 도트(픽셀아트) 화면 스타일: 색상표, 픽셀 폰트(Galmuri11), 픽셀 테두리와 버튼.
 * 폰트 파일은 fonts/ 폴더에 있다 (SIL Open Font License).
 */
public final class PixelStyle {
    /** Swing 컴포넌트(버튼, 테두리)의 도트 한 칸 크기. */
    public static final int UNIT = 2;
    /** Galmuri11은 12px 격자에 맞춰 그려진 폰트라 12의 배수에서 선명하다. */
    public static final int FONT_SIZE = 12;

    // ---------- 색상표 (적은 색만 쓰는 게 도트 느낌의 핵심) ----------
    public static final Color INK = new Color(0x1A1C2C);
    public static final Color NAVY = new Color(0x29366F);
    public static final Color BLUE = new Color(0x3B5DC9);
    public static final Color SKY = new Color(0x41A6F6);
    public static final Color VIOLET = new Color(0x8E4EC6);
    public static final Color LAVENDER = new Color(0xD7B4F3);
    public static final Color WHITE = new Color(0xF4F4F4);
    public static final Color GRAY = new Color(0x94B0C2);
    public static final Color DARK_GRAY = new Color(0x566C86);
    public static final Color YELLOW = new Color(0xFFCD75);
    public static final Color ORANGE = new Color(0xEF7D57);
    public static final Color RED = new Color(0xB13E53);

    // 트랙
    public static final Color GRASS = new Color(0x38B764);
    public static final Color GRASS_DARK = new Color(0x2C9A54);
    public static final Color DIRT = new Color(0xC28A57);
    public static final Color DIRT_DARK = new Color(0xA8703F);
    public static final Color DIRT_LIGHT = new Color(0xD9A877);

    private static Font regular = new Font(Font.DIALOG, Font.PLAIN, FONT_SIZE);
    private static Font bold = new Font(Font.DIALOG, Font.BOLD, FONT_SIZE);

    private PixelStyle() {
    }

    /** 프로그램 시작 시 한 번 호출: 폰트 등록 + Swing 기본 모양을 도트 스타일로 바꾼다. */
    public static void install() {
        regular = loadFont("fonts/Galmuri11.ttf", regular);
        bold = loadFont("fonts/Galmuri11-Bold.ttf", bold);
        useMetalLookAndFeel();
        applyFontToAllComponents();
        applyColors();
    }

    public static Font font() {
        return regular;
    }

    public static Font boldFont() {
        return bold;
    }

    // ---------- 그리기 도우미 ----------

    /** 픽셀이 뭉개지지 않게 안티앨리어싱과 보간을 끈다. */
    public static void crisp(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
    }

    /** 모서리가 한 칸씩 깎인 픽셀 상자 (테두리 edge, 안쪽 fill). */
    public static void fillPixelBox(Graphics g, int x, int y, int w, int h, int unit, Color fill, Color edge) {
        g.setColor(edge);
        g.fillRect(x + unit, y, w - unit * 2, h);
        g.fillRect(x, y + unit, w, h - unit * 2);
        g.setColor(fill);
        g.fillRect(x + unit * 2, y + unit, w - unit * 4, h - unit * 2);
        g.fillRect(x + unit, y + unit * 2, w - unit * 2, h - unit * 4);
    }

    /** 8방향으로 한 칸씩 외곽선을 두른 글자 (도트 게임 스타일). */
    public static void outlinedText(Graphics g, String text, int x, int y, Color fill, Color outline, int unit) {
        g.setColor(outline);
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx != 0 || dy != 0) {
                    g.drawString(text, x + dx * unit, y + dy * unit);
                }
            }
        }
        g.setColor(fill);
        g.drawString(text, x, y);
    }

    // ---------- 컴포넌트 ----------

    public static JButton button(String text) {
        return new PixelButton(text);
    }

    /** 입력칸/콤보박스를 어두운 바탕 + 픽셀 테두리로. */
    public static void styleField(JComponent field) {
        field.setFont(regular);
        field.setBackground(INK);
        field.setForeground(WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
                new PixelBorder(GRAY, 0),
                BorderFactory.createEmptyBorder(2, 6, 2, 6)));
    }

    // ---------- install() 내부 ----------

    private static Font loadFont(String path, Font fallback) {
        File file = new File(path);
        if (!file.isFile()) {
            return fallback;
        }
        try {
            Font font = Font.createFont(Font.TRUETYPE_FONT, file).deriveFont(Font.PLAIN, (float) FONT_SIZE);
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font);
            return font;
        } catch (FontFormatException | IOException e) {
            return fallback;
        }
    }

    /** 맥 기본 모양(Aqua)은 버튼 색을 바꿀 수 없어서 Java 기본 모양(Metal)을 쓴다. */
    private static void useMetalLookAndFeel() {
        try {
            UIManager.setLookAndFeel(new MetalLookAndFeel());
        } catch (Exception ignored) {
            // 실패하면 기본 모양 그대로
        }
        UIManager.getLookAndFeelDefaults().remove("Button.gradient"); // 도트는 단색이라 그라데이션 끄기
    }

    private static void applyFontToAllComponents() {
        FontUIResource uiFont = new FontUIResource(regular);
        UIDefaults defaults = UIManager.getLookAndFeelDefaults();
        for (Object key : defaults.keySet().toArray()) {
            if (key.toString().endsWith(".font")) {
                UIManager.put(key, uiFont);
            }
        }
        UIManager.put("OptionPane.buttonFont", new FontUIResource(bold));
    }

    private static void applyColors() {
        ColorUIResource ink = new ColorUIResource(INK);
        ColorUIResource navy = new ColorUIResource(NAVY);
        ColorUIResource white = new ColorUIResource(WHITE);
        ColorUIResource gray = new ColorUIResource(GRAY);

        UIManager.put("Panel.background", navy);
        UIManager.put("OptionPane.background", navy);
        UIManager.put("OptionPane.messageForeground", white);
        UIManager.put("Label.foreground", white);

        UIManager.put("Button.background", new ColorUIResource(ORANGE));
        UIManager.put("Button.foreground", white);
        UIManager.put("Button.select", new ColorUIResource(RED));
        UIManager.put("Button.focus", new ColorUIResource(YELLOW));
        UIManager.put("Button.disabledText", gray);
        UIManager.put("Button.border", new BorderUIResource(new PixelBorder(INK, 4)));

        UIManager.put("TextField.background", ink);
        UIManager.put("TextField.foreground", white);
        UIManager.put("TextField.caretForeground", new ColorUIResource(YELLOW));
        UIManager.put("TextField.border", new BorderUIResource(new PixelBorder(GRAY, 4)));

        UIManager.put("ComboBox.background", ink);
        UIManager.put("ComboBox.foreground", white);
        UIManager.put("ComboBox.selectionBackground", new ColorUIResource(BLUE));
        UIManager.put("ComboBox.selectionForeground", white);
        UIManager.put("ComboBox.disabledBackground", new ColorUIResource(DARK_GRAY));
        UIManager.put("ComboBox.disabledForeground", gray);
        UIManager.put("List.background", ink);
        UIManager.put("List.foreground", white);
        UIManager.put("PopupMenu.border", new BorderUIResource(new PixelBorder(GRAY, 0)));
    }

    // ---------- 컴포넌트 클래스 ----------

    /** 두께 UNIT, 모서리가 한 칸 깎인 픽셀 테두리. */
    public static class PixelBorder extends AbstractBorder {
        private final Color color;
        private final int padding;

        public PixelBorder(Color color, int padding) {
            this.color = color;
            this.padding = padding;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            int u = UNIT;
            g.setColor(color);
            g.fillRect(x + u, y, w - u * 2, u);           // 위
            g.fillRect(x + u, y + h - u, w - u * 2, u);   // 아래
            g.fillRect(x, y + u, u, h - u * 2);           // 왼쪽
            g.fillRect(x + w - u, y + u, u, h - u * 2);   // 오른쪽
        }

        @Override
        public Insets getBorderInsets(Component c) {
            int inset = UNIT + padding;
            return new Insets(inset, inset + padding, inset, inset + padding);
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            Insets i = getBorderInsets(c);
            insets.set(i.top, i.left, i.bottom, i.right);
            return insets;
        }
    }

    /** 누르면 아래로 한 칸 눌리는 픽셀 버튼. */
    private static final class PixelButton extends JButton {
        PixelButton(String text) {
            super(text);
            setFont(bold);
            setForeground(WHITE);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
        }

        @Override
        public Dimension getPreferredSize() {
            Dimension size = super.getPreferredSize();
            return new Dimension(size.width + 16, Math.max(size.height, 30));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            crisp(g2);
            int u = UNIT;
            int w = getWidth();
            int h = getHeight();
            boolean enabled = isEnabled();
            boolean pressed = getModel().isArmed() && getModel().isPressed();
            int pressOffset = pressed ? u : 0;
            Color face = !enabled ? DARK_GRAY : getModel().isRollover() ? SKY : ORANGE;

            fillPixelBox(g2, 0, u, w, h - u, u, INK, INK);               // 그림자
            fillPixelBox(g2, 0, pressOffset, w, h - u, u, face, INK);    // 버튼 면
            g2.setColor(new Color(255, 255, 255, enabled ? 90 : 30));
            g2.fillRect(u * 2, pressOffset + u, w - u * 4, u);           // 윗면 하이라이트

            g2.setFont(getFont());
            String text = getText();
            int textX = (w - g2.getFontMetrics().stringWidth(text)) / 2;
            int textY = pressOffset + (h - u) / 2 + g2.getFontMetrics().getAscent() / 2 - 1;
            g2.setColor(enabled ? WHITE : GRAY);
            g2.drawString(text, textX, textY);
            g2.dispose();
        }
    }
}
