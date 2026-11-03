package horserace.ui;

import horserace.game.Race;
import horserace.game.TurnEvent;
import horserace.model.horse.Horse;
import horserace.model.player.Player;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import java.awt.BorderLayout;
import java.util.List;

/**
 * 메인 창. 게임 흐름을 담당한다.
 * 말 고르기 -> 베팅(출발) -> 레이스(300ms마다 한 턴) -> 정산 -> 계속/종료
 */
public class GameFrame extends JFrame {
    private static final int TURN_DELAY_MS = 300;    // 콘솔 버전의 Thread.sleep(300) 대신 Timer 사용
    private static final int RESULT_DELAY_MS = 900;  // 결승선 통과 모습을 보여준 뒤 결과 창 띄우기

    private final Player player;
    private final Race race;
    private final BetPanel betPanel;
    private final TrackPanel trackPanel;
    private final Timer turnTimer;

    public GameFrame(String playerName) {
        super("경마 게임");
        List<Horse> horses = createHorses();
        player = new Player(playerName);
        race = new Race(horses);
        betPanel = new BetPanel(horses, this::startRace, this::selectHorse);
        trackPanel = new TrackPanel(race, this::selectHorse);
        turnTimer = new Timer(TURN_DELAY_MS, e -> playTurn());

        getContentPane().setBackground(PixelStyle.INK);
        setLayout(new BorderLayout());
        add(betPanel, BorderLayout.NORTH);
        add(trackPanel, BorderLayout.CENTER);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);

        betPanel.setBalance(player.getBalance());
        prepareRace("말을 클릭해서 고르고, 베팅 금액을 입력한 뒤 [출발!]을 누르세요");
    }

    private static List<Horse> createHorses() {
        // TODO: 새 캐릭터(Horse 하위 클래스)로 채우기. 번호는 1부터 (images/horse{번호}.png 사진과 연결됨)
        return List.of(
        );
    }

    // ---------- 1. 레이스 준비 / 말 고르기 ----------

    /** 레인을 새로 배정하고 베팅을 받을 수 있는 상태로 만든다. */
    private void prepareRace(String guide) {
        race.shuffleLanes();
        race.start();
        trackPanel.resetDisplay();
        trackPanel.setBanner(guide);
        betPanel.setInputEnabled(true);
        selectHorse(betPanel.getSelectedHorse());
        betPanel.focusAmount();
    }

    /** 트랙 클릭이나 콤보박스로 말을 골랐을 때. 레이스 중에는 무시한다. */
    private void selectHorse(Horse horse) {
        if (horse == null || !betPanel.isInputEnabled()) {
            return;
        }
        betPanel.setSelectedHorse(horse);
        trackPanel.setSelectedHorse(horse);
    }

    // ---------- 2. 베팅 + 출발 ----------

    /** 출발 버튼: 금액 검사 -> 베팅 -> 타이머 시작. 잘못된 입력이면 안내만 하고 멈춘다. */
    private void startRace() {
        if (turnTimer.isRunning()) {
            return;
        }
        Integer amount = readBetAmount();
        if (amount == null) {
            return;
        }

        Horse picked = player.getBettingHorse();
        betPanel.setBalance(player.getBalance());
        betPanel.setInputEnabled(false);
        trackPanel.setBanner(picked.getDisplayName() + "에게 " + Formats.money(amount) + " 베팅!");

        race.start();
        trackPanel.resetDisplay();
        turnTimer.start();
    }

    /** 입력칸의 금액으로 베팅한다. 숫자가 아니거나 범위를 벗어나면 안내 후 null. */
    private Integer readBetAmount() {
        int amount;
        try {
            amount = Integer.parseInt(betPanel.getAmountText());
        } catch (NumberFormatException e) {
            warn("베팅 금액은 숫자로 입력하세요.");
            return null;
        }
        try {
            player.bet(betPanel.getSelectedHorse(), amount);
        } catch (IllegalArgumentException e) {
            warn("베팅 금액은 1원 이상, 잔액(" + Formats.money(player.getBalance()) + ") 이하로 입력하세요.");
            return null;
        }
        return amount;
    }

    // ---------- 3. 레이스 진행 ----------

    /** 타이머가 300ms마다 호출: 한 턴 진행. 말은 다음 턴까지 300ms 동안 새 위치로 부드럽게 달린다. */
    private void playTurn() {
        List<TurnEvent> events = race.nextTurn();
        trackPanel.moveToCurrent(TURN_DELAY_MS);
        for (TurnEvent event : events) {
            trackPanel.playEffect(event);
        }
        if (race.isFinished()) {
            turnTimer.stop();
            trackPanel.setBanner(race.getWinner().getDisplayName() + " 우승!");
            runLater(RESULT_DELAY_MS, this::settle);
        }
    }

    // ---------- 4. 정산 + 계속 여부 ----------

    private void settle() {
        String result = settleBet();
        betPanel.setBalance(player.getBalance());

        if (player.isBankrupt()) {
            JOptionPane.showMessageDialog(this,
                    result + "\n\n잔액이 0원이 되어 파산했습니다. 게임을 종료합니다.",
                    "파산", JOptionPane.ERROR_MESSAGE);
            quit();
            return;
        }

        int answer = JOptionPane.showConfirmDialog(this, result + "\n\n계속하시겠습니까?",
                "레이스 결과", JOptionPane.YES_NO_OPTION);
        if (answer == JOptionPane.YES_OPTION) {
            prepareRace("다음 레이스! 레인이 새로 배정됐어요. 말을 클릭해서 고르세요");
        } else {
            quit();
        }
    }

    /** 이겼으면 당첨금을 받고, 결과 메시지를 만든다. */
    private String settleBet() {
        Horse winner = race.getWinner();
        String outcome;
        if (winner == player.getBettingHorse()) {
            outcome = "축하합니다! " + Formats.money(player.win()) + " 획득";
        } else {
            outcome = "아쉽네요. " + Formats.money(player.getBetAmount()) + "을 잃었습니다.";
        }
        return winner.getDisplayName() + " 우승!\n"
                + outcome + "\n"
                + "현재 잔액: " + Formats.money(player.getBalance());
    }

    // ---------- 도우미 ----------

    private void warn(String message) {
        JOptionPane.showMessageDialog(this, message, "입력 오류", JOptionPane.WARNING_MESSAGE);
        betPanel.focusAmount();
    }

    private void quit() {
        dispose();
        System.exit(0);
    }

    private static void runLater(int delayMs, Runnable action) {
        Timer timer = new Timer(delayMs, e -> action.run());
        timer.setRepeats(false);
        timer.start();
    }
}
