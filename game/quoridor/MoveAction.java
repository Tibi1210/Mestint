package game.quoridor;

import game.quoridor.utils.QuoridorAction;

/**
 * Represents the action when a player moves from a position to another position.
 */
public class MoveAction implements QuoridorAction {
  private static final long serialVersionUID = -585260252454934522L;
  /** Row and column indices of the start position. */
  public final int from_i, from_j;
  /** Row and column indices of the destination position. */
  public final int to_i, to_j;
  /**
   * Creates a move action by the specified values.
   * @param from_i row index of move from
   * @param from_j column index of move from
   * @param to_i row index of move to
   * @param to_j column index of move to
   */
  public MoveAction(int from_i, int from_j, int to_i, int to_j) {
    this.from_i = from_i;
    this.from_j = from_j;
    this.to_i = to_i;
    this.to_j = to_j;
  }
  @Override
  public final String toString() {
    return "MOVE: (" + from_i + "," + from_j + ")->(" + to_i + "," + to_j + ")";
  }
}
