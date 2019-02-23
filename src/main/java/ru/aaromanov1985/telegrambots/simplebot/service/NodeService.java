package ru.aaromanov1985.telegrambots.simplebot.service;

import ru.aaromanov1985.telegrambots.simplebot.node.Node;

public interface NodeService {

    Node getStartNode();

    Node getEndNode();

    Node getSuccessNode();

    Node getAnswer(Node currentNode, String request);

    Node findNodeForCode(String nodeCode);

    Node getErrorNode();

    boolean isErrorNode(Node node);

    boolean isStartNode(Node node);
}
