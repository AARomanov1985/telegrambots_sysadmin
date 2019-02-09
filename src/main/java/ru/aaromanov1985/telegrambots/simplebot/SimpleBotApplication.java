package ru.aaromanov1985.telegrambots.simplebot;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.telegram.telegrambots.ApiContextInitializer;

public class SimpleBotApplication {

	public static void main(String[] args) {
		ApiContextInitializer.init();
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"SpringBeans.xml");

		Bot bot = context.getBean("bot", Bot.class);
		bot.execute();
	}
}
