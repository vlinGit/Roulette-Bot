package pumpkin.roulette.bot.enums;

public enum LobbyEnums {
    MAX_PLAYERS (10),
    SPIN_TIME (7000); // ms

    private int value;

    LobbyEnums(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
