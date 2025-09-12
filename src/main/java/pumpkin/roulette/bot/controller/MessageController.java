package pumpkin.roulette.bot.controller;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
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
import java.util.List;
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

    // !give @p1 100
    public void give(MessageReceivedEvent event){
        Message message = event.getMessage();

        try{
            List<User> mentions = message.getMentions().getUsers();
            if (mentions.size() > 1){
                throw new Exception();
            }

            int amount = Integer.parseInt(message.getContentRaw().split(" ")[2].strip());
            try (SqlSession session = batisBuilder.getSession()) {
                UserMapper userMapper = session.getMapper(UserMapper.class);
                PlayerInfo reciever = userMapper.selectByUserId(mentions.get(0).getId());
                PlayerInfo sender = userMapper.selectByUserId(event.getAuthor().getId());
                sender.setBalance(sender.getBalance() - amount);
                reciever.setBalance(reciever.getBalance() + amount);
                userMapper.update(reciever);
                userMapper.update(sender);
            }

            event.getChannel().sendMessage("<@" + event.getAuthor().getId() + "> sent <@" + mentions.get(0).getId() + "> $" + amount).queue();
        }catch (Exception e){
            e.printStackTrace();
            event.getChannel().sendMessage("Invalid input!").queue();
        }
    }

    public void leaderboard(MessageReceivedEvent event){
        try(SqlSession session = batisBuilder.getSession()) {
            UserMapper userMapper = session.getMapper(UserMapper.class);
            List<PlayerInfo> leaderboard = userMapper.selectLeaderboard();

            event.getChannel().sendMessage(MessageBuilder.buildLeaderboard(leaderboard)).queue();
        }
    }

    public void leave(MessageReceivedEvent event){
        try(SqlSession session = batisBuilder.getSession()) {
            UserMapper userMapper = session.getMapper(UserMapper.class);
            PlayerInfo playerInfo = userMapper.selectByUserId(event.getAuthor().getId());
            String lobbyId = playerInfo.getLobbyId();
            Player player = new Player();
            player.setUserId(playerInfo.getUserId());
            Lobby lobby = lobbyController.get(lobbyId);

            try{
                lobby.removePlayer(player);
            }catch (NullPointerException e){
                System.out.println(e.getMessage());
                System.out.println("Lobby not found, likely because lobby was already emptied due to restart. Manually clearing player data");
            }catch (Exception e){
                System.out.println(e.getMessage());
                System.out.println("Critical error, manually clearing player data");
            }finally{
                playerInfo.setLobbyId("");
                playerInfo.setInLobby(0);
                userMapper.update(playerInfo);
            }
        }
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
