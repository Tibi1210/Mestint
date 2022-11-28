package game.quoridor.utils;

import game.quoridor.QuoridorPlayer;

/**
 * Represents an object of the game ({@link PlaceObject}, {@link QuoridorPlayer}, {@link WallObject})
 */
public interface QuoridorObject {
  public String toString();
  /**
   * Returns the log type string representation of the object
   * @return log representation
   */
  public String toLogString();
}
