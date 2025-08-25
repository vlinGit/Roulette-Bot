package pumpkin.roulette.bot;

import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.ibatis.session.SqlSession;
import pumpkin.roulette.bot.common.PlayerInfo;
import pumpkin.roulette.bot.enums.DefaultEnums;
import pumpkin.roulette.bot.mapper.UserMapper;
import pumpkin.roulette.bot.router.MessageRouter;

import java.lang.reflect.InvocationTargetException;

public class BotListener extends ListenerAdapter {
    private final MessageRouter router;
    private final BatisBuilder batisBuilder;

    public BotListener(MessageRouter router, BatisBuilder batisBuilder) {
        this.router = router;
        this.batisBuilder = batisBuilder;
    }

    public void checkUserExists(String userId, String name) {
        try(SqlSession session = batisBuilder.getSession()){
            UserMapper userMapper = session.getMapper(UserMapper.class);
            PlayerInfo existingUser = userMapper.selectByUserId(userId);
            if (existingUser == null) {
                PlayerInfo playerInfo = new PlayerInfo();
                playerInfo.setUserId(userId);
                playerInfo.setName(name);
                playerInfo.setBalance(DefaultEnums.START_BALANCE.getValue());

                userMapper.insert(playerInfo);
            }
        }
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event){
        if (event.getAuthor().isBot()) return;

        checkUserExists(event.getAuthor().getId(), event.getAuthor().getName());

        try {
            router.route(event);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (NullPointerException e) {
            System.out.println("Null pointer exception, likely just a command that doesn't exist");
            e.printStackTrace();
        }
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event){
        checkUserExists(event.getInteraction().getUser().getId(), event.getInteraction().getUser().getName());

        try{
            router.route(event);
        }catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (NullPointerException e) {
            System.out.println("Null pointer exception, likely just a command that doesn't exist");
            e.printStackTrace();
        }
    }

    @Override
    public void onModalInteraction(ModalInteractionEvent event){
        try{
            router.route(event);
        }catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (NullPointerException e) {
            System.out.println("Null pointer exception, likely just a command that doesn't exist");
            e.printStackTrace();
        }
    }
}
