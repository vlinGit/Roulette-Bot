package pumpkin.roulette.bot.builder;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import pumpkin.roulette.bot.common.Lobby;

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
            bets.append("$").append(player.getBet().getAmount()).append(" on ").append(player.getBet().getBet().toUpperCase());
            bets.append("Spinning...");
        });

        return String.format(message, lobby.getBets(), lobby.getPlayerCount(), bets);
    }

    public static String buildResultMenu(Lobby lobby){
        String message = "Roulette Table\n"
                + "Result:\n"
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
            results.append("$").append(Math.abs(player.getWinnings()));
        });

        return String.format(message, results);
    }
}
