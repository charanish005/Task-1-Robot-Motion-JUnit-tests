# Robot Motion Simulator
**Course:** COEN 448/6761 – Software Testing and Validation, Winter 2026  
**Dev Team (Dev 1 – G4):** Charanish Miriyala & Sachin Raj Rajesh  
**Repository:** https://github.com/charanish005/Task-1-Robot-Motion-JUnit-tests

---

## Project Description

A Java simulation of a robot moving on an N×N grid floor. The robot holds a pen that can be up or down. While the pen is down, the robot marks cells as it moves. Commands control initialization, movement, direction, pen state, floor printing, history replay, and termination.

---

## Commands

| Command | Description |
|---------|-------------|
| `I n`   | Initialize an n×n floor; robot resets to [0,0], pen up, facing north |
| `D`     | Pen down |
| `U`     | Pen up |
| `L`     | Turn left 90° |
| `R`     | Turn right 90° |
| `M s`   | Move forward s steps |
| `P`     | Print the floor (marked cells = `*`) |
| `C`     | Print current position, pen state, and direction |
| `H`     | Replay all commands since last start |
| `Q`     | Quit |

---

## Project Structure

```
src/
├── main/java/com/coen448/robot/
│   ├── RobotEngine.java      # Core robot logic
│   ├── Direction.java        # NORTH / EAST / SOUTH / WEST enum
│   ├── PenState.java         # UP / DOWN enum
│   └── App.java              # CLI entry point
└── test/java/com/coen448/robot/
    ├── RobotEngineTest.java  # Original unit tests
    └── RegressionTest.java   # Task 4 regression tests (bug fix verification)
```

---

## How to Build and Run

**Prerequisites:** Java 17+, Maven 3.8+

```bash
# Clone the repo
git clone https://github.com/charanish005/Task-1-Robot-Motion-JUnit-tests.git
cd Task-1-Robot-Motion-JUnit-tests

# Compile
mvn compile

# Run all tests
mvn test

# Run with coverage report (JaCoCo)
mvn clean verify
# → Coverage report: target/site/jacoco/index.html

# Run the application
mvn exec:java -Dexec.mainClass="com.coen448.robot.App"
```

---

## Bug Fix (Task 4)

A bug was identified by the QA team (Dev 2 – Deepak Sunil Chavan) in `RobotEngine.move()`:

**Bug:** NORTH and SOUTH directions were inverted in the `move()` method:
- `NORTH` was using `nextY = y - 1` (wrong — should increase y)
- `SOUTH` was using `nextY = y + 1` (wrong — should decrease y)

**Fix applied:** Swapped to `NORTH → nextY = y + 1` and `SOUTH → nextY = y - 1`, consistent with `internalMove()`.

**Regression tests:** See `RegressionTest.java` — 15 test cases verifying the fix and ensuring no regressions.

---

## Test Coverage

| Metric | Result |
|--------|--------|
| Line Coverage | > 80% |
| Branch Coverage | > 80% |
| Method Coverage | > 90% |

Run `mvn clean verify` to generate the full JaCoCo HTML report.
