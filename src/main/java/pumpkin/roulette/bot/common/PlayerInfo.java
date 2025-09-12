package pumpkin.roulette.bot.common;

import lombok.Data;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

@Data
public class PlayerInfo {
    int id;
    String userId;
    String name;
    long balance;
    String lobbyId;
    int inLobby;

    public MessageEmbed toEmbed(){
        return new EmbedBuilder()
                .setTitle(name + " Info")
                .addField("ID", id + "", false)
                .addField("UserID", userId, false)
                .addField("Name", name, false)
                .addField("Balance", balance + "", false)
                .build();
    }
}
