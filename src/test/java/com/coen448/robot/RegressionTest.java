package com.coen448.robot;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Regression Test Suite — Task 4
 * Dev Team: Charanish Miriyala & Sachin Raj Rajesh (Dev 1 / G4)
 * COEN 448/6761 – Software Testing and Validation, Winter 2026
 *
 * These regression tests were written in response to the QA report submitted
 * by Dev 2 (Deepak Sunil Chavan). The QA team identified two failing data flow
 * tests caused by an inverted NORTH/SOUTH direction in RobotEngine.move():
 *
 *   BUG: NORTH used  nextY = y - 1  (should be y + 1)
 *        SOUTH used  nextY = y + 1  (should be y - 1)
 *
 * FIX:  NORTH → nextY = y + 1
 *       SOUTH → nextY = y - 1
 *
 * This file verifies:
 *   1. The bug is fixed (floor marks correctly with pen down)
 *   2. No regressions in existing behaviour
 *   3. All QA-reported failing tests now pass
 */
@DisplayName("Regression Tests – Task 4 (Response to Dev 2 QA Report)")
public class RegressionTest {

    private RobotEngine engine;

    @BeforeEach
    void setUp() {
        engine = new RobotEngine();
        engine.initialize(10);
    }

    // ── BUG FIX VERIFICATION ─────────────────────────────────────────────────

    @Test
    @DisplayName("REG-01: [BUG FIX] Pen DOWN – floor cells marked correctly when moving NORTH")
    void testMovePenDownFloorUpdated_North() {
        // QA test 'testMovePenDownFloorUpdated' was failing because NORTH moved
        // in the wrong direction, so floor[y][x] was marked at the wrong coordinates.
        engine.penDown();
        engine.move(3);  // moves NORTH: y goes 0→1→2→3

        // Starting cell and all traversed cells should be marked
        assertEquals(1, engine.getCell(0, 0), "Cell (0,0) should be marked");
        assertEquals(1, engine.getCell(1, 0), "Cell (1,0) should be marked");
        assertEquals(1, engine.getCell(2, 0), "Cell (2,0) should be marked");
        assertEquals(1, engine.getCell(3, 0), "Cell (3,0) — final cell — should be marked");
        assertEquals(3, engine.getY(), "Robot should be at y=3 after moving NORTH 3");
        assertEquals(0, engine.getX(), "Robot x should remain 0");
    }

    @Test
    @DisplayName("REG-02: [BUG FIX] Pen UP – floor cells NOT marked when moving NORTH")
    void testMovePenUpFloorUnchanged_North() {
        // QA test 'testMovePenUpFloorUnchanged' was also failing.
        // With pen UP, no floor cells should be marked regardless of movement.
        // pen is UP by default after initialize()
        engine.move(3);  // moves NORTH, pen up

        assertEquals(0, engine.getCell(0, 0), "Cell (0,0) must NOT be marked — pen was up");
        assertEquals(0, engine.getCell(1, 0), "Cell (1,0) must NOT be marked — pen was up");
        assertEquals(0, engine.getCell(2, 0), "Cell (2,0) must NOT be marked — pen was up");
        assertEquals(0, engine.getCell(3, 0), "Cell (3,0) must NOT be marked — pen was up");
        assertEquals(3, engine.getY(), "Robot should still have moved to y=3");
    }

    @Test
    @DisplayName("REG-03: [BUG FIX] NORTH direction moves y in positive direction")
    void testNorthMovesYPositive() {
        int yBefore = engine.getY();  // 0
        engine.move(4);
        assertTrue(engine.getY() > yBefore,
                "NORTH must increase y — fix verified (old bug decreased y)");
        assertEquals(4, engine.getY());
    }

    @Test
    @DisplayName("REG-04: [BUG FIX] SOUTH direction moves y in negative direction")
    void testSouthMovesYNegative() {
        engine.move(5);           // go to y=5 facing NORTH first
        engine.turnRight();
        engine.turnRight();       // now facing SOUTH
        int yBefore = engine.getY();  // 5
        engine.move(3);
        assertTrue(engine.getY() < yBefore,
                "SOUTH must decrease y — fix verified (old bug increased y)");
        assertEquals(2, engine.getY());
    }

    @Test
    @DisplayName("REG-05: [BUG FIX] Pen DOWN + SOUTH — floor marked correctly going south")
    void testMovePenDownFloorUpdated_South() {
        engine.move(5);           // go north to y=5
        engine.turnRight();
        engine.turnRight();       // face SOUTH
        engine.penDown();
        engine.move(3);           // move south: y goes 5→4→3→2

        assertEquals(1, engine.getCell(5, 0), "Cell (5,0) should be marked");
        assertEquals(1, engine.getCell(4, 0), "Cell (4,0) should be marked");
        assertEquals(1, engine.getCell(3, 0), "Cell (3,0) should be marked");
        assertEquals(1, engine.getCell(2, 0), "Cell (2,0) — final — should be marked");
        assertEquals(2, engine.getY(), "Robot should be at y=2 after moving SOUTH 3");
    }

    // ── EXISTING BEHAVIOUR — NO REGRESSIONS ──────────────────────────────────

    @Test
    @DisplayName("REG-06: EAST movement still works correctly after fix")
    void testEastMovementUnaffected() {
        engine.penDown();
        engine.turnRight();   // EAST
        engine.move(3);

        assertEquals(3, engine.getX(), "x should be 3 after M 3 facing EAST");
        assertEquals(0, engine.getY(), "y should be unchanged moving EAST");
        assertEquals(1, engine.getCell(0, 0), "Cell (0,0) marked");
        assertEquals(1, engine.getCell(0, 1), "Cell (0,1) marked");
        assertEquals(1, engine.getCell(0, 2), "Cell (0,2) marked");
        assertEquals(1, engine.getCell(0, 3), "Cell (0,3) marked");
    }

    @Test
    @DisplayName("REG-07: WEST movement still works correctly after fix")
    void testWestMovementUnaffected() {
        engine.turnRight();
        engine.move(4);       // go east to x=4
        engine.turnRight();
        engine.turnRight();   // now WEST
        engine.penDown();
        engine.move(2);

        assertEquals(2, engine.getX(), "x should be 2 after moving WEST 2 from x=4");
        assertEquals(0, engine.getY(), "y should be unchanged moving WEST");
    }

    @Test
    @DisplayName("REG-08: Boundary stop still works after fix")
    void testBoundaryStopsMovementAfterFix() {
        engine.initialize(3);
        engine.penDown();
        engine.move(10);   // try to go north beyond floor boundary

        // Should stop at y=2 (last valid row on 3x3 floor)
        assertEquals(2, engine.getY(), "Robot should stop at boundary y=2");
        assertEquals(0, engine.getX());
    }

    @Test
    @DisplayName("REG-09: Initialize still resets state correctly after fix")
    void testInitializeStillWorks() {
        engine.penDown();
        engine.move(3);
        engine.initialize(5);   // re-initialize

        assertEquals(0, engine.getX());
        assertEquals(0, engine.getY());
        assertEquals(PenState.UP,      engine.getPen());
        assertEquals(Direction.NORTH,  engine.getDirection());
        assertEquals(5, engine.size());
    }

    @Test
    @DisplayName("REG-10: Pen state transitions still work after fix")
    void testPenStateTransitionsStillWork() {
        engine.penDown();
        assertEquals(PenState.DOWN, engine.getPen());
        engine.penUp();
        assertEquals(PenState.UP, engine.getPen());
    }

    @Test
    @DisplayName("REG-11: Turn right/left still cycle directions correctly after fix")
    void testTurnCycleStillWorks() {
        engine.turnRight();
        assertEquals(Direction.EAST,  engine.getDirection());
        engine.turnRight();
        assertEquals(Direction.SOUTH, engine.getDirection());
        engine.turnRight();
        assertEquals(Direction.WEST,  engine.getDirection());
        engine.turnRight();
        assertEquals(Direction.NORTH, engine.getDirection());
    }

    @Test
    @DisplayName("REG-12: History replay still produces correct state after fix")
    void testHistoryReplayStillWorks() {
        engine.executeCommand("D");
        engine.executeCommand("M 3");
        int xAfter = engine.getX();
        int yAfter = engine.getY();

        engine.executeCommand("H");

        assertEquals(xAfter, engine.getX(), "X should match after replay");
        assertEquals(yAfter, engine.getY(), "Y should match after replay");
    }

    @Test
    @DisplayName("REG-13: executeCommand 'M' and 'D' flow works end-to-end after fix")
    void testExecuteCommandEndToEnd() {
        engine.executeCommand("D");
        engine.executeCommand("M 2");

        assertEquals(2, engine.getY(), "Should be at y=2 after D + M 2 facing NORTH");
        assertEquals(1, engine.getCell(0, 0), "Cell (0,0) must be marked");
        assertEquals(1, engine.getCell(1, 0), "Cell (1,0) must be marked");
        assertEquals(1, engine.getCell(2, 0), "Cell (2,0) must be marked");
    }

    @Test
    @DisplayName("REG-14: Negative steps still throw IllegalArgumentException after fix")
    void testNegativeStepsStillThrows() {
        assertThrows(IllegalArgumentException.class, () -> engine.move(-1),
                "Negative steps must still throw IllegalArgumentException");
    }

    @Test
    @DisplayName("REG-15: move() before initialize still throws IllegalStateException after fix")
    void testMoveBeforeInitStillThrows() {
        RobotEngine fresh = new RobotEngine();
        assertThrows(IllegalStateException.class, () -> fresh.move(1),
                "move() before initialize must still throw IllegalStateException");
    }
}
