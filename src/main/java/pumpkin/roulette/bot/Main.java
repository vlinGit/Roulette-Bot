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
// - Add more bet options:
//      - 18 numbers (even)
// - Add help menu for all bets

// - Separate logic so it's unique per server
// - Show final balance after spin
// - Track timestamp via DB
//      - update timestamp every loop
//      - use when restarting bot and compare the elapsed time
// - Make enums take fields from application.properties
// - Come up with a way that allows me to edit the results live
// - Add a green option (should deduct percentages from changes of red/black)
// - stock market
// - web gui for placing bets
public class Main {
    public static void main(String[] args) throws IOException {
        BatisBuilder batisBuilder = new BatisBuilder();

        MessageRouter router = new MessageRouter();
        JDA api = JDABuilder.createDefault(BotConfig.get("token"))
                .addEventListeners(new BotListener(router, batisBuilder))
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .build();

        Refiller refiller = new Refiller(batisBuilder);
        refiller.startRefill();

        LobbyController lobbyController = new LobbyController();
        MessageController messageController = new MessageController(api, lobbyController, batisBuilder, refiller);
        ButtonController buttonController = new ButtonController(api, lobbyController, batisBuilder);
        ModalController modalController = new ModalController(api, lobbyController, batisBuilder);

        router.addMessageRoute("!ping", messageController::ping);
        router.addMessageRoute("!startlobby", messageController::startLobby);
        router.addMessageRoute("!stats", messageController::playerInfo);
        router.addMessageRoute("!help", messageController::helpMenu);
        router.addMessageRoute("!nextrefill", messageController::nextRefill);
        router.addMessageRoute("!give", messageController::give);
        router.addMessageRoute("!steal", messageController::steal);
        router.addMessageRoute("!stealrules", messageController::stealrules);
        router.addMessageRoute("!leaderboard", messageController::leaderboard);
        router.addMessageRoute("!bets", messageController::bets);

        router.addButtonRoute("join", buttonController::joinLobby);
        router.addButtonRoute("start", buttonController::startGame);
        router.addButtonRoute("openbetmodal", buttonController::openBetModal);

        router.addModalRoute("submitbetmodal", modalController::betVerify);
    }
}