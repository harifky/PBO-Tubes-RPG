package com.elemental.observer;

import com.elemental.model.Character;
import com.elemental.model.BattleStatus;
import com.elemental.model.Skill;
import com.elemental.model.Item;

/**
 * Observer interface untuk mengamati event-event dalam pertempuran.
 * Implementasi Observer Pattern untuk decoupling antara Battle logic dan UI.
 *
 * Generic Programming: Interface ini bisa diimplementasikan oleh berbagai
 * class UI (Console, JavaFX, dll) tanpa mengubah Battle logic.
 */
public interface BattleObserver {

    /**
     * Dipanggil ketika pertempuran dimulai
     */
    void onBattleStart();

    /**
     * Dipanggil ketika giliran berubah ke karakter baru
     * @param character Karakter yang mendapat giliran
     * @param turnNumber Nomor giliran saat ini
     */
    void onTurnChange(Character character, int turnNumber);

    /**
     * Dipanggil ketika karakter menyerang
     * @param attacker Karakter yang menyerang
     * @param target Karakter yang diserang
     * @param damage Jumlah damage yang diberikan
     * @param isCritical Apakah serangan kritikal
     */
    void onAttack(Character attacker, Character target, int damage, boolean isCritical);

    /**
     * Dipanggil ketika karakter menggunakan skill
     * @param user Karakter yang menggunakan skill
     * @param skill Skill yang digunakan
     * @param target Karakter target
     */
    void onSkillUsed(Character user, Skill skill, Character target);

    /**
     * Dipanggil ketika item digunakan
     * @param user Karakter yang menggunakan item
     * @param item Item yang digunakan
     * @param target Karakter target
     */
    void onItemUsed(Character user, Item item, Character target);

    /**
     * Dipanggil ketika karakter bertahan (defend)
     * @param character Karakter yang bertahan
     */
    void onDefend(Character character);

    /**
     * Dipanggil ketika HP karakter berubah
     * @param character Karakter yang HP-nya berubah
     * @param oldHP HP sebelumnya
     * @param newHP HP setelahnya
     */
    void onHPChange(Character character, int oldHP, int newHP);

    /**
     * Dipanggil ketika karakter dikalahkan
     * @param character Karakter yang dikalahkan
     */
    void onCharacterDefeated(Character character);

    /**
     * Dipanggil ketika pertempuran berakhir
     * @param status Status akhir pertempuran (VICTORY/DEFEAT)
     */
    void onBattleEnd(BattleStatus status);

    /**
     * Dipanggil untuk menampilkan pesan log
     * @param message Pesan yang akan ditampilkan
     */
    void onLogMessage(String message);
}
