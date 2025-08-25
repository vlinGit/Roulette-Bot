package pumpkin.roulette.bot.router;

import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class MessageRouter {
    private final Map<String, Consumer<MessageReceivedEvent>> messageMap = new HashMap<>();
    private final Map<String, Consumer<ButtonInteractionEvent>> buttonMap = new HashMap<>();
    private final Map<String, Consumer<ModalInteractionEvent>> modalMap = new HashMap<>();

    public void addMessageRoute(String path, Consumer<MessageReceivedEvent> method){
        messageMap.put(path, method);
    }

    public void addButtonRoute(String path, Consumer<ButtonInteractionEvent> method){
        buttonMap.put(path, method);
    }

    public void addModalRoute(String path, Consumer<ModalInteractionEvent> method){
        modalMap.put(path, method);
    }

    public void route(MessageReceivedEvent event) throws InvocationTargetException, IllegalAccessException {
        String command = event.getMessage().getContentRaw()
                .trim()
                .toLowerCase()
                .split(" ")[0];
        messageMap.get(command).accept(event);
    }

    public void route(ButtonInteractionEvent event) throws InvocationTargetException, IllegalAccessException {
        String command = event.getInteraction().getButton().getId().toLowerCase();
        buttonMap.get(command).accept(event);
    }

    public void route(ModalInteractionEvent event) throws InvocationTargetException, IllegalAccessException {
        String command = event.getInteraction().getModalId().toLowerCase();
        modalMap.get(command).accept(event);
    }
}
