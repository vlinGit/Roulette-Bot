package pumpkin.roulette.bot.enums;

public enum LobbyEnums {
    MAX_PLAYERS (2);

    private int value;

    LobbyEnums(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
