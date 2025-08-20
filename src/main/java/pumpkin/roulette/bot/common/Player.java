package pumpkin.roulette.bot.common;

import lombok.Data;

@Data
public class Player {
    String userId;
    String name;
    Bet bet;
    String lobbyId;
}
