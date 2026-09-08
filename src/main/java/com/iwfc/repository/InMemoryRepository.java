package com.iwfc.repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * Single generic implementation of {@link Repository} backed by a HashMap
 * (LinkedHashMap to keep deterministic iteration order for console output).
 * One class, instantiated once per entity type - the "generic repository"
 * requirement in place of a bare ArrayList<String>.
 */
public class InMemoryRepository<T, ID> implements Repository<T, ID> {

    private final Map<ID, T> store = new LinkedHashMap<>();
    private final Function<T, ID> idExtractor;

    public InMemoryRepository(Function<T, ID> idExtractor) {
        this.idExtractor = idExtractor;
    }

    @Override
    public void add(T item) {
        store.put(idExtractor.apply(item), item);
    }

    @Override
    public Optional<T> findById(ID id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<T> findAll() {
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
