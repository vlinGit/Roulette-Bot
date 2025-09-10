package pumpkin.roulette.bot.enums;

public enum LobbyEnums {
    MAX_PLAYERS (10),
    SPIN_TIME (7000), // ms
    LOBBY_EXPIRE_TIME(1); // minutes

    private int value;

    LobbyEnums(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
