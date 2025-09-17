package pumpkin.roulette.bot.enums;

public enum WinningEnums {
    NUMBER (36),
    COLOR (2),
    PARITY (2),
    COLUMN (3),
    DOZEN (3),
    ;

    private int value;

    WinningEnums(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
