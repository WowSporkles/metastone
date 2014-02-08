package net.pferdimanzug.hearthstone.analyzer.playmode;

import javafx.scene.layout.BorderPane;
import net.pferdimanzug.hearthstone.analyzer.game.behaviour.human.HumanTargetOptions;

public class PlayModePane extends BorderPane {
	
	private final GameBoardView boardView;

	public PlayModePane() {
		boardView = new GameBoardView();
		setCenter(boardView);
	}
	
	public void updateGameState(GameContextVisualizable context) {
		boardView.updateGameState(context);
	}
	
	public void enableTargetSelection(HumanTargetOptions targetOptions) {
		boardView.enableTargetSelection(targetOptions);
	}
}
