package enigma;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import static enigma.EnigmaException.*;

/** Class that represents a complete enigma machine.
 *  @author Roland Li
 */
class Machine {

    /** A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     *  and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     *  available rotors. */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        _alphabet = alpha;
        _numRotors = numRotors;
        _pawls = pawls;
        _allRotors = allRotors;
        _rotorConfig = new ArrayList<Rotor>();
        _plugboard = new Permutation("", _alphabet);
    }

    /** Return the number of rotor slots I have. */
    int numRotors() {
        return _numRotors;
    }

    /** Return the number pawls (and thus rotating rotors) I have. */
    int numPawls() {
        return _pawls;
    }

    /** Set my rotor slots to the rotors named ROTORS from my set of
     *  available rotors (ROTORS[0] names the reflector).
     *  Initially, all rotors are set at their 0 setting. */
    void insertRotors(String[] rotors) {
        _rotorConfig = new ArrayList<Rotor>();
        int i = 0;
        while (i < rotors.length) {
            for (Rotor rotor : _allRotors) {
                if (rotor.name().toUpperCase().equals(rotors[i])) {
                    _rotorConfig.add(rotor);
                }
            }
            i++;
        }
        if (_rotorConfig.size() < rotors.length) {
            throw error("Bad rotor name");
        }
        if (!_rotorConfig.get(0).reflecting()) {
            throw error("Reflector must be placed at beginning");
        }
    }

    /** Set my rotors according to SETTING, which must be a string of
     *  numRotors()-1 upper-case letters. The first letter refers to the
     *  leftmost rotor setting (not counting the reflector).  */
    void setRotors(String setting) {
        int i = 0;
        while (i < _rotorConfig.size()) {
            if (i == 0) {
                _rotorConfig.get(i).set(0);
            } else {
                _rotorConfig.get(i).set(_alphabet.toInt
                        (setting.charAt((i - 1) % _alphabet.size())));
            }
            i++;
        }
    }

    /** Returns the current rotor configuration. */
    ArrayList<Rotor> rotorConfig() {
        return _rotorConfig;
    }

    /** Sets the rotor configuartion to ROTOR. */
    void setRotorConfig(ArrayList<Rotor> rotor) {
        _rotorConfig = rotor;
    }

    /** Set the plugboard to PLUGBOARD. */
    void setPlugboard(Permutation plugboard) {
        _plugboard.addCycle(plugboard.cycles());
    }

    /** Returns the result of converting the input character C (as an
     *  index in the range 0..alphabet size - 1), after first advancing
     *  the machine. */
    int convert(int c) {
        HashSet<Rotor> spinrotor = new HashSet<Rotor>(numRotors() - 1);
        spinrotor.add(_rotorConfig.get(_rotorConfig.size() - 1));
        for (Rotor rotor : _rotorConfig) {
            int rotorindex = _rotorConfig.indexOf(rotor);
            if (rotorindex != 0) {
                if (rotor instanceof MovingRotor
                        && _rotorConfig.get(rotorindex - 1).rotates()
                        && rotor.atNotch()) {
                    spinrotor.add(rotor);
                    spinrotor.add(_rotorConfig.get(rotorindex - 1));
                }
            }
        }
        for (Rotor rotor : spinrotor) {
            rotor.advance();
        }
        c = _plugboard.permute(c);

        int i = _rotorConfig.size() - 1;
        while (i >= 0) {
            Rotor rotor = _rotorConfig.get(i);
            c = rotor.convertForward(c);
            i--;
        }
        int j = 1;
        while (j < _rotorConfig.size()) {
            Rotor rotor = _rotorConfig.get(j);
            c = rotor.convertBackward(c);
            j++;
        }
        c = _plugboard.permute(c);
        return c;
    }

    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {
        String a = msg.toUpperCase();
        String newmsg = "";
        char[] sepmsg = a.toCharArray();
        for (char letter : sepmsg) {
            if (_alphabet.contains(letter)) {
                int index = _alphabet.toInt(letter);
                int convertedindex = convert(index);
                char convertedchar = _alphabet.toChar(convertedindex);
                String addedletter = String.valueOf(convertedchar);
                newmsg = newmsg + addedletter;
            }
        }

        return newmsg;
    }

    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;

    /** Number of rotors. */
    private int _numRotors;

    /** Number of pawls or moving rotors. */
    private int _pawls;

    /** All the available rotors to be inserted. */
    private Collection<Rotor> _allRotors;

    /** The configuration of the rotors. */
    private ArrayList<Rotor> _rotorConfig;

    /** Plugboard representation. */
    private Permutation _plugboard;
}
