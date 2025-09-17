package pumpkin.roulette.bot.enums;

public enum BetEnum {
    BLACK ("BLACK"),
    RED ("RED"),
    ODD ("ODD"),
    EVEN ("EVEN"),
    NUMBER ("NUMBER"),
    C1 ("C1"),
    C2 ("C2"),
    C3 ("C3"),
    D1 ("D1"),
    D2 ("D2"),
    D3 ("D3"),
    ;

    private String value;

    BetEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
