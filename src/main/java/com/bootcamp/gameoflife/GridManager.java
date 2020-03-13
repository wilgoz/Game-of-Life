package com.bootcamp.gameoflife;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class GridManager {

  private static final int[][] OFFSETS = {
      {1, 1}, {1, -1}, {-1, -1}, {-1, 1}, {0, 1}, {0, -1}, {1, 0}, {-1, 0}
  };

  private enum Row {TOP, BOTTOM}

  private Set<Cell> cells;
  private int rows;
  private int cols;

  public GridManager(Grid grid) {
    cells = grid.getCells();
    rows = grid.getRows();
    cols = grid.getCols();
  }

  public void displayGrid() {
    for (int row = 0; row < rows; row++) {
      for (int col = 0; col < cols; col++) {
        var state = cells.contains(new Cell(row, col)) ? State.ALIVE : State.DEAD;
        System.out.print(state.label + " ");
      }
      System.out.println();
    }
  }

  public void updateGrid() {
    Map<Cell, Integer> deadCells = new HashMap<>();
    Map<Cell, Integer> liveCells = new HashMap<>();
    cells.forEach((cell) -> {
      for (Cell c : getAdjacentCells(cell, false)) {
        deadCells.merge(c, 1, Integer::sum);
      }
      for (Cell c : getAdjacentCells(cell, true)) {
        liveCells.merge(c, 1, Integer::sum);
      }
    });
    cells.clear();
    deadCells.forEach((cell, ctr) -> {
      if (ctr == 3) {
        cells.add(cell);
      }
    });
    liveCells.forEach((cell, ctr) -> {
      if (ctr == 3 || ctr == 2) {
        cells.add(cell);
      }
    });
  }

  public void expandGrid() {
    Arrays.stream(Row.values()).forEach(row -> {
      if (shouldExpandRow(row)) {
        expandRow(row);
      }
    });
  }

  private List<Cell> getAdjacentCells(Cell cell, boolean alive) {
    return Arrays.stream(OFFSETS)
        .map(n -> {
          int nextRow = cell.getRow() + n[0];
          int nextCol = cell.getCol() + n[1];
          var adj = new Cell(nextRow, nextCol);
          return cells.contains(adj) == alive ? adj : null;
        })
        .collect(Collectors.toList());
  }

  private boolean shouldExpandRow(Row row) {
    boolean expand = false;
    for (int col = 0; col < cols; col++) {
      if (cells.contains(new Cell(row == Row.BOTTOM ? rows - 1 : 0, col))) {
        expand = true;
        break;
      }
    }
    return expand;
  }

  private void expandRow(Row row) {
    if (row == Row.TOP) {
      cells = cells.stream()
          .map(c -> new Cell(c.getRow() + 1, c.getCol()))
          .collect(Collectors.toSet());
    }
    rows++;
  }
}
