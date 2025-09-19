package pumpkin.roulette.bot.enums;

public enum StealEnum {
    STEAL_MIN_BAL (0.5),
    STEAL_CHANCE (0.2),
    STEAL_CUT(0.2),
    STEAL_BASE(10),
    STEAL_OFFSET(1),
    STEAL_TIME (3000),
    ;

    private double value;

    StealEnum(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }
}
