package pumpkin.roulette.bot.builder;

import pumpkin.roulette.bot.common.Lobby;
import pumpkin.roulette.bot.common.PlayerInfo;

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
                "**!leaderboard**: shows top 10 richest players";
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
            builder.append("<@").append(playerInfo.getUserId()).append(">")
                    .append(" | $")
                    .append(playerInfo.getBalance())
                    .append("\n");
        });

        return builder.toString();
    }
}
