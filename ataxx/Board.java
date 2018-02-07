package ataxx;

/* Author: P. N. Hilfinger, (C) 2008. */

import java.util.Arrays;
import java.util.Stack;
import java.util.Formatter;
import java.util.Observable;

import static ataxx.PieceColor.*;
import static ataxx.GameException.error;

/**
 * An Ataxx board.   The squares are labeled by column (a char value between
 * 'a' - 2 and 'g' + 2) and row (a char value between '1' - 2 and '7'
 * + 2) or by linearized index, an integer described below.  Values of
 * the column outside 'a' and 'g' and of the row outside '1' to '7' denote
 * two layers of border squares, which are always blocked.
 * This artificial border (which is never actually printed) is a common
 * trick that allows one to avoid testing for edge conditions.
 * For example, to look at all the possible moves from a square, sq,
 * on the normal board (i.e., not in the border region), one can simply
 * look at all squares within two rows and columns of sq without worrying
 * about going off the board. Since squares in the border region are
 * blocked, the normal logic that prevents moving to a blocked square
 * will apply.
 * <p>
 * For some purposes, it is useful to refer to squares using a single
 * integer, which we call its "linearized index".  This is simply the
 * number of the square in row-major order (counting from 0).
 * <p>
 * Moves on this board are denoted by Moves.
 *
 * @author Keith Pacana
 */
class Board extends Observable {

    /**
     * Number of squares on a side of the board.
     */
    static final int SIDE = 7;
    /**
     * Length of a side + an artificial 2-deep border region.
     */
    static final int EXTENDED_SIDE = SIDE + 4;

    /**
     * Number of non-extending moves before game ends.
     */
    static final int JUMP_LIMIT = 25;
    /**
     * copy of board.
     */
    private Board copy;

    /**
     * A new, cleared board at the start of the game.
     */
    Board() {
        _board = new PieceColor[EXTENDED_SIDE * EXTENDED_SIDE];
        _whoseMove = RED;
        _red = PieceColor.RED;
        _blue = PieceColor.BLUE;
        bluepieces = 2;
        redpieces = 2;
        jumps = 0;
        for (int i = 0; i < _board.length; i++) {
            unrecordedSet(i, BLOCKED);
        }

        clear();
    }

    /**
     * A copy of B.
     */
    Board(Board b) {
        this._board = b._board.clone();
        this._red = PieceColor.RED;
        this._blue = PieceColor.BLUE;
        this.redpieces = b.redPieces();
        this.bluepieces = b.bluePieces();
        this._whoseMove = b.whoseMove();
    }

    /**
     * Return the linearized index of square COL ROW.
     */
    static int index(char col, char row) {
        return (row - '1' + 2) * EXTENDED_SIDE + (col - 'a' + 2);
    }

    /**
     * Return the linearized index of the square that is DC columns and DR
     * rows away from the square with index SQ.
     */
    static int neighbor(int sq, int dc, int dr) {
        return sq + dc + dr * EXTENDED_SIDE;
    }

    /**
     * Clear me to my starting state, with pieces in their initial
     * positions and no blocks.
     */
    void clear() {
        _whoseMove = RED;
        for (char i = 'a'; i <= 'g'; i = (char) (i + 1)) {
            for (char j = '1'; j <= '7'; j = (char) (j + 1)) {
                unrecordedSet(i, j, EMPTY);
            }
        }
        unrecordedSet(index('a', '7'), _red);
        unrecordedSet(index('g', '1'), _red);
        unrecordedSet(index('a', '1'), _blue);
        unrecordedSet(index('g', '7'), _blue);
        setChanged();
        notifyObservers();
    }


    /**
     * Return true iff the game is over: i.e., if neither side has
     * any moves, if one side has no pieces, or if there have been
     * MAX_JUMPS consecutive jumps without intervening extends.
     */
    boolean gameOver() {
        if (!canMove(RED) && !canMove(BLUE)) {
            return true;
        } else if (redPieces() == 0 || bluePieces() == 0) {
            return true;
        } else if (jumps >= JUMP_LIMIT) {
            return true;
        }
        return false;
    }

    /**
     * Return number of red pieces on the board.
     */
    int redPieces() {
        return numPieces(RED);
    }

    /**
     * Return number of blue pieces on the board.
     */
    int bluePieces() {
        return numPieces(BLUE);
    }

    /**
     * Return number of COLOR pieces on the board.
     */
    int numPieces(PieceColor color) {
        if (color.equals(RED)) {
            return redpieces;
        } else {
            return bluepieces;
        }
    }

    /**
     * Increment numPieces(COLOR) by K.
     */
    private void incrPieces(PieceColor color, int k) {
        if (color.equals(RED)) {
            redpieces += k;
        } else {
            bluepieces += k;
        }
    }

    /**
     * The current contents of square CR, where 'a'-2 <= C <= 'g'+2, and
     * '1'-2 <= R <= '7'+2.  Squares outside the range a1-g7 are all
     * BLOCKED.  Returns the same value as get(index(C, R)).
     */
    PieceColor get(char c, char r) {
        return _board[index(c, r)];
    }

    /**
     * Return the current contents of square with linearized index SQ.
     */
    PieceColor get(int sq) {
        return _board[sq];
    }

    /**
     * Set get(C, R) to V, where 'a' <= C <= 'g', and
     * '1' <= R <= '7'.
     */
    private void set(char c, char r, PieceColor v) {
        set(index(c, r), v);
    }

    /**
     * Set square with linearized index SQ to V.  This operation is
     * undoable.
     */
    private void set(int sq, PieceColor v) {
        posn.push(sq);
        oldpiece.push(get(sq));
        _board[sq] = v;
    }

    /**
     * Set square at C R to V (not undoable).
     */
    private void unrecordedSet(char c, char r, PieceColor v) {
        _board[index(c, r)] = v;
    }

    /**
     * Set square at linearized index SQ to V (not undoable).
     */
    private void unrecordedSet(int sq, PieceColor v) {
        _board[sq] = v;
    }

    /**
     * Return true iff MOVE is legal on the current board.
     */
    boolean legalMove(Move move) {
        try {
            if (move.fromIndex() < 0 || get(move.fromIndex()) != _whoseMove) {
                return false;
            }
            boolean fromColor = get(move.fromIndex()) == RED
                    || get(move.fromIndex()) == BLUE;
            boolean legal = move.isPass() || move.isExtend()
                    || move.isJump();
            return legal && fromColor && get(move.toIndex()) == EMPTY;
        } catch (java.lang.NullPointerException e) {
            return false;
        }

    }

    /**
     * Return true iff player WHO can move, ignoring whether it is
     * that player's move and whether the game is over.
     */
    boolean canMove(PieceColor who) {
        for (char i = 'a'; i <= 'g'; i = (char) (i + 1)) {
            for (char j = '1'; j <= '7'; j = (char) (j + 1)) {
                if (get(i, j).equals(who)) {
                    for (int dc = -2; dc <= 2; dc += 1) {
                        for (int dr = -2; dr <= 2; dr += 1) {
                            if (get(((char) (i + dr)),
                                    ((char) (j + dc))).equals(EMPTY)) {
                                return true;
                            }
                        }
                    }
                }

            }
        }
        return false;
    }

    /**
     * Return the color of the player who has the next move.  The
     * value is arbitrary if gameOver().
     */
    PieceColor whoseMove() {
        return _whoseMove;
    }

    /**
     * Return total number of moves and passes since the last
     * clear or the creation of the board.
     */
    int numMoves() {
        return 0;
    }
    /**
     * Return number of non-pass moves made in the current game since the
     * last extend move added a piece to the board (or since the
     * start of the game). Used to detect end-of-game.
     */
    int numJumps() {
        return jumps;
    }
    /**
     * Perform the move C0R0-C1R1, or pass if C0 is '-'.  For moves
     * other than pass, assumes that legalMove(C0, R0, C1, R1).
     */
    void makeMove(char c0, char r0, char c1, char r1) {
        if (c0 == '-') {
            makeMove(Move.pass());
        } else {
            makeMove(Move.move(c0, r0, c1, r1));
        }
    }

    /**
     * Make the MOVE on this Board, assuming it is legal.
     */
    void makeMove(Move move) {
        try {
            copy = new Board(this);
            if (legalMove(move)) {
                if (move.isPass()) {
                    pass();
                    return;
                } else if (move.isExtend()) {
                    posn.push(LIM_1);
                    oldpiece.push(null);
                    set(move.toIndex(), _whoseMove);
                    incrPieces(_whoseMove, 1);
                    for (int i = -1; i <= 1; i++) {
                        for (int j = -1; j <= 1; j++) {
                            if (get(neighbor(move.toIndex(), i, j))
                                    .equals(_whoseMove.opposite())) {
                                set(neighbor(move.toIndex(), i, j), _whoseMove);
                                incrPieces(_whoseMove, 1);
                                incrPieces(_whoseMove.opposite(), -1);
                                jumps = 0;
                            }
                        }
                    }
                } else {
                    posn.push(LIM_1);
                    oldpiece.push(null);
                    set(move.fromIndex(), EMPTY);
                    set(move.toIndex(), _whoseMove);
                    jumps += 1;
                    for (int i = -1; i <= 1; i++) {
                        for (int j = -1; j <= 1; j++) {
                            if (get(neighbor(move.toIndex(), i, j))
                                    .equals(_whoseMove.opposite())) {
                                set(neighbor(move.toIndex(), i, j), _whoseMove);
                                incrPieces(_whoseMove, 1);
                                incrPieces(_whoseMove.opposite(), -1);
                            }
                        }
                    }
                }
                _whoseMove = _whoseMove.opposite();
                setChanged();
                notifyObservers();
            }
        } catch (java.lang.NullPointerException e) {
            System.out.println("invalid move");
        }
    }

    /**
     * Update to indicate that the current player passes, assuming it
     * is legal to do so.  The only effect is to change whoseMove().
     */
    void pass() {
        if (!canMove(_whoseMove)) {
            makeMove(Move.pass());
            _whoseMove = _whoseMove.opposite();
        }
        setChanged();
        notifyObservers();
    }

    /**
     * Undo the last move.
     */
    void undo() {
        while (posn.peek() != LIM_1) {
            unrecordedSet(posn.pop(), oldpiece.pop());
        }
        posn.pop();
        oldpiece.pop();
        setChanged();
        notifyObservers();
    }

    /**
     * Return true iff it is legal to place a block at C R.
     */
    boolean legalBlock(char c, char r) {
        if (_board[index(c, r)].equals(EMPTY)) {
            return true;
        }
        return false;
    }

    /**
     * Return true iff it is legal to place a block at CR.
     */
    boolean legalBlock(String cr) {
        return legalBlock(cr.charAt(0), cr.charAt(1));
    }

    /**
     * Set a block on the square C R and its reflections across the middle
     * row and/or column, if that square is unoccupied and not
     * in one of the corners. Has no effect if any of the squares is
     * already occupied by a block.  It is an error to place a block on a
     * piece.
     */
    void setBlock(char c, char r) {
        if (!legalBlock(c, r)) {
            throw error("illegal block placement");
        }
        unrecordedSet(c, r, BLOCKED);
        unrecordedSet(c, (char) ('7' - (r - '1')), BLOCKED);
        unrecordedSet((char) ('g' - (c - 'a')),
                r, BLOCKED);
        unrecordedSet((char) ('g' - (c - 'a')), (char)
                ('7' - (r - '1')), BLOCKED);
        setChanged();
        notifyObservers();
    }

    /**
     * Place a block at CR.
     */
    void setBlock(String cr) {
        setBlock(cr.charAt(0), cr.charAt(1));
    }

    /**
     * Return a list of all moves made since the last clear (or start of
     * game).
     */
    @Override
    public String toString() {
        String str = "";
        for (char i = 'a'; i <= 'g'; i = (char) (i + 1)) {
            for (char j = '7'; j >= '1'; j = (char) (j - 1)) {
                if (j == '7') {
                    str += "\r\n";
                }
                if (get(i, j).equals(RED)) {
                    str += "r ";
                } else if (get(i, j).equals(BLUE)) {
                    str += "b ";
                } else if (get(i, j).equals(BLOCKED)) {
                    str += "X ";
                } else if (get(i, j).equals(EMPTY)) {
                    str += "- ";
                }
            }
        }
        return str;
    }

    /* .equals used only for testing purposes. */
    @Override
    public boolean equals(Object obj) {
        Board other = (Board) obj;
        return Arrays.equals(_board, other._board);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(_board);
    }

    /**
     * Return a text depiction of the board (not a dump).  If LEGEND,
     * supply row and column numbers around the edges.
     */
    String toString(boolean legend) {
        Formatter out = new Formatter();
        String x = "";
        return out.toString();
    }

    /**
     * For reasons of efficiency in copying the board,
     * we use a 1D array to represent it, using the usual access
     * algorithm: row r, column c => index(r, c).
     * <p>
     * Next, instead of using a 7x7 board, we use an 11x11 board in
     * which the outer two rows and columns are blocks, and
     * row 2, column 2 actually represents row 0, column 0
     * of the real board.  As a result of this trick, there is no
     * need to special-case being near the edge: we don't move
     * off the edge because it looks blocked.
     * <p>
     * Using characters as indices, it follows that if 'a' <= c <= 'g'
     * and '1' <= r <= '7', then row c, column r of the board corresponds
     * to board[(c -'a' + 2) + 11 (r - '1' + 2) ], or by a little
     * re-grouping of terms, board[c + 11 * r + SQUARE_CORRECTION].
     */
    private final PieceColor[] _board;

    /**
     * Player that is on move.
     */
    private PieceColor _whoseMove;

    /**
     * Color of red piece.
     */
    private PieceColor _red;

    /**
     * Color of blue piece.
     */
    private PieceColor _blue;
    /**
     * Number of blue pieces.
     */
    private int bluepieces;
    /**
     * Number of red pieces.
     */
    private int redpieces;
    /**
     * Number of jumps.
     */
    private int jumps;
    /**
     * Stack of piececolor for undo.
     */
    private Stack<PieceColor> oldpiece = new Stack<PieceColor>();
    /**
     * stack of positions for undo.
     */
    private Stack<Integer> posn = new Stack<Integer>();
    /** magic num error.*/
    static final int LIM_1 = -100;
}
