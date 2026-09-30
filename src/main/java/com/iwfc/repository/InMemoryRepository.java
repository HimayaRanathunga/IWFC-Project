package com.iwfc.repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * [Generics and Collections] Generic class with type parameters T and ID.
 * Polymorphism: implements the Repository interface, so callers can depend on the interface.
 *
 * Single generic implementation of {@link Repository} backed by a HashMap
 * (LinkedHashMap to keep deterministic iteration order for console output).
 * One class, instantiated once per entity type - the "generic repository"
 * requirement in place of a bare ArrayList<String>.
 */
public class InMemoryRepository<T, ID> implements Repository<T, ID> {

    // Encapsulation: the map is private. LinkedHashMap keeps insertion order.
    private final Map<ID, T> store = new LinkedHashMap<>();
    // Function is injected so the repository knows how to get the id from any T
    // (for example Equipment::getId), similar to a Strategy passed in by the caller.
    private final Function<T, ID> idExtractor;

    public InMemoryRepository(Function<T, ID> idExtractor) {
        this.idExtractor = idExtractor;
    }

    // Note: add and update both use put, so an existing id is replaced (upsert).
    // Duplicate checks are done in the service layer, not here.
    @Override
    public void add(T item) {
        store.put(idExtractor.apply(item), item);
    }

    @Override
    public Optional<T> findById(ID id) {
        // Optional: wraps a possible null from the map so callers must handle "not found".
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<T> findAll() {
        // Defensive copy: return a new list so callers cannot change the internal map.
        return new ArrayList<>(store.values());
    }

    @Override
    public void update(T item) {
        store.put(idExtractor.apply(item), item);
    }

    @Override
    public void remove(ID id) {
        store.remove(id);
    }

    @Override
    public boolean existsById(ID id) {
        return store.containsKey(id);
    }
}
