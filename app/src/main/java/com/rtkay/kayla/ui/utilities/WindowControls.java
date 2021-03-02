package com.rtkay.kayla.ui.utilities;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.util.Duration;

public abstract class WindowControls {
    public static void centerNodeInScrollPane(ScrollPane scrollPane, Node node) {
        double width = scrollPane.getContent().getBoundsInLocal().getWidth();
        double x = node.getBoundsInParent().getMaxX();
        // scrolling values range from 0 to 1
        Animation animation = new Timeline(
                new KeyFrame(Duration.seconds(0.8),
                        new KeyValue(scrollPane.hvalueProperty(), (x/width))));
        animation.play();
        node.requestFocus();
    }
}
