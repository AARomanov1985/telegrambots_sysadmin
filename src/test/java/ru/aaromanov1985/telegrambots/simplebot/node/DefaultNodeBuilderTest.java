package ru.aaromanov1985.telegrambots.simplebot.node;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import static org.junit.jupiter.api.Assertions.*;

// TODO
class DefaultNodeBuilderTest {

    private DefaultNodeBuilder builder = new DefaultNodeBuilder();

    @BeforeEach
    void setUp() {
        builder.setPath("resources/Nodes.xml");
        Nodes nodes = builder.buildNodes();
        // TODO
    }

    @Test
    void buildNodes() {
    }
}