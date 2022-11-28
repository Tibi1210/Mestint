package game.quoridor.utils;

import game.quoridor.QuoridorGame;
import game.quoridor.WallAction;

/**
 * Represents a wall on the table.
 * A wall is 2 place wide and starts at the specified positions.
 * The wall is below the position if it is horizontal and on the  
 * right side if it is vertical.
 * <br/>
 * Results:
 * <ul>
 * <li>a horizontal and a vertical wall can not start at the same position</li>
 * <li>wall can not be placed on the last row and the last column of the table</li>
 * </ul>
 */
public class WallObject implements QuoridorObject, Comparable<WallObject> {
  /** start position of the wall */
  public final int i, j;
  /** orientation of the wall */
  public final boolean horizontal;
  /**
   * Constructs the wall object by the specified values.
   * @param i row index
   * @param j column index
   * @param horizontal orientation
   */
  public WallObject(int i, int j, boolean horizontal) {
    this.i = i;
    this.j = j;
    this.horizontal = horizontal;
  }
  /**
   * Returns true iff the specified object represents the same wall as the current.
   * @param wall to be checked
   * @return true iff the specified object represents the same wall as the current.
   */
  public final boolean same(WallObject wall) {
    return i == wall.i && j == wall.j && horizontal == wall.horizontal;
  }
  @Override
  public boolean equals(Object o) {
    if (o instanceof WallObject) {
      return same((WallObject)o);
    }
    return false;
  }
  @Override
  public int compareTo(WallObject o) {
    if (i < o.i || (i == o.i && j < o.j) || (i == o.i && j == o.j && horizontal && !o.horizontal)) {
      return -1;
    }
    if (o.i < i || (i == o.i && o.j < j) || (i == o.i && j == o.j && !horizontal && o.horizontal)) {
      return 1;
    }
    return 0;
  }
  @Override
  public String toString() {
    return "(" + i + ", " + j + ", " + (horizontal ? "H)" : "V)") + " - " + toLogString();
  }
  @Override
  public String toLogString() {
    return QuoridorGame.int2character(i) + "" + j;
  }
  /**
   * Returns true iff the current wall is between the two specified places.
   * @param a one place to be checked
   * @param b other place to be checked
   * @return true iff the current wall is between the two specified places.
   */
  public boolean betweenPlaces(PlaceObject a, PlaceObject b) {
    return (horizontal && a.i != b.i && a.j == b.j && (a.i + b.i == 2 * i + 1) && j <= a.j && a.j <= j + 1) ||
        (!horizontal && a.i == b.i && a.j != b.j && (a.j + b.j == 2 * j + 1) && i <= a.i && a.i <= i + 1);
  }
  /**
   * Returns true iff the current wall intersects the specified wall.
   * @param wall to be checked
   * @return true iff the current wall intersects.
   */
  public boolean conflicts(WallObject wall) {
    if ((horizontal && wall.horizontal && (i == wall.i) && (Math.abs(j - wall.j) <= 1)) || 
        (!horizontal && !wall.horizontal && (j == wall.j) && (Math.abs(i - wall.i) <= 1)) ||
        (i == wall.i && j == wall.j && horizontal != wall.horizontal)) {
      return true;
    }
    return false;
  }
  /**
   * Returns the corresponding wall action to the current object.
   * @return corresponding wall action
   */
  public WallAction toWallAction() {
    return new WallAction(i, j, horizontal);
  }
}
