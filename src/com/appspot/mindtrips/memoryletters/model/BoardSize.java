package com.appspot.mindtrips.memoryletters.model;

public enum BoardSize {
    SMALL (4, 4),
    MEDIUM (6, 6),
    LARGE (8, 8);

    public final int rows;
    public final int cols;

    private BoardSize(final int rows, final int cols) {
        this.rows = rows;
        this.cols = cols;
    }
}
