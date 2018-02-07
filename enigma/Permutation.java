package enigma;

import static enigma.EnigmaException.*;
import java.util.HashMap;
import java.util.Map;
/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author keith pacana
 */
class Permutation {

    /** Set this Permutation to that specified by CYCLES, a string in the
     *  form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     *  is interpreted as a permutation in cycle notation.  Characters not
     *  included in any cycle map to themselves. Whitespace is ignored. */
    /** @param intialize cycles */private Map<Character, Character> _cycles;
    /** @param intialize cycles inverse */
    private Map<Character, Character> _cyclesinv;
     /**
    * @param cycles string of cycles
    * @param alphabet characters */
    Permutation(String cycles, Alphabet alphabet) {
        _alphabet = alphabet;
        String[] cyc = cycles.replaceAll("\\)", "").split("\\(");
        _cycles = new HashMap<Character, Character>(_alphabet.size());
        _cyclesinv = new HashMap<Character, Character>(_alphabet.size());
        for (int i = 1; cyc.length > i; i++) {
            for (int j = 0; cyc[i].length() > j; j++) {
                if (j == cyc[i].length() - 1) {
                    _cycles.put(cyc[i].charAt(j), cyc[i].charAt(0));
                    _cyclesinv.put(cyc[i].charAt(0), cyc[i].charAt(j));
                } else {
                    _cycles.put(cyc[i].charAt(j), cyc[i].charAt(j + 1));
                    _cyclesinv.put(cyc[i].charAt(j + 1), cyc[i].charAt(j));
                }
            }
        }

    }
    /** returns cycles. */
    public Map<Character, Character> getcycles() {
        return _cycles;
    }
    /** Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
     *  c0c1...cm. */
    private void addCycle(String cycle) {
    }
    /** returns alphabet. */
    public Alphabet getalphabet() {
        return _alphabet;
    }

    /** Return the value of P modulo the size of this permutation. */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    /** Returns the size of the alphabet I permute. */
    int size() {
        return _alphabet.size();
    }
    /** Return the result of applying this permutation to P modulo the
     *  alphabet size. */
    int permute(int p) {
        char ind = _alphabet.toChar(p);
        return _alphabet.toInt(permute(ind));
    }

    /** Return the result of applying the inverse of this permutation
     *  to  C modulo the alphabet size. */
    int invert(int c) {
        char ind = _alphabet.toChar(c);
        return  _alphabet.toInt(invert(ind));
    }

    /** Return the result of applying this permutation to the index of P
     *  in ALPHABET, and converting the result to a character of ALPHABET. */
    char permute(char p) {
        if (_cycles.containsKey(p)) {
            return _cycles.get(p);
        } else {
            return p;
        }
    }

    /** Return the result of applying the inverse of this permutation to C. */
    char invert(char c) {
        if (_cyclesinv.containsKey(c)) {
            return _cyclesinv.get(c);
        } else {
            return c;
        }
    }

    /** Return the alphabet used to initialize this Permutation. */
    Alphabet alphabet() {
        return _alphabet;
    }

    /** Return true iff this permutation is a derangement (i.e., a
     *  permutation for which no value maps to itself). */
    boolean derangement() {
        return true;
    }
    /** Alphabet of this permutation. */
    private Alphabet _alphabet;
}

