package game.quoridor;

import game.quoridor.utils.QuoridorAction;
import game.quoridor.utils.WallObject;

/**
 * Represents an action when a player places a {@link WallObject} on the board.
 */
public class WallAction implements QuoridorAction {
  private static final long serialVersionUID = -8145033609219117190L;
  /** Start position of the wall. */
  public final int i, j;
  /** Orientation of the wall */
  public final boolean horizontal;
  /**
   * Constructs a wall action by the specified parameters.
   * @param i row index of the start position
   * @param j column index of the start position
   * @param horizontal orientation of the wall
   */
  public WallAction(int i, int j, boolean horizontal) {
    this.i = i;
    this.j = j;
    this.horizontal = horizontal;
  }
  public String toString() {
    return "WALL: " + i + " " + j + " " + (horizontal ? "horizontal" : "vertical");
  }
  /**
   * Converts the action to wall.
   * @return wall of the action.
   */
  public WallObject toWall() {
    return new WallObject(i, j, horizontal);
  }
}
