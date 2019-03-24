package ru.aaromanov1985.telegrambots.simplebot;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.aaromanov1985.telegrambots.simplebot.node.Node;
import ru.aaromanov1985.telegrambots.simplebot.service.NodeService;

import javax.annotation.Resource;
import java.util.Objects;

public class Conversation {

    private Logger LOG = LoggerFactory.getLogger(Conversation.class);

    private final static String INCORRECT_REQUEST = "Неверный запрос";
    private long id;
    private long lastRequest;
    private Node currentNode;
    private boolean isActive;
    private boolean isFirstRequest;
    // 30 min
    private static final long TIMEOUT = 1800000;

    private NodeService nodeService;

    public Conversation(long id, NodeService nodeService) {
        LOG.debug("Create conversation");
        this.id = id;
        this.nodeService = nodeService;
        init();

        LOG.debug("id= {}", id);
        LOG.debug("currentNode= {}", currentNode);
        LOG.debug("isActive= {}", isActive);
        LOG.debug("nodeService= {}", nodeService);
    }

    private void init(){
        currentNode = nodeService.getStartNode();
        isActive = true;
        isFirstRequest = true;
    }

    public String execute(String message) {
        LOG.debug("execute conversation for message {}", message);
        lastRequest = System.currentTimeMillis();

        return getAnswer(message);
    }

    private String getAnswer(String request) {

        if (nodeService.isEndNode(currentNode)){
            init();
        }

        if (isFirstRequest) {
            isFirstRequest = false;
            return currentNode.getMessage();
        }

        Node node = nodeService.getAnswer(currentNode, request);

        if (nodeService.isErrorNode(node)) {
            LOG.error("It's error node!");
            return INCORRECT_REQUEST;
        }

        updateCurrentNode(node);
        return node.getMessage();
    }

    private void updateCurrentNode(Node node) {
        if (node != null) {
            currentNode = node;
            LOG.info("current node changed to " + node.getCode());
        }
    }

    public boolean isActual() {
        long time = System.currentTimeMillis() - lastRequest;
        return time < TIMEOUT && isActive;
    }

    public long getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Conversation that = (Conversation) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}