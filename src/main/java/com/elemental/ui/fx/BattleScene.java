package com.elemental.ui.fx;

import com.elemental.MainFX;
import com.elemental.model.*;
import com.elemental.model.Character;
import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node; // <--- INI YANG TADINYA KURANG
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Duration;

public class BattleScene {
    private BorderPane layout;
    private Pane overlayPane;
    private TextArea battleLog;
    private VBox playerBox, enemyBox;
    private HBox actionMenu;

    private ProgressBar playerHP, playerMP, enemyHP;
    private Label playerInfo, enemyInfo;

    public BattleScene() {
        StackPane rootStack = new StackPane();
        rootStack.getStyleClass().add("root");

        layout = new BorderPane();
        layout.setPadding(new Insets(10));

        overlayPane = new Pane();
        overlayPane.setPickOnBounds(false);

        // --- TOP: ENEMY ---
        enemyBox = new VBox(10);
        enemyBox.setAlignment(Pos.CENTER);
        enemyBox.getStyleClass().add("panel-background");
        enemyBox.setPadding(new Insets(10));
        enemyBox.setMaxHeight(150);
        enemyBox.setId("enemyBox");

        enemyInfo = new Label("Enemy Info");
        enemyHP = new ProgressBar(1.0);
        enemyHP.getStyleClass().add("hp-bar");
        enemyHP.setPrefWidth(300);

        enemyBox.getChildren().addAll(enemyInfo, enemyHP);
        layout.setTop(enemyBox);

        // --- CENTER ---
        Label vsLabel = new Label("VS");
        vsLabel.setStyle("-fx-font-size: 50px; -fx-text-fill: rgba(255,255,255,0.2);");
        StackPane centerPane = new StackPane(vsLabel);
        layout.setCenter(centerPane);

        // --- RIGHT: LOG ---
        battleLog = new TextArea();
        battleLog.setEditable(false);
        battleLog.setPrefWidth(250);
        battleLog.setStyle("-fx-control-inner-background: #f4e4bc;");
        layout.setRight(battleLog);

        // --- BOTTOM: PLAYER ---
        VBox bottomContainer = new VBox(10);
        bottomContainer.getStyleClass().add("panel-background");
        bottomContainer.setPadding(new Insets(10));
        bottomContainer.setId("playerBox");

        playerInfo = new Label("Player Info");
        playerHP = new ProgressBar(1.0);
        playerHP.getStyleClass().add("hp-bar");
        playerMP = new ProgressBar(1.0);
        playerMP.getStyleClass().add("mp-bar");

        actionMenu = new HBox(15);
        actionMenu.setAlignment(Pos.CENTER);

        Button btnAttack = new Button("ATTACK");
        btnAttack.getStyleClass().add("button-medieval");
        btnAttack.setPrefWidth(100);
        btnAttack.setOnAction(e -> executePlayerAction(ActionType.ATTACK, null));

        Button btnDefend = new Button("DEFEND");
        btnDefend.getStyleClass().add("button-medieval");
        btnDefend.setPrefWidth(100);
        btnDefend.setOnAction(e -> executePlayerAction(ActionType.DEFEND, null));

        actionMenu.getChildren().addAll(btnAttack, btnDefend);
        bottomContainer.getChildren().addAll(playerInfo, playerHP, playerMP, actionMenu);
        layout.setBottom(bottomContainer);

        rootStack.getChildren().addAll(layout, overlayPane);

        this.playerBox = bottomContainer;

        initialUpdate();
    }

    public StackPane getLayout() { return (StackPane) overlayPane.getParent(); }

    private void initialUpdate() {
        Battle battle = MainFX.battleService.getCurrentBattle();
        updateBars(battle.getPlayerTeam().get(0), battle.getEnemyTeam().get(0));
        battleLog.setText("Battle Start!");
    }

    private void updateUI(Character attacker, Character target, int damageDealt, boolean isCritical) {
        Battle battle = MainFX.battleService.getCurrentBattle();
        Character player = battle.getPlayerTeam().get(0);
        Character enemy = battle.getEnemyTeam().get(0);

        playerInfo.setText(String.format("%s (Lv.%d) - %s", player.getName(), player.getLevel(), player.getStatus()));
        enemyInfo.setText(String.format("%s (Lv.%d) - %s", enemy.getName(), enemy.getLevel(), enemy.getStatus()));

        // Cek agar log tidak error jika kosong
        if (!battle.getBattleLog().getRecentEntries().isEmpty()) {
            battleLog.appendText("\n" + battle.getBattleLog().getRecentEntries().get(battle.getBattleLog().getRecentEntries().size()-1));
        }

        UIEffects.animateBarChange(playerHP, (double) player.getCurrentHP() / player.getMaxHP());
        UIEffects.animateBarChange(playerMP, (double) player.getCurrentMP() / player.getMaxMP());
        UIEffects.animateBarChange(enemyHP, (double) enemy.getCurrentHP() / enemy.getMaxHP());

        if (damageDealt > 0) {
            boolean targetIsEnemy = (target == enemy);
            // Variable Node sekarang sudah dikenali
            Node targetNode = targetIsEnemy ? enemyBox : playerBox;

            UIEffects.shakeNode(targetNode);

            double xPos = layout.getWidth()/2;
            double yPos = targetIsEnemy ? enemyBox.getHeight() + 20 : layout.getHeight() - playerBox.getHeight() - 50;

            String text = (isCritical ? "CRIT " : "") + damageDealt;
            String style = isCritical ? "crit-text" : "damage-text";

            UIEffects.showFloatingText(overlayPane, text, style, xPos, yPos);
        }

        if (battle.getBattleStatus() != BattleStatus.ONGOING) {
            handleBattleEnd(battle.getBattleStatus());
        }
    }

    private void updateBars(Character p, Character e) {
        playerHP.setProgress((double) p.getCurrentHP() / p.getMaxHP());
        enemyHP.setProgress((double) e.getCurrentHP() / e.getMaxHP());
        playerInfo.setText(p.getName());
        enemyInfo.setText(e.getName());
    }

    private void executePlayerAction(ActionType type, Skill skill) {
        Battle battle = MainFX.battleService.getCurrentBattle();
        Character player = battle.getPlayerTeam().get(0);
        Character enemy = battle.getEnemyTeam().get(0);

        int hpBefore = enemy.getCurrentHP();

        BattleAction action = new BattleAction(player, type);
        action.setTarget(enemy);
        if (skill != null) action.setSkill(skill);

        battle.executeAction(action);

        int damageDealt = hpBefore - enemy.getCurrentHP();
        boolean isCrit = false;
        if (!battle.getBattleLog().getRecentEntries().isEmpty()) {
            String lastLog = battle.getBattleLog().getRecentEntries().get(battle.getBattleLog().getRecentEntries().size()-1);
            isCrit = lastLog.contains("CRITICAL");
        }

        updateUI(player, enemy, damageDealt, isCrit);

        if (battle.getBattleStatus() != BattleStatus.ONGOING) return;

        // --- PERBAIKAN LOGIKA DELAY MUSUH (Error Void Type) ---
        actionMenu.setDisable(true);

        PauseTransition pause = new PauseTransition(Duration.seconds(1.0));
        pause.setOnFinished(e -> {
            int pHpBefore = player.getCurrentHP();

            BattleAction enemyAction = new BattleAction(enemy, ActionType.ATTACK);
            enemyAction.setTarget(player);
            battle.executeAction(enemyAction);

            int pDamage = pHpBefore - player.getCurrentHP();
            boolean pCrit = false;
            if (!battle.getBattleLog().getRecentEntries().isEmpty()) {
                pCrit = battle.getBattleLog().getRecentEntries().toString().contains("CRITICAL");
            }

            updateUI(enemy, player, pDamage, pCrit);
            actionMenu.setDisable(false);
        });
        pause.play(); // .play() dipanggil terpisah dari setOnFinished
    }

    private void handleBattleEnd(BattleStatus status) {
        actionMenu.setDisable(true);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Battle Over");
        alert.setHeaderText(status == BattleStatus.VICTORY ? "VICTORY!" : "DEFEAT");
        alert.setContentText("Press OK to return to menu.");
        alert.show();
        alert.setOnHidden(e -> MainFX.primaryStage.setScene(new MainMenuScene().getScene()));
    }
}