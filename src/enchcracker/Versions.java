package enchcracker;

public enum Versions {
    V1_8("1.8 - 1.8.9"),
    V1_9("1.9 - 1.10.2"),
    V1_11("1.11"),
    V1_11_1("1.11.1 - 1.12.2"),
    V1_13("1.13 - 1.13.2"),
    V1_14("1.14 - 1.14.2"),
    V1_14_3("1.14.3 - 1.15"),
    ;

    public final String name;
    Versions(String name) {
        this.name = name;
    }

    public boolean before(Versions other) {
        return ordinal() < other.ordinal();
    }

    public boolean after(Versions other) {
        return ordinal() > other.ordinal();
    }

    public static Versions latest() {
        return values()[values().length - 1];
    }

    public String toString() {
        return name;
    }
}
