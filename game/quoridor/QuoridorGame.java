package game.quoridor;

import java.awt.Frame;
import java.awt.Image;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import game.engine.Action;
import game.engine.Engine;
import game.engine.Game;
import game.engine.ui.Drawable;
import game.engine.ui.GameFrame;
import game.engine.ui.GameObject;
import game.engine.utils.Pair;
import game.engine.utils.Utils;
import game.quoridor.players.DummyPlayer;
import game.quoridor.players.HumanPlayer;
import game.quoridor.ui.QuoridorCanvas;
import game.quoridor.utils.PlaceObject;
import game.quoridor.utils.QuoridorAction;
import game.quoridor.utils.ReplayAction;
import game.quoridor.utils.WallObject;

/**
 * Represents the two player version of the <a target="_blank" href="https://en.wikipedia.org/wiki/Quoridor">Quoridor</a> game.
 */
public final class QuoridorGame implements Game<QuoridorPlayer, QuoridorAction>, Drawable {
  
  /** Specifies the wall place value on the board */
  public static final int WALL_PLACE = -1;
  /** Specifies the color of the first player on the board */
  public static final int BLACK = 0;
  /** Specifies the color of the second player on the board */
  public static final int WHITE = 1;
  /** Specifies the wall value on the board */
  public static final int WALL = 2;
  /** Specifies the empty value on the board */
  public static final int EMPTY = 4;
  
  /** Width of board */
  public static final int WIDTH = 9;
  /** Height of board */
  public static final int HEIGHT = 9;
  /** The number of walls that can be placed by a player */
  public static final int MAX_WALLS = 10;
  /** The maximum number of steps or actions in the game */
  public static final int MAX_ITER = 1000;
  
  /** Character representation of the board. */
  public static final HashMap<Integer, String> TILES;
  static {
    TILES = new HashMap<Integer, String>();
    TILES.put(WALL_PLACE, "*");
    TILES.put(EMPTY, " ");
    TILES.put(BLACK, "X");
    TILES.put(WHITE, "O");
    TILES.put(WALL, "#");
  }
  
  /**
   * Returns true if one of the two specified payers reaches the opposite wall.
   * @param players to be checked
   * @return true if one of the two payers reaches the opposite wall.
   */
  public static boolean isFinished(QuoridorPlayer[] players) {
    return players[0].i == HEIGHT-1 || players[1].i == 0;
  }
  /**
   * Returns true if one of the specified players is on the specified position.
   * @param players to be checked
   * @param place position to be checked
   * @return true if one of the specified players is on the specified position.
   */
  public static boolean isPlayerOn(QuoridorPlayer[] players, PlaceObject place) {
    for (QuoridorPlayer player : players) {
      if (player.sameLocation(place)) {
        return true;
      }
    }
    return false;
  }
  /**
   * Returns true if one of the specified walls is between the specified positions.
   * @param walls to be checked
   * @param a one position
   * @param b other position
   * @return true if one of the specified walls is between the specified positions.
   */
  public static boolean isWallBetween(List<WallObject> walls, PlaceObject a, PlaceObject b) {
    for (WallObject wall : walls) {
      if (wall.betweenPlaces(a, b)) {
        return true;
      }
    }
    return false;
  }
  /**
   * Returns true if the move is valid from the specified current position to 
   * the specified candidate position, regarding the specified walls and players. 
   * @param current check move from
   * @param candidate check move to
   * @param walls to be considered
   * @param players to be considered
   * @return true if the move is valid.
   */
  public static boolean checkCandidateMove(PlaceObject current, PlaceObject candidate, List<WallObject> walls, QuoridorPlayer[] players) {
    // out of table
    if (candidate.i < 0 || candidate.j < 0 || QuoridorGame.HEIGHT <= candidate.i || QuoridorGame.WIDTH <= candidate.j) {
      //System.out.println(current + "->" + candidate + ": OUT OF THE TABLE");
      return false;
    }
    // same places
    if (current.same(candidate)) {
      //System.out.println(current + "->" + candidate + ": SAME PLACE");
      return false;
    }
    // is empty
    if (isPlayerOn(players, candidate)) {
      //System.out.println(current + "->" + candidate + ": NOT EMPTY");
      return false;
    }
    
    int di = candidate.i - current.i;
    int dj = candidate.j - current.j;
    // is in range
    if (2 < Math.abs(di) + Math.abs(dj)) {
      //System.out.println(current + "->" + candidate + ": OUT OF RANGE");
      return false;
    }
    // is wall in regular directions
    if ((di == 0 && (dj == -1 || dj == 1)) || 
        (dj == 0 && (di == -1 || di == 1))) {
      if (isWallBetween(walls, current, candidate)) {
        //System.out.println(current + "->" + candidate + ": WALL BETWEEN");
        return false;
      }
    }
    // is wall jump over
    if ((di == 0 && (dj == -2 || dj == 2)) || 
        (dj == 0 && (di == -2 || di == 2))) {
      PlaceObject half = new PlaceObject(candidate.i - (di/2), candidate.j - (dj/2));
      if (!isPlayerOn(players, half) || isWallBetween(walls, half, candidate) || isWallBetween(walls, current, half)) {
        //System.out.println(current + "->" + candidate + ": INVALID LONG JUMP");
        return false;
      }
    }
    // jump by
    if (Math.abs(di) == 1 && Math.abs(dj) == 1) {
      // has to be a player in one direction part 
      // and a [wall or an other player or edge of the board] next to it 
      // and no wall near the current 
      PlaceObject place11 = new PlaceObject(current.i, current.j + dj);
      PlaceObject place12 = new PlaceObject(current.i, current.j + dj + dj);
      PlaceObject place21 = new PlaceObject(current.i + di, current.j);
      PlaceObject place22 = new PlaceObject(current.i + di + di, current.j);
      if (!(isPlayerOn(players, place11) && (isPlayerOn(players, place12) || isWallBetween(walls, place11, place12) || WIDTH <= place12.j || place12.j < 0) && !isWallBetween(walls, place11, candidate) && !isWallBetween(walls, current, place11)) && 
          !(isPlayerOn(players, place21) && (isPlayerOn(players, place22) || isWallBetween(walls, place21, place22) || HEIGHT <= place22.i || place22.i < 0) && !isWallBetween(walls, place21, candidate) && !isWallBetween(walls, current, place21))) {
        //System.out.println(current + "->" + candidate + ": INVALID SHORT JUMP");
        return false;
      }
    }
    //System.out.println(current + "->" + candidate + ": OK");
    return true;
  }
  /**
   * Returns true if the specified candidate wall can be placed on the board, regarding the specified walls and players.
   * Walls can not be crossed by each other, and players can not be blocked from reaching the target position.
   * @param candidate to be checked
   * @param walls considered walls
   * @param players considered players
   * @return true if the  wall can be placed.
   */
  public static boolean checkWall(WallObject candidate, List<WallObject> walls, QuoridorPlayer[] players) {
    if (candidate == null) {
      return false;
    }
    if (candidate.i < 0 || candidate.j < 0 || HEIGHT < candidate.i + 2 || WIDTH < candidate.j + 2) {
      //System.out.println("WALL OUT OF RANGE: " + candidate);
      return false;
    }
    for (WallObject wall : walls) {
      if (wall.conflicts(candidate)) {
        //System.out.println("CONFLICT: " + wall + " <-> " + candidate);
        return false;
      }
    }
    List<WallObject> copyWalls = new LinkedList<WallObject>();
    copyWalls.addAll(walls);
    copyWalls.add(candidate);
    return pathExists(copyWalls, players, 0) && pathExists(copyWalls, players, 1);
  }
  /**
   * Returns true if the player identified by the specified color from the 
   * specified players can reach its destination and not blocked by the specified walls.
   * @param walls considered walls
   * @param players players on the board
   * @param color identifies the player
   * @return true if the player is not blocked.
   */
  public static boolean pathExists(List<WallObject> walls, QuoridorPlayer[] players, int color) {
    List<PlaceObject> O = new LinkedList<PlaceObject>();
    List<PlaceObject> C = new LinkedList<PlaceObject>();
    O.add(new PlaceObject(players[color].i, players[color].j));
    while(O.size() > 0) {
      PlaceObject c = O.remove(0);
      if (c.i == (1-color)*(HEIGHT-1)) {
        return true;
      }
      C.add(c);
      for (PlaceObject n : c.getNeighbors(walls, players)) {
        if (!O.contains(n) && !C.contains(n)) {
          O.add(n);
        }
      }
    }
    //System.out.println(players[color] + " NO ROUTE!");
    return false;
  }
  /**
   * Returns a 2D array board representation based on the specified game objects.
   * The size of the result is 2*{@link QuoridorGame#HEIGHT}-1 X 2*{@link QuoridorGame#WIDTH}-1.
   * The odd rows and columns are reserved for walls, the evens are places where players can be on.
   * @param walls walls on the board
   * @param players players on the board
   * @return 2D array representation
   */
  public static int[][] getBoard(List<WallObject> walls, QuoridorPlayer[] players) {
    int[][] board = new int[2*HEIGHT-1][2*WIDTH-1];
    for(int i = 0; i < board.length; i++) {
      for(int j = 0; j < board[i].length; j++) {
        board[i][j] = EMPTY;
        if (i % 2 == 1 || j % 2 == 1) {
          board[i][j] = WALL_PLACE;
        }
      }
    }
    for (WallObject wall : walls) {
      if (wall.horizontal) {
        board[1 + 2 * wall.i][2 * wall.j] = WALL;
        board[1 + 2 * wall.i][2 * wall.j + 1] = WALL;
        board[1 + 2 * wall.i][2 * wall.j + 2] = WALL;
      } else {
        board[2 * wall.i][1 + 2 * wall.j] = WALL;
        board[2 * wall.i + 1][1 + 2 * wall.j] = WALL;
        board[2 * wall.i + 2][1 + 2 * wall.j] = WALL;
      }
    }
    for (QuoridorPlayer player : players) {
      board[2 * player.i][2 * player.j] = player.color;
    }
    return board;
  }
  /**
   * Returns the <a href=https://en.wikipedia.org/wiki/Forsyth%E2%80%93Edwards_Notation>Forsyth–Edwards Notation</a>
   * like string representation of the board defined by the specified objects.
   * @param walls walls on the board
   * @param players players on the board
   * @param availableWalls remaining walls of the players
   * @param currentPlayer active player
   * @return Forsyth–Edwards Notation representation
   */
  public static String getState(List<WallObject> walls, QuoridorPlayer[] players, int[] availableWalls, int currentPlayer) {
    String result = "";
    for (WallObject wall : walls) {
      if (wall.horizontal) {
        result += wall.toLogString();
      }
    }
    result += " ";
    for (WallObject wall : walls) {
      if (!wall.horizontal) {
        result += wall.toLogString();
      }
    }
    for (QuoridorPlayer player : players) {
      result += " " + player.toLogString();
    }
    for (int aw : availableWalls) {
      result += " " + aw;
    }
    return result + " " + currentPlayer;
  }
  /**
   * Parses the walls by the specified string representations and the specified orientation.
   * @param wallState string representation of the start positions
   * @param horizontal orientation
   * @return parsed wall objects.
   */
  public static List<WallObject> parseWalls(String wallState, boolean horizontal) {
    List<WallObject> result = new LinkedList<WallObject>();
    for(int idx = 0; idx < wallState.length(); idx += 2) {
      int i = character2int(wallState.charAt(idx));
      int j = number2int(wallState.charAt(idx + 1));
      result.add(new WallObject(i, j, horizontal));
      }
    return result;
  }
  /**
   * Parses a {@link DummyPlayer} from the specified string representation and color.
   * @param playerState string position representation
   * @param color player color
   * @return parsed player
   */
  public static QuoridorPlayer parsePlayer(String playerState, int color) {
    int i = character2int(playerState.charAt(0));
    int j = number2int(playerState.charAt(1));
    return new DummyPlayer(i, j, color, null);
  }
  /**
   * Converts the specified number by character to integer.
   * @param chr to be parsed
   * @return parsed integer.
   */
  public static int number2int(char chr) {
    //System.out.println(chr + " -> " + (chr - 48));
    return chr - 48;
  }
  /**
   * Converts the specified character to integer (A -&gt; 0, B -&gt; 1, ...).
   * @param chr to be parsed
   * @return parsed integer
   */
  public static int character2int(char chr) {
    //System.out.println(chr + " -> " + (chr - 65));
    return chr - 65;
  }
  /**
   * Converts the specified integer to character (0 -&gt; A, 1 -&gt; B, ...).
   * @param i to be converted
   * @return converted character
   */
  public static char int2character(int i) {
    //System.out.println(i + " -> " + (i + 65));
    return (char)(i + 65);
  }
  /**
   * Returns the string representation of the specified board array.
   * @param board to be printed.
   * @return string representation of the board.
   */
  public static String print(int[][] board) {
    StringBuffer sb = new StringBuffer();
    sb.append("  ");
    for (int i = 0; i < 9; i++) {
      sb.append(i + " ");
    }
    sb.append("\n");
    sb.append("  ");
    for (int i = 0; i < 9; i++) {
      sb.append("--");
    }
    sb.append("\n");
    for (int i = 0; i < board.length; i++) {
      if (i % 2 == 0) sb.append((char)((i/2)+65) + "|");
      else sb.append(" |");
      for (int j = 0; j < board[i].length; j++) {
        sb.append(TILES.get(board[i][j]));
      }
      if (i % 2 == 0) sb.append("|" + (char)((i/2)+65) + "\n");
      else sb.append("|\n");
    }
    sb.append("  ");
    for (int i = 0; i < 9; i++) {
      sb.append("--");
    }
    sb.append("\n");
    sb.append("  ");
    for (int i = 0; i < 9; i++) {
      sb.append(i + " ");
    }
    sb.append("\n");
    return sb.toString();
  }
  
  //public static final String TEST_STATE = "d3f3e6 a1a7 a4 i4 7 8 0";
  
  private final QuoridorPlayer[] players = new QuoridorPlayer[2];
  // for avoiding position hacking
  private final int[][] playerPlaces = new int[players.length][2];
  private final List<WallObject> walls = new LinkedList<WallObject>();
  private final long[] remainingTimes = new long[players.length];
  private final int[] availableWalls = new int[players.length];
  private int currentPlayer;
  
  private final QuoridorCanvas canvas;
  private final long seed;
  private final PrintStream errStream;
  private long numiters = 0;
  private final boolean isReplay;
  
  public QuoridorGame(PrintStream errStream, String[] params, boolean isReplay) throws Exception {
    
    if (params.length != 4) {
      errStream.println("required parameters for the game are:");
      errStream.println("\t- random seed      : controls the sequence of the random numbers");
      errStream.println("\t- timeout          : play-time for a player in milliseconds");
      errStream.println("\t- player classes...: list of player classes (exactly 2)");
      System.exit(1);
    }
    this.errStream = errStream;
    this.seed = Long.parseLong(params[0]);
    long timeout = Long.parseLong(params[1]) * 1000000;
    remainingTimes[0] = timeout;
    remainingTimes[1] = timeout;
    this.isReplay = isReplay;
    
    for (int i = 0; i < players.length; i++) {
      Random r = new Random(seed);
      Class<? extends QuoridorPlayer> clazz = Class.forName(DummyPlayer.class.getName()).asSubclass(QuoridorPlayer.class);
      if (isReplay) {
        errStream.println("Game is in replay mode, Player: " + i + " is the DummyPlayer, but was: " + params[i + 2]);
      } else {
        clazz = Class.forName(params[i + 2]).asSubclass(QuoridorPlayer.class);
      }
      Constructor<? extends QuoridorPlayer> constructor = clazz.getConstructor(int.class, int.class, int.class, Random.class);
      Pair<? extends QuoridorPlayer, Long> created = Engine.construct(timeout, constructor, i * (HEIGHT - 1), 4, i, r);
      players[i] = created.first;

      remainingTimes[i] = timeout - created.second;
      if (players[i] instanceof HumanPlayer) {
        remainingTimes[i] = Long.MAX_VALUE - created.second - 10;
      }
      
      availableWalls[i] = MAX_WALLS;

      // check color hacking
      if (players[i].color != i) {
        int color = players[i].color;
        remainingTimes[i] = 0;
        Field field = QuoridorPlayer.class.getDeclaredField("color");
        field.setAccessible(true);
        field.set(players[i], i);
        field.setAccessible(false);
        errStream.println("Illegal color (" + color + ") was set for player: " + players[i]);
      }
      // check position hacking
      if (players[i].i != i * (HEIGHT - 1) || players[i].j != 4) {
        int wrongi = players[i].i;
        int wrongj = players[i].j;
        remainingTimes[i] = 0;
        Field field = QuoridorPlayer.class.getDeclaredField("i");
        field.setAccessible(true);
        field.set(players[i], i * (HEIGHT - 1));
        field.setAccessible(false);
        field = QuoridorPlayer.class.getDeclaredField("j");
        field.setAccessible(true);
        field.set(players[i], 4);
        field.setAccessible(false);
        errStream.println("Illegal position (" + wrongi + " " + wrongj + ") was set for player: " + players[i]);
      }
      playerPlaces[i][0] = players[i].i;
      playerPlaces[i][1] = players[i].j;
    }
    currentPlayer = 0;
    
    canvas = new QuoridorCanvas(HEIGHT, WIDTH, this);
  }

  @Override
  public Frame getFrame() {
    String iconPath = "/game/engine/ui/resources/icon-game.png";
    return new GameFrame("Quoridor", iconPath, canvas);
  }

  @Override
  public List<GameObject> getGameObjects() {
    Image[] images = Utils.getImages("/game/quoridor/ui/resources/", new String[] {"black_pawn", "white_pawn", "cell-0000007f"});
    List<GameObject> objects = new LinkedList<GameObject>();
    for (QuoridorPlayer player : players) {
      objects.add(new GameObject(player.j * canvas.multiplier, player.i * canvas.multiplier, canvas.multiplier, canvas.multiplier, images[player.color]));
    }
    for (WallObject wall : walls) {
      objects.add(new GameObject((wall.horizontal ? wall.j : wall.j + 1) * canvas.multiplier - (wall.horizontal ? 0 : canvas.multiplier/8), (wall.horizontal ? wall.i + 1 : wall.i) * canvas.multiplier - (wall.horizontal ? canvas.multiplier/8 : 0), wall.horizontal ? canvas.multiplier * 2 : canvas.multiplier / 4, wall.horizontal ? canvas.multiplier / 4 : canvas.multiplier * 2, images[2]));
    }
    return objects;
  }

  @Override
  public QuoridorPlayer[] getPlayers() {
    return players;
  }

  @Override
  public QuoridorPlayer getNextPlayer() {
    return players[currentPlayer];
  }

  @Override
  public boolean isValid(QuoridorAction action) {
    if (isReplay && action instanceof ReplayAction) {
      action = ((ReplayAction) action).toAction();
    }
    if (playerPlaces[currentPlayer][0] != players[currentPlayer].i || playerPlaces[currentPlayer][1] != players[currentPlayer].j) {
      errStream.println("COORDINATE HACKING OF PLAYER: " + players[currentPlayer] + " (" + playerPlaces[currentPlayer][0] + "," + playerPlaces[currentPlayer][1] + ")");
      return false;
    } else if (action instanceof MoveAction) {
      MoveAction a = (MoveAction) action;
      return a.from_i == playerPlaces[currentPlayer][0] && a.from_j == playerPlaces[currentPlayer][1] && checkCandidateMove(new PlaceObject(a.from_i, a.from_j), new PlaceObject(a.to_i, a.to_j), walls, players);
    } else if (action instanceof WallAction) {
      WallAction a = (WallAction) action;
      return availableWalls[currentPlayer] > 0 && checkWall(new WallObject(a.i, a.j, a.horizontal), walls, players);
    } else if (action == null) {
      // JUST FOR TESTING
      return false;
    }
    return false;
  }

  @Override
  public void setAction(QuoridorPlayer player, QuoridorAction action, long time) {
    if (isReplay && action instanceof ReplayAction) {
      action = ((ReplayAction) action).toAction();
      System.out.println("SET: " + action);
    }
    if (player.color != currentPlayer) {
      errStream.println("Something went wrong: player color sould be " + currentPlayer + " instead of " + player.color + "(" + player + ")");
      remainingTimes[currentPlayer] = -1;
      return;
    }
    if (!isValid(action)) {
      remainingTimes[player.color] = -1;
      errStream.println("INVALID ACTION: " + action + " OF PLAYER: " + player);
      return;
    }
    remainingTimes[player.color] -= time;
    if (action instanceof WallAction) {
      WallAction a = (WallAction) action;
      walls.add(new WallObject(a.i, a.j, a.horizontal));
      availableWalls[player.color] -= 1;
    } else if (action instanceof MoveAction) {
      MoveAction a = (MoveAction) action;
      player.i = a.to_i;
      player.j = a.to_j;
      playerPlaces[player.color][0] = a.to_i;
      playerPlaces[player.color][1] = a.to_j;
    }
    currentPlayer = (currentPlayer + 1) % players.length;
    numiters ++;
  }

  @Override
  public long getRemainingTime(QuoridorPlayer player) {
    return remainingTimes[player.color];
  }

  @Override
  public boolean isFinished() {
    return MAX_ITER <= numiters || remainingTimes[0] <= 0 || remainingTimes[1] <= 0 || isFinished(players);
  }

  @Override
  public double getScore(QuoridorPlayer player) {
    return (player.i == (1 - player.color) * (HEIGHT-1)) || remainingTimes[1-player.color] <= 0 ? 1 : 0;
  }

  @Override
  public Class<? extends Action> getActionClass() {
    return ReplayAction.class;
  }
  
  @Override
  public String toString() {
    return print(getBoard(walls, players)) + "\n" + getState(walls, players, availableWalls, currentPlayer) + "\nITER: " + numiters;
  }
}
