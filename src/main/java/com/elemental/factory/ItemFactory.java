package com.elemental.factory;

import com.elemental.model.*;

import java.util.*;

/**
 * Factory for creating predefined items
 * FR-ITEM-002: All 7 item types defined here
 */
public class ItemFactory {
    private static final Map<String, Item> ITEM_REGISTRY = new HashMap<>();

    static {
        registerItems();
    }

    /**
     * Register all available items in the game
     */
    private static void registerItems() {
        // Health Potion - Restore 50 HP
        ITEM_REGISTRY.put("Health Potion", new Item(
                "Health Potion",
                "Restores 50 HP",
                ItemType.HEALING,
                ItemEffect.RESTORE_HP,
                50,
                0,
                ItemTarget.ALIVE_ALLY));

        // Mana Potion - Restore 30 MP
        ITEM_REGISTRY.put("Mana Potion", new Item(
                "Mana Potion",
                "Restores 30 MP",
                ItemType.HEALING,
                ItemEffect.RESTORE_MP,
                30,
                0,
                ItemTarget.ALIVE_ALLY));

        // Elixir - Restore 100 HP + 50 MP
        ITEM_REGISTRY.put("Elixir", new Item(
                "Elixir",
                "Restores 100 HP and 50 MP",
                ItemType.HEALING,
                ItemEffect.RESTORE_BOTH,
                100, // HP value
                0,
                ItemTarget.ALIVE_ALLY));

        // Attack Boost - +30% Attack for 3 turns
        ITEM_REGISTRY.put("Attack Boost", new Item(
                "Attack Boost",
                "+30% Attack for 3 turns",
                ItemType.BUFF,
                ItemEffect.BOOST_ATTACK,
                30, // Percentage
                3, // Duration
                ItemTarget.ALIVE_ALLY));

        // Defense Boost - +40% Defense for 3 turns
        ITEM_REGISTRY.put("Defense Boost", new Item(
                "Defense Boost",
                "+40% Defense for 3 turns",
                ItemType.BUFF,
                ItemEffect.BOOST_DEFENSE,
                40, // Percentage
                3, // Duration
                ItemTarget.ALIVE_ALLY));

        // Antidote - Remove Poison/Burn status
        ITEM_REGISTRY.put("Antidote", new Item(
                "Antidote",
                "Removes Poison and Burn status",
                ItemType.STATUS_CURE,
                ItemEffect.CURE_STATUS,
                0,
                0,
                ItemTarget.ALIVE_ALLY));

        // Revive - Revive dead character with 30% HP
        ITEM_REGISTRY.put("Revive", new Item(
                "Revive",
                "Revives a dead ally with 30% HP",
                ItemType.REVIVAL,
                ItemEffect.REVIVE,
                30, // Percentage of max HP
                0,
                ItemTarget.DEAD_ALLY));
    }

    /**
     * Get an item by name
     */
    public static Item getItem(String itemName) {
        Item template = ITEM_REGISTRY.get(itemName);
        if (template == null) {
            throw new IllegalArgumentException("Unknown item: " + itemName);
        }
        // Return a copy to prevent modification of the template
        return new Item(
                template.getName(),
                template.getDescription(),
                template.getItemType(),
                template.getEffect(),
                template.getValue(),
                template.getDuration(),
                template.getTargetType());
    }

    /**
     * Check if an item exists in the registry
     */
    public static boolean isValidItem(String itemName) {
        return ITEM_REGISTRY.containsKey(itemName);
    }

    /**
     * Get all available item names
     */
    public static List<String> getAllItemNames() {
        return new ArrayList<>(ITEM_REGISTRY.keySet());
    }

    /**
     * Get items by type
     */
    public static List<String> getItemsByType(ItemType type) {
        List<String> items = new ArrayList<>();
        for (Map.Entry<String, Item> entry : ITEM_REGISTRY.entrySet()) {
            if (entry.getValue().getItemType() == type) {
                items.add(entry.getKey());
            }
        }
        return items;
    }
}
