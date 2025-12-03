package com.elemental.util;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Generic Repository Pattern untuk mengelola collections of entities
 * Demonstrasi Generic Programming dengan type parameters
 *
 * @param <T> Tipe entity yang dikelola
 * @param <ID> Tipe identifier entity
 */
public class GenericRepository<T, ID> {

    private final Map<ID, T> storage;
    private final Function<T, ID> idExtractor;

    /**
     * Constructor
     * @param idExtractor Function untuk extract ID dari entity
     */
    public GenericRepository(Function<T, ID> idExtractor) {
        this.storage = new HashMap<>();
        this.idExtractor = idExtractor;
    }

    /**
     * Simpan atau update entity
     * @param entity Entity yang akan disimpan
     * @return Entity yang disimpan
     */
    public T save(T entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity cannot be null");
        }
        ID id = idExtractor.apply(entity);
        storage.put(id, entity);
        return entity;
    }

    /**
     * Simpan multiple entities
     * @param entities Collection of entities
     * @return List of saved entities
     */
    public List<T> saveAll(Collection<T> entities) {
        List<T> saved = new ArrayList<>();
        for (T entity : entities) {
            saved.add(save(entity));
        }
        return saved;
    }

    /**
     * Cari entity berdasarkan ID
     * @param id ID entity
     * @return Optional berisi entity jika ditemukan
     */
    public Optional<T> findById(ID id) {
        return Optional.ofNullable(storage.get(id));
    }

    /**
     * Ambil semua entities
     * @return List of all entities
     */
    public List<T> findAll() {
        return new ArrayList<>(storage.values());
    }

    /**
     * Cari entities yang memenuhi predicate
     * @param predicate Kondisi yang harus dipenuhi
     * @return List of matching entities
     */
    public List<T> findWhere(Predicate<T> predicate) {
        List<T> results = new ArrayList<>();
        for (T entity : storage.values()) {
            if (predicate.test(entity)) {
                results.add(entity);
            }
        }
        return results;
    }

    /**
     * Cari entity pertama yang memenuhi predicate
     * @param predicate Kondisi yang harus dipenuhi
     * @return Optional berisi entity pertama yang match
     */
    public Optional<T> findFirst(Predicate<T> predicate) {
        for (T entity : storage.values()) {
            if (predicate.test(entity)) {
                return Optional.of(entity);
            }
        }
        return Optional.empty();
    }

    /**
     * Cek apakah entity dengan ID tersebut ada
     * @param id ID yang dicari
     * @return true jika ada, false jika tidak
     */
    public boolean existsById(ID id) {
        return storage.containsKey(id);
    }

    /**
     * Cek apakah ada entity yang memenuhi predicate
     * @param predicate Kondisi yang dicek
     * @return true jika ada yang match
     */
    public boolean exists(Predicate<T> predicate) {
        return findFirst(predicate).isPresent();
    }

    /**
     * Hitung jumlah entities
     * @return Jumlah entities
     */
    public int count() {
        return storage.size();
    }

    /**
     * Hapus entity berdasarkan ID
     * @param id ID entity yang akan dihapus
     * @return true jika berhasil dihapus, false jika tidak ditemukan
     */
    public boolean deleteById(ID id) {
        return storage.remove(id) != null;
    }

    /**
     * Hapus entity
     * @param entity Entity yang akan dihapus
     * @return true jika berhasil dihapus, false jika tidak ditemukan
     */
    public boolean delete(T entity) {
        if (entity == null) {
            return false;
        }
        ID id = idExtractor.apply(entity);
        return deleteById(id);
    }

    /**
     * Hapus semua entities
     */
    public void deleteAll() {
        storage.clear();
    }

    /**
     * Update entity
     * @param entity Entity yang akan diupdate
     * @return Entity yang sudah diupdate
     */
    public T update(T entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity cannot be null");
        }
        ID id = idExtractor.apply(entity);
        if (!existsById(id)) {
            throw new IllegalArgumentException("Entity with ID " + id + " does not exist");
        }
        return save(entity);
    }

    /**
     * CONTOH PENGGUNAAN DI PROYEK RPG:
     *
     * 1. Character Repository:
     *    GenericRepository<Character, String> characterRepo =
     *        new GenericRepository<>(Character::getName);
     *
     *    // Save character
     *    characterRepo.save(warrior);
     *
     *    // Find by name
     *    Optional<Character> found = characterRepo.findById("Warrior");
     *
     *    // Find all mages
     *    List<Character> mages = characterRepo.findWhere(
     *        c -> c.getCharacterClass() == CharacterClass.MAGE
     *    );
     *
     *    // Find fire characters
     *    List<Character> fireChars = characterRepo.findWhere(
     *        c -> c.getElement() == Element.FIRE
     *    );
     *
     * 2. Battle Repository:
     *    GenericRepository<Battle, Integer> battleRepo =
     *        new GenericRepository<>(Battle::getBattleId);
     *
     *    // Save battle
     *    battleRepo.save(battle);
     *
     *    // Find active battles
     *    List<Battle> active = battleRepo.findWhere(
     *        b -> b.getStatus() == BattleStatus.ONGOING
     *    );
     *
     * 3. Item Repository:
     *    GenericRepository<Item, String> itemRepo =
     *        new GenericRepository<>(Item::getName);
     *
     *    // Find healing items
     *    List<Item> healItems = itemRepo.findWhere(
     *        i -> i.getEffect().getEffectType() == "HEAL"
     *    );
     *
     *    // Check if item exists
     *    boolean hasPotion = itemRepo.existsById("Health Potion");
     */
}

