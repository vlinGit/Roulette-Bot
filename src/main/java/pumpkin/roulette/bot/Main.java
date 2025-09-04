package pumpkin.roulette.bot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import pumpkin.roulette.bot.controller.ButtonController;
import pumpkin.roulette.bot.controller.LobbyController;
import pumpkin.roulette.bot.controller.MessageController;
import pumpkin.roulette.bot.controller.ModalController;
import pumpkin.roulette.bot.router.MessageRouter;

import java.io.IOException;

// TODO:
// - Add help menu
// - Add 24hr timer to top-up player balance if < DefaultEnums.RECHARGE_BALANCE
// - Test multiplayer lobby
public class Main {
    public static void main(String[] args) throws IOException {
        BatisBuilder batisBuilder = new BatisBuilder();

        MessageRouter router = new MessageRouter();
        JDA api = JDABuilder.createDefault(BotConfig.get("token"))
                .addEventListeners(new BotListener(router, batisBuilder))
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .build();

        LobbyController lobbyController = new LobbyController();
        MessageController messageController = new MessageController(api, lobbyController, batisBuilder);
        ButtonController buttonController = new ButtonController(api, lobbyController, batisBuilder);
        ModalController modalController = new ModalController(api, lobbyController, batisBuilder);

        router.addMessageRoute("!ping", messageController::ping);
        router.addMessageRoute("!startlobby", messageController::startLobby);
        router.addMessageRoute("!stats", messageController::playerInfo);
        router.addMessageRoute("!help", messageController::helpMenu);

        router.addButtonRoute("join", buttonController::joinLobby);
        router.addButtonRoute("start", buttonController::startGame);
        router.addButtonRoute("openbetmodal", buttonController::openBetModal);

        router.addModalRoute("submitbetmodal", modalController::betVerify);
    }
}