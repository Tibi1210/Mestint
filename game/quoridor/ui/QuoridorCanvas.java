package game.quoridor.ui;

import java.awt.Color;
import java.awt.Graphics;

import game.engine.ui.BoardGameCanvas;
import game.engine.ui.Drawable;
import game.quoridor.MoveAction;

public class QuoridorCanvas extends BoardGameCanvas {
  private static final long serialVersionUID = 7381574098044093609L;
  /** pixel width and height of a cell. */
  public static final int MULTIPLIER = 32;

  public QuoridorCanvas(int n, int m, Drawable game) {
    super(n, m, MULTIPLIER, game);
  }
  @Override
  public void paintObjects(Graphics graphics) {
    super.paintObjects(graphics);
    //graphics.setColor(Color.WHITE);
    //graphics.fillRect(lastx * multiplier + 1, lasty * multiplier + 1, multiplier - 1, multiplier - 1);
    if (lastx != -1 && lasty != -1) {
      graphics.setColor(Color.LIGHT_GRAY);
      if ((lastx + 1) * multiplier - x < multiplier/4) {
        graphics.fillRect((lastx + 1) * multiplier - multiplier/8, lasty * multiplier, multiplier/4, 2*multiplier);
      } else if ((lasty + 1) * multiplier - y < multiplier/4) {
        graphics.fillRect(lastx * multiplier, (lasty + 1) * multiplier - multiplier/8, 2*multiplier, multiplier/4);
      } else {
        //graphics.fillRect(lastx * multiplier + multiplier / 8, lasty * multiplier + multiplier / 8, multiplier - multiplier / 4, multiplier - multiplier / 4);
      }
    }
  }
  @Override
  public void setCoordinates() {
    QUEUE.add(new MoveAction(x, y, lastx, lasty));
  }
  
}
