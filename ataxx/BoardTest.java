package ataxx;

import org.junit.Test;

import static ataxx.PieceColor.EMPTY;
import static ataxx.PieceColor.RED;
import static org.junit.Assert.*;
import static ataxx.PieceColor.*;

/**
 * Tests of the Board class.
 *
 * @author
 */
public class BoardTest {
    private static final String[]
                GAME1 = {"a7-b7", "a1-a2",
                         "a7-a6", "a2-a3",
                         "a6-a5", "a3-a4"};

    private static void makeMoves(Board b, String[] moves) {
        for (String s : moves) {
            b.makeMove(s.charAt(0), s.charAt(1),
                    s.charAt(3), s.charAt(4));
        }
    }

    @Test
    public void testUndo() {
        Board b0 = new Board();
        Board b1 = new Board(b0);
        makeMoves(b0, GAME1);
        Board b2 = new Board(b0);
        for (int i = 0; i < GAME1.length; i += 1) {
            b0.undo();
        }
        assertEquals("failed to return to start", b1, b0);
        makeMoves(b0, GAME1);
        assertEquals("second pass failed to reach same position", b2, b0);
    }

    @Test
    public void testcanMove() {
        Board b2 = new Board();
        b2.clear();
        for (char i = 'a'; i <= 'g'; i = (char) (i + 1)) {
            for (char j = '1'; j <= '7'; j = (char) (j + 1)) {
                if (b2.legalBlock(i, j)) {
                    b2.setBlock(i, j);
                }
            }
        }
        assertFalse(b2.canMove(RED));
    }

    @Test
    public void testClear() {
        Board test = new Board();
        test.clear();
        assertEquals("top left", test.get('a', '7'), RED);
        assertEquals("top right", test.get('g', '7'), BLUE);
        assertEquals("bottom left", test.get('a', '1'), BLUE);
        assertEquals("bottom left", test.get('g', '1'), RED);

        assertEquals("Empty", test.get('c', '5'), EMPTY);
        assertEquals("Border", test.get(0), BLOCKED);
        assertEquals("Border", test.get(20), BLOCKED);
        assertEquals("Border", test.get(120), BLOCKED);
    }

    @Test
    public void testlegalMove() {
        Board x = new Board();
        assertEquals("top left", x.get('a', '7'), RED);
        assertEquals("top right", x.get('g', '7'), BLUE);
        assertEquals("bottom left", x.get('a', '1'), BLUE);
        assertEquals("bottom left", x.get('g', '1'), RED);
        Move y = Move.move('a', '7', 'a', '6');
        assertTrue(x.legalMove(y));
    }

    @Test
    public void makeMove() {
        Board z = new Board();
        assertEquals("top left", z.get('a', '7'), RED);
        assertEquals("top right", z.get('g', '7'), BLUE);
        assertEquals("bottom left", z.get('a', '1'), BLUE);
        assertEquals("bottom left", z.get('g', '1'), RED);
        Move y = Move.move('a', '7', 'a', '6');
        z.makeMove(y);
        assertEquals(z.get(y.toIndex()), RED);
        Board ab = new Board();
        makeMoves(ab, GAME1);
        assertEquals("a and 5", BLUE, ab.get('a', '5'));
    }

    @Test
    public void setBlock() {
        Board x = new Board();
        assertEquals("top left", x.get('a', '7'), RED);
        assertEquals("top right", x.get('g', '7'), BLUE);
        assertEquals("bottom left", x.get('a', '1'), BLUE);
        assertEquals("bottom left", x.get('g', '1'), RED);
        x.setBlock('b', '2');
        assertEquals(BLOCKED, x.get('b', '6'));
        assertEquals(BLOCKED, x.get('f', '2'));
        assertEquals(BLOCKED, x.get('f', '6'));

    }

    @Test
    public void testGameOver() {
        Board x = new Board();
        assertEquals("top left", x.get('a', '7'), RED);
        assertEquals("top right", x.get('g', '7'), BLUE);
        assertEquals("bottom left", x.get('a', '1'), BLUE);
        assertEquals("bottom left", x.get('g', '1'), RED);
        Move a = Move.move('a', '7', 'a', '6');
        Move b = Move.move('a', '1', 'a', '3');
        Move c = Move.move('a', '6', 'a', '4');
        Move d = Move.move('g', '7', 'g', '5');
        Move e = Move.move('g', '1', 'g', '3');
        Move f = Move.move('g', '5', 'f', '5');
        Move g = Move.move('g', '3', 'g', '4');
        x.makeMove(a);
        x.makeMove(b);
        x.makeMove(c);
        x.makeMove(d);
        x.makeMove(e);
        x.makeMove(f);
        x.makeMove(g);
    }

}
