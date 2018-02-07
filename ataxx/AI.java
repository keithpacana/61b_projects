package ataxx;

import java.util.Stack;
import static ataxx.PieceColor.*;
import static java.lang.Math.min;
import static java.lang.Math.max;

/**
 * A Player that computes its own moves.
 *
 * @author Keith Pacana
 */
class AI extends Player {

    /**
     * Maximum minimax search depth before going to static evaluation.
     */
    private static final int MAX_DEPTH = 4;
    /**
     * A position magnitude indicating a win (for red if positive, blue
     * if negative).
     */
    private static final int WINNING_VALUE = Integer.MAX_VALUE - 1;
    /**
     * A magnitude greater than a normal value.
     */
    private static final int INFTY = Integer.MAX_VALUE;

    /**
     * A new AI for GAME that will play MYCOLOR.
     */
    AI(Game game, PieceColor myColor) {
        super(game, myColor);
    }

    @Override
    Move myMove() {
        if (!board().canMove(myColor())) {
            return Move.pass();
        }
        Move move = findMove();
        if (move.isExtend() || move.isJump()) {
            System.out.println(myColor().toString() + " moves "
                    + Character.toString(move.col0())
                    + Character.toString(move.row0())
                    + "-" + Character.toString(move.col1())
                    + Character.toString(move.row1()) + ".");
            return move;
        } else if (move.isPass()) {
            System.out.println(myColor().toString() + "passes");
            return move;
        }
        return move;
    }


    /**
     * Return a move for me from the current position, assuming there
     * is a move.
     */
    private Move findMove() {
        Board b = new Board(board());
        if (myColor() == RED) {
            findMove(b, MAX_DEPTH, true, 1, -INFTY, INFTY);
        } else {
            findMove(b, MAX_DEPTH, true, -1, -INFTY, INFTY);
        }
        return _lastFoundMove;
    }

    /**
     * Used to communicate best moves found by findMove, when asked for.
     */
    private Move _lastFoundMove;

    /**
     * Find a move from position BOARD and return its value, recording
     * the move found in _lastFoundMove iff SAVEMOVE. The move
     * should have maximal value or have value >= BETA if SENSE==1,
     * and minimal value or value <= ALPHA if SENSE==-1. Searches up to
     * DEPTH levels before using a static estimate.
     */
    Stack<Move> possibleMoves(Board board) {
        Stack<Move> allpossible = new Stack<Move>();
        for (char i = 'a'; i <= 'g'; i = (char) (i + 1)) {
            for (char j = '1'; j <= '7'; j = (char) (j + 1)) {
                if (board.get(i, j).equals(board.whoseMove())) {
                    for (int x = -2; x <= 2; x++) {
                        for (int y = -2; y <= 2; y++) {
                            Move a = Move.move(i, j, (char) (i + x),
                                    (char) (j + y));
                            allpossible.push(a);
                        }
                    }
                }
            }
        }
        return allpossible;
    }
    /**
     * Find a move from position BOARD and return its value, recording
     * the move found in _lastFoundMove iff SAVEMOVE. The move
     * should have maximal value or have value >= BETA if SENSE==1,
     * and minimal value or value <= ALPHA if SENSE==-1. Searches up to
     * DEPTH levels before using a static estimate.
     */
    private int findMove(Board board, int depth, boolean saveMove,
                         int sense, int alpha, int beta) {
        int bestsofar;
        if (depth == 0 || board.gameOver()) {
            return staticScore(board);
        }
        if (sense == 1) {
            Stack<Move> allmoves = possibleMoves(board);
            bestsofar = -INFTY;
            while (!allmoves.empty()) {
                Move m = allmoves.pop();
                if (board.legalMove(m)) {
                    board.makeMove(m);
                    int response = findMove(board, depth - 1, false,
                            -1, alpha, beta);
                    board.undo();
                    bestsofar = max(bestsofar, response);
                    if (response >= bestsofar) {
                        alpha = response;
                        if (saveMove) {
                            _lastFoundMove = m;
                        }
                    }
                    if (beta <= alpha) {
                        break;
                    }
                }
            }
        } else {
            Stack<Move> allmoves = possibleMoves(board);
            bestsofar = INFTY;
            while (!allmoves.empty()) {
                Move m = allmoves.pop();
                if (board.legalMove(m)) {
                    board.makeMove(m);
                    int response = findMove(board, depth - 1, false,
                            1, alpha, beta);
                    board.undo();
                    bestsofar = min(bestsofar, response);
                    if (response <= bestsofar) {
                        beta = response;
                        if (saveMove) {
                            _lastFoundMove = m;
                        }
                    }
                    if (beta <= alpha) {
                        break;
                    }
                }
            }
        }
        return bestsofar;
    }

    /**
     * Return a heuristic value for BOARD.
     */
    private int staticScore(Board board) {
        return board.redPieces() - board.bluePieces();
    }
}
