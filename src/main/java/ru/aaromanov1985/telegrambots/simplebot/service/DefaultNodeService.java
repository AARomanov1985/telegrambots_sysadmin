package ru.aaromanov1985.telegrambots.simplebot.service;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.aaromanov1985.telegrambots.simplebot.node.*;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;

public class DefaultNodeService implements NodeService {

    @Resource
    private DefaultNodeBuilder nodeBuilder;

    private Nodes nodes;

    private static final Logger LOG = LoggerFactory.getLogger(DefaultNodeService.class);

    private static final String START_NODE = "startNode";
    private static final String END_NODE = "endNode";
    private static final String ERROR_NODE = "errorNode";
    private static final String SUCCESS_NODE = "successNode";

    @PostConstruct
    private void buildNodels(){
        nodes = nodeBuilder.buildNodes();
    }

    @Override
    public Node getStartNode() {
        return findNodeForCode(START_NODE);
    }

    @Override
    public Node getEndNode() {
        return findNodeForCode(END_NODE);
    }

    @Override
    public Node getSuccessNode() {
        return findNodeForCode(SUCCESS_NODE);
    }

    @Override
    public Node getAnswer(Node currentNode, String request) {
        LOG.debug("getAnswer for node {} and request {}", currentNode.getCode(), request);
        List<Variant> variants = currentNode.getVariants();
        if (CollectionUtils.isNotEmpty(variants)) {
            for (Variant variant : variants) {
                if (request.equals(variant.getValue())) {
                    Node nodeForCode = findNodeForCode(variant.getTarget());
                    LOG.debug("Found Node: {}", nodeForCode);
                    return findNodeForCode(variant.getTarget());
                }
            }
        }
        LOG.error("targetNode is not found for request " + request);
        return getErrorNode();
    }

    @Override
    public Node findNodeForCode(String nodeCode) {
        LOG.debug("Find node for code {}",nodeCode);
        for (Node node : nodes.getNodes()) {
            if (node.getCode().equals(nodeCode)) {
                LOG.debug("Found Node: {}", node.getCode());
                return node;
            }
        }
        LOG.error("Node for code {} is not found!", nodeCode);
        return getErrorNode();
    }

    @Override
    public Node getErrorNode() {
        Node node = new Node();
        node.setCode(ERROR_NODE);
        return node;
    }

    @Override
    public boolean isErrorNode(Node node) {
        return node == null || node.getCode().equals(ERROR_NODE);
    }

    @Override
    public boolean isStartNode(Node node) {
        return node != null && node.getCode().equals(START_NODE);
    }

    @Override
    public boolean isEndNode(Node node){
        return node != null && END_NODE.equals(node.getCode());
    }
}
