package pumpkin.roulette.bot.builder;

import pumpkin.roulette.bot.common.Lobby;
import pumpkin.roulette.bot.common.PlayerInfo;
import pumpkin.roulette.bot.enums.DefaultEnums;
import pumpkin.roulette.bot.enums.StealEnum;
import pumpkin.roulette.bot.enums.WinningEnums;

import java.util.List;

public class MessageBuilder {
    public static String buildStartMenu(Lobby lobby) {
        String message = "Roulette Table\n" +
                "Current Players: (%d/%d)\n" +
                "%s\n";
        StringBuilder players = new StringBuilder();
        lobby.getPlayers().forEach((userId, player) -> {
            players.append("<@").append(userId).append(">")
                    .append("\n");
        });

        return String.format(message, lobby.getPlayerCount(), lobby.getMaxPlayers(), players);
    }

    public static String buildHelpMenu() {
        String message = "Commands\n" +
                "**!help**: shows this menu\n" +
                "**!startlobby**: starts a new game\n" +
                "**!stats**: view your stats\n" +
                "**!nextrefill**: see timer until next refill\n" +
                "**!give**: gives money to another player. Format: !give @<reciever> <amount> (Note: there is a space between each word!)\n" +
                "**!leaderboard**: shows top 10 richest players\n" +
                "**!bets**: shows all possible bets\n"+
                "**!steal**: steal money from another player. Format: !steal @<victim> <amount>\n" +
                "**!stealrules**: outputs rules for stealing";
        return message;
    }

    public static String buildBetMenu(Lobby lobby) {
        String message = "Roulette Table\n"
                + "Place your bets: (%d/%d)\n"
                + "%s";
        StringBuilder bets = new StringBuilder();
        lobby.getPlayers().forEach((userId, player) -> {
            bets.append("<@").append(userId).append(">")
                    .append(" -> ")
                    .append("Bet: ");
            if (player.getBet() != null){
                bets.append("$").append(player.getBet().getAmount()).append(" on ").append(player.getBet().getBet().toUpperCase());
            }else{
                bets.append("waiting on bet");
            }
            bets.append("\n");
        });

        return String.format(message, lobby.getBets(), lobby.getPlayerCount(), bets);
    }

    public static String buildSpinningMenu(Lobby lobby){
        String message = "Roulette Table\n"
                + "Place your bets: (%d/%d)\n"
                + "%s";
        StringBuilder bets = new StringBuilder();
        lobby.getPlayers().forEach((userId, player) -> {
            bets.append("<@").append(userId).append(">")
                    .append(" -> ")
                    .append("Bet: ");
            bets.append("$").append(player.getBet().getAmount()).append(" on ").append(player.getBet().getBet().toUpperCase()).append("\n");
        });

        bets.append("\n https://tenor.com/view/roulette-spin-gif-11706381442831131809 \n***Spinning***...");
        return String.format(message, lobby.getBets(), lobby.getPlayerCount(), bets);
    }

    public static String buildResultMenu(Lobby lobby){
        String message = "Roulette Table\n"
                + "Result: %s\n"
                + "%s\n";
        StringBuilder results = new StringBuilder();
        lobby.getPlayers().forEach((userId, player) -> {
            results.append("<@").append(userId).append(">")
                    .append(" -> ");
            if (player.getWinnings() < 0){
                results.append("LOST -");
            }else{
                results.append("WON +");
            }
            results.append("$").append(Math.abs(player.getWinnings()))
                    .append(" (Bet ").append("$").append(player.getBet().getAmount()).append(" on ").append(player.getBet().getBet().toUpperCase()).append(")").append("\n");
        });

        return String.format(message, lobby.getWinningNumber() + " " + lobby.getWinningColor(), results);
    }

    public static String buildLeaderboard(List<PlayerInfo> leaderboard){
        StringBuilder builder = new StringBuilder();
        builder.append("**Top 10 Highest Balance**\n");
        leaderboard.forEach((playerInfo) -> {
            builder.append(playerInfo.getName())
                    .append(" | $")
                    .append(playerInfo.getBalance())
                    .append("\n");
        });

        return builder.toString();
    }

    public static String buildBets(){
        StringBuilder builder = new StringBuilder();
        builder.append("**Possible Bets**\n")
                .append("**Black**:\n").append("\t**Win condition**: black\n\t**Pays out**: ").append(WinningEnums.COLOR.getValue() - 1).append(" to 1\n")
                .append("**Red**:\n").append("\t**Win condition**: red\n\t**Pays out**: ").append(WinningEnums.COLOR.getValue() - 1).append(" to 1\n")
                .append("**Even**:\n").append("\t**Win condition**: a even number\n\t**Pays out**: ").append(WinningEnums.PARITY.getValue() - 1).append(" to 1\n")
                .append("**Odd**:\n").append("\t**Win condition**: a odd number\n\t**Pays out**: ").append(WinningEnums.PARITY.getValue() - 1).append(" to 1\n")
                .append("**C1**:\n").append("\t**Win condition**: 1st column\n\t(1,4,7,10,13,16,19,22,25,28,31,34)\n\t**Pays out**: ").append(WinningEnums.COLUMN.getValue() - 1).append(" to 1\n")
                .append("**C2**:\n").append("\t**Win condition**: 2nd column\n\t(2,5,8,11,14,17,20,23,26,29,32,35)\n\t**Pays out**: ").append(WinningEnums.COLUMN.getValue() - 1).append(" to 1\n")
                .append("**C3**:\n").append("\t**Win condition**: 3rd column\n\t(3,6,9,12,15,18,21,24,27,30,33,36)\n\t**Pays out**: ").append(WinningEnums.COLUMN.getValue() - 1).append(" to 1\n")
                .append("**D1**:\n").append("\t**Win condition**: 1-12\n\t**Pays out**: ").append(WinningEnums.DOZEN.getValue() - 1).append(" to 1\n")
                .append("**D2**:\n").append("\t**Win condition**: 13-24\n\t**Pays out**: ").append(WinningEnums.DOZEN.getValue() - 1).append(" to 1\n")
                .append("**D3**:\n").append("\t**Win condition**: 25-36\n\t**Pays out**: ").append(WinningEnums.DOZEN.getValue() - 1).append(" to 1\n")
                .append("**A number (ex: 1, 2, 3)**:\n").append("\t**Win condition**: the exact number chosen\n\t**Pays out**: ").append(WinningEnums.NUMBER.getValue() - 1).append(" to 1");

        return builder.toString();
    }

    public static String buildStealrules(){
        StringBuilder builder = new StringBuilder();
        builder.append("**Steal Rules**\n")
                .append("1. The max amount is 20% of the victim's balance\n")
                .append("2. The final amount to steal is calculated with this formula: [Max Amount * " + StealEnum.STEAL_BASE.getValue() + "^(X-" +  StealEnum.STEAL_OFFSET.getValue() + ")] where X is a random number between 0-1\n")
                .append("3. The chance of a successful steal is " + (StealEnum.STEAL_CHANCE.getValue() * 100) + "%\n")
                .append("4. You get " + DefaultEnums.RECHARGE_STEALS.getValue() + " steal every 24hr. Once you are out, you cannot steal until you get another one\n");

        return builder.toString();
    }
}
