package pumpkin.roulette.bot.common;

import lombok.Data;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.apache.ibatis.session.SqlSession;
import pumpkin.roulette.bot.BatisBuilder;
import pumpkin.roulette.bot.builder.MessageBuilder;
import pumpkin.roulette.bot.enums.LobbyEnums;
import pumpkin.roulette.bot.enums.WinningEnums;
import pumpkin.roulette.bot.mapper.UserMapper;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

// Call with the JDA and Player objects
// Set the messageId and channelId after
@Data
public class Lobby {
    private JDA api;

    private String messageId; // lobbyId
    private String channelId;

    private int playerCount;
    private HashMap<String, Player> players; // userId <-> Player
    private boolean joining;
    private boolean closed;
    private int maxPlayers;
    private Player owner;

    private int bets;
    private int winningNumber;
    private String winningColor;

    private Runnable listener;
    private BatisBuilder batisBuilder;

    private final ScheduledExecutorService scheduler;
    private final ScheduledFuture<?> future;

    public Lobby(Player owner, JDA api, BatisBuilder batisBuilder) {
        this.api = api;
        this.players = new HashMap<>();
        this.closed = false;
        this.maxPlayers = LobbyEnums.MAX_PLAYERS.getValue();
        this.owner = owner;
        this.bets = 0;
        this.batisBuilder = batisBuilder;
        this.scheduler = Executors.newScheduledThreadPool(1);
        this.future = this.scheduler.schedule(this::stopLobby, LobbyEnums.LOBBY_EXPIRE_TIME.getValue(), TimeUnit.MINUTES);
    }

    public void startGame(Player operator){
        this.future.cancel(false);
        if (!operator.getName().equals(owner.getName())){
            return;
        }
        drawBetMenu();
    }

    public void addPlayer(Player player){
        try{
            if (players.containsKey(player.getUserId())){
                System.out.println("Player already exists");
                return;
            }

            players.put(player.getUserId(), player);
            playerCount++;

            drawStartMenu();

            if (playerCount == maxPlayers){
                startGame(owner);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void stopLobby() {
        if (listener != null){
            listener.run();
        }
    }

    private String checkParity(int number){
        return (number % 2 == 0) ? "EVEN" : "ODD";
    }

    private void handleResult() {
        List<String> winningChoices =  new ArrayList<>(){{
            add("BLACK");
            add("RED");
        }};
        winningColor = winningChoices.get(new Random().nextInt(2)); // black = 0, red = 1;
        winningNumber = new Random().nextInt(36) + 1;
        String winningParity = checkParity(winningNumber);

        players.forEach((userId, player) -> {
            Bet playerBet = player.getBet();
            player.setWinnings(playerBet.getAmount() * -1);

            if (String.valueOf(winningNumber).matches(playerBet.getBet())) {
                player.setWinnings(playerBet.getAmount() * WinningEnums.NUMBER.getValue());
            }else if(winningColor.matches(playerBet.getBet())) {
                player.setWinnings(playerBet.getAmount() * WinningEnums.COLOR.getValue());
            }else if(winningParity.matches(playerBet.getBet())) {
                player.setWinnings(playerBet.getAmount() * WinningEnums.PARITY.getValue());
            }
        });

        players.forEach((userId, player) -> {
            try(SqlSession session = batisBuilder.getSession()){
                UserMapper userMapper = session.getMapper(UserMapper.class);
                PlayerInfo playerInfo = userMapper.selectByUserId(player.getUserId());
                playerInfo.setBalance(playerInfo.getBalance() + player.getWinnings());

                userMapper.update(playerInfo);
            }
        });
        drawResultMenu();
        stopLobby();
    }

    public void addBet(Bet bet, String userId){
        try{
            Player player = players.get(userId);
            if (player.getBet() != null){
                System.out.println("Bet already placed");
            }else{
                player.setBet(bet);
                bets++;
            }

            drawBetMenu();

            if (bets == playerCount){
                System.out.println("Betting phase closed");
                startSpin();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void startSpin(){
        drawSpinningMenu();
        Timer timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handleResult();
            }
        }, LobbyEnums.SPIN_TIME.getValue());
    }

    public void drawStartMenu(){
        TextChannel textChannel = api.getTextChannelById(channelId);
        String message = MessageBuilder.buildStartMenu(this);
        textChannel.editMessageById(messageId, message)
                .setActionRow(
                        Button.secondary("join", "Join Game"),
                        Button.primary("start", "Start Game")
                )
                .queue();
    }

    // Bet menu is the main menu showing all bets
    public void drawBetMenu(){
        TextChannel textChannel = api.getTextChannelById(channelId);
        String message = MessageBuilder.buildBetMenu(this);
        textChannel.editMessageById(messageId, message)
                .setActionRow(
                        Button.primary("openbetmodal", "Place Bet")
                )
                .queue();
    }

    public void drawSpinningMenu(){
        TextChannel textChannel = api.getTextChannelById(channelId);
        String message = MessageBuilder.buildSpinningMenu(this);
        textChannel.editMessageById(messageId, message).setComponents().queue();
    }

    public void drawResultMenu(){
        TextChannel textChannel = api.getTextChannelById(channelId);
        String message = MessageBuilder.buildResultMenu(this);
        textChannel.editMessageById(messageId, message).setComponents().queue();
    }
}
