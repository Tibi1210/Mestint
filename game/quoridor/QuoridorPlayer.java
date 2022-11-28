package game.quoridor;

import java.util.List;
import java.util.Random;

import game.engine.Player;
import game.engine.utils.Pair;
import game.quoridor.utils.PlaceObject;
import game.quoridor.utils.QuoridorAction;
import game.quoridor.utils.QuoridorObject;

/**
 * Describes the players of the {@link QuoridorGame} game.
 */
public abstract class QuoridorPlayer implements Player<QuoridorAction>, QuoridorObject {
  /** Player's position on the board */
  public int i, j;
  /** Player's color */
  public final int color;
  /** Random number generator */
  public final Random random;
  
  /**
   * Constructs the player object and sets the specified parameters.
   * @param i row index
   * @param j column index
   * @param color player's color
   * @param random random number generator
   */
  public QuoridorPlayer(int i, int j, int color, Random random) {
    this.i = i;
    this.j = j;
    this.color = color;
    this.random = random;
  }
  @Override
  public final String toString() {
    return getClass().getName() + " (" + i + "," + j + ") color: " + QuoridorGame.TILES.get(color) + " <- " + color;
  }
  @Override
  public final String toLogString() {
    return QuoridorGame.int2character(i) + "" + j;
  }
  /**
   * Returns true iff the specified place is the same as the player's position. 
   * @param place position to be checked.
   * @return true iff the specified place is the same as the player's position.
   */
  public final boolean sameLocation(PlaceObject place) {
    return i == place.i && j == place.j;
  }
  /**
   * Transforms the position of the player to {@link PlaceObject}
   * @return the place of the player.
   */
  public final PlaceObject toPlace() {
    return new PlaceObject(i, j);
  }
  @Override
  public final int getColor() {
    return color;
  }
  @Override
  public final QuoridorAction getAction(List<Pair<Integer, QuoridorAction>> prevActions, long[] remainingTimes) {
    return getAction(prevActions.isEmpty() ? null : prevActions.get(0).second, remainingTimes);
  }
  /**
   * Returns the next action of the player.
   * @param prevAction the last action of the enemy player or null iff no previous action available.
   * @param remainingTimes the overall remaining times of the players in the game
   * @return the next action
   */
  public abstract QuoridorAction getAction(QuoridorAction prevAction, long[] remainingTimes);

}
