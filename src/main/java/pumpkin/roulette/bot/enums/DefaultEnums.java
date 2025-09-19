package pumpkin.roulette.bot.enums;

public enum DefaultEnums {
    START_BALANCE (100),
    RECHARGE_BALANCE (100),
    RECHARGE_DAYS(1),
    RECHARGE_STEALS(1),
    ;

    private long value;

    DefaultEnums(long value) {
        this.value = value;
    }

    public long getValue() {
        return value;
    }
}
