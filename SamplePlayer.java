///Avocado,h053353@stud.u-szeged.hu
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import game.quoridor.MoveAction;
import game.quoridor.QuoridorGame;
import game.quoridor.QuoridorPlayer;
import game.quoridor.WallAction;
import game.quoridor.players.DummyPlayer;
import game.quoridor.utils.PlaceObject;
import game.quoridor.utils.QuoridorAction;
import game.quoridor.utils.WallObject;

public class SamplePlayer extends QuoridorPlayer {

    private class Nodes {
        private PlaceObject place;
        private Nodes parent;
        private int gcost;
        private int hcost;

        public Nodes(PlaceObject place, Nodes parent, int g, int h) {
            this.place = place;
            this.parent = parent;
            setGcost(g);
            setGcost(h);

        }

        public int getFcost() {
            return this.gcost + this.hcost;
        }

        public void setGcost(int g) {
            this.gcost = g;
        }

        public void setHcost(int g) {
            this.gcost = g;
        }
    }

    private final List<WallObject> walls = new LinkedList<WallObject>();

    private final QuoridorPlayer[] players = new QuoridorPlayer[2];
    private final List<PlaceObject> targets = new LinkedList<PlaceObject>();
    private List<PlaceObject> excluded_targets = new LinkedList<PlaceObject>();

    private PlaceObject targetPlace;

    public SamplePlayer(int i, int j, int color, Random random) {

        super(i, j, color, random);
        players[color] = this;
        players[1 - color] = new DummyPlayer((1 - color) * (QuoridorGame.HEIGHT - 1), j, 1 - color, null);

        if (toPlace().i == 0) {
            for (int n = 0; n < 9; n++) {
                targets.add(new PlaceObject(8, n));
            }
            this.targetPlace = new PlaceObject(8, 0);
        }
        if (toPlace().i == 8) {
            for (int n = 0; n < 9; n++) {
                targets.add(new PlaceObject(0, n));
            }
            this.targetPlace = new PlaceObject(0, 0);
        }
        this.excluded_targets = this.targets;
    }

    public int getDistance(PlaceObject a, PlaceObject b) {
        int horisontal = 0;
        int vertical = 0;

        if (a.i < b.i) {
            vertical = b.i - a.i;
        } else {
            vertical = a.i - b.i;
        }

        if (a.j < b.j) {
            vertical = b.j - a.j;
        } else {
            vertical = a.j - b.j;
        }

        return vertical + horisontal;
    }

    public PlaceObject pathFind(PlaceObject start, List<PlaceObject> fin) {
        List<Nodes> open = new LinkedList<Nodes>();
        List<Nodes> closed = new LinkedList<Nodes>();
        Nodes next = null;

        int distmax = 1000;
        PlaceObject mainTarget = targetPlace;
        for (PlaceObject goal : fin) {
            if (getDistance(start, goal) < distmax) {
                distmax = getDistance(start, goal);
                mainTarget = goal;
            }
        }

        Nodes startnode = new Nodes(start, null, 0, getDistance(start, mainTarget));
        open.add(startnode);

        while (!open.isEmpty()) {
            Nodes current = open.get(0);
            for (Nodes node : open) {
                if ((node.getFcost() < current.getFcost())
                        || (node.getFcost() == current.getFcost() && node.hcost < current.hcost)) {
                    current = node;
                }
            }
            open.remove(current);
            closed.add(current);

            if (current.place.equals(mainTarget)) {
                next = pathSteps(startnode, current);
                return next.place;
            }

            for (PlaceObject step : current.place.getNeighbors(walls, players)) {
                boolean inclosed = false;
                for (Nodes fail : closed) {
                    if (fail.place.i == step.i && fail.place.j == step.j) {
                        inclosed = true;
                    }
                }
                if (inclosed) {
                    continue;
                }

                int newGcost = current.gcost + getDistance(current.place, step);
                boolean inopen = false;
                int inopenwhere = 0;
                for (Nodes fail : open) {
                    if (fail.place.i == step.i && fail.place.j == step.j) {
                        inopen = true;
                    }
                    if (!inopen) {
                        inopenwhere++;
                    }
                }

                if (!inopen) {
                    open.add(new Nodes(step, current, newGcost, getDistance(step, mainTarget)));
                }
                if (inopen && newGcost < open.get(inopenwhere).gcost) {
                    open.get(inopenwhere).setGcost(newGcost);
                    open.get(inopenwhere).setHcost(getDistance(step, mainTarget));
                    open.get(inopenwhere).parent = current;
                }

            }

        }
        if (next == null) {
            excluded_targets = targets;
            excluded_targets.remove(mainTarget);
            return pathFind(start, excluded_targets);
        }
        return null;
    }

    public Nodes pathSteps(Nodes start, Nodes finish) {
        List<Nodes> steps = new LinkedList<Nodes>();
        Nodes current = finish;
        while (current != start) {
            steps.add(current);
            current = current.parent;
        }
        return steps.get(steps.size() - 1);
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

        PlaceObject local = toPlace();
        PlaceObject next = pathFind(local, targets);

        MoveAction action = null;
        action = new MoveAction(i, j, next.i, next.j);

        return action;
    }
}