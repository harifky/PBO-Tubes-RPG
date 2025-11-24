package com.elemental.factory;

import com.elemental.model.Character;
import com.elemental.model.CharacterClass;
import com.elemental.model.Element;

import java.util.Random;

/**
 * Factory for creating enemy characters
 */
public class EnemyFactory {
    private static final Random random = new Random();
    private static final String[] ENEMY_NAMES = {
        "Goblin", "Orc", "Troll", "Skeleton", "Zombie",
        "Bandit", "Assassin", "Witch", "Warlock", "Knight",
        "Dragon", "Phoenix", "Golem", "Wraith", "Demon"
    };

    /**
     * Create a random enemy at specified level
     */
    public static Character createEnemy(int level) {
        String name = getRandomEnemyName();
        CharacterClass enemyClass = getRandomClass();
        Element element = getRandomElement();

        Character enemy = CharacterFactory.createCharacter(enemyClass, name, element);

        // Level up enemy to specified level
        for (int i = 1; i < level; i++) {
            enemy.gainExperience(enemy.getLevel() * 100);
        }

        return enemy;
    }

    /**
     * Create enemy with specific class and element
     */
    public static Character createEnemy(String name, CharacterClass characterClass, Element element, int level) {
        Character enemy = CharacterFactory.createCharacter(characterClass, name, element);

        for (int i = 1; i < level; i++) {
            enemy.gainExperience(enemy.getLevel() * 100);
        }

        return enemy;
    }

    /**
     * Create a boss enemy (higher stats)
     */
    public static Character createBoss(String name, int level) {
        CharacterClass bossClass = getRandomClass();
        Element element = getRandomElement();

        Character boss = CharacterFactory.createCharacter(bossClass, name, element);

        // Level up
        for (int i = 1; i < level; i++) {
            boss.gainExperience(boss.getLevel() * 100);
        }

        return boss;
    }

    private static String getRandomEnemyName() {
        return ENEMY_NAMES[random.nextInt(ENEMY_NAMES.length)];
    }

    private static CharacterClass getRandomClass() {
        CharacterClass[] classes = CharacterClass.values();
        return classes[random.nextInt(classes.length)];
    }

    private static Element getRandomElement() {
        Element[] elements = Element.values();
        return elements[random.nextInt(elements.length)];
    }
}
