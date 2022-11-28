package game.quoridor.players;

import java.util.Random;

import game.engine.ui.GameCanvas;
import game.quoridor.MoveAction;
import game.quoridor.QuoridorPlayer;
import game.quoridor.WallAction;
import game.quoridor.ui.QuoridorCanvas;
import game.quoridor.utils.QuoridorAction;

/**
 * Player for GUI.
 */
public class HumanPlayer extends QuoridorPlayer {

  public HumanPlayer(int i, int j, int color, Random random) {
    super(i, j, color, random);
  }

  @Override
  public QuoridorAction getAction(QuoridorAction prevAction, long[] remainingTimes) {
    try {
      QuoridorAction result = null;
      MoveAction ma = (MoveAction)GameCanvas.getAction();
      if ((ma.to_i + 1) * QuoridorCanvas.MULTIPLIER - ma.from_i < QuoridorCanvas.MULTIPLIER/4) {
        result = new WallAction(ma.to_j, ma.to_i, false);
      }  else if ((ma.to_j + 1) * QuoridorCanvas.MULTIPLIER - ma.from_j < QuoridorCanvas.MULTIPLIER/4) {
        result = new WallAction(ma.to_j, ma.to_i, true);
      } else {
        result = new MoveAction(i, j, ma.to_j, ma.to_i);
      }
      return result;
    } catch (InterruptedException e) {
      e.printStackTrace();
      return null;
    }
  }

}
