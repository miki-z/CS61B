package creatures;

import huglife.Direction;
import huglife.Action;
import huglife.Occupant;
import huglife.Empty;
import org.junit.Test;

import java.awt.Color;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class TestClorus {

    @Test
    public void testBasics() {
        Clorus c = new Clorus();
        assertEquals("clorus", c.name());
        assertEquals(2, c.energy(), 0.01);
        assertEquals(new Color(34, 0, 231), c.color());
        c.move();
        assertEquals(1.97, c.energy(), 0.01);
        c.move();
        assertEquals(1.94, c.energy(), 0.01);
        c.stay();
        assertEquals(1.93, c.energy(), 0.01);
        c.stay();
        assertEquals(1.92, c.energy(), 0.01);
    }

    @Test
    public void testAttack() {
        Clorus c = new Clorus();
        HashMap<Direction, Occupant> surrounded = new HashMap<Direction, Occupant>();
        surrounded.put(Direction.TOP, new Plip(1.5));
        surrounded.put(Direction.BOTTOM, new Empty());
        surrounded.put(Direction.LEFT, new Empty());
        surrounded.put(Direction.RIGHT, new Empty());

        assertEquals(2.5, c.energy(), 0.01);

    }

    @Test
    public void testReplicate() {
        Clorus c = new Clorus(2);
        Clorus newC = c.replicate();
        assertNotEquals(c, newC);
        assertEquals(1.00, c.energy(), 0.01);
        assertEquals(1.00, newC.energy(), 0.01);
        Clorus newP2 = c.replicate();
        assertEquals(0.50, c.energy(), 0.01);
        assertEquals(0.50, newP2.energy(), 0.01);
    }

    @Test
    public void testChoose() {
        Clorus c = new Clorus();
        HashMap<Direction, Occupant> surrounded = new HashMap<Direction, Occupant>();
        surrounded.put(Direction.TOP, new Plip());
        surrounded.put(Direction.BOTTOM, new Plip());
        surrounded.put(Direction.LEFT, new Plip());
        surrounded.put(Direction.RIGHT, new Plip());

        //You can create new empties with new Empty();
        //Despite what the spec says, you cannot test for Cloruses nearby yet.
        //Sorry!

        Action actual1 = c.chooseAction(surrounded);
        Action expected1 = new Action(Action.ActionType.STAY);

        assertEquals(expected1, actual1);

        surrounded.put(Direction.TOP, new Plip());
        surrounded.put(Direction.BOTTOM, new Empty());
        surrounded.put(Direction.LEFT, new Empty());
        surrounded.put(Direction.RIGHT, new Empty());

        Action actual2 = c.chooseAction(surrounded);
        Action expected2 = new Action(Action.ActionType.ATTACK, Direction.TOP);

        assertEquals(expected2, actual2);

//        surrounded.put(Direction.TOP, new Impassible());
//        surrounded.put(Direction.BOTTOM, new Impassible());
//        surrounded.put(Direction.LEFT, new Impassible());
//        surrounded.put(Direction.RIGHT, new Impassible());
//
//        Action actual3 = c.chooseAction(surrounded);
//        Action expected3 = new Action(Action.ActionType.REPLICATE);
//
//        assertEquals(expected3, actual3);



    }
}
