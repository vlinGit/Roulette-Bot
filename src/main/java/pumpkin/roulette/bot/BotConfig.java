package pumpkin.roulette.bot;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

public class BotConfig {
    private static final Properties config = new Properties();

    static{
        try{
            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            InputStream input = classloader.getResourceAsStream("application.properties");
            config.load(input);
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public static String get(String field){
        return config.getProperty(field);
    }
}
