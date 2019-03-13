package enigma;

import static enigma.EnigmaException.*;

/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author Roland Li
 */
class Permutation {

    /** Set this Permutation to that specified by CYCLES, a string in the
     *  form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     *  is interpreted as a permutation in cycle notation.  Characters in the
     *  alphabet that are not included in any cycle map to themselves.
     *  Whitespace is ignored. */
    Permutation(String cycles, Alphabet alphabet) {
        _alphabet = alphabet;
        _cycles = cycles;
    }

    /** Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
     *  c0c1...cm. */
    void addCycle(String cycle) {
        _cycles = _cycles + "(" + cycle + ")";
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
        char charofp = _alphabet.toChar(wrap(p));
        int indexincycle = _cycles.indexOf(charofp);
        if (indexincycle == -1) {
            return p;
        } else if (_cycles.charAt(indexincycle + 1) == ')') {
            while (_cycles.charAt(indexincycle) != '(') {
                indexincycle--;
            } char chartoindex = _cycles.charAt(indexincycle + 1);
            return _alphabet.toInt(chartoindex);
        }
        char chartoindex = _cycles.charAt(indexincycle + 1);
        return _alphabet.toInt(chartoindex);
    }

    /** Return the result of applying the inverse of this permutation
     *  to  C modulo the alphabet size. */
    int invert(int c) {
        char charofc = _alphabet.toChar(wrap(c));
        int indexincycle = _cycles.indexOf(charofc);
        if (indexincycle == -1) {
            return c;
        } else if (_cycles.charAt(indexincycle - 1) == '(') {
            while (_cycles.charAt(indexincycle) != ')') {
                indexincycle++;
            } char chartoindex = _cycles.charAt(indexincycle - 1);
            return _alphabet.toInt(chartoindex);
        }
        char chartoindex = _cycles.charAt(indexincycle - 1);
        return _alphabet.toInt(chartoindex);
    }

    /** Return the result of applying this permutation to the index of P
     *  in ALPHABET, and converting the result to a character of ALPHABET. */
    char permute(char p) {
        int indexincycle = _cycles.indexOf(p);
        if (indexincycle == -1) {
            return p;
        } else if (_cycles.charAt(indexincycle + 1) == ')') {
            while (_cycles.charAt(indexincycle) != '(') {
                indexincycle--;
            } return _cycles.charAt(indexincycle + 1);
        }
        return _cycles.charAt(indexincycle + 1);


    }

    /** Return the result of applying the inverse of this permutation to C. */
    int invert(char c) {
        int indexincycle = _cycles.indexOf(c);
        if (indexincycle == -1) {
            return c;
        } else if (_cycles.charAt(indexincycle - 1) == '(') {
            while (_cycles.charAt(indexincycle) != ')') {
                indexincycle++;
            } return _cycles.charAt(indexincycle - 1);
        }
        return _cycles.charAt(indexincycle - 1);
    }

    /** Return the alphabet used to initialize this Permutation. */
    Alphabet alphabet() {
        return _alphabet;
    }

    /** Return the cycles. */
    String cycles() {
        return _cycles;
    }

    /** Set the cycles of the permutation to CYCLE. */
    void setCycles(String cycle) {
        _cycles = cycle;
    }

    /** Return true iff this permutation is a derangement (i.e., a
     *  permutation for which no value maps to itself). */
    boolean derangement() {
        int i = 0;
        while (i < size()) {
            if (permute(_alphabet.toChar(i)) == _alphabet.toChar(i)) {
                return false;
            }
        } return true;
    }

    /** Alphabet of this permutation. */
    private Alphabet _alphabet;

    /** Cycles of this permutation. */
    private String _cycles;

}
