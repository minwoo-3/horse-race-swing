package horserace.ui;

import horserace.model.horse.Horse;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.plaf.basic.BasicComboBoxUI;
import java.awt.Component;
import java.awt.FlowLayout;
import java.util.List;
import java.util.function.Consumer;

/** 잔액 표시, 말 선택, 베팅 금액 입력, 출발 버튼. */
public class BetPanel extends JPanel {
    private final JLabel balanceLabel = new JLabel();
    private final JComboBox<Horse> horseBox;
    private final JTextField amountField = new JTextField(8);
    private final JButton startButton = PixelStyle.button("출발!");

    public BetPanel(List<Horse> horses, Runnable onStart, Consumer<Horse> onSelect) {
        super(new FlowLayout(FlowLayout.LEFT, 10, 8));
        setBackground(PixelStyle.INK);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, PixelStyle.UNIT * 2, 0, PixelStyle.NAVY),
                BorderFactory.createEmptyBorder(2, 6, 2, 6)));

        balanceLabel.setFont(PixelStyle.boldFont());
        balanceLabel.setForeground(PixelStyle.YELLOW);

        horseBox = new JComboBox<>(horses.toArray(new Horse[0]));
        horseBox.setUI(new BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                return PixelStyle.button("v");
            }
        });
        PixelStyle.styleField(horseBox);
        PixelStyle.styleField(amountField);
        horseBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Horse horse) {
                    setText(Formats.horse(horse));
                }
                return this;
            }
        });

        horseBox.addActionListener(e -> onSelect.accept(getSelectedHorse())); // 콤보박스로 고르면 트랙에도 표시
        startButton.addActionListener(e -> onStart.run());
        amountField.addActionListener(e -> onStart.run()); // 엔터로도 출발

        add(balanceLabel);
        add(label("말"));
        add(horseBox);
        add(label("베팅"));
        add(amountField);
        add(startButton);
    }

    private static JLabel label(String text) {
        JLabel label = new JLabel(text);
        label.setFont(PixelStyle.font());
        label.setForeground(PixelStyle.GRAY);
        return label;
    }

    public Horse getSelectedHorse() {
        return (Horse) horseBox.getSelectedItem();
    }

    /** 트랙에서 말을 클릭했을 때 콤보박스 선택도 맞춘다. */
    public void setSelectedHorse(Horse horse) {
        if (horseBox.getSelectedItem() != horse) { // 같은 말이면 다시 이벤트가 돌지 않게
            horseBox.setSelectedItem(horse);
        }
    }

    public boolean isInputEnabled() {
        return startButton.isEnabled();
    }

    /** 입력한 금액 문자열. 숫자 검사는 GameFrame에서 한다. */
    public String getAmountText() {
        return amountField.getText().trim();
    }

    public void setBalance(int balance) {
        balanceLabel.setText("$ " + Formats.money(balance));
    }

    /** 레이스 중에는 입력을 막는다. */
    public void setInputEnabled(boolean enabled) {
        horseBox.setEnabled(enabled);
        amountField.setEnabled(enabled);
        startButton.setEnabled(enabled);
    }

    public void focusAmount() {
        amountField.requestFocusInWindow();
        amountField.selectAll();
    }
}
