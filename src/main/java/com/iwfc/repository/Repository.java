package com.iwfc.repository;

import java.util.List;
import java.util.Optional;

/**
 * Generic repository abstraction reused for every entity type in the system,
 * so storage/query logic is written once instead of per-entity.
 */
public interface Repository<T, ID> {
    void add(T item);
    Optional<T> findById(ID id);
    List<T> findAll();
    void update(T item);
    void remove(ID id);
    boolean existsById(ID id);
}
