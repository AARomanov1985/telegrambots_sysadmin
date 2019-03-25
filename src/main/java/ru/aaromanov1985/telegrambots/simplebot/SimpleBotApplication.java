package ru.aaromanov1985.telegrambots.simplebot;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.telegram.telegrambots.ApiContextInitializer;
import ru.aaromanov1985.telegrambots.simplebot.bot.DefaultBot;

public class SimpleBotApplication {

    private static final String TEST_MODE = "test";

    public static void main(String[] args) {
        ApiContextInitializer.init();
        ApplicationContext context = new ClassPathXmlApplicationContext(
                "SpringBeans.xml");

        DefaultBot bot = context.getBean("bot", DefaultBot.class);

        if (isTestMode(args)){
            bot.enableTestMode();
        }

        bot.execute();
    }

    private static boolean isTestMode(String[] args) {
        if (args != null && args.length > 0) {
			return TEST_MODE.equals(args[0]);
        }
        return false;
    }
}
