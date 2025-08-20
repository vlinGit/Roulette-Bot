package pumpkin.roulette.bot.common;

import lombok.Data;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import pumpkin.roulette.bot.builder.MessageBuilder;
import java.util.HashMap;

// Call with the JDA and Player objects
// Set the messageId and channelId after
@Data
public class Lobby {
    private JDA api;

    private String messageId; // lobbyId
    private String channelId;

    private int playerCount;
    private HashMap<String, Player> players;
    private boolean joining;
    private boolean closed;
    private int maxPlayers;
    private Player owner;

    private int bets;

    public Lobby(Player owner, JDA api) {
        this.api = api;
        this.playerCount = 1;
        this.players = new HashMap<>(){{
            put(owner.getUserId(), owner);
        }};
        this.closed = false;
        this.maxPlayers = 8;
        this.owner = owner;
        this.bets = 0;
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

    public void startGame(Player operator){
        if (operator.getName() != owner.getName()){
            return;
        }
        drawBetMenu();
    }

    public void stopLobby() {
        joining = false;
        closed = true;
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

    public String startSpin(){
        return "";
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

    // Betting menu is the pop-up that allows users to place a bet
    public void drawBettingMenu(){

    }

    public void drawSpinningMenu(){

    }

    public void resultMenu(){

    }
}
