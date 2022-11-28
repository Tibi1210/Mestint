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

public class BlockRandomPlayer extends QuoridorPlayer {
  
  private final List<WallObject> walls = new LinkedList<WallObject>();
  private final QuoridorPlayer[] players = new QuoridorPlayer[2];
  private int numWalls;

  public BlockRandomPlayer(int i, int j, int color, Random random) {
    super(i, j, color, random);
    players[color] = this;
    players[1-color] = new DummyPlayer((1-color) * (QuoridorGame.HEIGHT - 1), j, 1-color, null);
    numWalls = 0;
  }

  @Override
  public QuoridorAction getAction(QuoridorAction prevAction, long[] remainingTimes) {
    if (prevAction instanceof WallAction) {
      WallAction a = (WallAction) prevAction;
      walls.add(new WallObject(a.i, a.j, a.horizontal));
    } else if (prevAction instanceof MoveAction) {
      MoveAction a = (MoveAction) prevAction;
      players[1 - color].i = a.to_i;
      players[1 - color].j = a.to_j;
    }
    
    int di = (color * (QuoridorGame.HEIGHT - 1)) - players[1-color].i < 0 ? -1 : 0;
    List<WallObject> wallObjects = new LinkedList<WallObject>();
    wallObjects.add(new WallObject(players[1-color].i + di, players[1-color].j - color, true));
    wallObjects.add(new WallObject(players[1-color].i + di, players[1-color].j - 1 + color, true));
    wallObjects.add(new WallObject(players[1-color].i + di, players[1-color].j - color, false));
    wallObjects.add(new WallObject(players[1-color].i + di, players[1-color].j - 1 + color, false));
    for (WallObject wall : wallObjects) {
      if (numWalls < QuoridorGame.MAX_WALLS && QuoridorGame.checkWall(wall, walls, players)) {
        numWalls ++;
        walls.add(wall);
        return wall.toWallAction();
      }
    }
    
    double diff = -QuoridorGame.HEIGHT;
    MoveAction action = null;
    for (PlaceObject step : toPlace().getNeighbors(walls, players)) {
      double d = ((color == 0 ? step.i - i : i - step.i) + 1) / 4.0 + random.nextDouble();
      if (diff < d) {
        diff = d;
        action = new MoveAction(i, j, step.i, step.j);
      }
    }
    return action;
  }

}
