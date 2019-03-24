package ru.aaromanov1985.telegrambots.simplebot;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.aaromanov1985.telegrambots.simplebot.service.NodeService;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Bot extends TelegramLongPollingBot {

    private Logger LOG = LoggerFactory.getLogger(Bot.class);
    private final static String START =  "/start";
    private final static String CANCEL =  "/cancel";
    private final static String NAME = "sysadm623_bot";
    private final static String TOKEN = "727435438:AAFAWnJOCbasqYIYeRR--eaTCs_gYrVOumI";

    private Set<Conversation> conversations = new HashSet<>();

    private  ExecutorService executorService = Executors.newCachedThreadPool();

    @Resource
    private NodeService nodeService;



    public void execute() {
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
            validateConversations();
            // Set variables
            String message_text = update.getMessage().getText();
            LOG.info("message_has text {}", message_text);
            long chat_id = update.getMessage().getChatId();
            if (isStart(message_text)){
                startConversation(chat_id, message_text);
            }else if (isCancel(message_text)){
                endConversation(chat_id);
            }else{
                continueConversation(chat_id, message_text);
            }
        }
    }

    private void startConversation(long chat_id, String message_text){
        LOG.debug("Start conversation for chat_id"+chat_id);
        Conversation conversation = new Conversation(chat_id, nodeService);
        conversations.add(conversation);
        String answer = conversation.execute(message_text);
        sendMessage(chat_id, answer);
    }

    private void endConversation(long chat_id){
        LOG.debug("End conversation for chat_id"+chat_id);
        Conversation conversation = getConversation(chat_id);
        conversations.remove(conversation);
    }

    private void continueConversation(long chat_id, String message_text){
        LOG.debug("Continue conversation for chat_id"+chat_id);
        Conversation conversation = getConversation(chat_id);
        if (conversation!=null){
            String answer = conversation.execute(message_text);
            LOG.debug("Answer: {}",answer);
            sendMessage(chat_id, answer);
        }
    }

    private void sendMessage(long chat_id, String text){
        executorService.submit(new Runnable() {

            @Override
            public void run() {
                LOG.debug("Send message for chat "+chat_id);
                SendMessage message = new SendMessage() // Create a message object object
                        .setChatId(chat_id)
                        .setText(text);
                try {
                    execute(message); // Sending our message object to user
                } catch (TelegramApiException e) {
                    LOG.error(e.getMessage(), e);
                }
            }
        });
    }

    private void validateConversations(){
        for (Conversation conversation : conversations){
            if (!conversation.isActual()){
                LOG.info("Conversation with id {} will be removed", conversation.getId());
                conversations.remove(conversation);
            }
        }
    }

    private Conversation getConversation(long chatId){
        for (Conversation conversation : conversations){
            if (conversation.getId()==chatId){
                return conversation;
            }
        }
        LOG.error("Conversation doesNotFound for chat "+chatId);
        return null;
    }

    private boolean isStart(String message){
        return StringUtils.isNotEmpty(message) && START.equals(message);
    }

    private boolean isCancel(String message){
        return StringUtils.isNotEmpty(message) && CANCEL.equals(message);
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
