package com.coen448.robot;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RobotIntegrationTest {

    @Test
    void integrationTest_threeCycles_mixedPen_assertFinalPrintedShape() {
        RobotEngine e = new RobotEngine();

        // Cycle 1 (pen DOWN): move NORTH 3
        e.executeCommand("I 10");
        e.executeCommand("D");
        e.executeCommand("M 3");

        // Cycle 2 (pen UP): turn EAST, move 4 (no drawing)
        e.executeCommand("U");
        e.executeCommand("R");
        e.executeCommand("M 4");

        // Cycle 3 (pen DOWN): turn SOUTH, move 2 (draw vertical segment)
        e.executeCommand("D");
        e.executeCommand("R");
        e.executeCommand("M 2");

        // Print and validate expected shape
        String printed = e.executeCommand("P");

        // Expected marked cells:
        // Cycle 1 draws from (0,0) to (0,3)
        // Cycle 2 pen up (no marks) moves to (4,3)
        // Cycle 3 draws from (4,3) to (4,1)
        //
        // Marked: (0,0),(0,1),(0,2),(0,3),(4,3),(4,2),(4,1)

        assertTrue(printed.contains("0 1 2 3 4 5 6 7 8 9"));

        assertTrue(rowHasStarAt(printed, 0, 0));

        assertTrue(rowHasStarAt(printed, 1, 0));
        assertTrue(rowHasStarAt(printed, 1, 4));

        assertTrue(rowHasStarAt(printed, 2, 0));
        assertTrue(rowHasStarAt(printed, 2, 4));

        assertTrue(rowHasStarAt(printed, 3, 0));
        assertTrue(rowHasStarAt(printed, 3, 4));

        assertFalse(rowHasStarAt(printed, 3, 1));
    }

    private boolean rowHasStarAt(String printed, int rowIndex, int colIndex) {
        String[] lines = printed.split("\\R");
        String prefix = rowIndex + " ";
        for (String line : lines) {
            if (line.startsWith(prefix)) {
                int start = prefix.length() + (colIndex * 2);
                if (start + 1 >= line.length())
                    return false;
                return line.substring(start, start + 2).equals("* ");
            }
        }
        return false;
    }
}