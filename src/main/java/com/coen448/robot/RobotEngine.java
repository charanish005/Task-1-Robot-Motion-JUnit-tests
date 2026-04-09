package com.coen448.robot;

import java.util.ArrayList;
import java.util.List;

public class RobotEngine {

    private int[][] floor = new int[0][0];
    private int x = 0;
    private int y = 0;
    private Direction direction = Direction.NORTH;
    private PenState pen = PenState.UP;

    private final List<String> history = new ArrayList<>();

    public void initialize(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("n must be > 0");
        }
        floor = new int[n][n];
        x = 0;
        y = 0;
        direction = Direction.NORTH;
        pen = PenState.UP;
        history.add("I " + n);
    }

    public void penUp() {
        pen = PenState.UP;
        history.add("U");
    }

    public void penDown() {
        pen = PenState.DOWN;
        history.add("D");
    }

    public void turnLeft() {
        direction = direction.turnLeft();
        history.add("L");
    }

    public void turnRight() {
        direction = direction.turnRight();
        history.add("R");
    }

    public void move(int steps) {
        if (steps < 0) {
            throw new IllegalArgumentException("steps must be >= 0");
        }
        ensureInitialized();
        history.add("M " + steps);

        for (int i = 0; i < steps; i++) {
            int nextX = x;
            int nextY = y;

            switch (direction) {
                case NORTH -> nextY = y + 1;  // Fix: NORTH increments y (was y - 1)
                case SOUTH -> nextY = y - 1;  // Fix: SOUTH decrements y (was y + 1)
                case EAST -> nextX = x + 1;
                case WEST -> nextX = x - 1;
            }

            if (!inBounds(nextX, nextY)) {
                break;
            }

            if (pen == PenState.DOWN) {
                floor[y][x] = 1;
            }

            x = nextX;
            y = nextY;

            if (pen == PenState.DOWN) {
                floor[y][x] = 1;
            }
        }
    }

    public String statusString() {
        return "Position: " + x + ", " + y +
                " - Pen: " + pen.name().toLowerCase() +
                " - Facing: " + direction.name().toLowerCase();
    }

    public String renderFloorWithIndices() {
        ensureInitialized();
        int n = floor.length;
        StringBuilder sb = new StringBuilder();

        sb.append("  ");
        for (int c = 0; c < n; c++) {
            sb.append(c).append(" ");
        }
        sb.append("\n");

        for (int r = 0; r < n; r++) {
            sb.append(r).append(" ");
            for (int c = 0; c < n; c++) {
                sb.append(floor[r][c] == 1 ? "* " : "  ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public void replayHistory() {
        RobotEngine fresh = new RobotEngine();
        for (String cmd : history) {
            fresh.executeCommandInternal(cmd);
        }
        this.floor = fresh.floor;
        this.x = fresh.x;
        this.y = fresh.y;
        this.direction = fresh.direction;
        this.pen = fresh.pen;
    }

    public String executeCommand(String input) {
        String line = input.trim().toUpperCase();

        if (line.equals("U")) {
            penUp();
            return "";
        }
        if (line.equals("D")) {
            penDown();
            return "";
        }
        if (line.equals("L")) {
            turnLeft();
            return "";
        }
        if (line.equals("R")) {
            turnRight();
            return "";
        }
        if (line.equals("C")) {
            history.add("C");
            return statusString();
        }
        if (line.equals("P")) {
            history.add("P");
            return renderFloorWithIndices();
        }
        if (line.equals("H")) {
            replayHistory();
            return "Replayed history.";
        }

        if (line.startsWith("I")) {
            int n = Integer.parseInt(line.substring(1).trim());
            initialize(n);
            return "Initialized floor size " + n;
        }

        if (line.startsWith("M")) {
            int s = Integer.parseInt(line.substring(1).trim());
            move(s);
            return "";
        }

        throw new IllegalArgumentException("Unknown command");
    }

    private void executeCommandInternal(String input) {
        String line = input.trim().toUpperCase();

        if (line.equals("U"))
            pen = PenState.UP;
        else if (line.equals("D"))
            pen = PenState.DOWN;
        else if (line.equals("L"))
            direction = direction.turnLeft();
        else if (line.equals("R"))
            direction = direction.turnRight();
        else if (line.startsWith("I")) {
            int n = Integer.parseInt(line.substring(1).trim());
            floor = new int[n][n];
            x = 0;
            y = 0;
            direction = Direction.NORTH;
            pen = PenState.UP;
        } else if (line.startsWith("M")) {
            int s = Integer.parseInt(line.substring(1).trim());
            internalMove(s);
        }
    }

    private void internalMove(int steps) {
        ensureInitialized();
        for (int i = 0; i < steps; i++) {
            int nextX = x, nextY = y;
            switch (direction) {
                case NORTH -> nextY = y + 1;
                case SOUTH -> nextY = y - 1;
                case EAST -> nextX = x + 1;
                case WEST -> nextX = x - 1;
            }
            if (!inBounds(nextX, nextY))
                break;
            if (pen == PenState.DOWN)
                floor[y][x] = 1;
            x = nextX;
            y = nextY;
            if (pen == PenState.DOWN)
                floor[y][x] = 1;
        }
    }

    private void ensureInitialized() {
        if (floor.length == 0) {
            throw new IllegalStateException("Floor not initialized");
        }
    }

    private boolean inBounds(int px, int py) {
        int n = floor.length;
        return px >= 0 && px < n && py >= 0 && py < n;
    }

    public int size() {
        return floor.length;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Direction getDirection() {
        return direction;
    }

    public PenState getPen() {
        return pen;
    }

    public int getCell(int r, int c) {
        return floor[r][c];
    }
}
