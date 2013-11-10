package com.appspot.mindtrips.memoryletters.model;

import java.util.Random;
import com.appspot.mindtrips.memoryletters.model.Board.Tile.State;

public class Board {

    public static class Tile {

        public enum State {
            BACK, FRONT, HIDDEN;
        }

        private final char content;
        private State      state = State.BACK;
        private int        index = -1;

        private Tile(final char content) {
            this.content = content;
        }

        public char getContent() {
            return content;
        }

        public int getIndex() {
            return index;
        }

        public State getState() {
            return state;
        }

        public void setState(final State state) {
            this.state = state;
        }

    }

    private static Random rand = new Random();

    private static void shuffleSymbols(final char[] symbols, final int slicePos) {
        for (int i = 0; i < 1000; ++i) {
            final int pos1 = rand.nextInt(slicePos);
            final int pos2 = rand.nextInt(slicePos);
            if (pos1 != pos2) {
                final char tmp = symbols[pos1];
                symbols[pos1] = symbols[pos2];
                symbols[pos2] = tmp;
            }
        }
    }

    private static void shuffleTiles(final Tile[] tiles) {
        for (int i = 0; i < 1000; ++i) {
            final int pos1 = rand.nextInt(tiles.length);
            final int pos2 = rand.nextInt(tiles.length);
            if (pos1 != pos2) {
                final Tile tmp = tiles[pos1];
                tiles[pos1] = tiles[pos2];
                tiles[pos2] = tmp;
            }
        }
    }

    private final int    rows;
    private final int    cols;
    private final Tile[] tiles;

    public Board(final int rows, final int cols, final Difficulty difficulty) {
        final char[] symbols = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '@', '#', '$', '%', '&', '*' };
        this.rows = rows;
        this.cols = cols;
        tiles = new Tile[rows * cols];
        int symbolCount = difficulty == Difficulty.NORMAL ? tiles.length / 4 : tiles.length / 2;
        final int slicePos = 26 < symbolCount ? symbols.length : 26;
        shuffleSymbols(symbols, slicePos);
        for (int i = 0; i < symbolCount; ++i) {
            tiles[i] = new Tile(symbols[i]);
        }
        for (int i = symbolCount; i < symbolCount * 2; ++i) {
            tiles[i] = new Tile(symbols[i - symbolCount]);
        }
        if (difficulty == Difficulty.NORMAL) {
            symbolCount *= 2;
            for (int i = symbolCount; i < symbolCount * 2; ++i) {
                tiles[i] = new Tile(tiles[i - symbolCount].getContent());
            }
        }
        shuffleTiles(tiles);
        for (int i = 0; i < tiles.length; ++i) {
            tiles[i].index = i;
        }
    }

    public int getColumnCount() {
        return cols;
    }

    public int getRowCount() {
        return rows;
    }

    public Tile getTile(final int idx) {
        return tiles[idx];
    }

    public Tile getTile(final int row, final int col) {
        return tiles[row * cols + col];
    }

    public int getTileCount() {
        return tiles.length;
    }

    public boolean isSolved() {
        for (final Tile tile : tiles) {
            if (tile.getState() != State.HIDDEN) { return false; }
        }
        return true;
    }

}
