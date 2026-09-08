package com.iwfc.repository;

import com.iwfc.model.Equipment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryRepositoryTest {

    private InMemoryRepository<Equipment, String> repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryRepository<>(Equipment::getId);
    }

    @Test
    void add_and_findById() {
        Equipment equipment = new Equipment("EQ-1", "Treadmill", "Cardio Zone");
        repository.add(equipment);

        Optional<Equipment> found = repository.findById("EQ-1");

        assertTrue(found.isPresent());
        assertEquals("Treadmill", found.get().getName());
    }

    @Test
    void findAll_returnsAllItems() {
        repository.add(new Equipment("EQ-1", "Treadmill", "Cardio Zone"));
        repository.add(new Equipment("EQ-2", "Spin Bike", "Studio A"));

        List<Equipment> all = repository.findAll();

        assertEquals(2, all.size());
    }

    @Test
    void remove_deletesItem() {
        repository.add(new Equipment("EQ-1", "Treadmill", "Cardio Zone"));

        repository.remove("EQ-1");

        assertFalse(repository.existsById("EQ-1"));
    }
}
