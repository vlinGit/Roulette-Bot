package pumpkin.roulette.bot;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

public class BotConfig {
    private static final Properties config = new Properties();

    static{
        try{
            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            URL url = classloader.getResource("application.properties");
            FileInputStream configFile = new FileInputStream(url.getPath());
            config.load(configFile);
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public static String get(String field){
        return config.getProperty(field);
    }
}
