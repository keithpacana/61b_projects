package enigma;

import static enigma.EnigmaException.*;

/** Superclass that represents a rotor in the enigma machine.
 *  @author keith pacana
 */
class Rotor {

    /** A rotor named NAME whose permutation is given by PERM. */
    private int _setting;
    /** set up rotor & initialize. */
    /**
    * @param name rotor name
    * @param perm rotor permutation*/
    Rotor(String name, Permutation perm) {
        _setting = 0;
        _name = name;
        _permutation = perm;
    }

    /** Return my name. */
    String name() {
        return _name;
    }

    /** Return my alphabet. */
    Alphabet alphabet() {
        return _permutation.getalphabet();
    }

    /** Return my permutation. */
    Permutation permutation() {
        return _permutation;
    }

    /** Return the size of my alphabet. */
    int size() {
        return _permutation.alphabet().size();
    }

    /** Return true iff I have a ratchet and can move. */
    boolean rotates() {
        return false;
    }

    /** Return true iff I reflect. */
    boolean reflecting() {
        return false;
    }

    /** Return my current setting. */
    int setting() {
        return _setting;
    }

    /** Set setting() to POSN.  */
    void set(int posn) {
        _setting = _permutation.wrap(posn);
    }

    /** Set setting() to character CPOSN. */
    void set(char cposn) {
        set(_permutation.alphabet().toInt(cposn));
    }

    /** Return the conversion of P (an integer in the range 0..size()-1)
     *  according to my permutation. */
    int convertForward(int p) {
        int newposn = _permutation.wrap(p + _setting);
        int convert = _permutation.permute(newposn);
        int nextposn = _permutation.wrap(convert - _setting);
        return nextposn;
    }

    /** Return the conversion of E (an integer in the range 0..size()-1)
     *  according to the inverse of my permutation. */
    int convertBackward(int e) {
        int newposn = _permutation.wrap(e + _setting);
        int convert = _permutation.invert(newposn);
        int nextposn = _permutation.wrap(convert - _setting);
        return nextposn;
    }

    /** Returns true iff I am positioned to allow the rotor to my left
     *  to advance. */
    boolean atNotch() {
        return false;
    }

    /** Advance me one position, if possible. By default, does nothing. */
    void advance() {
    }

    @Override
    public String toString() {
        return "Rotor " + _name;
    }

    /** My name. */
    private final String _name;

    /** The permutation implemnted by this rotor in its 0 position. */
    private Permutation _permutation;

}
