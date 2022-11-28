package game.quoridor.players;

import java.util.Random;

import game.quoridor.QuoridorPlayer;
import game.quoridor.utils.QuoridorAction;

/**
 * Placeholder player.
 */
public class DummyPlayer extends QuoridorPlayer {

  public DummyPlayer(int i, int j, int color, Random random) {
    super(i, j, color, random);
  }

  @Override
  public QuoridorAction getAction(QuoridorAction prevAction, long[] remainingTimes) {
    return null;
  }

}
