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
    int steals;

    public MessageEmbed toEmbed(){
        return new EmbedBuilder()
                .setTitle(name + " Info")
                .addField("ID", String.valueOf(id), false)
                .addField("UserID", userId, false)
                .addField("Name", name, false)
                .addField("Balance", String.valueOf(balance), false)
                .addField("Steals", String.valueOf(steals), false)
                .build();
    }
}
