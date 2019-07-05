package enchcracker;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.ToIntFunction;

public class Enchantments {

	// @formatter:off
	public static final String
			PROTECTION = "protection",
			FIRE_PROTECTION = "fire_protection",
			FEATHER_FALLING = "feather_falling",
			BLAST_PROTECTION = "blast_protection",
			PROJECTILE_PROTECTION = "projectile_protection",
			RESPIRATION = "respiration",
			AQUA_AFFINITY = "aqua_affinity",
			THORNS = "thorns",
			DEPTH_STRIDER = "depth_strider",
			FROST_WALKER = "frost_walker",
			BINDING_CURSE = "binding_curse",
			SHARPNESS = "sharpness",
			SMITE = "smite",
			BANE_OF_ARTHROPODS = "bane_of_arthropods",
			KNOCKBACK = "knockback",
			FIRE_ASPECT = "fire_aspect",
			LOOTING = "looting",
			SWEEPING = "sweeping",
			EFFICIENCY = "efficiency",
			SILK_TOUCH = "silk_touch",
			UNBREAKING = "unbreaking",
			FORTUNE = "fortune",
			POWER = "power",
			PUNCH = "punch",
			FLAME = "flame",
			INFINITY = "infinity",
			LUCK_OF_THE_SEA = "luck_of_the_sea",
			LURE = "lure",
			LOYALTY = "loyalty",
			IMPALING = "impaling",
			RIPTIDE = "riptide",
			CHANNELING = "channeling",
			MULTISHOT = "multishot",
			QUICK_CHARGE = "quick_charge",
			PIERCING = "piercing",
			MENDING = "mending",
			VANISHING_CURSE = "vanishing_curse";
	// @formatter:on

	// @formatter:off
	public static final LinkedHashSet<String> ALL_ENCHANTMENTS = new LinkedHashSet<>();
	// @formatter:on

	private static final Set<Set<String>> INCOMPATIBLE_GROUPS = new HashSet<>();

	static {
		for (Field field : Enchantments.class.getFields()) {
			if (Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers()) && field.getType() == String.class) {
				try {
					ALL_ENCHANTMENTS.add((String) field.get(null));
				} catch (Exception e) {
					throw new AssertionError(e);
				}
			}
		}

		Set<String> set;

		set = new HashSet<>();
		set.add(INFINITY);
		set.add(MENDING);
		INCOMPATIBLE_GROUPS.add(set);

		set = new HashSet<>();
		set.add(SHARPNESS);
		set.add(SMITE);
		set.add(BANE_OF_ARTHROPODS);
		INCOMPATIBLE_GROUPS.add(set);

		set = new HashSet<>();
		set.add(DEPTH_STRIDER);
		set.add(FROST_WALKER);
		INCOMPATIBLE_GROUPS.add(set);

		set = new HashSet<>();
		set.add(SILK_TOUCH);
		set.add(LOOTING);
		INCOMPATIBLE_GROUPS.add(set);

		set = new HashSet<>();
		set.add(SILK_TOUCH);
		set.add(FORTUNE);
		INCOMPATIBLE_GROUPS.add(set);

		set = new HashSet<>();
		set.add(SILK_TOUCH);
		set.add(LUCK_OF_THE_SEA);
		INCOMPATIBLE_GROUPS.add(set);

		set = new HashSet<>();
		set.add(RIPTIDE);
		set.add(LOYALTY);
		INCOMPATIBLE_GROUPS.add(set);

		set = new HashSet<>();
		set.add(RIPTIDE);
		set.add(CHANNELING);
		INCOMPATIBLE_GROUPS.add(set);

		set = new HashSet<>();
		set.add(MULTISHOT);
		set.add(PIERCING);
		INCOMPATIBLE_GROUPS.add(set);

		set = new HashSet<>();
		set.add(PROTECTION);
		set.add(BLAST_PROTECTION);
		set.add(FIRE_PROTECTION);
		set.add(PROJECTILE_PROTECTION);
		INCOMPATIBLE_GROUPS.add(set);

		for (Field field : Enchantments.class.getDeclaredFields()) {
			if (field.getModifiers() == (Modifier.PUBLIC | Modifier.STATIC | Modifier.FINAL)) {
				if (field.getType() == String.class) {
					String enchantmentName;
					try {
						enchantmentName = (String) field.get(null);
					} catch (Exception e) {
						throw new Error(e);
					}
					ALL_ENCHANTMENTS.add(enchantmentName);
				}
			}
		}
	}

	public static boolean canApply(String enchantment, String item, boolean primary) {
		if (Items.BOOK.equals(item)) {
			return true;
		}

		switch (enchantment) {
		case PROTECTION:
		case FIRE_PROTECTION:
		case BLAST_PROTECTION:
		case PROJECTILE_PROTECTION:
			return Items.isArmor(item);
		case THORNS:
			return primary ? Items.isChestplate(item) : Items.isArmor(item);
		case FEATHER_FALLING:
		case DEPTH_STRIDER:
		case FROST_WALKER:
			return Items.isBoots(item);
		case RESPIRATION:
		case AQUA_AFFINITY:
			return Items.isHelmet(item);
		case BINDING_CURSE:
			return Items.isArmor(item) || Items.PUMPKIN.equals(item) || Items.ELYTRA.equals(item)
					|| Items.SKULL.equals(item);
		case SHARPNESS:
		case SMITE:
		case BANE_OF_ARTHROPODS:
			return Items.isSword(item) || !primary && Items.isAxe(item);
		case KNOCKBACK:
		case FIRE_ASPECT:
		case LOOTING:
		case SWEEPING:
			return Items.isSword(item);
		case EFFICIENCY:
			return Items.isTool(item) || !primary && Items.SHEARS.equals(item);
		case SILK_TOUCH:
		case FORTUNE:
			return Items.isTool(item);
		case POWER:
		case PUNCH:
		case FLAME:
		case INFINITY:
			return Items.BOW.equals(item);
		case LUCK_OF_THE_SEA:
		case LURE:
			return Items.FISHING_ROD.equals(item);
		case UNBREAKING:
		case MENDING:
			return Items.hasDurability(item);
		case VANISHING_CURSE:
			return Items.hasDurability(item) || Items.PUMPKIN.equals(item) || Items.SKULL.equals(item);
		case LOYALTY:
		case IMPALING:
		case RIPTIDE:
		case CHANNELING:
			return Items.TRIDENT.equals(item);
		case MULTISHOT:
		case QUICK_CHARGE:
		case PIERCING:
			return Items.CROSSBOW.equals(item);
		default:
			throw new IllegalArgumentException("Unknown enchantment: " + enchantment);
		}
	}

	public static boolean isTreasure(String enchantment) {
		return FROST_WALKER.equals(enchantment) || MENDING.equals(enchantment) || BINDING_CURSE.equals(enchantment)
				|| VANISHING_CURSE.equals(enchantment);
	}

	public static int getMaxLevel(String enchantment) {
		switch (enchantment) {
		case SHARPNESS:
		case SMITE:
		case BANE_OF_ARTHROPODS:
		case EFFICIENCY:
		case POWER:
		case IMPALING:
			return 5;
		case PROTECTION:
		case FIRE_PROTECTION:
		case BLAST_PROTECTION:
		case PROJECTILE_PROTECTION:
		case FEATHER_FALLING:
		case PIERCING:
			return 4;
		case THORNS:
		case DEPTH_STRIDER:
		case RESPIRATION:
		case LOOTING:
		case SWEEPING:
		case FORTUNE:
		case LUCK_OF_THE_SEA:
		case LURE:
		case UNBREAKING:
		case LOYALTY:
		case RIPTIDE:
		case QUICK_CHARGE:
			return 3;
		case FROST_WALKER:
		case KNOCKBACK:
		case FIRE_ASPECT:
		case PUNCH:
			return 2;
		case AQUA_AFFINITY:
		case BINDING_CURSE:
		case SILK_TOUCH:
		case FLAME:
		case INFINITY:
		case MENDING:
		case VANISHING_CURSE:
		case CHANNELING:
		case MULTISHOT:
			return 1;
		default:
			throw new IllegalArgumentException("Unknown enchantment: " + enchantment);
		}
	}

	public static int getMinEnchantability(String enchantment, int level) {
		switch (enchantment) {
		case PROTECTION:
			return 1 + (level - 1) * 11;
		case FIRE_PROTECTION:
			return 10 + (level - 1) * 8;
		case FEATHER_FALLING:
			return 5 + (level - 1) * 6;
		case BLAST_PROTECTION:
			return 5 + (level - 1) * 8;
		case PROJECTILE_PROTECTION:
			return 3 + (level - 1) * 6;
		case RESPIRATION:
			return level * 10;
		case AQUA_AFFINITY:
			return 1;
		case THORNS:
			return 10 + (level - 1) * 20;
		case DEPTH_STRIDER:
			return level * 10;
		case FROST_WALKER:
			return level * 10;
		case BINDING_CURSE:
			return 25;
		case SHARPNESS:
			return 1 + (level - 1) * 11;
		case SMITE:
			return 5 + (level - 1) * 8;
		case BANE_OF_ARTHROPODS:
			return 5 + (level - 1) * 8;
		case KNOCKBACK:
			return 5 + (level - 1) * 20;
		case FIRE_ASPECT:
			return 10 + (level - 1) * 20;
		case LOOTING:
			return 15 + (level - 1) * 9;
		case SWEEPING:
			return 5 + (level - 1) * 9;
		case EFFICIENCY:
			return 1 + (level - 1) * 10;
		case SILK_TOUCH:
			return 15;
		case UNBREAKING:
			return 5 + (level - 1) * 8;
		case FORTUNE:
			return 15 + (level - 1) * 9;
		case POWER:
			return 1 + (level - 1) * 10;
		case PUNCH:
			return 12 + (level - 1) * 20;
		case FLAME:
			return 20;
		case INFINITY:
			return 20;
		case LUCK_OF_THE_SEA:
			return 15 + (level - 1) * 9;
		case LURE:
			return 15 + (level - 1) * 9;
		case MENDING:
			return 25;
		case VANISHING_CURSE:
			return 25;
		case LOYALTY:
			return 5 + level * 7;
		case IMPALING:
			return 1 + (level - 1) * 8;
		case RIPTIDE:
			return 10 * level + 7;
		case CHANNELING:
			return 25;
		case MULTISHOT:
			return 20;
		case QUICK_CHARGE:
			return 12 + (level - 1) * 20;
		case PIERCING:
			return 1 + (level - 1) * 10;
		default:
			throw new IllegalArgumentException("Unknown enchantment: " + enchantment);
		}
	}

	public static int getMaxEnchantability(String enchantment, int level) {
		switch (enchantment) {
		case PROTECTION:
			return 1 + level * 11;
		case FIRE_PROTECTION:
			return 10 + level * 8;
		case FEATHER_FALLING:
			return 5 + level * 6;
		case BLAST_PROTECTION:
			return 5 + level * 8;
		case PROJECTILE_PROTECTION:
			return 3 + level * 6;
		case RESPIRATION:
			return 30 + level * 10;
		case AQUA_AFFINITY:
			return 41;
		case THORNS:
			return 40 + level * 20;
		case DEPTH_STRIDER:
			return 15 + level * 10;
		case FROST_WALKER:
			return 15 + level * 10;
		case BINDING_CURSE:
			return 50;
		case SHARPNESS:
			return 21 + (level - 1) * 11;
		case SMITE:
			return 25 + (level - 1) * 8;
		case BANE_OF_ARTHROPODS:
			return 25 + (level - 1) * 8;
		case KNOCKBACK:
			return 55 + (level - 1) * 20;
		case FIRE_ASPECT:
			return 40 + level * 20;
		case LOOTING:
			return 65 + (level - 1) * 9;
		case SWEEPING:
			return 20 + (level - 1) * 9;
		case EFFICIENCY:
			return 50 + level * 10;
		case SILK_TOUCH:
			return 65;
		case UNBREAKING:
			return 55 + (level - 1) * 8;
		case FORTUNE:
			return 65 + (level - 1) * 9;
		case POWER:
			return 16 + (level - 1) * 10;
		case PUNCH:
			return 37 + (level - 1) * 20;
		case FLAME:
			return 50;
		case INFINITY:
			return 50;
		case LUCK_OF_THE_SEA:
			return 65 + (level - 1) * 9;
		case LURE:
			return 65 + (level - 1) * 9;
		case MENDING:
			return 75;
		case VANISHING_CURSE:
			return 50;
		case LOYALTY:
			return 50;
		case IMPALING:
			return 21 + (level - 1) * 8;
		case RIPTIDE:
			return 50;
		case CHANNELING:
			return 50;
		case MULTISHOT:
			return 50;
		case QUICK_CHARGE:
			return 50;
		case PIERCING:
			return 50;
		default:
			throw new IllegalArgumentException("Unknown enchantment: " + enchantment);
		}
	}

	public static int getWeight(String enchantment) {
		switch (enchantment) {
		case PROTECTION:
		case SHARPNESS:
		case EFFICIENCY:
		case POWER:
		case PIERCING:
			return 30;
		case FIRE_PROTECTION:
		case FEATHER_FALLING:
		case PROJECTILE_PROTECTION:
		case SMITE:
		case BANE_OF_ARTHROPODS:
		case KNOCKBACK:
		case UNBREAKING:
		case LOYALTY:
		case QUICK_CHARGE:
			return 10;
		case BLAST_PROTECTION:
		case RESPIRATION:
		case AQUA_AFFINITY:
		case DEPTH_STRIDER:
		case FROST_WALKER:
		case FIRE_ASPECT:
		case LOOTING:
		case SWEEPING:
		case FORTUNE:
		case PUNCH:
		case FLAME:
		case LUCK_OF_THE_SEA:
		case LURE:
		case MENDING:
		case IMPALING:
		case RIPTIDE:
		case MULTISHOT:
			return 3;
		case THORNS:
		case BINDING_CURSE:
		case SILK_TOUCH:
		case INFINITY:
		case VANISHING_CURSE:
		case CHANNELING:
			return 1;
		default:
			throw new IllegalArgumentException("Unknown enchantment: " + enchantment);
		}
	}

	public static boolean areCompatible(String enchA, String enchB) {
		// Can't have same enchantment twice
		if (enchA.equals(enchB)) {
			return false;
		}

		return INCOMPATIBLE_GROUPS.stream().noneMatch(group -> group.contains(enchA) && group.contains(enchB));
	}

	public static List<EnchantmentInstance> parseEnchantmentInstance(String item, String str, boolean maxOnly) {
		String[] parts = str.trim().split("\\s+");

		String enchantment = parts[0].toLowerCase();
		if (!ALL_ENCHANTMENTS.contains(enchantment)) {
			return Collections.emptyList();
		}

		// Get the max level on enchantment tables by maximizing the random
		// values
		int enchantability = Items.getEnchantability(item);
		int maxLevel;
		if (enchantability == 0 || isTreasure(enchantment) || !canApply(enchantment, item, true)) {
			maxLevel = 0;
		} else {
			int level = 30 + 1 + enchantability / 4 + enchantability / 4;
			level += Math.round(level * 0.15f);
			for (maxLevel = getMaxLevel(enchantment); maxLevel >= 1; maxLevel--) {
				if (level >= getMinEnchantability(enchantment, maxLevel)) {
					break;
				}
			}
		}

		if (parts.length == 1) {
			// Infer the level

			if (maxLevel == 0) {
				return Collections.emptyList();
			}

			if (maxOnly) {
				return Collections.singletonList(new EnchantmentInstance(enchantment, maxLevel));
			} else {
				List<EnchantmentInstance> list = new ArrayList<>();
				for (int level = 1; level <= maxLevel; level++) {
					list.add(new EnchantmentInstance(enchantment, level));
				}
				return list;
			}
		} else if (parts.length == 2) {
			// Read the level
			int level;
			try {
				level = Integer.parseInt(parts[1]);
			} catch (NumberFormatException e) {
				return Collections.emptyList();
			}

			if (level < 1 || level > maxLevel) {
				return Collections.emptyList();
			}

			return Collections.singletonList(new EnchantmentInstance(enchantment, level));
		} else {
			return Collections.emptyList();
		}
	}

	public static int calcEnchantmentTableLevel(Random rand, int slot, int bookshelves, String item) {
		if (Items.getEnchantability(item) == 0) {
			return 0;
		}

		int level = rand.nextInt(8) + 1 + (bookshelves >> 1) + rand.nextInt(bookshelves + 1);

		switch (slot) {
		case 0:
			return Math.max(level / 3, 1);
		case 1:
			return level * 2 / 3 + 1;
		case 2:
			return Math.max(level, bookshelves * 2);
		default:
			throw new IllegalArgumentException();
		}
	}

	public static List<EnchantmentInstance> getEnchantmentsInTable(Random rand, int xpSeed, String item, int slot,
			int levels) {
		rand.setSeed(xpSeed + slot);

		List<EnchantmentInstance> list = addRandomEnchantments(rand, item, levels, false);
		if (Items.BOOK.equals(item) && list.size() > 1) {
			list.remove(rand.nextInt(list.size()));
		}

		return list;
	}

	public static List<EnchantmentInstance> getHighestAllowedEnchantments(int level, String item, boolean treasure) {
		List<EnchantmentInstance> allowedEnchantments = new ArrayList<>();
		for (String enchantment : ALL_ENCHANTMENTS) {
			if ((treasure || !isTreasure(enchantment)) && canApply(enchantment, item, true)) {
				for (int enchLvl = getMaxLevel(enchantment); enchLvl >= 1; enchLvl--) {
					if (level >= getMinEnchantability(enchantment, enchLvl)
							&& level <= getMaxEnchantability(enchantment, enchLvl)) {
						allowedEnchantments.add(new EnchantmentInstance(enchantment, enchLvl));
						break;
					}
				}
			}
		}
		return allowedEnchantments;
	}

	public static List<EnchantmentInstance> addRandomEnchantments(Random rand, String item, int level,
			boolean treasure) {
		int enchantability = Items.getEnchantability(item);
		List<EnchantmentInstance> enchantments = new ArrayList<>();

		if (enchantability > 0) {
			// Modify the enchantment level randomly and according to
			// enchantability
			level = level + 1 + rand.nextInt(enchantability / 4 + 1) + rand.nextInt(enchantability / 4 + 1);
			float percentChange = (rand.nextFloat() + rand.nextFloat() - 1) * 0.15f;
			level += Math.round(level * percentChange);
			if (level < 1) {
				level = 1;
			}

			// Get a list of allowed enchantments with their max allowed levels
			List<EnchantmentInstance> allowedEnchantments = getHighestAllowedEnchantments(level, item, treasure);

			// allowedEnchantments.forEach(ench -> System.out.println("Allowed:
			// " + ench));

			if (!allowedEnchantments.isEmpty()) {
				// Get first enchantment
				EnchantmentInstance enchantmentInstance = weightedRandom(rand, allowedEnchantments,
						it -> getWeight(it.enchantment));
				enchantments.add(enchantmentInstance);

				// Get optional extra enchantments
				while (rand.nextInt(50) <= level) {
					// 1.14 enchantment nerf
					level = level * 4 / 5 + 1;
					allowedEnchantments = getHighestAllowedEnchantments(level, item, treasure);

					// Remove incompatible enchantments from allowed list with
					// last enchantment
					for (EnchantmentInstance ench : enchantments) {
						String enchantment = ench.enchantment;
						allowedEnchantments.removeIf(it -> !areCompatible(it.enchantment, enchantment));
					}

					if (allowedEnchantments.isEmpty()) {
						// no enchantments left
						break;
					}

					// Get extra enchantment
					enchantmentInstance = weightedRandom(rand, allowedEnchantments, it -> getWeight(it.enchantment));
					enchantments.add(enchantmentInstance);

					// Make it less likely for another enchantment to happen
					level /= 2;
				}
			}
		}

		return enchantments;
	}

	public static class EnchantmentInstance {
		public final String enchantment;
		public final int level;

		public EnchantmentInstance(String enchantment, int level) {
			this.enchantment = enchantment;
			this.level = level;
		}

		@Override
		public int hashCode() {
			return enchantment.hashCode() + 31 * level;
		}

		@Override
		public boolean equals(Object other) {
			return other instanceof EnchantmentInstance && equals((EnchantmentInstance) other);
		}

		public boolean equals(EnchantmentInstance other) {
			return enchantment.equals(other.enchantment) && level == other.level;
		}

		@Override
		public String toString() {
			if (level == 1 && getMaxLevel(enchantment) == 1) {
				return enchantment;
			}
			String lvlName;
			switch (level) {
			case 1:
				lvlName = "I";
				break;
			case 2:
				lvlName = "II";
				break;
			case 3:
				lvlName = "III";
				break;
			case 4:
				lvlName = "IV";
				break;
			case 5:
				lvlName = "V";
				break;
			default:
				lvlName = String.valueOf(level);
				break;
			}
			return enchantment + " " + lvlName;
		}
	}

	private static <T> T weightedRandom(Random rand, List<T> list, ToIntFunction<T> weightExtractor) {
		int weight = list.stream().mapToInt(weightExtractor).sum();
		if (weight <= 0) {
			return null;
		}
		weight = rand.nextInt(weight);
		for (T t : list) {
			weight -= weightExtractor.applyAsInt(t);
			if (weight < 0) {
				return t;
			}
		}
		return null;
	}

}
