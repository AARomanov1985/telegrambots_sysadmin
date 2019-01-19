package ru.aaromanov1985.telegrambots.simplebot;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import ru.aaromanov1985.telegrambots.simplebot.node.NodeBuilderImpl;
import ru.aaromanov1985.telegrambots.simplebot.node.NodeHandlerImpl;

public class SimpleBotApplication {

	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"SpringBeans.xml");

		NodeHandlerImpl nodeHandler = context.getBean("nodeHandler", NodeHandlerImpl.class);
		nodeHandler.start();
	}
}
