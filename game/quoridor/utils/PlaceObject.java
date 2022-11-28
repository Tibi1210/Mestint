package game.quoridor.utils;

import java.util.LinkedList;
import java.util.List;

import game.quoridor.QuoridorGame;
import game.quoridor.QuoridorPlayer;

/**
 * Represents a position of the table by its row and column indices.
 */
public class PlaceObject implements QuoridorObject, Comparable<PlaceObject> {
  /** row and column index of the position */
  public final int i, j;
  /**
   * Creates a position by the specified parameters.
   * @param i row index
   * @param j column index
   */
  public PlaceObject(int i, int j) {
    this.i = i;
    this.j = j;
  }
  @Override
  public String toString() {
    return "(" + i + ", " + j + ") - " + toLogString();
  }
  @Override
  public final String toLogString() {
    return QuoridorGame.int2character(i) + "" + j;
  }
  /**
   * Returns true iff the specified object represents the same position as the current.
   * @param place to be checked
   * @return true iff the specified object represents the same position as the current.
   */
  public final boolean same(PlaceObject place) {
    return i == place.i && j == place.j;
  }
  @Override
  public boolean equals(Object o) {
    if (o instanceof PlaceObject) {
      return same((PlaceObject)o);
    }
    return false;
  }
  @Override
  public int compareTo(PlaceObject o) {
    if (i < o.i || (i == o.i && j < o.j)) {
      return -1;
    }
    if (o.i < i || (i == o.i && o.j < j)) {
      return 1;
    }
    return 0;
  }
  /**
   * Returns the neighbors of the current position where a player can move to from here
   * based on the specified walls and players.
   * @param walls to be checked (blocks)
   * @param players to be checked (can be jumped over)
   * @return available neighbor positions.
   */
  public List<PlaceObject> getNeighbors(List<WallObject> walls, QuoridorPlayer[] players) {
    List<PlaceObject> neighbors = new LinkedList<PlaceObject>();
    for (int di = -1; di <= 1; di ++) {
      for (int dj = -1; dj <= 1; dj ++) {
        PlaceObject candidate = new PlaceObject(i + di, j + dj);
        if (QuoridorGame.checkCandidateMove(this, candidate, walls, players)) {
          neighbors.add(candidate);
        }
      }
    }
    for (int d = -2; d <= 2; d += 4) {
      PlaceObject candidate = new PlaceObject(i, j + d);
      if (QuoridorGame.checkCandidateMove(this, candidate, walls, players)) {
        neighbors.add(candidate);
      }
      candidate = new PlaceObject(i + d, j);
      if (QuoridorGame.checkCandidateMove(this, candidate, walls, players)) {
        neighbors.add(candidate);
      }
    }
    return neighbors;
  }
}
