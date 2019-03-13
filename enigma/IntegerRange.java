package enigma;

import static enigma.EnigmaException.*;

/** An Alphabet consisting of integers in a certain range in
 *  order.
 *  @author Roland Li
 */
class IntegerRange extends Alphabet {

    /** An alphabet consisting of all characters in NUMBERS. */
    IntegerRange(String numbers) {
        _numbers = numbers;
        _separatedint = numbers.toCharArray();
    }

    @Override
    int size() {
        return _numbers.length();
    }

    @Override
    boolean contains(char ch) {
        for (char integer : _separatedint) {
            if (integer == ch) {
                return true;
            }
        } return false;
    }

    @Override
    char toChar(int index) {
        if (!contains(_numbers.charAt(index))) {
            throw error("integer index out of range");
        }
        return _numbers.charAt(index);
    }

    @Override
    int toInt(char ch) {
        if (!contains(ch)) {
            throw error("integer out of range");
        }
        return _numbers.indexOf(ch);
    }

    /** Range of integers in this Alphabet. */
    private String _numbers;

    /** Split up numbers string. */
    private char[] _separatedint;

}
