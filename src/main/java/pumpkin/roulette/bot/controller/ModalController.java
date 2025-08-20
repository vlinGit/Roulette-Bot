package pumpkin.roulette.bot.controller;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;

public class ModalController {
    private final JDA api;
    private final LobbyController controller;

    public ModalController(JDA api, LobbyController controller) {
        this.api = api;
        this.controller = controller;
    }

    public boolean betVerify(ModalInteractionEvent event){

        return true;
    }
}
