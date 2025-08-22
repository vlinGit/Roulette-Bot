package pumpkin.roulette.bot.enums;

public enum BetEnum {
    BLACK ("BLACK"),
    RED ("RED"),
    ODD ("ODD"),
    EVEN ("EVEN"),
    NUMBER ("NUMBER");

    private String value;

    BetEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
