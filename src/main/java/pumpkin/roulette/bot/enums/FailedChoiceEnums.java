package pumpkin.roulette.bot.enums;

public enum FailedChoiceEnums {
    BET (6), // when bet type is invalid
    LOBBY (6), // when player isn't part of lobby
    AMOUNT (6); // when balance too low

    private int value;

    FailedChoiceEnums(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
