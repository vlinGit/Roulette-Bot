package pumpkin.roulette.bot.enums;

public enum Bet {
    BLACK ("BLACK"),
    WHITE ("WHITE"),
    ODD ("ODD"),
    EVEN ("EVEN");

    private String value;

    Bet(String value) {
        this.value = value;
    }
}
