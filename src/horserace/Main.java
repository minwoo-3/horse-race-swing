package horserace;

import horserace.ui.GameFrame;
import horserace.ui.PixelStyle;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Swing 화면은 이벤트 디스패치 스레드(EDT)에서 만든다.
        SwingUtilities.invokeLater(() -> {
            PixelStyle.install(); // 도트 폰트/색 적용
            new GameFrame("플레이어").setVisible(true);
        });
    }
}
