package com.elemental.ui.fx;

import java.util.Map;

import com.elemental.MainFX;
import com.elemental.factory.ItemFactory;
import com.elemental.model.ActionType;
import com.elemental.model.Battle;
import com.elemental.model.BattleAction;
import com.elemental.model.BattleStatus;
import com.elemental.model.Character;
import com.elemental.model.GameSettings;
import com.elemental.model.Inventory;
import com.elemental.model.Item;
import com.elemental.model.Skill;

import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class BattleScene {
    // Layout Components
    private StackPane rootStack;
    private BorderPane layout;
    private Pane overlayPane;
    private TextArea battleLog;

    // Sprite Containers
    private HBox arenaContainer;
    private VBox enemySpriteBox;
    private VBox playerSpriteBox;

    // HUD Components
    private BorderPane topBar;
    private VBox topHudCenter;
    private Label enemyNameLbl;
    private ProgressBar enemyHP;

    private VBox bottomHud;
    private Label playerNameLbl;
    private ProgressBar playerHP, playerMP;
    private HBox actionMenu;

    // State
    private boolean isPaused = false;

    public BattleScene() {
        // 1. Root Stack (Untuk menumpuk Game + Overlay Menu + Popup)
        rootStack = new StackPane();
        rootStack.getStyleClass().add("root");

        layout = new BorderPane();

        // Layer untuk efek floating text (damage numbers)
        overlayPane = new Pane();
        overlayPane.setPickOnBounds(false);

        // =========================================
        // 2. TOP SECTION (ENEMY HUD + PAUSE BTN)
        // =========================================
        topBar = new BorderPane();
        topBar.setPadding(new Insets(15));
        BorderPane.setMargin(topBar, new Insets(10));

        // -- Enemy HUD (Tengah) --
        topHudCenter = new VBox(5);
        topHudCenter.setAlignment(Pos.CENTER);
        topHudCenter.getStyleClass().add("panel-background");
        topHudCenter.setPadding(new Insets(10, 40, 10, 40));
        topHudCenter.setMaxWidth(500);
        topHudCenter.setEffect(new DropShadow(10, Color.BLACK)); // Efek bayangan

        enemyNameLbl = new Label("Enemy Name");
        enemyNameLbl.setStyle("-fx-font-family: 'Times New Roman'; -fx-font-size: 20px; -fx-font-weight: bold;");

        enemyHP = new ProgressBar(1.0);
        enemyHP.getStyleClass().add("hp-bar");
        enemyHP.setPrefWidth(400);
        enemyHP.setStyle("-fx-accent: #ff3333;");

        topHudCenter.getChildren().addAll(enemyNameLbl, enemyHP);

        // -- Tombol Pause (Kanan) --
        Button btnPause = new Button("||");
        btnPause.getStyleClass().add("button-medieval");
        btnPause.setPrefSize(45, 45);
        btnPause.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-background-radius: 10;");
        btnPause.setOnAction(e -> showPauseMenu());

        topBar.setCenter(topHudCenter);
        topBar.setRight(btnPause);

        layout.setTop(topBar);

        // =========================================
        // 3. CENTER SECTION (ARENA PERTARUNGAN)
        // =========================================
        arenaContainer = new HBox();
        arenaContainer.setAlignment(Pos.CENTER);
        arenaContainer.setSpacing(150); // Jarak antar karakter

        // Styling Arena (Background + Border Kayu)
        arenaContainer.setStyle(
                "-fx-background-image: url('/assets/battle_bg.jpg');" +
                        "-fx-background-size: cover;" +
                        "-fx-background-position: center center;" +
                        "-fx-border-color: #3e2723;" +
                        "-fx-border-width: 8px;" +
                        "-fx-border-radius: 0;" +
                        "-fx-effect: innerShadow(gaussian, rgba(0,0,0,0.9), 40, 0.4, 0, 0);"
        );

        enemySpriteBox = new VBox();
        enemySpriteBox.setAlignment(Pos.CENTER);
        enemySpriteBox.setId("enemyBox");

        playerSpriteBox = new VBox();
        playerSpriteBox.setAlignment(Pos.CENTER);
        playerSpriteBox.setId("playerBox");

        arenaContainer.getChildren().addAll(enemySpriteBox, playerSpriteBox);
        layout.setCenter(arenaContainer);

        // =========================================
        // 4. RIGHT SECTION (LOG)
        // =========================================
        VBox logContainer = new VBox();
        logContainer.setPadding(new Insets(10));
        logContainer.getStyleClass().add("panel-background");
        BorderPane.setMargin(logContainer, new Insets(10, 10, 10, 0));

        Label logTitle = new Label("Battle Log");
        logTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        battleLog = new TextArea();
        battleLog.setEditable(false);
        battleLog.setPrefWidth(220);
        battleLog.setPrefHeight(400);
        battleLog.setWrapText(true);
        battleLog.setStyle("-fx-control-inner-background: #f4e4bc; -fx-font-family: 'Courier New'; -fx-font-size: 12px;");

        logContainer.getChildren().addAll(logTitle, battleLog);
        layout.setRight(logContainer);

        // =========================================
        // 5. BOTTOM SECTION (PLAYER HUD)
        // =========================================
        bottomHud = new VBox(10);
        bottomHud.setPadding(new Insets(20));
        bottomHud.getStyleClass().add("panel-background");
        bottomHud.setEffect(new DropShadow(10, Color.BLACK));
        BorderPane.setMargin(bottomHud, new Insets(0, 10, 10, 10));

        playerNameLbl = new Label("Player Name");
        playerNameLbl.setStyle("-fx-font-weight: bold; -fx-font-size: 18px;");

        VBox barsBox = new VBox(8);
        playerHP = new ProgressBar(1.0);
        playerHP.getStyleClass().add("hp-bar");
        playerHP.setPrefWidth(350);

        playerMP = new ProgressBar(1.0);
        playerMP.getStyleClass().add("mp-bar");
        playerMP.setPrefWidth(350);

        barsBox.getChildren().addAll(new Label("HP"), playerHP, new Label("MP"), playerMP);

        // Action Buttons
        actionMenu = new HBox(20);
        actionMenu.setAlignment(Pos.CENTER_RIGHT);

        double btnW = 110;
        double btnH = 45;

        Button btnAttack = createActionButton("ATTACK", btnW, btnH);
        btnAttack.setStyle("-fx-base: #8b0000;"); // Merah tua
        btnAttack.setOnAction(e -> executePlayerAction(ActionType.ATTACK, null, null));

        Button btnSkill = createActionButton("SKILL", btnW, btnH);
        btnSkill.setOnAction(e -> showSkillSelection());

        Button btnItem = createActionButton("ITEM", btnW, btnH);
        btnItem.setOnAction(e -> showItemSelection());

        Button btnDefend = createActionButton("DEFEND", btnW, btnH);
        btnDefend.setStyle("-fx-base: #2f4f4f;"); // Abu-abu biru
        btnDefend.setOnAction(e -> executePlayerAction(ActionType.DEFEND, null, null));

        actionMenu.getChildren().addAll(btnAttack, btnSkill, btnItem, btnDefend);

        HBox bottomLayout = new HBox(30);
        bottomLayout.setAlignment(Pos.CENTER_LEFT);

        VBox infoContainer = new VBox(5);
        infoContainer.getChildren().addAll(playerNameLbl, barsBox);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        bottomLayout.getChildren().addAll(infoContainer, spacer, actionMenu);
        bottomHud.getChildren().add(bottomLayout);

        layout.setBottom(bottomHud);

        // Gabungkan Layer: Layout Utama -> Overlay Efek (Damage)
        rootStack.getChildren().addAll(layout, overlayPane);

        initialUpdate();
    }

    // Helper untuk membuat tombol seragam
    private Button createActionButton(String text, double w, double h) {
        Button btn = new Button(text);
        btn.getStyleClass().add("button-medieval");
        btn.setPrefSize(w, h);
        return btn;
    }

    // Penting: Mengembalikan rootStack (agar overlay bisa menumpuk di atasnya)
    public StackPane getLayout() { return rootStack; }

    // =========================================
    // UI LOGIC: MENU & POPUPS
    // =========================================

    // Menu Pause Utama
    private void showPauseMenu() {
        isPaused = true;

        VBox pauseContent = new VBox(15);
        pauseContent.setAlignment(Pos.CENTER);

        Button btnResume = createActionButton("RESUME", 220, 50);
        btnResume.setOnAction(e -> closeOverlay());

        Button btnSettings = createActionButton("SETTINGS", 220, 50);
        btnSettings.setOnAction(e -> showInGameSettingsMenu());

        Button btnExit = createActionButton("EXIT TO MENU", 220, 50);
        btnExit.setStyle("-fx-background-color: #8b0000; -fx-text-fill: white; -fx-border-color: #ffd700; -fx-border-width: 2;");
        btnExit.setOnAction(e -> {
            // MENGGUNAKAN MEDIEVAL POPUP UNTUK KONFIRMASI
            MedievalPopup.showConfirm(rootStack, "LEAVE BATTLE?",
                    "Are you sure you want to surrender?\nAll unsaved progress will be lost.",
                    () -> MainFX.primaryStage.setScene(new MainMenuScene().getScene())
            );
        });

        pauseContent.getChildren().addAll(btnResume, btnSettings, btnExit);

        showFancyOverlay("GAME PAUSED", pauseContent);
    }

    // Menu Settings dalam Game
    private void showInGameSettingsMenu() {
        VBox settingsBox = new VBox(20);
        settingsBox.setAlignment(Pos.CENTER);

        CheckBox cbLog = new CheckBox("Detailed Battle Log");
        cbLog.setStyle("-fx-font-size: 16px; -fx-text-fill: #3e1903; -fx-font-weight: bold;");
        cbLog.setSelected(GameSettings.getInstance().isShowDetailedLog());
        cbLog.setOnAction(e -> GameSettings.getInstance().setShowDetailedLog(cbLog.isSelected()));

        CheckBox cbAuto = new CheckBox("Auto Progress");
        cbAuto.setStyle("-fx-font-size: 16px; -fx-text-fill: #3e1903; -fx-font-weight: bold;");
        cbAuto.setSelected(GameSettings.getInstance().isAutoProgress());
        cbAuto.setOnAction(e -> GameSettings.getInstance().setAutoProgress(cbAuto.isSelected()));

        settingsBox.getChildren().addAll(cbLog, cbAuto);

        showFancyOverlay("SETTINGS", settingsBox);
    }

    // Overlay Premium (Background Gelap + Panel Elegan)
    private void showFancyOverlay(String title, Node content) {
        StackPane overlayBg = new StackPane();
        overlayBg.setStyle("-fx-background-color: rgba(0, 0, 0, 0.85);");

        VBox menuBox = new VBox(20);
        menuBox.setAlignment(Pos.CENTER);
        menuBox.setMaxSize(400, 400);
        menuBox.setPadding(new Insets(30));

        // Styling Inline
        menuBox.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #2b1b17, #1a1a1a);" +
                        "-fx-border-color: #ffd700;" +
                        "-fx-border-width: 4;" +
                        "-fx-border-radius: 15;" +
                        "-fx-background-radius: 15;" +
                        "-fx-effect: dropshadow(gaussian, black, 20, 0.5, 0, 0);"
        );

        Label lblTitle = new Label(title);
        lblTitle.setStyle(
                "-fx-font-family: 'Times New Roman';" +
                        "-fx-font-size: 32px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #ffd700;" +
                        "-fx-effect: dropshadow(one-pass-box, black, 2, 2, 0, 0);"
        );

        Button btnBack = new Button("Back");
        btnBack.getStyleClass().add("button-medieval");
        btnBack.setOnAction(e -> closeOverlay());

        if (!title.equals("GAME PAUSED")) {
            menuBox.getChildren().addAll(lblTitle, new Separator(), content, new Separator(), btnBack);
        } else {
            menuBox.getChildren().addAll(lblTitle, new Separator(), content);
        }

        overlayBg.getChildren().add(menuBox);
        rootStack.getChildren().add(overlayBg);

        // Animasi Pop-up
        ScaleTransition st = new ScaleTransition(Duration.millis(200), menuBox);
        st.setFromX(0.5); st.setFromY(0.5);
        st.setToX(1.0); st.setToY(1.0);
        st.play();
    }

    private void closeOverlay() {
        // Menutup layer paling atas (overlay terakhir)
        if (rootStack.getChildren().size() > 2) {
            rootStack.getChildren().remove(rootStack.getChildren().size() - 1);
        }
        // Jika tidak ada overlay lagi, unpause game
        if (rootStack.getChildren().size() <= 2) {
            isPaused = false;
        }
    }

    // --- Overlay Sederhana untuk Skill/Item ---
    private void showOverlayMenu(String title, Node content) {
        StackPane overlayBg = new StackPane();
        overlayBg.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");

        VBox menuBox = new VBox(15);
        menuBox.getStyleClass().add("panel-background");
        menuBox.setPadding(new Insets(20));
        menuBox.setAlignment(Pos.CENTER);
        menuBox.setMaxSize(400, 350);

        Label lblTitle = new Label(title);
        lblTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-underline: true;");

        Button btnBack = new Button("Back");
        btnBack.getStyleClass().add("button-medieval");
        btnBack.setOnAction(e -> closeOverlay());

        menuBox.getChildren().addAll(lblTitle, content, new Separator(), btnBack);
        overlayBg.getChildren().add(menuBox);
        rootStack.getChildren().add(overlayBg);
    }

    private void showSkillSelection() {
        if (isPaused) return;

        Character player = MainFX.battleService.getCurrentBattle().getPlayerTeam().get(0);
        VBox skillList = new VBox(10);
        skillList.setAlignment(Pos.CENTER);

        for (Skill skill : player.getSkills()) {
            Button btn = new Button(String.format("%s (%d MP)", skill.getName(), skill.getMpCost()));
            btn.getStyleClass().add("button-medieval");
            btn.setPrefWidth(250);

            if (!player.canUseSkill(skill)) {
                btn.setDisable(true);
                btn.setStyle("-fx-opacity: 0.6; -fx-text-fill: #555;");
            } else {
                btn.setOnAction(e -> {
                    closeOverlay();
                    executePlayerAction(ActionType.SKILL, skill, null);
                });
            }
            skillList.getChildren().add(btn);
        }
        showOverlayMenu("SELECT SKILL", skillList);
    }

    private void showItemSelection() {
        if (isPaused) return;

        Character player = MainFX.battleService.getCurrentBattle().getPlayerTeam().get(0);
        Inventory inv = player.getInventory();
        VBox itemList = new VBox(10);
        itemList.setAlignment(Pos.CENTER);

        Map<String, Integer> items = inv.getAllItems();

        if (items.isEmpty()) {
            itemList.getChildren().add(new Label("Inventory Empty!"));
        } else {
            for (Map.Entry<String, Integer> entry : items.entrySet()) {
                String itemName = entry.getKey();
                int qty = entry.getValue();
                Item itemTemplate = ItemFactory.getItem(itemName);

                Button btn = new Button(String.format("%s (x%d)", itemName, qty));
                btn.getStyleClass().add("button-medieval");
                btn.setPrefWidth(250);

                Tooltip tooltip = new Tooltip(itemTemplate.getDescription());
                btn.setTooltip(tooltip);

                btn.setOnAction(e -> {
                    closeOverlay();
                    executePlayerAction(ActionType.ITEM, null, itemTemplate);
                });
                itemList.getChildren().add(btn);
            }
        }
        showOverlayMenu("SELECT ITEM", itemList);
    }

    // =========================================
    // GAME LOGIC & ANIMATION
    // =========================================

    private void initialUpdate() {
        Battle battle = MainFX.battleService.getCurrentBattle();
        Character player = battle.getPlayerTeam().get(0);
        Character enemy = battle.getEnemyTeam().get(0);

        enemySpriteBox.getChildren().clear();
        playerSpriteBox.getChildren().clear();

        ImageView enemyImg = SpriteFactory.getEnemySprite(enemy.getName());
        ImageView playerImg = SpriteFactory.getPlayerSprite(player.getCharacterClass());

        addBreathingEffect(enemyImg);
        addBreathingEffect(playerImg);

        enemySpriteBox.getChildren().add(enemyImg);
        playerSpriteBox.getChildren().add(playerImg);

        updateBars(player, enemy);
        battleLog.setText("Battle Start!\n" + player.getName() + " vs " + enemy.getName() + "\n");
    }

    private void addBreathingEffect(Node node) {
        ScaleTransition st = new ScaleTransition(Duration.seconds(1.5), node);
        st.setFromX(1.0); st.setFromY(1.0);
        st.setToX(1.02); st.setToY(1.02);
        st.setCycleCount(ScaleTransition.INDEFINITE);
        st.setAutoReverse(true);
        st.play();
    }

    private void updateUI(Character attacker, Character target, int damageDealt, boolean isCritical) {
        Battle battle = MainFX.battleService.getCurrentBattle();
        Character player = battle.getPlayerTeam().get(0);
        Character enemy = battle.getEnemyTeam().get(0);

        if (!battle.getBattleLog().getRecentEntries().isEmpty()) {
            String lastEntry = battle.getBattleLog().getRecentEntries().get(battle.getBattleLog().getRecentEntries().size()-1);
            battleLog.appendText(lastEntry + "\n");
        }

        UIEffects.animateBarChange(playerHP, (double) player.getCurrentHP() / player.getMaxHP());
        UIEffects.animateBarChange(playerMP, (double) player.getCurrentMP() / player.getMaxMP());
        UIEffects.animateBarChange(enemyHP, (double) enemy.getCurrentHP() / enemy.getMaxHP());

        updateBars(player, enemy);

        if (damageDealt > 0) {
            boolean targetIsEnemy = (target == enemy);
            Node targetNode = targetIsEnemy ? enemySpriteBox : playerSpriteBox;
            UIEffects.shakeNode(targetNode);

            double xPos = targetIsEnemy ?
                    enemySpriteBox.localToScene(enemySpriteBox.getBoundsInLocal()).getMinX() + 50 :
                    playerSpriteBox.localToScene(playerSpriteBox.getBoundsInLocal()).getMinX() + 50;

            double yPos = layout.getCenter().getBoundsInParent().getMinY() + 100;

            String text = (isCritical ? "CRIT " : "") + damageDealt;
            String style = isCritical ? "crit-text" : "damage-text";
            UIEffects.showFloatingText(overlayPane, text, style, xPos, yPos);
        }

        if (battle.getBattleStatus() != BattleStatus.ONGOING) {
            // Use Platform.runLater to avoid showAndWait during animation
            javafx.application.Platform.runLater(() -> handleBattleEnd(battle.getBattleStatus()));
        }
    }

    private void updateBars(Character p, Character e) {
        // Show boss indicator
        String bossIndicator = e.isBoss() ? " 👑 BOSS" : "";
        enemyNameLbl.setText(String.format("%s (Lv.%d)%s", e.getName(), e.getLevel(), bossIndicator));
        playerNameLbl.setText(String.format("%s (Lv.%d) - %s", p.getName(), p.getLevel(), p.getStatus()));

        // Update progress bar values to reflect current state
        playerHP.setProgress((double) p.getCurrentHP() / p.getMaxHP());
        playerMP.setProgress((double) p.getCurrentMP() / p.getMaxMP());
        enemyHP.setProgress((double) e.getCurrentHP() / e.getMaxHP());
    }

    private void executePlayerAction(ActionType type, Skill skill, Item item) {
        if (isPaused) return;

        Battle battle = MainFX.battleService.getCurrentBattle();
        Character player = battle.getPlayerTeam().get(0);
        Character enemy = battle.getEnemyTeam().get(0);

        int hpBefore = enemy.getCurrentHP();

        BattleAction action = new BattleAction(player, type);

        if (type == ActionType.ITEM && item != null) {
            action.setItem(item);
            action.setTarget(player);
        } else {
            action.setTarget(enemy);
        }

        if (skill != null) action.setSkill(skill);

        battle.executeAction(action);

        int damageDealt = Math.max(0, hpBefore - enemy.getCurrentHP());
        boolean isCrit = false;
        if(!battle.getBattleLog().getRecentEntries().isEmpty()){
            isCrit = battle.getBattleLog().getRecentEntries().get(battle.getBattleLog().getRecentEntries().size()-1).contains("CRITICAL");
        }

        updateUI(player, enemy, damageDealt, isCrit);

        if (battle.getBattleStatus() != BattleStatus.ONGOING) return;

        actionMenu.setDisable(true);
        PauseTransition pause = new PauseTransition(Duration.seconds(1.2));
        pause.setOnFinished(e -> {
            if (battle.getBattleStatus() != BattleStatus.ONGOING) return;
            int pHpBefore = player.getCurrentHP();

            com.elemental.strategy.AIStrategy ai = com.elemental.strategy.AIStrategyFactory.create(GameSettings.getInstance().getAIDifficulty());
            BattleAction enemyAction = ai.decideAction(enemy, battle.getEnemyTeam(), battle.getPlayerTeam());
            battle.executeAction(enemyAction);

            int pDamage = Math.max(0, pHpBefore - player.getCurrentHP());
            boolean pCrit = false;

            updateUI(enemy, player, pDamage, pCrit);
            actionMenu.setDisable(false);
        });
        pause.play();
    }

    // MENGGUNAKAN MEDIEVAL POPUP UNTUK HASIL BATTLE
    private void handleBattleEnd(BattleStatus status) {
        actionMenu.setDisable(true);

        Battle battle = MainFX.battleService.getCurrentBattle();
        if (battle == null) {
            // Safety check
            MainFX.primaryStage.setScene(new MainMenuScene().getScene());
            return;
        }

        // Auto-save if enabled
        if (com.elemental.model.GameSettings.getInstance().isAutoSave()) {
            MainFX.saveLoadService.autoSave();
            battleLog.appendText("\n💾 Game auto-saved!\n");
        }

        if (status == BattleStatus.VICTORY) {
            // Victory: Show continue or exit dialog
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("⚔️ VICTORY!");
            alert.setHeaderText("🎉 You won the battle!");

            // Get current player safely
            if (battle.getPlayerTeam().isEmpty()) {
                MainFX.primaryStage.setScene(new MainMenuScene().getScene());
                return;
            }
            Character player = battle.getPlayerTeam().get(0);

            // Create buttons
            ButtonType btnContinue = new ButtonType("⚔️ Lanjut Battle");
            ButtonType btnExit = new ButtonType("🏠 Keluar ke Menu");

            alert.getButtonTypes().setAll(btnContinue, btnExit);

            alert.showAndWait().ifPresent(response -> {
                if (response == btnContinue) {
                    try {
                        // Continue battle - start new battle with same character
                        battleLog.appendText("\n⚔️ Preparing next battle...\n");

                        // Generate new enemies
                        List<com.elemental.model.Character> enemies = generateEnemyTeam(player.getLevel());
                        MainFX.battleService.startBattle(Collections.singletonList(player), enemies);

                        // Reload battle scene
                        MainFX.primaryStage.getScene().setRoot(new BattleScene().getLayout());
                    } catch (Exception e) {
                        // If error, go back to menu
                        System.err.println("Error starting next battle: " + e.getMessage());
                        MainFX.primaryStage.setScene(new MainMenuScene().getScene());
                    }
                } else {
                    // Exit to menu
                    MainFX.primaryStage.setScene(new MainMenuScene().getScene());
                }
            });
        } else {
            // Defeat: Direct exit to menu
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("💀 DEFEAT");
            alert.setHeaderText("You were defeated...");
            alert.setContentText("Returning to main menu.");
            alert.showAndWait();
            MainFX.primaryStage.setScene(new MainMenuScene().getScene());
        }
    }

    /**
     * Generate enemy team with boss system and smart difficulty
     */
    private List<com.elemental.model.Character> generateEnemyTeam(int playerLevel) {
        // Get player element from current battle or character service
        com.elemental.model.Element playerElement = null;
        Battle currentBattle = MainFX.battleService.getCurrentBattle();
        if (currentBattle != null && !currentBattle.getPlayerTeam().isEmpty()) {
            playerElement = currentBattle.getPlayerTeam().get(0).getElement();
        }

        if (playerLevel >= 5 && playerLevel % 5 == 0) {
            // Boss battle
            String[] bossNames = {"Dragon", "Phoenix", "Golem", "Wraith", "Demon Lord"};
            int bossIndex = ((playerLevel / 5) - 1) % bossNames.length;
            String bossName = bossNames[bossIndex];

            com.elemental.model.Character boss = com.elemental.factory.EnemyFactory.createBoss(bossName, playerLevel);
            return List.of(boss);
        } else {
            // Normal enemy - smart generation based on AI difficulty for level 1-10
            if (playerLevel <= 10) {
                com.elemental.model.AIDifficulty difficulty = com.elemental.model.GameSettings.getInstance().getAIDifficulty();

                switch (difficulty) {
                    case EASY:
                        // Easy: Weak enemy (disadvantage element) - Learn element system
                        return List.of(createWeakEnemy(playerLevel, playerElement));

                    case MEDIUM:
                        // Medium: Balanced enemy (neutral or same element) - Fair fight
                        return List.of(createBalancedEnemy(playerLevel, playerElement));

                    case HARD:
                        // Hard: Strong enemy (advantage element) - Challenge
                        return List.of(createStrongEnemy(playerLevel, playerElement));

                    default:
                        return List.of(com.elemental.factory.EnemyFactory.createEnemy(playerLevel));
                }
            } else {
                // Level 11+: Always random enemy (normal difficulty)
                return List.of(com.elemental.factory.EnemyFactory.createEnemy(playerLevel));
            }
        }
    }

    /**
     * Create weak enemy (element disadvantage) for beginner friendly gameplay
     * Player has advantage: 1.5x damage to enemy, enemy deals 0.7x damage
     */
    private com.elemental.model.Character createWeakEnemy(int level, com.elemental.model.Element playerElement) {
        if (playerElement == null) {
            return com.elemental.factory.EnemyFactory.createEnemy(level);
        }

        // Get weak element (player has advantage)
        com.elemental.model.Element weakElement;
        switch (playerElement) {
            case FIRE:
                weakElement = com.elemental.model.Element.EARTH; // Fire beats Earth
                break;
            case WATER:
                weakElement = com.elemental.model.Element.FIRE; // Water beats Fire
                break;
            case EARTH:
                weakElement = com.elemental.model.Element.WATER; // Earth beats Water
                break;
            default:
                weakElement = com.elemental.model.Element.FIRE;
        }

        // Create enemy with weak element
        String[] weakEnemyNames = {"Goblin", "Skeleton", "Bandit"};
        String name = weakEnemyNames[(int)(Math.random() * weakEnemyNames.length)];
        com.elemental.model.CharacterClass randomClass = com.elemental.model.CharacterClass.values()[(int)(Math.random() * 3)];

        return com.elemental.factory.EnemyFactory.createEnemy(name, randomClass, weakElement, level);
    }

    /**
     * Create balanced enemy (neutral element) for fair gameplay
     * Both deal normal damage: 1.0x (no advantage/disadvantage)
     */
    private com.elemental.model.Character createBalancedEnemy(int level, com.elemental.model.Element playerElement) {
        if (playerElement == null) {
            return com.elemental.factory.EnemyFactory.createEnemy(level);
        }

        // Use same element as player (neutral matchup: 1.0x damage both ways)
        String[] balancedEnemyNames = {"Orc", "Troll", "Warrior"};
        String name = balancedEnemyNames[(int)(Math.random() * balancedEnemyNames.length)];
        com.elemental.model.CharacterClass randomClass = com.elemental.model.CharacterClass.values()[(int)(Math.random() * 3)];

        return com.elemental.factory.EnemyFactory.createEnemy(name, randomClass, playerElement, level);
    }

    /**
     * Create strong enemy (element advantage) for challenging gameplay
     * Enemy has advantage: 1.5x damage to player, player deals 0.7x damage
     */
    private com.elemental.model.Character createStrongEnemy(int level, com.elemental.model.Element playerElement) {
        if (playerElement == null) {
            return com.elemental.factory.EnemyFactory.createEnemy(level);
        }

        // Get strong element (enemy has advantage)
        com.elemental.model.Element strongElement;
        switch (playerElement) {
            case FIRE:
                strongElement = com.elemental.model.Element.WATER; // Water beats Fire
                break;
            case WATER:
                strongElement = com.elemental.model.Element.EARTH; // Earth beats Water
                break;
            case EARTH:
                strongElement = com.elemental.model.Element.FIRE; // Fire beats Earth
                break;
            default:
                strongElement = com.elemental.model.Element.WATER;
        }

        // Create enemy with strong element
        String[] strongEnemyNames = {"Elite Guard", "Warlock", "Champion"};
        String name = strongEnemyNames[(int)(Math.random() * strongEnemyNames.length)];
        com.elemental.model.CharacterClass randomClass = com.elemental.model.CharacterClass.values()[(int)(Math.random() * 3)];

        return com.elemental.factory.EnemyFactory.createEnemy(name, randomClass, strongElement, level);
    }
}