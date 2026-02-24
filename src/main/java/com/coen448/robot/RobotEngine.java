package com.coen448.robot;

import java.util.ArrayList;
import java.util.List;

public class RobotEngine {

    private int[][] floor = new int[0][0];

    // x = column, y = row
    private int x = 0;
    private int y = 0;

    private Direction direction = Direction.NORTH;
    private PenState pen = PenState.UP;

    // Store normalized commands that change state (plus I/M/U/D/L/R)
    private final List<String> history = new ArrayList<>();

    // ----------------------------
    // Core robot operations
    // ----------------------------

    public void initialize(int n) {
        if (n <= 0)
            throw new IllegalArgumentException("Size must be > 0");

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

    // IMPORTANT: NORTH increases y (matches sample output: (0,0) -> (0,4) after M
    // 4)
    public void move(int steps) {
        if (steps < 0)
            throw new IllegalArgumentException("Steps must be >= 0");
        ensureInitialized();

        history.add("M " + steps);

        for (int i = 0; i < steps; i++) {
            int nextX = x;
            int nextY = y;

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
        for (int c = 0; c < n; c++)
            sb.append(c).append(" ");
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
            fresh.applyCommandNoRecord(cmd);
        }
        this.floor = fresh.floor;
        this.x = fresh.x;
        this.y = fresh.y;
        this.direction = fresh.direction;
        this.pen = fresh.pen;
    }

    // ----------------------------
    // CLI command execution
    // ----------------------------

    public String executeCommand(String input) {
        String raw = input == null ? "" : input.trim();
        if (raw.isEmpty())
            return "";

        String upper = raw.toUpperCase();

        if (upper.equals("U")) {
            penUp();
            return "";
        }
        if (upper.equals("D")) {
            penDown();
            return "";
        }
        if (upper.equals("L")) {
            turnLeft();
            return "";
        }
        if (upper.equals("R")) {
            turnRight();
            return "";
        }

        if (upper.equals("C"))
            return statusString();
        if (upper.equals("P"))
            return renderFloorWithIndices();

        if (upper.equals("H")) {
            replayHistory();
            return "Replayed history.";
        }

        if (upper.equals("Q")) {
            return "Bye!";
        }

        if (upper.startsWith("I")) {
            String rest = raw.substring(1).trim();
            if (rest.isEmpty())
                throw new IllegalArgumentException("Missing size after I");
            int n = Integer.parseInt(rest);
            initialize(n);
            return "Initialized floor size " + n;
        }

        if (upper.startsWith("M")) {
            String rest = raw.substring(1).trim();
            if (rest.isEmpty())
                throw new IllegalArgumentException("Missing steps after M");
            int s = Integer.parseInt(rest);
            move(s);
            return "";
        }

        throw new IllegalArgumentException("Unknown command: " + input);
    }

    // ----------------------------
    // Internal replay methods
    // ----------------------------

    private void applyCommandNoRecord(String cmd) {
        String raw = cmd == null ? "" : cmd.trim();
        if (raw.isEmpty())
            return;

        String upper = raw.toUpperCase();

        if (upper.equals("U")) {
            pen = PenState.UP;
            return;
        }
        if (upper.equals("D")) {
            pen = PenState.DOWN;
            return;
        }
        if (upper.equals("L")) {
            direction = direction.turnLeft();
            return;
        }
        if (upper.equals("R")) {
            direction = direction.turnRight();
            return;
        }

        if (upper.startsWith("I")) {
            String rest = raw.substring(1).trim();
            int n = Integer.parseInt(rest);
            if (n <= 0)
                throw new IllegalArgumentException("Size must be > 0");
            floor = new int[n][n];
            x = 0;
            y = 0;
            direction = Direction.NORTH;
            pen = PenState.UP;
            return;
        }

        if (upper.startsWith("M")) {
            String rest = raw.substring(1).trim();
            int steps = Integer.parseInt(rest);
            internalMoveNoRecord(steps);
        }
    }

    private void internalMoveNoRecord(int steps) {
        if (steps < 0)
            throw new IllegalArgumentException("Steps must be >= 0");
        ensureInitialized();

        for (int i = 0; i < steps; i++) {
            int nextX = x;
            int nextY = y;

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

    // ----------------------------
    // Helpers + getters
    // ----------------------------

    private void ensureInitialized() {
        if (floor.length == 0)
            throw new IllegalStateException("Floor not initialized");
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

    public int getCell(int row, int col) {
        return floor[row][col];
    }

    public List<String> getHistory() {
        return List.copyOf(history);
    }
}