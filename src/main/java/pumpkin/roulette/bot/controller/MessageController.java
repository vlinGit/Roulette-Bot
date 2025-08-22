package pumpkin.roulette.bot.controller;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.apache.ibatis.session.SqlSession;
import pumpkin.roulette.bot.BatisBuilder;
import pumpkin.roulette.bot.common.Lobby;
import pumpkin.roulette.bot.common.Player;
import pumpkin.roulette.bot.common.PlayerInfo;
import pumpkin.roulette.bot.mapper.UserMapper;

import java.io.IOException;

public class MessageController {
    private final JDA api;
    private final LobbyController lobbyController;
    private final BatisBuilder batisBuilder;

    public MessageController(JDA api, LobbyController lobbyController, BatisBuilder batisBuilder) throws IOException {
        this.api = api;
        this.lobbyController = lobbyController;
        this.batisBuilder = batisBuilder;
    }

    public void ping(MessageReceivedEvent event){
        event.getChannel().sendMessage("Pong!").queue();
    }

    public void playerInfo(MessageReceivedEvent event){
        try (SqlSession session = batisBuilder.getSession()) {
            UserMapper userMapper = session.getMapper(UserMapper.class);
            PlayerInfo playerInfo = userMapper.selectByUserId(event.getAuthor().getId());
            event.getChannel().sendMessageEmbeds(playerInfo.toEmbed()).queue();
        }
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
            lobby.addPlayer(player);
            lobbyController.add(lobby);
        });
    }
}
