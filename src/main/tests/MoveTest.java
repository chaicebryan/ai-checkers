package main.tests;

import static org.junit.Assert.assertTrue;

import java.util.HashSet;

import org.junit.Test;

import main.java.gui.Piece;
import main.java.gui.PieceType;
import main.java.gui.Side;
import main.java.impl.Move;
import main.java.impl.Position;
import main.java.impl.Take;

public class MoveTest {

    @Test
    public void testTwoMovesAreEqualIfCoordinatesAreTheSame() {
        Move move = new Take(new Piece(3,4, PieceType.BLACK, Side.BOTTOM), new Position(1, 2), new Piece(2,2, PieceType.RED, Side.TOP));
        Move move2 = new Take(new Piece(3,4, PieceType.BLACK, Side.BOTTOM), new Position(1, 2), new Piece(2,2, PieceType.RED, Side.TOP));
        Move move3 = new Move(new Piece(3,4, PieceType.BLACK, Side.BOTTOM), new Position(1,2));

        System.out.println(move.hashCode());
        System.out.println(move2.hashCode());
        System.out.println(move.equals(move2));

        HashSet<Move> takes = new HashSet<>();
        takes.add(move);

        assertTrue(takes.contains(move3));

        System.out.println(move);
        System.out.println(move2);
    }
}
