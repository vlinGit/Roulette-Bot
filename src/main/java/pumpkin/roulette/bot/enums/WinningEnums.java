package pumpkin.roulette.bot.enums;

public enum WinningEnums {
    NUMBER (35),
    COLOR (2),
    PARITY (2);

    private int value;

    WinningEnums(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
