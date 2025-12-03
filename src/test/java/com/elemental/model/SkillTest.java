package com.elemental.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Skill Tests")
class SkillTest {

    @Test
    @DisplayName("Should create skill with all parameters")
    void testCreateSkillWithElement() {
        Skill skill = new Skill("Fireball", 15, 1.5, SkillType.DAMAGE, Element.FIRE);

        assertEquals("Fireball", skill.getName());
        assertEquals(15, skill.getMpCost());
        assertEquals(1.5, skill.getDamageMultiplier());
        assertEquals(SkillType.DAMAGE, skill.getSkillType());
        assertEquals(Element.FIRE, skill.getElement());
    }

    @Test
    @DisplayName("Should create skill without element")
    void testCreateSkillWithoutElement() {
        Skill skill = new Skill("Heal", 20, 0.5, SkillType.HEAL);

        assertEquals("Heal", skill.getName());
        assertEquals(20, skill.getMpCost());
        assertEquals(0.5, skill.getDamageMultiplier());
        assertEquals(SkillType.HEAL, skill.getSkillType());
        assertNull(skill.getElement());
    }

    @Test
    @DisplayName("Fire Mage should have fire skills")
    void testFireMageSkills() {
        List<Skill> skills = Skill.getMageSkills(Element.FIRE);

        assertNotNull(skills);
        assertFalse(skills.isEmpty());

        // Check for specific fire skills
        assertTrue(skills.stream().anyMatch(s -> s.getName().equals("Fireball")));
        assertTrue(skills.stream().anyMatch(s -> s.getName().equals("Meditation")));
    }

    @Test
    @DisplayName("Water Mage should have water skills")
    void testWaterMageSkills() {
        List<Skill> skills = Skill.getMageSkills(Element.WATER);

        assertNotNull(skills);
        assertFalse(skills.isEmpty());

        assertTrue(skills.stream().anyMatch(s -> s.getName().equals("Water Bolt")));
        assertTrue(skills.stream().anyMatch(s -> s.getName().equals("Healing Stream")));
        assertTrue(skills.stream().anyMatch(s -> s.getName().equals("Meditation")));
    }

    @Test
    @DisplayName("Earth Mage should have earth skills")
    void testEarthMageSkills() {
        List<Skill> skills = Skill.getMageSkills(Element.EARTH);

        assertNotNull(skills);
        assertFalse(skills.isEmpty());

        assertTrue(skills.stream().anyMatch(s -> s.getName().equals("Stone Strike")));
        assertTrue(skills.stream().anyMatch(s -> s.getName().equals("Earth Shield")));
    }

    @Test
    @DisplayName("Fire Warrior should have fire skills")
    void testFireWarriorSkills() {
        List<Skill> skills = Skill.getWarriorSkills(Element.FIRE);

        assertNotNull(skills);
        assertFalse(skills.isEmpty());

        assertTrue(skills.stream().anyMatch(s -> s.getName().equals("Flame Slash")));
    }

    @Test
    @DisplayName("Water Warrior should have water skills")
    void testWaterWarriorSkills() {
        List<Skill> skills = Skill.getWarriorSkills(Element.WATER);

        assertNotNull(skills);
        assertFalse(skills.isEmpty());

        assertTrue(skills.stream().anyMatch(s -> s.getName().equals("Aqua Slash")));
    }

    @Test
    @DisplayName("Earth Warrior should have earth skills")
    void testEarthWarriorSkills() {
        List<Skill> skills = Skill.getWarriorSkills(Element.EARTH);

        assertNotNull(skills);
        assertFalse(skills.isEmpty());

        assertTrue(skills.stream().anyMatch(s -> s.getName().equals("Earth Slam")));
    }

    @Test
    @DisplayName("Fire Ranger should have fire skills")
    void testFireRangerSkills() {
        List<Skill> skills = Skill.getRangerSkills(Element.FIRE);

        assertNotNull(skills);
        assertFalse(skills.isEmpty());
    }

    @Test
    @DisplayName("Water Ranger should have water skills")
    void testWaterRangerSkills() {
        List<Skill> skills = Skill.getRangerSkills(Element.WATER);

        assertNotNull(skills);
        assertFalse(skills.isEmpty());
    }

    @Test
    @DisplayName("Earth Ranger should have earth skills")
    void testEarthRangerSkills() {
        List<Skill> skills = Skill.getRangerSkills(Element.EARTH);

        assertNotNull(skills);
        assertFalse(skills.isEmpty());
    }

    @Test
    @DisplayName("All mage skills should have consistent structure")
    void testMageSkillsStructure() {
        for (Element element : Element.values()) {
            List<Skill> skills = Skill.getMageSkills(element);

            assertFalse(skills.isEmpty());

            // Check that all skills have valid properties
            for (Skill skill : skills) {
                assertNotNull(skill.getName());
                assertTrue(skill.getMpCost() >= 0);
                assertTrue(skill.getDamageMultiplier() >= 0);
                assertNotNull(skill.getSkillType());
            }
        }
    }

    @Test
    @DisplayName("All warrior skills should have consistent structure")
    void testWarriorSkillsStructure() {
        for (Element element : Element.values()) {
            List<Skill> skills = Skill.getWarriorSkills(element);

            assertFalse(skills.isEmpty());

            for (Skill skill : skills) {
                assertNotNull(skill.getName());
                assertTrue(skill.getMpCost() >= 0);
                assertTrue(skill.getDamageMultiplier() >= 0);
                assertNotNull(skill.getSkillType());
            }
        }
    }

    @Test
    @DisplayName("All ranger skills should have consistent structure")
    void testRangerSkillsStructure() {
        for (Element element : Element.values()) {
            List<Skill> skills = Skill.getRangerSkills(element);

            assertFalse(skills.isEmpty());

            for (Skill skill : skills) {
                assertNotNull(skill.getName());
                assertTrue(skill.getMpCost() >= 0);
                assertTrue(skill.getDamageMultiplier() >= 0);
                assertNotNull(skill.getSkillType());
            }
        }
    }

    @Test
    @DisplayName("Damage skills should have damage multiplier > 1")
    void testDamageSkillMultiplier() {
        List<Skill> skills = Skill.getMageSkills(Element.FIRE);

        for (Skill skill : skills) {
            if (skill.getSkillType() == SkillType.DAMAGE) {
                assertTrue(skill.getDamageMultiplier() >= 1.0,
                    "Damage skill should have multiplier >= 1.0");
            }
        }
    }
}

