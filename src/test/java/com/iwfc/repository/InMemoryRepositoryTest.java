package com.iwfc.repository;

import com.iwfc.model.Equipment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * [Unit Testing] Tests for the generic Repository (Generics/Collections requirement).
 */
class InMemoryRepositoryTest {

    // Generic repository reused for Equipment, with the ID type String.
    private InMemoryRepository<Equipment, String> repository;

    // JUnit 5 lifecycle: @BeforeEach runs before every test, so each test starts with a fresh empty repository.
    @BeforeEach
    void setUp() {
        // Method reference Equipment::getId tells the repository how to read the ID of an item.
        repository = new InMemoryRepository<>(Equipment::getId);
    }

    // Verifies: add stores an item and findById returns it inside an Optional.
    @Test
    void add_and_findById() {
        Equipment equipment = new Equipment("EQ-1", "Treadmill", "Cardio Zone");
        repository.add(equipment);

        Optional<Equipment> found = repository.findById("EQ-1");

        assertTrue(found.isPresent());
        assertEquals("Treadmill", found.get().getName());
    }

    // Verifies: findAll returns every stored item.
    @Test
    void findAll_returnsAllItems() {
        repository.add(new Equipment("EQ-1", "Treadmill", "Cardio Zone"));
        repository.add(new Equipment("EQ-2", "Spin Bike", "Studio A"));

        List<Equipment> all = repository.findAll();

        assertEquals(2, all.size());
    }

    // Verifies: remove deletes the item so existsById becomes false.
    @Test
    void remove_deletesItem() {
        repository.add(new Equipment("EQ-1", "Treadmill", "Cardio Zone"));

        repository.remove("EQ-1");

        assertFalse(repository.existsById("EQ-1"));
    }
}
