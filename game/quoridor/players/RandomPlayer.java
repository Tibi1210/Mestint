package game.quoridor.players;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import game.quoridor.MoveAction;
import game.quoridor.QuoridorGame;
import game.quoridor.QuoridorPlayer;
import game.quoridor.WallAction;
import game.quoridor.utils.PlaceObject;
import game.quoridor.utils.QuoridorAction;
import game.quoridor.utils.WallObject;

/**
 * Player that takes a random legal move or place a wall.
 */
public class RandomPlayer extends QuoridorPlayer {

  private final List<WallObject> walls = new LinkedList<WallObject>();
  private final QuoridorPlayer[] players = new QuoridorPlayer[2];
  private int numWalls;
  
  public RandomPlayer(int i, int j, int color, Random random) {
    super(i, j, color, random);
    players[color] = this;
    players[1-color] = new DummyPlayer((1-color) * (QuoridorGame.HEIGHT - 1), j, 1-color, null);
    numWalls = 0;
  }

  @Override
  public QuoridorAction getAction(QuoridorAction prevAction, long[] remainingTimes) {
    // register enemy action
    if (prevAction instanceof WallAction) {
      WallAction a = (WallAction) prevAction;
      walls.add(new WallObject(a.i, a.j, a.horizontal));
    } else if (prevAction instanceof MoveAction) {
      MoveAction a = (MoveAction) prevAction;
      players[1 - color].i = a.to_i;
      players[1 - color].j = a.to_j;
    }
    
    if (numWalls < QuoridorGame.MAX_WALLS && random.nextDouble() < 0.5) {
      // place a wall
      WallObject candidate = null;
      while (!QuoridorGame.checkWall(candidate, walls, players)) {
        candidate = new WallObject(random.nextInt(QuoridorGame.HEIGHT - 1), random.nextInt(QuoridorGame.WIDTH - 1), random.nextBoolean());
      }
      // store wall locally
      walls.add(candidate);
      numWalls ++;
      return new WallAction(candidate.i, candidate.j, candidate.horizontal);
    } else {
      // take a step (i and j will be updated by the engine)
      List<PlaceObject> steps = toPlace().getNeighbors(walls, players);
      PlaceObject step = steps.get(random.nextInt(steps.size()));
      return new MoveAction(i, j, step.i, step.j);
    }
  }

}
