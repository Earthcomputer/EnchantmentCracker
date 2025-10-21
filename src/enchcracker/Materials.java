package enchcracker;

public class Materials {

    public static final int NETHERITE = 0;
    public static final int DIAMOND = 1;
    public static final int GOLD = 2;
    public static final int IRON = 3;
    public static final int COPPER = 4;
    public static final int CHAIN = 5;
    public static final int STONE = 6;
    public static final int LEATHER = 7;

    public static Versions getIntroducedVersion(int material) {
        switch (material) {
            case COPPER:
                return Versions.V1_21_9;
            case NETHERITE:
                return Versions.V1_16;
            default:
                return Versions.V1_8;
        }
    }

}
