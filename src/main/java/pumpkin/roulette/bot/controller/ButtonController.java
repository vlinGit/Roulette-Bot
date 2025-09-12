package pumpkin.roulette.bot.controller;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import org.apache.ibatis.session.SqlSession;
import pumpkin.roulette.bot.BatisBuilder;
import pumpkin.roulette.bot.common.Bet;
import pumpkin.roulette.bot.common.Lobby;
import pumpkin.roulette.bot.common.Player;
import pumpkin.roulette.bot.common.PlayerInfo;
import pumpkin.roulette.bot.mapper.UserMapper;

import javax.swing.*;
import java.util.HashMap;
import java.util.List;

public class ButtonController {
    private final JDA api;
    private final LobbyController lobbyController;
    private final BatisBuilder batisBuilder;

    public ButtonController(JDA api, LobbyController lobbyController, BatisBuilder batisBuilder) {
        this.api = api;
        this.lobbyController = lobbyController;
        this.batisBuilder = batisBuilder;
    }

    public void joinLobby(ButtonInteractionEvent event) {
        Lobby lobby = lobbyController.get(event.getMessageId());

        User newUser = event.getUser();
        Player newPlayer = new Player();
        newPlayer.setUserId(newUser.getId());
        newPlayer.setName(newUser.getName());
        newPlayer.setLobbyId(lobby.getMessageId());

        lobby.addPlayer(newPlayer);
        event.deferEdit().queue();
    }

    public void leaveLobby(ButtonInteractionEvent event) {
        Lobby lobby = lobbyController.get(event.getMessageId());

        User newUser = event.getUser();
        Player player = new Player();
        player.setUserId(newUser.getId());
        player.setName(newUser.getName());
        player.setLobbyId(lobby.getMessageId());

        lobby.removePlayer(player);
        event.deferEdit().queue();
    }

    public void startGame(ButtonInteractionEvent event) {
        Lobby lobby = lobbyController.get(event.getMessageId());

        User newUser = event.getUser();
        Player operator = new Player();
        operator.setUserId(newUser.getId());
        operator.setName(newUser.getName());

        lobby.startGame(operator);
        event.deferEdit().queue();
    }

    public void openBetModal(ButtonInteractionEvent event) {
        Lobby lobby = lobbyController.get(event.getMessageId());

        TextInput lobbyField = TextInput.create("lobbyid", "Lobby Id", TextInputStyle.SHORT)
                .setRequired(true)
                .setValue(lobby.getMessageId())
                .setPlaceholder(lobby.getMessageId())
                .build();
        TextInput bet = TextInput.create("bet", "Bet", TextInputStyle.SHORT)
                .setPlaceholder("Black, Red, Even, Odd, A Number between 1-36")
                .setRequired(true)
                .build();
        TextInput amount = TextInput.create("amount", "Amount", TextInputStyle.SHORT)
                .setPlaceholder("Bet amount")
                .setRequired(true)
                .build();

        Modal modal = Modal.create("submitbetmodal", "Bet Modal")
                .addComponents(ActionRow.of(lobbyField), ActionRow.of(bet), ActionRow.of(amount))
                .build();

        event.replyModal(modal).queue();
    }
}
