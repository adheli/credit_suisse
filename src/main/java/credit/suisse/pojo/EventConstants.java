package credit.suisse.pojo;

public enum EventConstants {

    ID(1),
    DURATION(2),
    TYPE(3),
    HOST(4),
    ALERT(5);

    private final int index;

    EventConstants(int index) {
        this.index = index;
    }

    public String getValue() {
        return this.name();
    }

    public int getIndex() {
        return this.index;
    }
}
