package com.elemental.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Inventory Tests")
class InventoryTest {

    private Inventory inventory;

    @BeforeEach
    void setUp() {
        // Get singleton instance and reset it
        inventory = Inventory.getInstance();
        inventory.reset();
    }

    @Test
    @DisplayName("Should be singleton instance")
    void testSingleton() {
        Inventory instance1 = Inventory.getInstance();
        Inventory instance2 = Inventory.getInstance();
        assertSame(instance1, instance2);
    }

    @Test
    @DisplayName("Should initialize with beginner items")
    void testInitialization() {
        assertTrue(inventory.hasItem("Health Potion"));
        assertTrue(inventory.hasItem("Mana Potion"));
        assertTrue(inventory.hasItem("Revive"));
    }

    @Test
    @DisplayName("Should add items correctly")
    void testAddItem() {
        boolean added = inventory.addItem("Elixir", 3);
        assertTrue(added);
        assertTrue(inventory.hasItem("Elixir"));
    }

    @Test
    @DisplayName("Should not add negative quantity")
    void testAddItemNegativeQuantity() {
        boolean added = inventory.addItem("Elixir", -1);
        assertFalse(added);
    }

    @Test
    @DisplayName("Should remove items correctly")
    void testRemoveItem() {
        inventory.addItem("Test Item", 5);
        boolean removed = inventory.removeItem("Test Item", 3);
        assertTrue(removed);
        assertTrue(inventory.hasItem("Test Item"));
    }

    @Test
    @DisplayName("Should remove all quantity when removing exact amount")
    void testRemoveAllQuantity() {
        inventory.addItem("Test Item", 3);
        boolean removed = inventory.removeItem("Test Item", 3);
        assertTrue(removed);
        assertFalse(inventory.hasItem("Test Item"));
    }

    @Test
    @DisplayName("Should not remove more than available")
    void testCannotRemoveMoreThanAvailable() {
        inventory.addItem("Test Item", 2);
        boolean removed = inventory.removeItem("Test Item", 5);
        assertFalse(removed);
    }

    @Test
    @DisplayName("Should use item correctly")
    void testUseItem() {
        inventory.addItem("Test Item", 1);
        boolean used = inventory.useItem("Test Item");
        assertTrue(used);
        assertFalse(inventory.hasItem("Test Item"));
    }

    @Test
    @DisplayName("Should not use item that doesn't exist")
    void testCannotUseNonExistentItem() {
        boolean used = inventory.useItem("Non Existent Item");
        assertFalse(used);
    }

    @Test
    @DisplayName("Should check if has item")
    void testHasItem() {
        assertFalse(inventory.hasItem("New Item"));
        inventory.addItem("New Item", 1);
        assertTrue(inventory.hasItem("New Item"));
    }

    @Test
    @DisplayName("Should get all items")
    void testGetAllItems() {
        var items = inventory.getAllItems();
        assertNotNull(items);
        assertFalse(items.isEmpty());
        assertTrue(items.containsKey("Health Potion"));
    }

    @Test
    @DisplayName("Should reset inventory correctly")
    void testReset() {
        inventory.addItem("Extra Item", 10);
        inventory.reset();
        assertFalse(inventory.hasItem("Extra Item"));
        assertTrue(inventory.hasItem("Health Potion"));
    }
}

