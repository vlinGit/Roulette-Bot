package pumpkin.roulette.bot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.requests.GatewayIntent;
import pumpkin.roulette.bot.BotListener;
import pumpkin.roulette.bot.controller.ButtonController;
import pumpkin.roulette.bot.controller.LobbyController;
import pumpkin.roulette.bot.controller.MessageController;
import pumpkin.roulette.bot.controller.ModalController;
import pumpkin.roulette.bot.router.MessageRouter;

public class Main {
    public static void main(String[] args) {
        MessageRouter router = new MessageRouter();
        JDA api = JDABuilder.createDefault(BotConfig.get("token"))
                .addEventListeners(new BotListener(router))
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .build();

        LobbyController lobbyController = new LobbyController();
        MessageController messageController = new MessageController(api, lobbyController);
        ButtonController buttonController = new ButtonController(api, lobbyController);
        ModalController modalController = new ModalController(api, lobbyController);

        router.addMessageRoute("!ping", messageController::ping);
        router.addMessageRoute("!startlobby", messageController::startLobby);
        router.addButtonRoute("join", buttonController::joinLobby);
        router.addButtonRoute("start", buttonController::startGame);
        router.addButtonRoute("openbetmodal", buttonController::openBetModal);
        router.addModalRoute("bet", modalController::betVerify);
    }
}