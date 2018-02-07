package enigma;

import static enigma.EnigmaException.*;

/* Extra Credit Only */

/** An alphabet of encodable characters.  Provides a mapping from characters
 *  to and from indices into the alphabet.
 *  @author keith pacana
 */
class Alphabet {

    /** A new alphabet containing CHARS.  Character number #k has index
     *  K (numbering from 0). No character may be duplicated. */
    /** @param chars alphabet chars */private String _alphabet;
    Alphabet(String chars) {
        _alphabet = "";
        for (int k = 0; chars.length() > k; k++) {
            if (_alphabet.contains(Character.toString(chars.charAt(k)))) {
                throw error("already in alphabet");
            }
            _alphabet = _alphabet + Character.toString(chars.charAt(k));
        }
    }

    /** Returns the size of the alphabet. */
    int size() {
        return _alphabet.length();
    }

    /** Returns true if C is in this alphabet. */
    boolean contains(char c) {
        for (int j = 0; size() > j; j++) {
            if (_alphabet.charAt(j) == c) {
                return true;
            }
        }
        return false;
    }

    /** Returns character number INDEX in the alphabet, where
     *  0 <= INDEX < size(). */
    char toChar(int index) {
        if (index > size() || index < 0) {
            throw error("Index Out of Range");
        }
        return _alphabet.charAt(index);
    }

    /** Returns the index of character C, which must be in the alphabet. */
    int toInt(char c) {
        return _alphabet.indexOf(c);
    }
}

