package ru.aaromanov1985.telegrambots.simplebot;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.aaromanov1985.telegrambots.simplebot.node.Node;
import ru.aaromanov1985.telegrambots.simplebot.service.NodeService;

import javax.annotation.Resource;

public class Bot extends TelegramLongPollingBot {

    private Logger LOG = LoggerFactory.getLogger(Bot.class);
    private final static String NAME = "sysadm623_bot";
    private final static String TOKEN = "727435438:AAFAWnJOCbasqYIYeRR--eaTCs_gYrVOumI";

    @Resource
    private NodeService nodeService;

    private Node currentNode;

    public void execute() {
        currentNode = nodeService.getStartNode();
        // Initialize Api Context
        ApiContextInitializer.init();

        // Instantiate Telegram Bots API
        TelegramBotsApi botsApi = new TelegramBotsApi();

        // Register our bot
        try {
            botsApi.registerBot(this);
        } catch (TelegramApiException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        LOG.info("request received");
        // We check if the update has a message and the message has text
        if (update.hasMessage() && update.getMessage().hasText()) {
            // Set variables
            String message_text = update.getMessage().getText();
            LOG.info("message_has text {}", message_text);
            long chat_id = update.getMessage().getChatId();

            SendMessage message = new SendMessage() // Create a message object object
                    .setChatId(chat_id)
                    .setText(getAnswer(message_text));
            try {
                execute(message); // Sending our message object to user
            } catch (TelegramApiException e) {
                LOG.error(e.getMessage(), e);
            }
        }
    }

    private String getAnswer(String request){
        Node node = nodeService.getAnswer(currentNode, request);
        if (nodeService.isStartNode(currentNode)){
            return currentNode.getMessage();
        }else {
            if (nodeService.isErrorNode(node)) {
                return StringUtils.EMPTY;
            } else {
                currentNode = node;
                return node.getMessage();
            }
        }
    }

    @Override
    public String getBotUsername() {
        return NAME;
    }

    @Override
    public String getBotToken() {
        return TOKEN;
    }
}
