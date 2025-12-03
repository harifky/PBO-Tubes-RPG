# Elemental RPG - Utility Classes

## 📦 Generic Programming Utilities

Package ini berisi generic utility classes yang dapat digunakan di seluruh proyek untuk meningkatkan type safety, code reusability, dan maintainability.

---

## 📚 Available Classes

### 1. `Pair<F, S>` - Generic Tuple

Generic class untuk menyimpan dua nilai yang berhubungan.

**Type Parameters:**
- `F` - Tipe data elemen pertama
- `S` - Tipe data elemen kedua

**Example Usage:**
```java
// Return damage dan critical status
Pair<Integer, Boolean> result = Pair.of(150, true);
int damage = result.getFirst();
boolean isCritical = result.getSecond();

// Transform values
Pair<String, String> names = Pair.of("Warrior", "Mage");
Pair<String, String> upper = names.map(String::toUpperCase, String::toUpperCase);
```

**Key Features:**
- Immutable
- Factory method `of()`
- Transform methods: `map()`, `withFirst()`, `withSecond()`
- `swap()` untuk menukar elemen
- `equals()`, `hashCode()`, `toString()` implemented

---

### 2. `Result<T, E>` - Type-Safe Error Handling

Generic wrapper untuk operasi yang bisa sukses atau gagal, menggantikan penggunaan exception untuk expected errors.

**Type Parameters:**
- `T` - Tipe data untuk nilai sukses
- `E` - Tipe data untuk error (biasanya String)

**Example Usage:**
```java
// Create Result
Result<SaveData, String> result = loadGame(slot);

// Check and extract value
if (result.isSuccess()) {
    SaveData data = result.getValue();
    // use data
} else {
    String error = result.getError();
    // handle error
}

// Functional style
result.getValueOptional().ifPresent(this::initGame);
result.getErrorOptional().ifPresent(this::showError);

// Chaining operations
Result<Character, String> result = validateCharacter(char)
    .flatMap(this::levelUp)
    .flatMap(this::saveCharacter);
```

**Key Features:**
- Type-safe error handling
- Functional programming support
- Methods: `map()`, `flatMap()`, `mapError()`
- `getOrElse()` untuk default values
- No exception throwing for expected errors

---

### 3. `GenericRepository<T, ID>` - Repository Pattern

Generic repository untuk mengelola collections of entities dengan CRUD operations.

**Type Parameters:**
- `T` - Tipe entity yang dikelola
- `ID` - Tipe identifier entity

**Example Usage:**
```java
// Create repository
GenericRepository<Character, String> charRepo = 
    new GenericRepository<>(Character::getName);

// Save entity
charRepo.save(warrior);

// Find by ID
Optional<Character> found = charRepo.findById("Warrior");

// Find with predicate
List<Character> mages = charRepo.findWhere(
    c -> c.getCharacterClass() == CharacterClass.MAGE
);

// Find fire element characters
List<Character> fireChars = charRepo.findWhere(
    c -> c.getElement() == Element.FIRE
);

// Update and delete
charRepo.update(updatedCharacter);
charRepo.deleteById("OldWarrior");
```

**Key Features:**
- CRUD operations: `save()`, `findById()`, `update()`, `delete()`
- Query dengan predicate: `findWhere()`, `findFirst()`, `exists()`
- Batch operations: `saveAll()`, `deleteAll()`
- Functional style dengan lambdas
- In-memory storage dengan HashMap

---

## 🎯 Use Cases di Proyek RPG

### Damage Calculation
```java
// DamageCalculator.java
public Pair<Integer, Boolean> calculateDamageWithCrit(Character attacker, Character target) {
    int damage = calculateBaseDamage(attacker, target);
    boolean isCritical = Math.random() < 0.15;
    return Pair.of(damage, isCritical);
}
```

### Save/Load System
```java
// SaveLoadService.java
public Result<SaveData, String> loadGame(int slot) {
    try {
        SaveData data = loadFromFile(slot);
        return Result.success(data);
    } catch (Exception e) {
        return Result.failure("Failed to load: " + e.getMessage());
    }
}
```

### Character Management
```java
// CharacterService.java
private GenericRepository<Character, String> repository = 
    new GenericRepository<>(Character::getName);

public Optional<Character> getCharacter(String name) {
    return repository.findById(name);
}

public List<Character> getAllMages() {
    return repository.findWhere(c -> c.getCharacterClass() == MAGE);
}
```

---

## 📖 Documentation

Untuk contoh lengkap dan use cases, lihat:
- `GENERIC_EXAMPLES.md` - Contoh implementasi praktis
- `GENERIC_PROGRAMMING_ANALYSIS.md` - Analisis mendalam
- `GENERIC_PROGRAMMING_SUMMARY.md` - Rangkuman singkat

---

## ✨ Benefits

### Type Safety
- Compile-time type checking
- No runtime ClassCastException
- IDE autocomplete support

### Code Reusability
- Satu repository untuk semua entity types
- Generic error handling untuk semua operations
- Generic tuple untuk any paired values

### Maintainability
- Consistent API across codebase
- Self-documenting dengan type parameters
- Easy to test dan mock

### Performance
- No reflection overhead
- No boxing/unboxing (when used correctly)
- Compiler optimizations

---

## 🔧 Requirements

- Java 8 or higher (for lambdas and functional interfaces)
- No external dependencies

---

## 👥 Authors

PBO Tubes RPG Team - 2025

