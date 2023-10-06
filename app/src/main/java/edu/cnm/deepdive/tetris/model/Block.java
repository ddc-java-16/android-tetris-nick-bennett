package edu.cnm.deepdive.tetris.model;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Implements the functional characteristics and behavior of a single Tetris shape.
 */
@SuppressWarnings("unused")
public class Block implements Cloneable {
  
  private final ShapeType type;
  private final Field field;

  private Orientation orientation;
  private Extent extent;
  private int topRow;
  private int leftColumn;

  /**
   * @param type
   * @param field
   * @param topRow
   * @param leftColumn
   */
  public Block(ShapeType type, Field field, int topRow, int leftColumn) {
    this.type = type;
    this.field = field;
    this.topRow = topRow;
    this.leftColumn = leftColumn;
    orientation = Orientation.NORTH;
    extent = new Extent(type.getInitialForm());
  }

  @SuppressWarnings("MethodDoesntCallSuperMethod")
  @Override
  public Block clone() {
    Block clone = new Block(type, field, topRow, leftColumn);
    clone.orientation = orientation;
    clone.extent = extent;
    return clone;
  }

  private boolean canMove(int rowOffset, int colOffset, Extent extent) {
    int testTopRow = topRow + rowOffset;
    int testLeftCol = leftColumn + colOffset;
    boolean canMove = true;
    int size = extent.size();
    rowLoop:
    for (int rowIndex = 0; rowIndex < size; rowIndex++) {
      int fieldRowIndex = rowIndex + testTopRow;
      for (int colIndex = 0; colIndex < size; colIndex++) {
        int fieldColIndex = colIndex + testLeftCol;
        if (extent.isOccupied(rowIndex, colIndex)
            && (field.isOutOfBounds(fieldRowIndex, fieldColIndex)
            || field.isOccupied(fieldRowIndex, fieldColIndex))) {
          canMove = false;
          break rowLoop;
        }
      }
    }
    return canMove;
  }

  public boolean move(int rowOffset, int colOffset) {
    if (canMove(rowOffset, colOffset, this.extent)) {
      topRow += rowOffset;
      leftColumn += colOffset;
      return true;
    }
    return false;
  }

  public void freeze() {
    int size = extent.size();
    for (int rowIndex = 0; rowIndex < size; rowIndex++) {
      int fieldRowIndex = rowIndex + topRow;
      for (int colIndex = 0; colIndex < size; colIndex++) {
        int fieldColIndex = colIndex + leftColumn;
        if (extent.isOccupied(rowIndex, colIndex)) {
          field.set(fieldRowIndex, fieldColIndex, type);
        }
      }
    }
  }

  public boolean rotate(boolean clockwise) {
    boolean rotated = false;
    Extent extent = clockwise ? this.extent.turnClockwise() : this.extent.turnCounterClockwise();
    int rowOffset = 0;
    int colOffset = 0;
    boolean canMove = canMove(rowOffset, colOffset, extent);
    if (!canMove) {
      for (Kick kick : orientation.getKicks(type, clockwise)) {
        rowOffset = kick.rowOffset();
        colOffset = kick.columnOffset();
        if (canMove(rowOffset, colOffset, extent)) {
          canMove = true;
          break;
        }
      }
    }
    if (canMove) {
      this.extent = extent;
      orientation = clockwise ? orientation.turnClockwise() : orientation.turnCounterClockwise();
      move(rowOffset, colOffset);
      rotated = true;
    }
    return rotated;
  }

  public int getLastOccupiedRow() {
    return extent.lastOccupiedRow();
  }

  public int getTopRow() {
    return topRow;
  }

  public int getLeftColumn() {
    return leftColumn;
  }

  public ShapeType getType() {
    return type;
  }

  public int getHeight() {
    return extent.size();
  }

  public int getWidth() {
    return extent.size();
  }

  public boolean isOccupied(int rowIndex, int colIndex) {
    return extent.isOccupied(rowIndex, colIndex);
  }

  @SuppressWarnings("DuplicatedCode")
  public enum Orientation {

    NORTH {
      @Override
      protected Map<ShapeType, List<Kick>> generateKicks(boolean clockwise) {
        return clockwise
            ? Map.of(
            ShapeType.I, Kick.I_NORTH_CW,
            ShapeType.J, Kick.OTHER_NORTH_CW,
            ShapeType.L, Kick.OTHER_NORTH_CW,
            ShapeType.S, Kick.OTHER_NORTH_CW,
            ShapeType.T, Kick.OTHER_NORTH_CW,
            ShapeType.Z, Kick.OTHER_NORTH_CW
        )
            : Map.of(
                ShapeType.I, Kick.I_NORTH_CCW,
                ShapeType.J, Kick.OTHER_NORTH_CCW,
                ShapeType.L, Kick.OTHER_NORTH_CCW,
                ShapeType.S, Kick.OTHER_NORTH_CCW,
                ShapeType.T, Kick.OTHER_NORTH_CCW,
                ShapeType.Z, Kick.OTHER_NORTH_CCW
            );
      }
    },
    EAST {
      @Override
      protected Map<ShapeType, List<Kick>> generateKicks(boolean clockwise) {
        return clockwise
            ? Map.of(
            ShapeType.I, Kick.I_EAST_CW,
            ShapeType.J, Kick.OTHER_EAST_EITHER,
            ShapeType.L, Kick.OTHER_EAST_EITHER,
            ShapeType.S, Kick.OTHER_EAST_EITHER,
            ShapeType.T, Kick.OTHER_EAST_EITHER,
            ShapeType.Z, Kick.OTHER_EAST_EITHER
        )
            : Map.of(
                ShapeType.I, Kick.I_EAST_CCW,
                ShapeType.J, Kick.OTHER_EAST_EITHER,
                ShapeType.L, Kick.OTHER_EAST_EITHER,
                ShapeType.S, Kick.OTHER_EAST_EITHER,
                ShapeType.T, Kick.OTHER_EAST_EITHER,
                ShapeType.Z, Kick.OTHER_EAST_EITHER
            );
      }
    },
    SOUTH {
      @Override
      protected Map<ShapeType, List<Kick>> generateKicks(boolean clockwise) {
        return clockwise
            ? Map.of(
            ShapeType.I, Kick.I_SOUTH_CW,
            ShapeType.J, Kick.OTHER_SOUTH_CW,
            ShapeType.L, Kick.OTHER_SOUTH_CW,
            ShapeType.S, Kick.OTHER_SOUTH_CW,
            ShapeType.T, Kick.OTHER_SOUTH_CW,
            ShapeType.Z, Kick.OTHER_SOUTH_CW
        )
            : Map.of(
                ShapeType.I, Kick.I_SOUTH_CCW,
                ShapeType.J, Kick.OTHER_SOUTH_CCW,
                ShapeType.L, Kick.OTHER_SOUTH_CCW,
                ShapeType.S, Kick.OTHER_SOUTH_CCW,
                ShapeType.T, Kick.OTHER_SOUTH_CCW,
                ShapeType.Z, Kick.OTHER_SOUTH_CCW
            );
      }
    },
    WEST {
      @Override
      protected Map<ShapeType, List<Kick>> generateKicks(boolean clockwise) {
        return clockwise
            ? Map.of(
            ShapeType.I, Kick.I_WEST_CW,
            ShapeType.J, Kick.OTHER_WEST_EITHER,
            ShapeType.L, Kick.OTHER_WEST_EITHER,
            ShapeType.S, Kick.OTHER_WEST_EITHER,
            ShapeType.T, Kick.OTHER_WEST_EITHER,
            ShapeType.Z, Kick.OTHER_WEST_EITHER
        )
            : Map.of(
                ShapeType.I, Kick.I_WEST_CCW,
                ShapeType.J, Kick.OTHER_WEST_EITHER,
                ShapeType.L, Kick.OTHER_WEST_EITHER,
                ShapeType.S, Kick.OTHER_WEST_EITHER,
                ShapeType.T, Kick.OTHER_WEST_EITHER,
                ShapeType.Z, Kick.OTHER_WEST_EITHER
            );
      }
    };

    private final Map<ShapeType, List<Kick>> clockwiseKicks;
    private final Map<ShapeType, List<Kick>> counterClockwiseKicks;

    Orientation() {
      clockwiseKicks = generateKicks(true);
      counterClockwiseKicks = generateKicks(false);
    }


    public Orientation turnClockwise() {
      int position = ordinal();
      Orientation[] values = values();
      int newPosition = (position + 1) % values.length;
      return values[newPosition];
    }

    public Orientation turnCounterClockwise() {
      int position = ordinal();
      Orientation[] values = values();
      int newPosition = (position + values.length - 1) % values.length;
      return values[newPosition];
    }

    private List<Kick> getKicks(ShapeType type, boolean clockwise) {
      return clockwise ? clockwiseKicks.get(type) : counterClockwiseKicks.get(type);
    }

    abstract Map<ShapeType, List<Kick>> generateKicks(boolean clockwise);

  }

  public enum ShapeType {
    I(new boolean[][]{
        {false, false, false, false},
        {true, true, true, true},
        {false, false, false, false},
        {false, false, false, false}
    }),
    O(new boolean[][]{
        {false, false, false, false},
        {false, true, true, false},
        {false, true, true, false},
        {false, false, false, false}
    }),
    S(new boolean[][]{
        {false, true, true},
        {true, true, false},
        {false, false, false}
    }),
    Z(new boolean[][]{
        {true, true, false},
        {false, true, true},
        {false, false, false}
    }),
    L(new boolean[][]{
        {false, false, true},
        {true, true, true},
        {false, false, false}
    }),
    J(new boolean[][]{
        {true, false, false},
        {true, true, true},
        {false, false, false}
    }),
    T(new boolean[][]{
        {false, true, false},
        {true, true, true},
        {false, false, false}
    });

    private final boolean[][] initialForm;

    ShapeType(boolean[][] initialForm) {
      this.initialForm = initialForm;
    }

    public boolean[][] getInitialForm() {
      return initialForm;
    }

    public int width() {
      return initialForm[0].length;
    }

    public int height() {
      return initialForm.length;
    }

  }

  private static class Extent {

    private final boolean[][] contents;

    public Extent(boolean[][] contents) {
      this(contents, true);
    }

    private Extent(boolean[][] contents, boolean copy) {
      if (copy) {
        // Performs a deep copy by creating a new array, then populating that array with the results
        // Arrays.copy for each row.
        this.contents = new boolean[contents.length][];
        for (int rowIndex = 0; rowIndex < contents.length; rowIndex++) {
          this.contents[rowIndex] = Arrays.copyOf(contents[rowIndex], contents[rowIndex].length);
        }
      } else {
        this.contents = contents;
      }
    }

    public int size() {
      return contents.length;
    }

    public boolean isOccupied(int row, int column) {
      return contents[row][column];
    }

    public Extent turnClockwise() {
      //noinspection DuplicatedCode
      boolean[][] rotated = new boolean[contents.length][contents.length];
      for (int rowIndex = 0; rowIndex < contents.length; rowIndex++) {
        int destColIndex = contents.length - 1 - rowIndex;
        for (int colIndex = 0; colIndex < contents[rowIndex].length; colIndex++) {
          //noinspection UnnecessaryLocalVariable
          int destRowIndex = colIndex;
          rotated[destRowIndex][destColIndex] = contents[rowIndex][colIndex];
        }
      }
      return new Extent(rotated, false);
    }

    public Extent turnCounterClockwise() {
      //noinspection DuplicatedCode
      boolean[][] rotated = new boolean[contents.length][contents.length];
      for (int rowIndex = 0; rowIndex < contents.length; rowIndex++) {
        //noinspection UnnecessaryLocalVariable
        int destColIndex = rowIndex;
        for (int colIndex = 0; colIndex < contents[rowIndex].length; colIndex++) {
          int destRowIndex = contents.length - 1 - colIndex;
          rotated[destRowIndex][destColIndex] = contents[rowIndex][colIndex];
        }
      }
      return new Extent(rotated, false);
    }

    public int lastOccupiedRow() {
      int rowIndex = contents.length - 1;
      while (rowIndex >= 0 && isEmpty(rowIndex)) {
        rowIndex--;
      }
      return rowIndex;
    }

    private boolean isEmpty(int rowIndex) {
      boolean empty = true;
      for (boolean element : contents[rowIndex]) {
        if (element) {
          empty = false;
          break;
        }
      }
      return empty;
    }

  }

  private record Kick(int rowOffset, int columnOffset) {

     static final List<Kick> I_NORTH_CCW =
        List.of(new Kick(0, 0), new Kick(0, -1), new Kick(0, 2), new Kick(-2, -1), new Kick(1, 2));
     static final List<Kick> I_NORTH_CW =
        List.of(new Kick(0, 0), new Kick(0, -2), new Kick(0, 1), new Kick(1, -2), new Kick(-2, 1));

     static final List<Kick> I_EAST_CCW =
        List.of(new Kick(0, 0), new Kick(0, 2), new Kick(0, -1), new Kick(-1, 2), new Kick(2, -1));
     static final List<Kick> I_EAST_CW =
        List.of(new Kick(0, 0), new Kick(0, -1), new Kick(0, 2), new Kick(-2, -1), new Kick(1, 2));

     static final List<Kick> I_SOUTH_CCW = List.of(new Kick(0, 0), new Kick(0, 1),
        new Kick(0, -2), new Kick(2, 1), new Kick(-1, -2));
     static final List<Kick> I_SOUTH_CW = List.of(new Kick(0, 0), new Kick(0, 2),
        new Kick(0, -1), new Kick(-1, 2), new Kick(2, -1));

     static final List<Kick> I_WEST_CCW = List.of(new Kick(0, 0), new Kick(0, -2),
        new Kick(0, 1), new Kick(1, -2), new Kick(-2, 1));
     static final List<Kick> I_WEST_CW = List.of(new Kick(0, 0), new Kick(0, 1),
        new Kick(0, -2), new Kick(2, 1), new Kick(-1, -2));

     static final List<Kick> OTHER_NORTH_CCW = List.of(new Kick(0, 0), new Kick(0, 1),
        new Kick(-1, 1), new Kick(2, 0), new Kick(2, 1));
     static final List<Kick> OTHER_NORTH_CW = List.of(new Kick(0, 0), new Kick(0, -1),
        new Kick(-1, -1), new Kick(2, 0), new Kick(2, -1));

     static final List<Kick> OTHER_EAST_EITHER = List.of(new Kick(0, 0), new Kick(0, 1),
        new Kick(1, 1), new Kick(-2, 0), new Kick(-2, 1));

     static final List<Kick> OTHER_SOUTH_CCW = List.of(new Kick(0, 0), new Kick(0, -1),
        new Kick(-1, -1), new Kick(2, 0), new Kick(2, -1));
     static final List<Kick> OTHER_SOUTH_CW = List.of(new Kick(0, 0), new Kick(0, 1),
        new Kick(-1, 1), new Kick(2, 0), new Kick(2, 1));

     static final List<Kick> OTHER_WEST_EITHER = List.of(new Kick(0, 0), new Kick(0, -1),
        new Kick(1, -1), new Kick(-2, 0), new Kick(-2, -1));

  }

}
