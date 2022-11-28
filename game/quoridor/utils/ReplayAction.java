package game.quoridor.utils;

import game.engine.utils.Utils;
import game.quoridor.MoveAction;
import game.quoridor.WallAction;

public final class ReplayAction implements QuoridorAction {
  private static final long serialVersionUID = -5798163953825578421L;
  /** Start position of the wall. */
  public final int i, j;
  /** Orientation of the wall */
  public final boolean horizontal;
  /** Row and column indices of the start position. */
  public final int from_i, from_j;
  /** Row and column indices of the destination position. */
  public final int to_i, to_j;
  public ReplayAction() {
    i = j = from_i = from_j = to_i = to_j = -1;
    horizontal = false;
  }
  public QuoridorAction toAction() {
    if (i != -1 && j != -1) {
      return new WallAction(i, j, horizontal);
    }
    if (from_i != -1 && from_j != -1 && to_i != -1 && to_j != -1) {
      return new MoveAction(from_i, from_j, to_i, to_j);
    }
    System.out.println("REPLAY PARSE ERROR: " + this);
    return null;
  }
  @Override
  public String toString() {
    return Utils.jsonSerialize(this);
  }
}
