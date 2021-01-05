package enchcracker;

public class Materials {

    public static final int NETHERITE = 0;
    public static final int DIAMOND = 1;
    public static final int GOLD = 2;
    public static final int IRON = 3;
    public static final int CHAIN = 4;
    public static final int STONE = 5;
    public static final int LEATHER = 6;

    public static Versions getIntroducedVersion(int material) {
        if (material == NETHERITE)
            return Versions.V1_16;
        return Versions.V1_8;
    }

}
