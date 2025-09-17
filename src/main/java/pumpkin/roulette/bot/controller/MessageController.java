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
import pumpkin.roulette.bot.enums.StealEnum;
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

                if (sender.getUserId().equals(reciever.getUserId())){
                    event.getChannel().sendMessage("Can't give yourself money");
                    return;
                }

                if (sender.getBalance() < amount){
                    event.getChannel().sendMessage("Balance not enough");
                    return;
                }

                sender.setBalance(sender.getBalance() - amount);
                reciever.setBalance(reciever.getBalance() + amount);
                userMapper.update(sender);
                userMapper.update(reciever);
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

    public void bets(MessageReceivedEvent event){
        event.getChannel().sendMessage(MessageBuilder.buildBets()).queue();
    }

    public void steal(MessageReceivedEvent event){
        Message message = event.getMessage();

        try{
            List<User> mentions = message.getMentions().getUsers();
            if (mentions.size() > 1){
                throw new Exception();
            }

            int amount = Integer.parseInt(message.getContentRaw().split(" ")[2].strip());
            try (SqlSession session = batisBuilder.getSession()) {
                UserMapper userMapper = session.getMapper(UserMapper.class);
                PlayerInfo stealer = userMapper.selectByUserId(event.getAuthor().getId());
                long fee = (long) Math.floor(amount * StealEnum.STEAL_FEE.getValue());
                if (stealer.getSteals() == 0){
                    event.getChannel().sendMessage("You are out of steals").queue();
                    return;
                }
                if (stealer.getBalance() < amount * StealEnum.STEAL_MIN_BAL.getValue()){
                    event.getChannel().sendMessage("Balance too low to steal. Balance must be at least " + StealEnum.STEAL_MIN_BAL.getValue() * 100 + "% of the steal amount").queue();
                    return;
                }
                if(stealer.getBalance() < fee){
                    event.getChannel().sendMessage("Balance too low to steal. Not enough to pay the " + StealEnum.STEAL_FEE.getValue() * 100 + "% fee").queue();
                    return;
                }

                PlayerInfo victim = userMapper.selectByUserId(mentions.get(0).getId());
                if (victim.getBalance() < amount){
                    event.getChannel().sendMessage("Victim doesn't have that much money").queue();
                    return;
                }

                if (victim.getUserId().equals(stealer.getUserId())){
                    event.getChannel().sendMessage("Cannot steal from yourself").queue();
                    return;
                }

                if (Math.random() <= StealEnum.STEAL_CHANCE.getValue()){
                    victim.setBalance(victim.getBalance() - amount);
                    stealer.setBalance(stealer.getBalance() + (amount - fee));
                    userMapper.update(victim);
                    userMapper.update(stealer);

                    event.getChannel().sendMessage("<@" + stealer.getUserId() + "> stole " + amount + " from <@" + victim.getUserId() + ">\nCalculated fee: " + fee + " Final payout: " + (amount - fee)).queue();
                }else{
                    stealer.setBalance(stealer.getBalance() - fee);
                    userMapper.update(stealer);

                    event.getChannel().sendMessage("Steal failed! Calculated fee: " + fee).queue();
                }

                stealer.setSteals(stealer.getSteals() - 1);
                userMapper.update(stealer);
            }
        }catch (Exception e){
            e.printStackTrace();
            event.getChannel().sendMessage("Invalid input!").queue();
        }
    }

    public void stealrules(MessageReceivedEvent event){
        event.getChannel().sendMessage(MessageBuilder.buildStealrules()).queue();
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
