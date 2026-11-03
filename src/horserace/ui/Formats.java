package horserace.ui;

import horserace.model.horse.Horse;

/** 화면 표시용 문자열 포맷 모음. */
final class Formats {
    private Formats() {
    }

    /** 2.0 -> "배당 2배", 1.5 -> "배당 1.5배" */
    static String odds(double odds) {
        String value = odds == Math.floor(odds) ? String.valueOf((int) odds) : String.valueOf(odds);
        return "배당 " + value + "배";
    }

    /** 2.0 -> "x2", 1.5 -> "x1.5" (트랙처럼 좁은 곳용) */
    static String shortOdds(double odds) {
        return "x" + (odds == Math.floor(odds) ? String.valueOf((int) odds) : String.valueOf(odds));
    }

    static String horse(Horse horse) {
        return horse.getNumber() + ". " + horse.getName() + " (" + horse.getType() + ") " + odds(horse.getOdds());
    }

    static String money(int amount) {
        return String.format("%,d원", amount);
    }
}
