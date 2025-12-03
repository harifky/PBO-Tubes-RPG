package com.elemental.model;

import java.util.*;

/**
 * Global Inventory System (Singleton)
 */
public class Inventory {
    private static Inventory instance;
    private static final int MAX_SLOTS = 50; // Kapasitas lebih besar karena Global
    private Map<String, Integer> items;

    // Private constructor agar tidak bisa di-new sembarangan
    private Inventory() {
        this.items = new HashMap<>();
        initializeBeginnerItems();
    }

    // Singleton Instance Accessor
    public static Inventory getInstance() {
        if (instance == null) {
            instance = new Inventory();
        }
        return instance;
    }

    // Method untuk me-reset inventory (misal saat New Game)
    public void reset() {
        this.items.clear();
        initializeBeginnerItems();
    }

    // Method untuk Load data dari Save File
    public void loadFromData(Map<String, Integer> loadedItems) {
        this.items.clear();
        if (loadedItems != null) {
            this.items.putAll(loadedItems);
        }
    }

    private void initializeBeginnerItems() {
        items.put("Health Potion", 5);
        items.put("Mana Potion", 5);
        items.put("Revive", 2);
    }

    // --- SISA METHOD TETAP SAMA (Hanya hapus keyword static jika ada) ---

    public boolean addItem(String itemName, int quantity) {
        if (quantity <= 0) return false;
        if (!items.containsKey(itemName) && items.size() >= MAX_SLOTS) return false;
        items.put(itemName, items.getOrDefault(itemName, 0) + quantity);
        return true;
    }

    public boolean removeItem(String itemName, int quantity) {
        if (quantity <= 0 || !items.containsKey(itemName)) return false;
        int currentQty = items.get(itemName);
        if (currentQty < quantity) return false;

        if (currentQty == quantity) {
            items.remove(itemName);
        } else {
            items.put(itemName, currentQty - quantity);
        }
        return true;
    }

    public boolean hasItem(String itemName) {
        return items.containsKey(itemName) && items.get(itemName) > 0;
    }

    public boolean useItem(String itemName) {
        if (!hasItem(itemName)) return false;
        return removeItem(itemName, 1);
    }

    public Map<String, Integer> getAllItems() {
        return new HashMap<>(items);
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }
}