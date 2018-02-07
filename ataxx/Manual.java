package ataxx;

import static ataxx.PieceColor.*;

/**
 * A Player that receives its moves from its Game's getMoveCmnd method.
 *
 * @author Keith Pacana
 */
class Manual extends Player {

    /**
     * A Player that will play MYCOLOR on GAME, taking its moves from
     * GAME.
     */
    Manual(Game game, PieceColor myColor) {
        super(game, myColor);
    }

    @Override
    Move myMove() {
        String prompt = myColor().toString() + ":";
        Command cmd = game().getMoveCmnd(prompt);
        Command.Type type = cmd.commandType();
        String[] operands = cmd.operands();
        if (type.equals(Command.Type.PIECEMOVE)) {
            char col0 = operands[0].charAt(0);
            char row0 = operands[1].charAt(0);
            char col1 = operands[2].charAt(0);
            char row1 = operands[3].charAt(0);
            Move cmdmove = Move.move(col0, row0, col1, row1);
            return cmdmove;
        } else if (type.equals(Command.Type.PASS)) {
            Move cmdmove = Move.pass();
            return cmdmove;
        }
        return null;
    }

}

