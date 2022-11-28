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

public class Agent extends QuoridorPlayer {

    private class Nodes {
        private PlaceObject place;
        private Nodes parent;
        private int gcost;
        private int hcost;

        /**
         * Egy node elem konstruktora.
         * 
         * @param place  a mezo helye a tablan
         * @param parent a mezo amirol erre a mezore lepett a jatekos
         * @param g      a mezo G costja (tavolsag a kiinduloponttol)
         * @param h      a mezo H costja (tavolsag a celtol)
         */
        public Nodes(PlaceObject place, Nodes parent, int g, int h) {
            this.place = place;
            this.parent = parent;
            setGcost(g);
            setGcost(h);
        }

        /**
         * @return a mezo F costja amit a (G cost + H cost) határoz meg
         */
        public int getFcost() {
            return this.gcost + this.hcost;
        }

        /**
         * @param g az uj G cost
         */
        public void setGcost(int g) {
            this.gcost = g;
        }

        /**
         * @param h az uj H cost
         */
        public void setHcost(int h) {
            this.gcost = h;
        }
    }

    /**
     * falak listaja
     */
    private final List<WallObject> walls = new LinkedList<WallObject>();

    /**
     * jatekosok listaja
     */
    private final QuoridorPlayer[] players = new QuoridorPlayer[2];

    /**
     * cel mezok listaja
     */
    private final List<PlaceObject> targets = new LinkedList<PlaceObject>();

    /**
     * cel mezok azon listaja, mely azokat a celmezoket tartalmazza amihez van
     * hozzaferes
     */
    private List<PlaceObject> excluded_targets = new LinkedList<PlaceObject>();

    /**
     * alap cel mezo ha valamiert kellene
     */
    private PlaceObject targetPlace;

    /**
     * @param i      jatekos kezdoallapota az i koordinatan
     * @param j      jatekos kezdoallapota az j koordinatan
     * @param color  jatekos szine
     * @param random random num ha kennene
     */
    public Agent(int i, int j, int color, Random random) {

        super(i, j, color, random);
        players[color] = this;
        players[1 - color] = new DummyPlayer((1 - color) * (QuoridorGame.HEIGHT - 1), j, 1 - color, null);

        /**
         * cel mezok listaja aszerint hogy melyik oldalon kezd a jatekos
         */
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

    /**
     * @param a kezdoallapot
     * @param b celallapot
     * @return az a-b kozotti tavolsag (1 lepes = 1 tavolsag)
     */
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

    /**
     * @param start kezdoallapot
     * @param fin   lehetseges vegallapotok listaja
     * @return azon mezo koordinatai, melyre a kovetkezo lepesre lep a jatekos
     */
    public PlaceObject pathFind(PlaceObject start, List<PlaceObject> fin) {

        /**
         * A* algoritmus ket listaja
         */
        List<Nodes> open = new LinkedList<Nodes>();
        List<Nodes> closed = new LinkedList<Nodes>();

        /**
         * A kovetkezo mezo amire a jatekos lepni fog
         */
        Nodes next = null;

        /**
         * Kivalasztja a celmezok listajabol azt, amelyik eppen a legkozelebb van a
         * jatekoshoz
         * ezentul ez lesz a fo cel
         */
        int distmax = 1000;
        PlaceObject mainTarget = targetPlace;
        for (PlaceObject goal : fin) {
            if (getDistance(start, goal) < distmax) {
                distmax = getDistance(start, goal);
                mainTarget = goal;
            }
        }

        /**
         * A* algoritmus kezdoallapota
         */
        Nodes startnode = new Nodes(start, null, 0, getDistance(start, mainTarget));
        open.add(startnode);

        /**
         * A* algoritmus fo ciklusa
         */
        while (!open.isEmpty()) {

            /**
             * Kivalasztja a legidealisabb kovetkezo mezot
             */
            Nodes current = open.get(0);
            for (Nodes node : open) {
                if ((node.getFcost() < current.getFcost()) || (node.getFcost() == current.getFcost() && node.hcost < current.hcost)) {
                    current = node;
                }
            }
            open.remove(current);
            closed.add(current);

            /**
             * Ha a kovetkezo idealis mezo a celmezo, akkor meghivja a pathSteps fuggvenyt
             * es visszakeresi a utvonalat amin eljutott idaig
             * majd visszater az elso lepes koordianataival, amit a cel fele megtett
             */
            if (current.place.equals(mainTarget)) {
                next = pathSteps(startnode, current);
                return next.place;
            }

            /**
             * Vegigmegy az kovetkezo idealis mezo szomszedjain
             * ha benne volt a zart halmazban tovabblep rajta
             * ha nincs benne az ures halmazban hozzadja a kiszamolt costokkal egyutt
             * ha benne van az ures halmazban de most jobb G cost-tal rendelkezik akkor
             * atirja a parametereit az uj utvonal szerint
             */
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
        /**
         * Ha nem tudott eljutni a megadott celhoz,
         * akkor ezt a celt kitorli a celok listajabol es ujrafuttatja az algoritmus a
         * kovetkezo legkozelebbi celhoz
         */
        if (next == null) {
            excluded_targets = targets;
            excluded_targets.remove(mainTarget);
            return pathFind(start, excluded_targets);
        }
        return null;
    }

    /**
     * @param start  utvonal kezdeti mezoje
     * @param finish utvonal cel mezoje
     * @return a kovetkezo mezot amire lepni kell
     */
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