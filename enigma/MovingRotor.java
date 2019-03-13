package enigma;

import static enigma.EnigmaException.*;

/** Class that represents a rotating rotor in the enigma machine.
 *  @author Roland Li
 */
class MovingRotor extends Rotor {

    /** A rotor named NAME whose permutation in its default setting is
     *  PERM, and whose notches are at the positions indicated in NOTCHES.
     *  The Rotor is initally in its 0 setting (first character of its
     *  alphabet).
     */
    MovingRotor(String name, Permutation perm, String notches) {
        super(name, perm);
        _notches = notches;

    }
    /** Returns TRUE if the rotor moves. */
    boolean rotates() {
        return true;
    }

    /** Returns TRUE if the moving rotor is at a notch. */
    boolean atNotch() {
        int i = 0;
        while (i < _notches.length()) {
            char value = _notches.charAt(i);
            int index = alphabet().toInt(value);
            if (setting() == index) {
                return true;
            }
            i++;
        } return false;
    }

    @Override
    void advance() {
        if (setting() == alphabet().size() - 1) {
            set(0);
        } else {
            set(setting() + 1);
        }
    }

    /** The alphabet ring's notches. */
    private String _notches;
}
