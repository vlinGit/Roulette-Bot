package pumpkin.roulette.bot.controller;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.apache.ibatis.session.SqlSession;
import pumpkin.roulette.bot.BatisBuilder;
import pumpkin.roulette.bot.Refiller;
import pumpkin.roulette.bot.builder.MessageBuilder;
import pumpkin.roulette.bot.common.Lobby;
import pumpkin.roulette.bot.common.Player;
import pumpkin.roulette.bot.common.PlayerInfo;
import pumpkin.roulette.bot.enums.DefaultEnums;
import pumpkin.roulette.bot.mapper.UserMapper;

import java.io.IOException;
import java.sql.Time;
import java.util.concurrent.TimeUnit;

public class MessageController {
    private final JDA api;
    private final LobbyController lobbyController;
    private final BatisBuilder batisBuilder;
    private final Refiller refiller;

    public MessageController(JDA api, LobbyController lobbyController, BatisBuilder batisBuilder, Refiller refiller) throws IOException {
        this.api = api;
        this.lobbyController = lobbyController;
        this.batisBuilder = batisBuilder;
        this.refiller = refiller;
    }

    public void ping(MessageReceivedEvent event){
        event.getChannel().sendMessage("Pong!").queue();
    }

    public void helpMenu(MessageReceivedEvent event){
        event.getChannel().sendMessage(MessageBuilder.buildHelpMenu()).queue();
    }

    public void playerInfo(MessageReceivedEvent event){
        try (SqlSession session = batisBuilder.getSession()) {
            UserMapper userMapper = session.getMapper(UserMapper.class);
            PlayerInfo playerInfo = userMapper.selectByUserId(event.getAuthor().getId());
            event.getChannel().sendMessageEmbeds(playerInfo.toEmbed()).queue();
        }
    }

    public void nextRefill(MessageReceivedEvent event){
        long remaining = TimeUnit.DAYS.toMillis(DefaultEnums.RECHARGE_DAYS.getValue()) - refiller.ellapsedTime();
        String result = String.format("Next refill is in: %dH:%dM:%dS",
                TimeUnit.MILLISECONDS.toHours(remaining),
                TimeUnit.MILLISECONDS.toMinutes(remaining) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(remaining)),
                TimeUnit.MILLISECONDS.toSeconds(remaining) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((remaining))));
        event.getChannel().sendMessage(result).queue();
    }

    public void startLobby(MessageReceivedEvent event){
        Player player = new Player();
        player.setName(event.getAuthor().getName());
        player.setUserId(event.getAuthor().getId());

        Lobby lobby = new Lobby(player, api, batisBuilder);
        lobby.setOwner(player);

        event.getChannel().sendMessage("Creating lobby...").queue(sent -> {
            lobby.setMessageId(sent.getId());
            lobby.setChannelId(sent.getChannelId());
            lobby.addPlayer(player);
            lobbyController.add(lobby);
        });
    }
}
