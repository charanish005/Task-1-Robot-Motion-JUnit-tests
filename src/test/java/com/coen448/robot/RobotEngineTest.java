package com.coen448.robot;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RobotEngineTest {

    @Test
    void testInitializeResetsState() {
        RobotEngine e = new RobotEngine();
        e.initialize(5);

        assertEquals(5, e.size());
        assertEquals(0, e.getX());
        assertEquals(0, e.getY());
        assertEquals(Direction.NORTH, e.getDirection());
        assertEquals(PenState.UP, e.getPen());
    }

    @Test
    void testPenStates() {
        RobotEngine e = new RobotEngine();
        e.initialize(5);

        e.penDown();
        assertEquals(PenState.DOWN, e.getPen());

        e.penUp();
        assertEquals(PenState.UP, e.getPen());
    }

    @Test
    void testTurnRightLeft() {
        RobotEngine e = new RobotEngine();
        e.initialize(5);

        e.turnRight();
        assertEquals(Direction.EAST, e.getDirection());

        e.turnLeft();
        assertEquals(Direction.NORTH, e.getDirection());

        e.turnLeft();
        assertEquals(Direction.WEST, e.getDirection());
    }

    @Test
    void testDrawEast() {
        RobotEngine e = new RobotEngine();
        e.initialize(5);

        e.penDown();
        e.turnRight(); // EAST
        e.move(3);

        assertEquals(1, e.getCell(0, 0));
        assertEquals(1, e.getCell(0, 1));
        assertEquals(1, e.getCell(0, 2));
        assertEquals(1, e.getCell(0, 3));

        assertEquals(3, e.getX());
        assertEquals(0, e.getY());
    }

    @Test
    void testBoundaryStopsEast() {
        RobotEngine e = new RobotEngine();
        e.initialize(3);

        e.penDown();
        e.turnRight(); // EAST
        e.move(10); // stop at x=2

        assertEquals(2, e.getX());
        assertEquals(0, e.getY());
    }

    @Test
    void testMoveNorthIncreasesY() {
        RobotEngine e = new RobotEngine();
        e.initialize(10);

        e.penDown();
        e.move(4);

        assertEquals(0, e.getX());
        assertEquals(4, e.getY());
    }

    @Test
    void testMoveSouthDecreasesY() {
        RobotEngine e = new RobotEngine();
        e.initialize(10);

        e.penDown();
        e.move(4); // y = 4
        e.turnRight(); // EAST
        e.turnRight(); // SOUTH
        e.move(2); // y = 2

        assertEquals(0, e.getX());
        assertEquals(2, e.getY());
    }

    @Test
    void testBoundaryStopNorth() {
        RobotEngine e = new RobotEngine();
        e.initialize(3);

        e.penDown();
        e.move(10); // stop at y=2

        assertEquals(2, e.getY());
    }

    @Test
    void testPenUpDoesNotDraw() {
        RobotEngine e = new RobotEngine();
        e.initialize(5);

        e.penUp();
        e.turnRight();
        e.move(2);

        assertEquals(0, e.getCell(0, 0));
        assertEquals(0, e.getCell(0, 1));
        assertEquals(0, e.getCell(0, 2));
    }

    @Test
    void testStatusStringFormat() {
        RobotEngine e = new RobotEngine();
        e.initialize(4);

        String s = e.statusString();
        assertTrue(s.contains("Position: 0, 0"));
        assertTrue(s.contains("Pen: up"));
        assertTrue(s.contains("Facing: north"));
    }

    @Test
    void testRenderFloorContainsStars() {
        RobotEngine e = new RobotEngine();
        e.initialize(5);

        e.penDown();
        e.turnRight();
        e.move(2);

        String out = e.renderFloorWithIndices();
        assertTrue(out.contains("*"));
    }

    @Test
    void testHistoryReplayReproducesState() {
        RobotEngine e = new RobotEngine();

        e.executeCommand("I 5");
        e.executeCommand("D");
        e.executeCommand("R");
        e.executeCommand("M 2");

        int xBefore = e.getX();
        int yBefore = e.getY();
        int cell00 = e.getCell(0, 0);

        e.executeCommand("H");

        assertEquals(xBefore, e.getX());
        assertEquals(yBefore, e.getY());
        assertEquals(cell00, e.getCell(0, 0));
    }

    @Test
    void testMoveBeforeInitializeThrows() {
        RobotEngine e = new RobotEngine();
        assertThrows(IllegalStateException.class, () -> e.move(1));
    }

    @Test
    void testInitializeInvalidSizeThrows() {
        RobotEngine e = new RobotEngine();
        assertThrows(IllegalArgumentException.class, () -> e.initialize(0));
    }

    @Test
    void testExecuteCommandUnknownThrows() {
        RobotEngine e = new RobotEngine();
        e.initialize(5);
        assertThrows(IllegalArgumentException.class, () -> e.executeCommand("Z"));
    }

    @Test
    void testExecuteCommandMissingMoveArgThrows() {
        RobotEngine e = new RobotEngine();
        e.initialize(5);
        assertThrows(IllegalArgumentException.class, () -> e.executeCommand("M"));
    }

    @Test
    void testExecuteCommandMissingInitArgThrows() {
        RobotEngine e = new RobotEngine();
        assertThrows(IllegalArgumentException.class, () -> e.executeCommand("I"));
    }

    @Test
    void testRenderFloorThrowsBeforeInit() {
        RobotEngine e = new RobotEngine();
        assertThrows(IllegalStateException.class, e::renderFloorWithIndices);
    }

    @Test
    void testMoveNegativeStepsThrows() {
        RobotEngine e = new RobotEngine();
        e.initialize(5);
        assertThrows(IllegalArgumentException.class, () -> e.move(-1));
    }

    @Test
    void testExecuteCommandEmptyReturnsEmpty() {
        RobotEngine e = new RobotEngine();
        assertEquals("", e.executeCommand(""));
        assertEquals("", e.executeCommand("   "));
    }

    @Test
    void testExecuteCommandCAndPWorkAfterInit() {
        RobotEngine e = new RobotEngine();
        e.executeCommand("I 5");

        String c = e.executeCommand("C");
        assertTrue(c.contains("Position: 0, 0"));
        assertTrue(c.contains("Facing: north"));

        String p = e.executeCommand("P");
        assertTrue(p.contains("0 1 2 3 4"));
    }

    @Test
    void testExecuteCommandQReturnsBye() {
        RobotEngine e = new RobotEngine();
        String out = e.executeCommand("Q");
        assertTrue(out.toLowerCase().contains("bye"));
    }

    @Test
    void testMultipleTurnsCoverAllDirections() {
        RobotEngine e = new RobotEngine();
        e.initialize(5);

        e.turnRight(); // EAST
        e.turnRight(); // SOUTH
        e.turnRight(); // WEST
        e.turnRight(); // NORTH
        assertEquals(Direction.NORTH, e.getDirection());

        e.turnLeft(); // WEST
        assertEquals(Direction.WEST, e.getDirection());
    }

    @Test
    void testMoveWestAndBoundary() {
        RobotEngine e = new RobotEngine();
        e.initialize(5);

        e.penDown();
        e.turnLeft(); // WEST
        e.move(3); // should stop at x=0 immediately (boundary)
        assertEquals(0, e.getX());
        assertEquals(0, e.getY());
    }
}