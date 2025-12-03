package com.elemental.ui.fx;

import com.elemental.MainFX;
import com.elemental.factory.EnemyFactory;
import com.elemental.factory.ItemFactory;
import com.elemental.model.*;
import com.elemental.model.Character;
import com.elemental.strategy.AIStrategy;
import com.elemental.strategy.AIStrategyFactory;

import javafx.animation.Interpolator;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.transform.Scale;
import javafx.util.Duration;

import java.util.Collections;
import java.util.List;
import java.util.Map;

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
        // 1. Root Stack (Background Hitam Pekat)
        rootStack = new StackPane();
        rootStack.getStyleClass().add("root");
        rootStack.setStyle("-fx-background-color: #0a0908;");

        // --- SETUP LAYOUT UTAMA (FIXED 800x600) ---
        layout = new BorderPane();
        layout.setMaxSize(800, 600);
        layout.setPrefSize(800, 600);
        layout.setStyle("-fx-effect: dropshadow(three-pass-box, black, 80, 0.5, 0, 0);");

        overlayPane = new Pane();
        overlayPane.setPickOnBounds(false);
        overlayPane.setMaxSize(800, 600);
        overlayPane.setPrefSize(800, 600);

        // --- SCALING & CENTERING LOGIC ---
        StackPane gameContent = new StackPane(layout, overlayPane);
        gameContent.setMaxSize(800, 600);
        gameContent.setPrefSize(800, 600);
        StackPane.setAlignment(gameContent, Pos.TOP_LEFT);

        Scale scale = new Scale(1, 1);
        gameContent.getTransforms().add(scale);

        rootStack.widthProperty().addListener((obs, old, val) -> updateScale(gameContent, scale));
        rootStack.heightProperty().addListener((obs, old, val) -> updateScale(gameContent, scale));

        rootStack.getChildren().add(gameContent);

        // =========================================
        //        SETUP KOMPONEN UI
        // =========================================

        // 2. TOP SECTION
        topBar = new BorderPane();
        topBar.setPadding(new Insets(15));

        topHudCenter = new VBox(5);
        topHudCenter.setAlignment(Pos.CENTER);
        topHudCenter.getStyleClass().add("panel-background");
        topHudCenter.setPadding(new Insets(15, 50, 15, 50));
        topHudCenter.setMaxWidth(550);
        topHudCenter.setEffect(new DropShadow(15, Color.BLACK));

        enemyNameLbl = new Label("Enemy Name");
        enemyNameLbl.setStyle("-fx-font-family: 'Times New Roman'; -fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #d4cdba;");

        enemyHP = new ProgressBar(1.0);
        enemyHP.getStyleClass().add("hp-bar");
        enemyHP.setPrefWidth(450);

        topHudCenter.getChildren().addAll(enemyNameLbl, enemyHP);

        Button btnPause = new Button("||");
        btnPause.getStyleClass().add("button-medieval");
        btnPause.setPrefSize(50, 50);
        btnPause.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-background-radius: 2;");
        btnPause.setOnAction(e -> showPauseMenu());

        topBar.setCenter(topHudCenter);
        topBar.setRight(btnPause);

        layout.setTop(topBar);

        // 3. CENTER SECTION (ARENA)
        arenaContainer = new HBox();
        arenaContainer.setAlignment(Pos.CENTER);
        arenaContainer.setSpacing(180);

        arenaContainer.setStyle(
                "-fx-background-image: url('/assets/battle_bg.jpg');" +
                        "-fx-background-size: 100% 100%;" +
                        "-fx-background-position: center center;" +
                        "-fx-border-color: #2e2b26;" +
                        "-fx-border-width: 4px;" +
                        "-fx-effect: innerShadow(gaussian, rgba(0,0,0,0.8), 60, 0.4, 0, 0);"
        );

        enemySpriteBox = new VBox();
        enemySpriteBox.setAlignment(Pos.CENTER);
        enemySpriteBox.setId("enemyBox");
        enemySpriteBox.setCache(true);
        enemySpriteBox.setCacheHint(CacheHint.SPEED);

        playerSpriteBox = new VBox();
        playerSpriteBox.setAlignment(Pos.CENTER);
        playerSpriteBox.setId("playerBox");
        playerSpriteBox.setCache(true);
        playerSpriteBox.setCacheHint(CacheHint.SPEED);

        arenaContainer.getChildren().addAll(enemySpriteBox, playerSpriteBox);
        layout.setCenter(arenaContainer);

        // 4. RIGHT SECTION (LOG)
        VBox logContainer = new VBox();
        logContainer.setPadding(new Insets(15));
        logContainer.getStyleClass().add("panel-background");
        BorderPane.setMargin(logContainer, new Insets(10, 10, 10, 0));

        Label logTitle = new Label("Battle Log");
        logTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #d4cdba;");

        battleLog = new TextArea();
        battleLog.setEditable(false);
        battleLog.setPrefWidth(240);
        battleLog.setPrefHeight(420);
        battleLog.setWrapText(true);

        // Style Battle Log: Background Gelap, Teks Terang
        battleLog.setStyle(
                "-fx-control-inner-background: #1a1816; " +
                        "-fx-font-family: 'Courier New'; " +
                        "-fx-font-size: 13px; " +
                        "-fx-text-fill: #e8e1d5; " +
                        "-fx-font-weight: bold;"
        );

        logContainer.getChildren().addAll(logTitle, battleLog);
        layout.setRight(logContainer);

        // 5. BOTTOM SECTION
        bottomHud = new VBox(10);
        bottomHud.setPadding(new Insets(20));
        bottomHud.getStyleClass().add("panel-background");
        bottomHud.setEffect(new DropShadow(15, Color.BLACK));
        BorderPane.setMargin(bottomHud, new Insets(0, 10, 10, 10));

        playerNameLbl = new Label("Player Name");
        playerNameLbl.setStyle("-fx-font-weight: bold; -fx-font-size: 20px; -fx-text-fill: #d4cdba;");

        VBox barsBox = new VBox(8);
        playerHP = new ProgressBar(1.0);
        playerHP.getStyleClass().add("hp-bar");
        playerHP.setPrefWidth(350);

        playerMP = new ProgressBar(1.0);
        playerMP.getStyleClass().add("mp-bar");
        playerMP.setPrefWidth(350);

        barsBox.getChildren().addAll(new Label("HP"), playerHP, new Label("MP"), playerMP);

        // --- ACTION BUTTONS ---
        actionMenu = new HBox(15);
        actionMenu.setAlignment(Pos.CENTER_RIGHT);

        Button btnAttack = createActionButton("ATTACK");
        btnAttack.setStyle("-fx-base: #5e1b1b;");
        btnAttack.setOnAction(e -> executePlayerAction(ActionType.ATTACK, null, null));

        Button btnSkill = createActionButton("SKILL");
        btnSkill.setOnAction(e -> showSkillSelection());

        Button btnItem = createActionButton("ITEM");
        btnItem.setOnAction(e -> showItemSelection());

        Button btnDefend = createActionButton("DEFEND");
        btnDefend.setStyle("-fx-base: #2e2b26;");
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

        initialUpdate();
    }

    private void updateScale(StackPane content, Scale scale) {
        double windowWidth = rootStack.getWidth();
        double windowHeight = rootStack.getHeight();
        double contentWidth = 800;
        double contentHeight = 600;

        if (windowWidth <= 0 || windowHeight <= 0) return;

        double scaleX = windowWidth / contentWidth;
        double scaleY = windowHeight / contentHeight;
        double scaleFactor = Math.min(scaleX, scaleY);

        scale.setPivotX(0);
        scale.setPivotY(0);
        scale.setX(scaleFactor);
        scale.setY(scaleFactor);

        double newX = (windowWidth - (contentWidth * scaleFactor)) / 2;
        double newY = (windowHeight - (contentHeight * scaleFactor)) / 2;

        content.setTranslateX(newX);
        content.setTranslateY(newY);
    }

    private Button createActionButton(String text) {
        Button btn = new Button(text);
        btn.getStyleClass().add("button-medieval");
        btn.setMinWidth(120);
        btn.setPrefHeight(50);
        return btn;
    }

    private Button createActionButton(String text, double w, double h) {
        Button btn = new Button(text);
        btn.getStyleClass().add("button-medieval");
        btn.setMinWidth(w);
        btn.setPrefHeight(h);
        return btn;
    }

    public StackPane getLayout() { return rootStack; }

    // --- PAUSE MENU ---
    private void showPauseMenu() {
        isPaused = true;
        VBox pauseContent = new VBox(15);
        pauseContent.setAlignment(Pos.CENTER);

        Button btnResume = createActionButton("RESUME", 220, 50);
        btnResume.setOnAction(e -> closeOverlay());

        Button btnSettings = createActionButton("SETTINGS", 220, 50);
        btnSettings.setOnAction(e -> showInGameSettingsMenu());

        Button btnExit = createActionButton("EXIT TO MENU", 220, 50);
        btnExit.getStyleClass().add("button-exit");
        btnExit.setOnAction(e -> {
            MedievalPopup.showConfirm(rootStack, "LEAVE BATTLE?",
                    "Are you sure you want to surrender?\nAll unsaved progress will be lost.",
                    () -> MainFX.showMainMenu()
            );
        });

        pauseContent.getChildren().addAll(btnResume, btnSettings, btnExit);
        showFancyOverlay("GAME PAUSED", pauseContent);
    }

    private void showInGameSettingsMenu() {
        VBox settingsBox = new VBox(20);
        settingsBox.setAlignment(Pos.CENTER);

        String cbStyle = "-fx-font-size: 16px; -fx-text-fill: #d4cdba; -fx-font-weight: bold;";

        CheckBox cbLog = new CheckBox("Detailed Battle Log");
        cbLog.setStyle(cbStyle);
        cbLog.setSelected(GameSettings.getInstance().isShowDetailedLog());
        cbLog.setOnAction(e -> GameSettings.getInstance().setShowDetailedLog(cbLog.isSelected()));

        CheckBox cbAuto = new CheckBox("Auto Progress");
        cbAuto.setStyle(cbStyle);
        cbAuto.setSelected(GameSettings.getInstance().isAutoProgress());
        cbAuto.setOnAction(e -> GameSettings.getInstance().setAutoProgress(cbAuto.isSelected()));

        settingsBox.getChildren().addAll(cbLog, cbAuto);
        showFancyOverlay("SETTINGS", settingsBox);
    }

    private void showFancyOverlay(String title, Node content) {
        StackPane overlayBg = new StackPane();
        overlayBg.setStyle("-fx-background-color: rgba(0, 0, 0, 0.85);");

        VBox menuBox = new VBox(20);
        menuBox.setAlignment(Pos.CENTER);
        menuBox.setMaxSize(400, 400);
        menuBox.setPadding(new Insets(30));

        menuBox.getStyleClass().add("panel-background");
        menuBox.setStyle("-fx-border-width: 2px; -fx-border-color: #4a443b;");

        Label lblTitle = new Label(title);
        lblTitle.setStyle(
                "-fx-font-family: 'Times New Roman';" +
                        "-fx-font-size: 32px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #d4cdba;" +
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

        ScaleTransition st = new ScaleTransition(Duration.millis(200), menuBox);
        st.setFromX(0.5); st.setFromY(0.5);
        st.setToX(1.0); st.setToY(1.0);
        st.play();
    }

    private void closeOverlay() {
        if (rootStack.getChildren().size() > 1) {
            rootStack.getChildren().remove(rootStack.getChildren().size() - 1);
        }
        if (rootStack.getChildren().size() <= 1) {
            isPaused = false;
        }
    }

    // --- Overlay Skill/Item ---
    private void showOverlayMenu(String title, Node content) {
        StackPane overlayBg = new StackPane();
        overlayBg.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");

        VBox menuBox = new VBox(15);
        menuBox.getStyleClass().add("panel-background");
        menuBox.setPadding(new Insets(20));
        menuBox.setAlignment(Pos.CENTER);
        menuBox.setMaxSize(400, 350);

        Label lblTitle = new Label(title);
        lblTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-underline: true; -fx-text-fill: #d4cdba;");

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
            btn.setPrefWidth(300);

            if (!player.canUseSkill(skill)) {
                btn.setDisable(true);
                btn.setStyle("-fx-opacity: 0.5; -fx-text-fill: #8f8576;");
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

        Inventory inv = Inventory.getInstance();
        VBox itemList = new VBox(10);
        itemList.setAlignment(Pos.CENTER);

        Map<String, Integer> items = inv.getAllItems();

        if (items.isEmpty()) {
            itemList.getChildren().add(new Label("Global Inventory is Empty!"));
        } else {
            for (Map.Entry<String, Integer> entry : items.entrySet()) {
                String itemName = entry.getKey();
                int qty = entry.getValue();

                if (qty <= 0) continue;

                Item itemTemplate = ItemFactory.getItem(itemName);

                Button btn = new Button(String.format("%s (x%d)", itemName, qty));
                btn.getStyleClass().add("button-medieval");
                btn.setPrefWidth(300);

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

    // --- LOGIC ---

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
        st.setToX(1.03); st.setToY(1.03);
        st.setCycleCount(ScaleTransition.INDEFINITE);
        st.setAutoReverse(true);
        st.setInterpolator(Interpolator.EASE_BOTH);
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
                    enemySpriteBox.getBoundsInParent().getMinX() + 50 :
                    playerSpriteBox.getBoundsInParent().getMinX() + 50;

            double yPos = 300;

            String text = (isCritical ? "CRIT " : "") + damageDealt;
            String style = isCritical ? "crit-text" : "damage-text";
            UIEffects.showFloatingText(overlayPane, text, style, xPos, yPos);
        }

        if (battle.getBattleStatus() != BattleStatus.ONGOING) {
            javafx.application.Platform.runLater(() -> handleBattleEnd(battle.getBattleStatus()));
        }
    }

    private void updateBars(Character p, Character e) {
        boolean isBoss = e.getLevel() >= 5 && e.getLevel() % 5 == 0;
        String bossIndicator = isBoss ? " 👑 BOSS" : "";

        enemyNameLbl.setText(String.format("%s (Lv.%d)%s", e.getName(), e.getLevel(), bossIndicator));
        playerNameLbl.setText(String.format("%s (Lv.%d) - %s", p.getName(), p.getLevel(), p.getStatus()));
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

            AIStrategy ai = AIStrategyFactory.create(GameSettings.getInstance().getAIDifficulty());
            BattleAction enemyAction = ai.decideAction(enemy, battle.getEnemyTeam(), battle.getPlayerTeam());
            battle.executeAction(enemyAction);

            int pDamage = Math.max(0, pHpBefore - player.getCurrentHP());
            boolean pCrit = false;

            updateUI(enemy, player, pDamage, pCrit);
            actionMenu.setDisable(false);
        });
        pause.play();
    }

    // --- PERBAIKAN LOGIC: Handle Victory vs Defeat ---
    private void handleBattleEnd(BattleStatus status) {
        actionMenu.setDisable(true);

        if (MainFX.saveLoadService != null) {
            MainFX.saveLoadService.autoSave();
        }

        if (status == BattleStatus.VICTORY) {
            // MENANG: Tampilkan konfirmasi Lanjut
            MedievalPopup.showConfirm(rootStack, "VICTORY!",
                    "Enemy defeated!\nYou gained experience and loot.\nGame Auto-saved.\n\nContinue to next battle?",
                    () -> { // YES: Continue Battle
                        try {
                            Character player = MainFX.battleService.getCurrentBattle().getPlayerTeam().get(0);
                            List<Character> enemies = generateEnemyTeam(player.getLevel());
                            MainFX.battleService.startBattle(Collections.singletonList(player), enemies);
                            MainFX.primaryStage.getScene().setRoot(new BattleScene().getLayout());
                        } catch (Exception e) {
                            MainFX.showMainMenu();
                        }
                    },
                    () -> { // NO: Main Menu
                        MainFX.showMainMenu();
                    }
            );
        } else {
            // KALAH: Hanya tampilkan satu tombol "OK" ke Menu
            MedievalPopup.show(rootStack, "DEFEAT...",
                    "You have fallen in battle...\nBetter luck next time.",
                    MedievalPopup.Type.DEFEAT,
                    () -> MainFX.showMainMenu()
            );
        }
    }

    private List<Character> generateEnemyTeam(int playerLevel) {
        if (playerLevel >= 5 && playerLevel % 5 == 0) {
            String[] bossNames = {"Dragon", "Phoenix", "Golem", "Wraith", "Demon Lord"};
            String bossName = bossNames[(playerLevel / 5 - 1) % bossNames.length];
            return List.of(EnemyFactory.createBoss(bossName, playerLevel));
        } else {
            return List.of(EnemyFactory.createEnemy(playerLevel));
        }
    }
}