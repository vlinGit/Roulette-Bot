package pumpkin.roulette.bot.enums;

public enum StealEnum {
    STEAL_MIN_BAL (0.5),
    STEAL_CHANCE (0.25),
    STEAL_FEE(0.1),
    ;

    private double value;

    StealEnum(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }
}
