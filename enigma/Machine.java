package enigma;

import java.util.HashMap;
import java.util.Collection;
import static enigma.EnigmaException.*;
import java.util.ArrayList;
/** Class that represents a complete enigma machine.
 *  @author keith pacana
 */
class Machine {

    /** A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     *  and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     *  available rotors. */
    /** @param intialize # of rotors */private int _numrotors;
    /** @param initialize # of pawls */private int _pawls;
    /** @param hashmap all rotors */ private HashMap<String, Rotor> _allrotors;
    /** @param hashmap of used rotors */private HashMap<Integer, Rotor> _rotors;
     /**
    * @param alpha alphabet
    * @param numRotors number of rotors in place
    * @param pawls number of pawls
    * @param allRotors a list of all rotors*/
    Machine(Alphabet alpha, int numRotors, int pawls,
        Collection<Rotor> allRotors) {
        _alphabet = alpha;
        _numrotors = numRotors;
        _pawls = pawls;
        ArrayList<Rotor> x = (ArrayList<Rotor>) allRotors;
        _allrotors = new HashMap<String, Rotor>(x.size());
        for (int i = 0; x.size() > i; i++) {
            _allrotors.put(x.get(i).name().toUpperCase(), x.get(i));
        }
    }
    /** Return the number of rotor slots I have. */
    int numRotors() {
        return _numrotors;
    }

    /** Return the number pawls (and thus rotating rotors) I have. */
    int numPawls() {
        return _pawls;
    }

    /** Set my rotor slots to the rotors named ROTORS from my set of
     *  available rotors (ROTORS[0] names the reflector).
     *  Initially, all rotors are set at their 0 setting. */
    void insertRotors(String[] rotors) {
        _rotors = new HashMap<Integer, Rotor>(rotors.length);
        for (int i = 0; rotors.length > i; i++) {
            _rotors.put(i, _allrotors.get(rotors[i]));
        }
        int movingrot = 0;
        for (int j = 0; _rotors.size() > j; j++) {
            if ((_rotors.get(j) instanceof MovingRotor)) {
                movingrot += 1;
            }
        }
        if (movingrot > _pawls) {
            throw error("invalid number of arguments");
        }
    }
    /** Set my rotors according to SETTING, which must be a string of four
     *  upper-case letters. The first letter refers to the leftmost
     *  rotor setting (not counting the reflector).  */
    void setRotors(String setting) {
        for (int i = 0; setting.length() > i; i++) {
            _rotors.get(i + 1).set(setting.charAt(i));
        }

    }
    /** Set the plugboard to PLUGBOARD. */
    /** @param plugboard set up */private Permutation _plugboard;
    void setPlugboard(Permutation plugboard) {
        _plugboard = plugboard;
    }
    /** Returns the result of converting the input character C (as an
     *  index in the range 0..alphabet size - 1), after first advancing
     *  the machine. */
    int convert(int c) {
        c = _plugboard.permute(c);
        int numrots = _rotors.size();
        for (int i = 1; _rotors.size() > i; i++) {
            if (i == numrots - 1) {
                _rotors.get(i).advance();
            } else if (_rotors.get(i + 1).atNotch()) {
                if (!(_rotors.get(i) instanceof FixedRotor
                        || _rotors.get(i) instanceof Reflector)) {
                    _rotors.get(i).advance();
                }
            } else if (_rotors.get(i).atNotch()) {
                if (_rotors.get(i - 1) instanceof MovingRotor) {
                    _rotors.get(i).advance();
                }
            }
        }
        for (int i = _rotors.size() - 1; i >= 0; i -= 1) {
            c = _rotors.get(i).convertForward(c);
        }
        for (int j = 1; _rotors.size()  > j; j++) {
            c = _rotors.get(j).convertBackward(c);
        }
        c = _plugboard.permute(c);
        return c;
    }


    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {

        String translation = "";
        String msg1 = msg.toUpperCase();
        for (int i = 0; msg.length() > i; i++) {
            if (_alphabet.contains(msg1.charAt(i))) {
                int newval = convert(_alphabet.toInt(msg1.charAt(i)));
                String result = Character.toString(_alphabet.toChar(newval));
                translation = translation + result;
            }
        }
        return translation;
    }

    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;
}
