package creatures;

import huglife.Creature;
import huglife.Direction;
import huglife.Action;
import huglife.Occupant;
import huglife.HugLifeUtils;

import java.awt.Color;
import java.util.List;
import java.util.Map;

public class Clorus extends Creature {
    /** red color. */
    private int r = 34;
    /** green color. */
    private int g = 0;
    /** blue color. */
    private int b = 231;
    /** fraction of energy to lose when moving. */
    private double movEnergyLoss = 0.03;
    /** fraction of energy to retain when staying. */
    private double stayEnergyGained = 0.01;
    /** fraction of energy to retain when replicating. */
    private double repEnergyRetained = 0.5;
    /** fraction of energy to bestow upon offspring. */
    private double repEnergyGiven = 0.5;

    public Clorus(double e) {
        super("clorus");
        energy = e;
    }

    public Clorus() {
        this(1);
    }

    public Color color() {
        return color(r, g, b);
    }

    public void move() {
        energy -= movEnergyLoss;
    }

    public void stay() {
        energy += stayEnergyGained;
    }

    public Clorus replicate() {
        double babyEnergy = energy * repEnergyGiven;
        energy = energy * repEnergyRetained;
        return new Clorus(babyEnergy);
    }

    public void attack(Creature c) {
        energy += c.energy();
    }

    public Action chooseAction(Map<Direction, Occupant> neighbors) {
        List<Direction> empties = getNeighborsOfType(neighbors, "empty");
        if (empties.size() == 0) {
            return new Action(Action.ActionType.STAY);
        }
        List<Direction> plips = getNeighborsOfType(neighbors, "plip");
        if (plips.size() >= 1) {
            Direction moveDir = HugLifeUtils.randomEntry(plips);
            return new Action(Action.ActionType.ATTACK, moveDir);
        }
        Direction moveDir = HugLifeUtils.randomEntry(empties);
        if (energy >= 1) {
            return new Action(Action.ActionType.REPLICATE, moveDir);
        }

        return new Action(Action.ActionType.MOVE, moveDir);
    }
}
