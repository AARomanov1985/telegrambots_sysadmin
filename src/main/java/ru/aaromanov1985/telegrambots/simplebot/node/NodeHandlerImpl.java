package ru.aaromanov1985.telegrambots.simplebot.node;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class NodeHandlerImpl implements NodeHandler {

    private NodeBuilderImpl nodeBuilder;

    private Nodes nodes;

    private static final Logger LOG = LoggerFactory.getLogger(NodeHandlerImpl.class);

    private static final String START_NODE = "startNode";
    private static final String END_NODE = "endNode";
    private static final String SUCCESS_NODE = "successNode";

    public void start() {
        nodes = nodeBuilder.buildNodes();
        handle(findNodeForCode(START_NODE));
    }

    public void handle(Node node) {
        if (node == null) {
            LOG.error("Node is null!");
        }
        sendRequest(node);
        String userResponse = getUserResponse();
        String nodeCodeForRequest = getTargetNodeCodeForRequest(node, userResponse);
        Node nodeForRequest = findNodeForCode(nodeCodeForRequest);

        if (isEndNode(node) || isSuccessNode(node)) {
            LOG.info("Conversation is finished");
            return;
        } else {
            handle(nodeForRequest);
        }
    }

    private boolean isEndNode(Node node) {
        return node.getCode().equals(END_NODE);
    }

    private boolean isSuccessNode(Node node) {
        return node.getCode().equals(SUCCESS_NODE);
    }

    private String getTargetNodeCodeForRequest(Node node, String userResponse) {
        List<Variant> variants = node.getVariants();
        if (CollectionUtils.isNotEmpty(variants)) {
            for (Variant variant : variants) {
                if (userResponse.equals(variant.getValue())) {
                    return variant.getTarget();
                }
            }
        }
        return StringUtils.EMPTY;
    }

    private Node findNodeForCode(String nodeCode) {
        for (Node node : nodes.getNodes()) {
            if (node.getCode().equals(nodeCode)) {
                return node;
            }
        }
        return null;
    }

    private void sendRequest(Node node) {
        // TODO
    }

    private String getUserResponse() {
        // TODO
        return "Да";
    }

    public NodeBuilderImpl getNodeBuilder() {
        return nodeBuilder;
    }

    public void setNodeBuilder(NodeBuilderImpl nodeBuilder) {
        this.nodeBuilder = nodeBuilder;
    }
}
