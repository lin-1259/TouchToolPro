package top.bogey.touch_tool.data.pin;

public enum PinDirection {
    IN, OUT;

    public boolean isOut() {
        return this == OUT;
    }
}
