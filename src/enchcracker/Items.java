package enchcracker;

public class Items {

	// @formatter:off
	public static final String
			// 1.8
			LEATHER_HELMET = "leather_helmet",
			LEATHER_CHESTPLATE = "leather_chestplate",
			LEATHER_LEGGINGS = "leather_leggings",
			LEATHER_BOOTS = "leather_boots",
			IRON_HELMET = "iron_helmet",
			IRON_CHESTPLATE = "iron_chestplate",
			IRON_LEGGINGS = "iron_leggings",
			IRON_BOOTS = "iron_boots",
			CHAINMAIL_HELMET = "chainmail_helmet",
			CHAINMAIL_CHESTPLATE = "chainmail_chestplate",
			CHAINMAIL_LEGGINGS = "chainmail_leggings",
			CHAINMAIL_BOOTS = "chainmail_boots",
			GOLDEN_HELMET = "golden_helmet",
			GOLDEN_CHESTPLATE = "golden_chestplate",
			GOLDEN_LEGGINGS = "golden_leggings",
			GOLDEN_BOOTS = "golden_boots",
			DIAMOND_HELMET = "diamond_helmet",
			DIAMOND_CHESTPLATE = "diamond_chestplate",
			DIAMOND_LEGGINGS = "diamond_leggings",
			DIAMOND_BOOTS = "diamond_boots",
			WOODEN_SWORD = "wooden_sword",
			STONE_SWORD = "stone_sword",
			IRON_SWORD = "iron_sword",
			GOLDEN_SWORD = "golden_sword",
			DIAMOND_SWORD = "diamond_sword",
			WOODEN_PICKAXE = "wooden_pickaxe",
			STONE_PICKAXE = "stone_pickaxe",
			IRON_PICKAXE = "iron_pickaxe",
			GOLDEN_PICKAXE = "golden_pickaxe",
			DIAMOND_PICKAXE = "diamond_pickaxe",
			WOODEN_AXE = "wooden_axe",
			STONE_AXE = "stone_axe",
			IRON_AXE = "iron_axe",
			GOLDEN_AXE = "golden_axe",
			DIAMOND_AXE = "diamond_axe",
			WOODEN_SHOVEL = "wooden_shovel",
			STONE_SHOVEL = "stone_shovel",
			IRON_SHOVEL = "iron_shovel",
			GOLDEN_SHOVEL = "golden_shovel",
			DIAMOND_SHOVEL = "diamond_shovel",
			WOODEN_HOE = "wooden_hoe",
			STONE_HOE = "stone_hoe",
			IRON_HOE = "iron_hoe",
			GOLDEN_HOE = "golden_hoe",
			DIAMOND_HOE = "diamond_hoe",
			CARROT_ON_A_STICK = "carrot_on_a_stick",
			FISHING_ROD = "fishing_rod",
			FLINT_AND_STEEL = "flint_and_steel",
			SHEARS = "shears",
			BOW = "bow",
			BOOK = "book",
			PUMPKIN = "pumpkin",
			SKULL = "skull",
			// 1.9
			ELYTRA = "elytra",
			SHIELD = "shield",
			// 1.13
			TRIDENT = "trident",
			TURTLE_HELMET = "turtle_helmet",
			// 1.14
			CROSSBOW = "crossbow",
			// 1.16
			NETHERITE_HELMET = "netherite_helmet",
			NETHERITE_CHESTPLATE = "netherite_chestplate",
			NETHERITE_LEGGINGS = "netherite_leggings",
			NETHERITE_BOOTS = "netherite_boots",
			NETHERITE_SWORD = "netherite_sword",
			NETHERITE_PICKAXE = "netherite_pickaxe",
			NETHERITE_AXE = "netherite_axe",
			NETHERITE_SHOVEL = "netherite_shovel",
			NETHERITE_HOE = "netherite_hoe",
			// 1.21
			MACE = "mace",
            // 1.21.9
            COPPER_HELMET = "copper_helmet",
            COPPER_CHESTPLATE = "copper_chestplate",
            COPPER_LEGGINGS = "copper_leggings",
            COPPER_BOOTS = "copper_boots",
            COPPER_SWORD = "copper_sword",
            COPPER_PICKAXE = "copper_pickaxe",
            COPPER_AXE = "copper_axe",
            COPPER_SHOVEL = "copper_shovel",
            COPPER_HOE = "copper_hoe";
	// @formatter:on

	public static boolean isArmor(String item) {
		if (item.endsWith("_helmet") || item.endsWith("_chestplate") || item.endsWith("_leggings")
				|| item.endsWith("_boots")) {
			// @formatter:off
			return LEATHER_HELMET.equals(item)
				|| LEATHER_CHESTPLATE.equals(item)
				|| LEATHER_LEGGINGS.equals(item)
				|| LEATHER_BOOTS.equals(item)
				|| IRON_HELMET.equals(item)
				|| IRON_CHESTPLATE.equals(item)
				|| IRON_LEGGINGS.equals(item)
				|| IRON_BOOTS.equals(item)
				|| CHAINMAIL_HELMET.equals(item)
				|| CHAINMAIL_CHESTPLATE.equals(item)
				|| CHAINMAIL_LEGGINGS.equals(item)
				|| CHAINMAIL_BOOTS.equals(item)
				|| GOLDEN_HELMET.equals(item)
				|| GOLDEN_CHESTPLATE.equals(item)
				|| GOLDEN_LEGGINGS.equals(item)
				|| GOLDEN_BOOTS.equals(item)
				|| DIAMOND_HELMET.equals(item)
				|| DIAMOND_CHESTPLATE.equals(item)
				|| DIAMOND_LEGGINGS.equals(item)
				|| DIAMOND_BOOTS.equals(item)
				|| TURTLE_HELMET.equals(item)
				|| NETHERITE_HELMET.equals(item)
				|| NETHERITE_CHESTPLATE.equals(item)
				|| NETHERITE_LEGGINGS.equals(item)
				|| NETHERITE_BOOTS.equals(item)
                || COPPER_HELMET.equals(item)
                || COPPER_CHESTPLATE.equals(item)
                || COPPER_LEGGINGS.equals(item)
                || COPPER_BOOTS.equals(item);
			// @formatter:on
		}
		return false;
	}

	public static boolean isHelmet(String item) {
		return isArmor(item) && item.endsWith("_helmet");
	}

	public static boolean isChestplate(String item) {
		return isArmor(item) && item.endsWith("_chestplate");
	}

	public static boolean isLeggings(String item) {
		return isArmor(item) && item.endsWith("_leggings");
	}

	public static boolean isBoots(String item) {
		return isArmor(item) && item.endsWith("_boots");
	}

	public static boolean isSword(String item) {
		if (item.endsWith("_sword")) {
			// @formatter:off
			return WOODEN_SWORD.equals(item)
				|| STONE_SWORD.equals(item)
				|| IRON_SWORD.equals(item)
				|| GOLDEN_SWORD.equals(item)
				|| DIAMOND_SWORD.equals(item)
				|| NETHERITE_SWORD.equals(item)
                || COPPER_SWORD.equals(item);
			// @formatter:on
		}
		return false;
	}

	public static boolean isAxe(String item) {
		if (item.endsWith("_axe")) {
			// @formatter:off
			return WOODEN_AXE.equals(item)
				|| STONE_AXE.equals(item)
				|| IRON_AXE.equals(item)
				|| GOLDEN_AXE.equals(item)
				|| DIAMOND_AXE.equals(item)
				|| NETHERITE_AXE.equals(item)
                || COPPER_AXE.equals(item);
			// @formatter:on
		}
		return false;
	}

	public static boolean isTool(String item) {
		if (isAxe(item)) {
			return true;
		}
		if (item.endsWith("_pickaxe") || item.endsWith("_shovel") || item.endsWith("_hoe")) {
			// @formatter:off
			return WOODEN_PICKAXE.equals(item)
				|| STONE_PICKAXE.equals(item)
				|| IRON_PICKAXE.equals(item)
				|| GOLDEN_PICKAXE.equals(item)
				|| DIAMOND_PICKAXE.equals(item)
				|| NETHERITE_PICKAXE.equals(item)
                || COPPER_PICKAXE.equals(item)
				|| WOODEN_SHOVEL.equals(item)
				|| STONE_SHOVEL.equals(item)
				|| IRON_SHOVEL.equals(item)
				|| GOLDEN_SHOVEL.equals(item)
				|| DIAMOND_SHOVEL.equals(item)
				|| NETHERITE_SHOVEL.equals(item)
                || COPPER_SHOVEL.equals(item)
				|| WOODEN_HOE.equals(item)
				|| STONE_HOE.equals(item)
				|| IRON_HOE.equals(item)
				|| GOLDEN_HOE.equals(item)
				|| DIAMOND_HOE.equals(item)
				|| NETHERITE_HOE.equals(item)
                || COPPER_HOE.equals(item);
			// @formatter:on
		}
		return false;
	}

	public static boolean hasDurability(String item) {
		// @formatter:off
		return isArmor(item)
			|| isTool(item)
			|| isSword(item)
			|| BOW.equals(item)
			|| CARROT_ON_A_STICK.equals(item)
			|| ELYTRA.equals(item)
			|| FISHING_ROD.equals(item)
			|| FLINT_AND_STEEL.equals(item)
			|| SHEARS.equals(item)
			|| SHIELD.equals(item)
			|| TRIDENT.equals(item)
			|| CROSSBOW.equals(item)
			|| MACE.equals(item);
		// @formatter:on
	}

	public static int getEnchantability(String item) {
		if (isArmor(item)) {
			if (item.startsWith("leather_")) {
				return 15;
			}
			if (item.startsWith("iron_")) {
				return 9;
			}
			if (item.startsWith("chainmail_")) {
				return 12;
			}
			if (item.startsWith("golden_")) {
				return 25;
			}
			if (item.startsWith("diamond_")) {
				return 10;
			}
			if (item.startsWith("turtle_")) {
				return 9;
			}
			if (item.startsWith("netherite_")) {
				return 15;
			}
            if (item.startsWith("copper_")) {
                return 8;
            }
		}
		if (isSword(item) || isTool(item)) {
			if (item.startsWith("wooden_")) {
				return 15;
			}
			if (item.startsWith("stone_")) {
				return 5;
			}
			if (item.startsWith("iron_")) {
				return 14;
			}
			if (item.startsWith("golden_")) {
				return 22;
			}
			if (item.startsWith("diamond_")) {
				return 10;
			}
			if (item.startsWith("netherite_")) {
				return 15;
			}
            if (item.startsWith("copper_")) {
                return 13;
            }
		}
		if (BOW.equals(item)) {
			return 1;
		}
		if (FISHING_ROD.equals(item)) {
			return 1;
		}
		if (TRIDENT.equals(item)) {
			return 1;
		}
		if (CROSSBOW.equals(item)) {
			return 1;
		}
		if (MACE.equals(item)) {
			return 15;
		}
		if (BOOK.equals(item)) {
			return 1;
		}
		return 0;
	}

	public static Versions getIntroducedVersion(String item) {
		switch (item) {
			case ELYTRA:
			case SHIELD:
				return Versions.V1_9;
			case TRIDENT:
			case TURTLE_HELMET:
				return Versions.V1_13;
			case CROSSBOW:
				return Versions.V1_14;
			case NETHERITE_HELMET:
			case NETHERITE_CHESTPLATE:
			case NETHERITE_LEGGINGS:
			case NETHERITE_BOOTS:
			case NETHERITE_SWORD:
			case NETHERITE_PICKAXE:
			case NETHERITE_AXE:
			case NETHERITE_SHOVEL:
			case NETHERITE_HOE:
				return Versions.V1_16;
			case MACE:
				return Versions.V1_21;
            case COPPER_HELMET:
            case COPPER_CHESTPLATE:
            case COPPER_LEGGINGS:
            case COPPER_BOOTS:
            case COPPER_SWORD:
            case COPPER_PICKAXE:
            case COPPER_AXE:
            case COPPER_SHOVEL:
            case COPPER_HOE:
                return Versions.V1_21_9;
			default:
				return Versions.V1_8;
		}
	}
	
}
