package main.java.impl;

public class MoveAndScore implements Comparable<MoveAndScore> {

    // Move - Score mapping
    private final Move move;
    private final int score;

    public MoveAndScore(Move move, int score) {
        this.move = move;
        this.score = score;
    }

    // Return the move
    public Move getMove() {
        return move;
    }

    // Return the score associated with this move
    public int getScore() {
        return score;
    }

    @Override
    public int compareTo(MoveAndScore other) {
        if (this.score < other.getScore()) {
            return -1;
        } else if (this.score > other.getScore()) {
            return 1;
        } else {
            return 0;
        }
    }
}
