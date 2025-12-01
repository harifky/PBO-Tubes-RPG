package com.elemental.model;

import java.util.*;

/**
 * FR-ITEM-001: Inventory Management
 * Manages character's item inventory with max 20 slots
 */
public class Inventory {
    private static final int MAX_SLOTS = 20;
    private Map<String, Integer> items; // Item name -> quantity

    /**
     * Constructor - initializes with beginner items
     */
    public Inventory() {
        this.items = new HashMap<>();
        initializeBeginnerItems();
    }

    /**
     * Initialize beginner items (5 Health Potion + 5 Mana Potion)
     */
    private void initializeBeginnerItems() {
        items.put("Health Potion", 5);
        items.put("Mana Potion", 5);
    }

    /**
     * Add item to inventory
     * FR-ITEM-001: Max inventory slots: 20
     */
    public boolean addItem(String itemName, int quantity) {
        if (quantity <= 0) {
            return false;
        }

        // Check if we can add this item type (max 20 different types)
        if (!items.containsKey(itemName) && items.size() >= MAX_SLOTS) {
            return false; // Inventory full
        }

        items.put(itemName, items.getOrDefault(itemName, 0) + quantity);
        return true;
    }

    /**
     * Remove item from inventory
     */
    public boolean removeItem(String itemName, int quantity) {
        if (quantity <= 0 || !items.containsKey(itemName)) {
            return false;
        }

        int currentQty = items.get(itemName);
        if (currentQty < quantity) {
            return false; // Not enough items
        }

        if (currentQty == quantity) {
            items.remove(itemName);
        } else {
            items.put(itemName, currentQty - quantity);
        }

        return true;
    }

    /**
     * Get quantity of specific item
     */
    public int getQuantity(String itemName) {
        return items.getOrDefault(itemName, 0);
    }

    /**
     * Check if item exists in inventory
     */
    public boolean hasItem(String itemName) {
        return items.containsKey(itemName) && items.get(itemName) > 0;
    }

    /**
     * Check if can add more items (slots available)
     */
    public boolean canAddItem(String itemName, int quantity) {
        if (items.containsKey(itemName)) {
            return true; // Can always add more of existing item
        }
        return items.size() < MAX_SLOTS; // Check if new slot available
    }

    /**
     * Get all items in inventory
     */
    public Map<String, Integer> getAllItems() {
        return new HashMap<>(items);
    }

    /**
     * FR-ITEM-003: Use item from inventory
     * Returns true if item was successfully used and removed
     */
    public boolean useItem(String itemName) {
        if (!hasItem(itemName)) {
            return false;
        }
        return removeItem(itemName, 1);
    }

    /**
     * Get usable items (items with quantity > 0)
     */
    public List<String> getUsableItems() {
        List<String> usableItems = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : items.entrySet()) {
            if (entry.getValue() > 0) {
                usableItems.add(entry.getKey());
            }
        }
        return usableItems;
    }

    /**
     * Check if inventory is empty
     */
    public boolean isEmpty() {
        return items.isEmpty() || items.values().stream().allMatch(qty -> qty == 0);
    }

    /**
     * Get number of different item types
     */
    public int getItemTypeCount() {
        return items.size();
    }

    /**
     * Get total number of items
     */
    public int getTotalItemCount() {
        return items.values().stream().mapToInt(Integer::intValue).sum();
    }

    /**
     * Clear all items
     */
    public void clear() {
        items.clear();
    }

    /**
     * Display inventory contents
     */
    public String displayInventory() {
        if (isEmpty()) {
            return "Inventory is empty.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("=== INVENTORY ===\n");
        sb.append(String.format("Slots: %d/%d\n", items.size(), MAX_SLOTS));
        sb.append("─────────────────────────────\n");

        for (Map.Entry<String, Integer> entry : items.entrySet()) {
            sb.append(String.format("  %s x%d\n", entry.getKey(), entry.getValue()));
        }

        return sb.toString();
    }

    @Override
    public String toString() {
        return displayInventory();
    }
}
