package enigma;

import static enigma.EnigmaException.*;

/** Class that represents a rotating rotor in the enigma machine.
 *  @author keith pacana
 */
class MovingRotor extends Rotor {

    /** A rotor named NAME whose permutation in its default setting is
     *  PERM, and whose notches are at the positions indicated in NOTCHES.
     *  The Rotor is initally in its 0 setting (first character of its
     *  alphabet).
     */
    private String _notches;
    /**
    * @param name name of rotor
    * @param perm permutation of rotor
    * @param notches # of notches
    */
    MovingRotor(String name, Permutation perm, String notches) {
        super(name, perm);
        _notches = notches;
    }

    @Override
    boolean atNotch() {
        char setting2char = alphabet().toChar(setting());
        boolean outcome = false;
        if (_notches.equals("")) {
            return true;
        } else {
            for (int i = 0; _notches.length() > i; i++) {
                if (setting2char == (_notches.charAt(i))) {
                    outcome = true;
                }
            }
        }
        return outcome;
    }

    @Override
    void advance() {
        set(setting() + 1);
    }
}
