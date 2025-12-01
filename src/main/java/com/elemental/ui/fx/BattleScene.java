package com.elemental.ui.fx;

import com.elemental.MainFX;
import com.elemental.factory.ItemFactory;
import com.elemental.model.*;
import com.elemental.model.Character;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.Map;

public class BattleScene {
    // Layout Utama
    private StackPane rootStack; // Root untuk menumpuk overlay menu di atas game
    private BorderPane layout;
    private Pane overlayPane; // Untuk efek floating text
    private TextArea battleLog;

    // Area Sprite & Arena
    private HBox arenaContainer;
    private VBox enemySpriteBox;
    private VBox playerSpriteBox;

    // HUD (Heads-Up Display)
    private VBox topHud;
    private Label enemyNameLbl;
    private ProgressBar enemyHP;

    private VBox bottomHud;
    private Label playerNameLbl;
    private ProgressBar playerHP, playerMP;
    private HBox actionMenu;

    public BattleScene() {
        // 1. Setup Root & Layer
        rootStack = new StackPane();
        rootStack.getStyleClass().add("root");

        layout = new BorderPane();

        overlayPane = new Pane();
        overlayPane.setPickOnBounds(false); // Agar klik tembus ke layer bawah

        // =========================================
        // 2. TOP SECTION (ENEMY HUD)
        // =========================================
        topHud = new VBox(5);
        topHud.setPadding(new Insets(10, 20, 10, 20));
        topHud.setAlignment(Pos.CENTER);
        topHud.getStyleClass().add("panel-background");
        BorderPane.setMargin(topHud, new Insets(10, 10, 0, 10));

        enemyNameLbl = new Label("Enemy Name");
        enemyNameLbl.setStyle("-fx-font-family: 'Times New Roman'; -fx-font-size: 18px; -fx-font-weight: bold;");

        enemyHP = new ProgressBar(1.0);
        enemyHP.getStyleClass().add("hp-bar");
        enemyHP.setPrefWidth(400);
        enemyHP.setStyle("-fx-accent: #ff3333;");

        topHud.getChildren().addAll(enemyNameLbl, enemyHP);
        layout.setTop(topHud);

        // =========================================
        // 3. CENTER SECTION (BATTLE ARENA) - UPDATE BACKGROUND
        // =========================================
        arenaContainer = new HBox();
        arenaContainer.setAlignment(Pos.CENTER);
        arenaContainer.setSpacing(100);

        // CSS Baru: Background Image + Border Kayu
        arenaContainer.setStyle(
                "-fx-background-image: url('/assets/battle_bg.jpg');" +
                        "-fx-background-size: cover;" +
                        "-fx-background-position: center center;" +
                        "-fx-border-color: #5c4033;" +
                        "-fx-border-width: 6px;" +
                        "-fx-border-radius: 5px;" +
                        "-fx-effect: innerShadow(gaussian, rgba(0,0,0,0.8), 15, 0.2, 0, 0);"
        );

        // Container Sprite
        enemySpriteBox = new VBox();
        enemySpriteBox.setAlignment(Pos.CENTER);
        enemySpriteBox.setId("enemyBox");

        playerSpriteBox = new VBox();
        playerSpriteBox.setAlignment(Pos.CENTER);
        playerSpriteBox.setId("playerBox");

        // Label VS (Transparan)
        Label vsLabel = new Label("VS");
        vsLabel.setStyle("-fx-font-size: 60px; -fx-text-fill: rgba(255,255,255,0.1); -fx-font-weight: bold;");

        arenaContainer.getChildren().addAll(enemySpriteBox, vsLabel, playerSpriteBox);
        layout.setCenter(arenaContainer);

        // =========================================
        // 4. RIGHT SECTION (LOG)
        // =========================================
        VBox logContainer = new VBox();
        logContainer.setPadding(new Insets(10));
        logContainer.getStyleClass().add("panel-background");
        BorderPane.setMargin(logContainer, new Insets(10, 10, 10, 0));

        Label logTitle = new Label("Battle Log");
        logTitle.setStyle("-fx-font-weight: bold;");

        battleLog = new TextArea();
        battleLog.setEditable(false);
        battleLog.setPrefWidth(200);
        battleLog.setPrefHeight(400);
        battleLog.setWrapText(true);
        battleLog.setStyle("-fx-control-inner-background: #f4e4bc; -fx-font-family: 'Courier New'; -fx-font-size: 12px;");

        logContainer.getChildren().addAll(logTitle, battleLog);
        layout.setRight(logContainer);

        // =========================================
        // 5. BOTTOM SECTION (PLAYER HUD)
        // =========================================
        bottomHud = new VBox(10);
        bottomHud.setPadding(new Insets(15));
        bottomHud.getStyleClass().add("panel-background");
        BorderPane.setMargin(bottomHud, new Insets(0, 10, 10, 10));

        playerNameLbl = new Label("Player Name");
        playerNameLbl.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

        VBox barsBox = new VBox(5);
        playerHP = new ProgressBar(1.0);
        playerHP.getStyleClass().add("hp-bar");
        playerHP.setPrefWidth(300);

        playerMP = new ProgressBar(1.0);
        playerMP.getStyleClass().add("mp-bar");
        playerMP.setPrefWidth(300);

        barsBox.getChildren().addAll(new Label("HP"), playerHP, new Label("MP"), playerMP);

        // Action Buttons
        actionMenu = new HBox(15);
        actionMenu.setAlignment(Pos.CENTER);

        double btnW = 100;
        double btnH = 40;

        Button btnAttack = createActionButton("ATTACK", btnW, btnH);
        btnAttack.setOnAction(e -> executePlayerAction(ActionType.ATTACK, null, null));

        Button btnSkill = createActionButton("SKILL", btnW, btnH);
        btnSkill.setOnAction(e -> showSkillSelection());

        Button btnItem = createActionButton("ITEM", btnW, btnH);
        btnItem.setOnAction(e -> showItemSelection());

        Button btnDefend = createActionButton("DEFEND", btnW, btnH);
        btnDefend.setOnAction(e -> executePlayerAction(ActionType.DEFEND, null, null));

        actionMenu.getChildren().addAll(btnAttack, btnSkill, btnItem, btnDefend);

        HBox bottomLayout = new HBox(20);
        bottomLayout.setAlignment(Pos.CENTER_LEFT);

        VBox infoContainer = new VBox(5);
        infoContainer.getChildren().addAll(playerNameLbl, barsBox);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        bottomLayout.getChildren().addAll(infoContainer, spacer, actionMenu);
        bottomHud.getChildren().add(bottomLayout);

        layout.setBottom(bottomHud);

        // Gabungkan Layer
        rootStack.getChildren().addAll(layout, overlayPane);

        initialUpdate();
    }

    // Helper bikin tombol
    private Button createActionButton(String text, double w, double h) {
        Button btn = new Button(text);
        btn.getStyleClass().add("button-medieval");
        btn.setPrefSize(w, h);
        return btn;
    }

    public StackPane getLayout() { return rootStack; }

    // =========================================
    // UI LOGIC: MENU OVERLAYS
    // =========================================

    private void showOverlayMenu(String title, Node content) {
        StackPane overlayBg = new StackPane();
        overlayBg.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");

        VBox menuBox = new VBox(15);
        menuBox.getStyleClass().add("panel-background");
        menuBox.setPadding(new Insets(20));
        menuBox.setAlignment(Pos.CENTER);
        menuBox.setMaxSize(400, 300);

        Label lblTitle = new Label(title);
        lblTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-underline: true;");

        Button btnCancel = new Button("Cancel");
        btnCancel.getStyleClass().add("button-medieval");
        btnCancel.setOnAction(e -> rootStack.getChildren().remove(overlayBg));

        menuBox.getChildren().addAll(lblTitle, content, new Separator(), btnCancel);
        overlayBg.getChildren().add(menuBox);

        rootStack.getChildren().add(overlayBg);
    }

    private void showSkillSelection() {
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
                    rootStack.getChildren().remove(rootStack.getChildren().size() - 1);
                    executePlayerAction(ActionType.SKILL, skill, null);
                });
            }
            skillList.getChildren().add(btn);
        }
        showOverlayMenu("SELECT SKILL", skillList);
    }

    private void showItemSelection() {
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
                    rootStack.getChildren().remove(rootStack.getChildren().size() - 1);
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

            // Adjust posisi Y floating text agar pas di atas karakter
            double yPos = layout.getCenter().getBoundsInParent().getMinY() + 100;

            String text = (isCritical ? "CRIT " : "") + damageDealt;
            String style = isCritical ? "crit-text" : "damage-text";
            UIEffects.showFloatingText(overlayPane, text, style, xPos, yPos);
        }

        if (battle.getBattleStatus() != BattleStatus.ONGOING) {
            handleBattleEnd(battle.getBattleStatus());
        }
    }

    private void updateBars(Character p, Character e) {
        enemyNameLbl.setText(String.format("%s (Lv.%d)", e.getName(), e.getLevel()));
        playerNameLbl.setText(String.format("%s (Lv.%d) - %s", p.getName(), p.getLevel(), p.getStatus()));
    }

    private void executePlayerAction(ActionType type, Skill skill, Item item) {
        Battle battle = MainFX.battleService.getCurrentBattle();
        Character player = battle.getPlayerTeam().get(0);
        Character enemy = battle.getEnemyTeam().get(0);

        int hpBefore = enemy.getCurrentHP();

        BattleAction action = new BattleAction(player, type);

        if (type == ActionType.ITEM && item != null) {
            action.setItem(item);
            action.setTarget(player); // Item ke diri sendiri
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

        // ENEMY TURN (Delay)
        actionMenu.setDisable(true);
        PauseTransition pause = new PauseTransition(Duration.seconds(1.2));
        pause.setOnFinished(e -> {
            if (battle.getBattleStatus() != BattleStatus.ONGOING) return;
            int pHpBefore = player.getCurrentHP();

            BattleAction enemyAction = new BattleAction(enemy, ActionType.ATTACK);
            enemyAction.setTarget(player);
            battle.executeAction(enemyAction);

            int pDamage = Math.max(0, pHpBefore - player.getCurrentHP());
            boolean pCrit = false;

            updateUI(enemy, player, pDamage, pCrit);
            actionMenu.setDisable(false);
        });
        pause.play();
    }

    private void handleBattleEnd(BattleStatus status) {
        actionMenu.setDisable(true);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Battle Result");
        alert.setHeaderText(status == BattleStatus.VICTORY ? "VICTORY!" : "DEFEAT...");
        alert.show();
        alert.setOnHidden(e -> MainFX.primaryStage.setScene(new MainMenuScene().getScene()));
    }
}