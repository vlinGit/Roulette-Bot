package pumpkin.roulette.bot.controller;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import pumpkin.roulette.bot.common.Lobby;
import pumpkin.roulette.bot.common.Player;

public class MessageController {
    private final JDA api;
    private final LobbyController lobbyController;

    public MessageController(JDA api, LobbyController lobbyController) {
        this.api = api;
        this.lobbyController = lobbyController;
    }

    public void ping(MessageReceivedEvent event){
        event.getChannel().sendMessage("Pong!").queue();
    }

    public void startLobby(MessageReceivedEvent event){
        Player player = new Player();
        player.setName(event.getAuthor().getName());
        player.setUserId(event.getAuthor().getId());

        Lobby lobby = new Lobby(player, api);
        lobby.setOwner(player);

        event.getChannel().sendMessage("Creating lobby...").queue(sent -> {
            lobby.setMessageId(sent.getId());
            lobby.setChannelId(sent.getChannelId());
            lobby.drawStartMenu();
            lobbyController.add(lobby);
        });
    }
}
