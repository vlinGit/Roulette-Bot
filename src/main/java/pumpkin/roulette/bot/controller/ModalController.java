package pumpkin.roulette.bot.controller;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;
import org.apache.ibatis.session.SqlSession;
import pumpkin.roulette.bot.BatisBuilder;
import pumpkin.roulette.bot.common.Bet;
import pumpkin.roulette.bot.common.Lobby;
import pumpkin.roulette.bot.common.PlayerInfo;
import pumpkin.roulette.bot.mapper.UserMapper;

import java.util.List;
import java.util.regex.Pattern;

public class ModalController {
    private final JDA api;
    private final LobbyController lobbyController;
    private final BatisBuilder batisBuilder;

    public ModalController(JDA api, LobbyController lobbyController, BatisBuilder batisBuilder) {
        this.api = api;
        this.lobbyController = lobbyController;
        this.batisBuilder = batisBuilder;
    }

    public void betVerify(ModalInteractionEvent event){
        List<ModalMapping> mappings = event.getInteraction().getValues();
        String lobbyId = mappings.get(0).getAsString();
        String betType = mappings.get(1).getAsString();
        String amount = mappings.get(2).getAsString();
        String userId = event.getUser().getId();
        Lobby lobby = lobbyController.get(lobbyId);

        Pattern betPattern = Pattern.compile("\\b(?:black|red|even|odd|[1-9]|[12][0-9]|3[0-6])\\b", Pattern.CASE_INSENSITIVE);
        if (!lobby.getPlayers().containsKey(userId)){
            event.reply("You are not in this lobby")
                    .setEphemeral(true)
                    .queue();
            return;
        }
        if (!betPattern.matcher(betType).matches()){
            event.reply("Invalid bet. Valid bets are: black, even, odd, or a number between 1-36")
                    .queue();
            return;
        }
        try(SqlSession session = batisBuilder.getSession()){
            UserMapper mapper = session.getMapper(UserMapper.class);
            PlayerInfo playerInfo = mapper.selectByUserId(userId);

            if (playerInfo.getBalance() < Integer.valueOf(amount)){
                event.reply("Insufficent balance")
                        .setEphemeral(true)
                        .queue();
                return;
            }
        }

        try{
            Bet bet = new Bet();
            bet.setBet(betType);
            bet.setAmount(Integer.parseInt(amount));

            lobby.addBet(bet, userId);
        }catch (Exception e){
            e.printStackTrace();
            event.reply("Invalid bet").queue();
        }

        event.deferEdit().queue();
    }
}
