package sonar.core.client.gui.widgets;

public enum ScrollerOrientation {

    VERTICAL, HORIZONTAL;

    public boolean isVertical() {
        return this == VERTICAL;
    }

    public boolean isHorizontal() {
        return this == HORIZONTAL;
    }
}