package enigma;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

import static enigma.EnigmaException.*;

/** Enigma simulator.
 *  @author Roland Li
 */
public final class Main {

    /** Process a sequence of encryptions and decryptions, as
     *  specified by ARGS, where 1 <= ARGS.length <= 3.
     *  ARGS[0] is the name of a configuration file.
     *  ARGS[1] is optional; when present, it names an input file
     *  containing messages.  Otherwise, input comes from the standard
     *  input.  ARGS[2] is optional; when present, it names an output
     *  file for processed messages.  Otherwise, output goes to the
     *  standard output. Exits normally if there are no errors in the input;
     *  otherwise with code 1. */
    public static void main(String... args) {
        try {
            new Main(args).process();
            return;
        } catch (EnigmaException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /** Check ARGS and open the necessary files (see comment on main). */
    Main(String[] args) {
        if (args.length < 1 || args.length > 3) {
            throw error("Only 1, 2, or 3 command-line arguments allowed");
        }

        _storedname = "";

        _config = getInput(args[0]);

        if (args.length > 1) {
            _input = getInput(args[1]);
        } else {
            _input = new Scanner(System.in);
        }

        if (args.length > 2) {
            _output = getOutput(args[2]);
        } else {
            _output = System.out;
        }
    }

    /** Return a Scanner reading from the file named NAME. */
    private Scanner getInput(String name) {
        try {
            return new Scanner(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Return a PrintStream writing to the file named NAME. */
    private PrintStream getOutput(String name) {
        try {
            return new PrintStream(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Configure an Enigma machine from the contents of configuration
     *  file _config and apply it to the messages in _input, sending the
     *  results to _output. */
    private void process() {
        Machine M = readConfig();
        if (!_input.hasNext("\\*")) {
            throw error("Message with no config");
        }
        while (_input.hasNextLine()) {
            String storedvalue = _input.nextLine();
            if (storedvalue.isEmpty()) {
                _output.println();
            } else if (storedvalue.startsWith("*")) {
                setUp(M, storedvalue);
            } else {
                int i = 0;
                while (i < M.rotorConfig().size()) {
                    i++;
                }
                String output = M.convert(storedvalue);
                printMessageLine(output);
            }

        }
    }

    /** Return an Enigma machine configured from the contents of configuration
     *  file _config. */
    private Machine readConfig() {
        try {
            String starts = _config.next();
            if (starts.length() < 3) {
                throw error("First line must "
                        + "configure alphabet");
            }
            if (starts.charAt(1) == '-') {
                String[] ends = starts.split("-", 2);
                String alph = "";
                for (String end : ends) {
                    alph = alph + end;
                }
                char[] startend = alph.toCharArray();
                _alphabet = new CharacterRange(startend[0], startend[1]);
            } else {
                _alphabet = new IntegerRange(starts);
            }
            int numRotors = _config.nextInt();
            int pawls = _config.nextInt();
            ArrayList<Rotor> allRotors = new ArrayList<>();
            _config.nextLine();
            while (_config.hasNextLine()) {
                if (_config.hasNext(".*\\)")) {
                    String cycle = allRotors.get
                            (allRotors.size() - 1).permutation().cycles();
                    Scanner storedvalue = new Scanner(_config.nextLine());
                    while (storedvalue.hasNext()) {
                        if (storedvalue.hasNext(".*\\)")) {
                            cycle = cycle + storedvalue.next();
                        } else {
                            throw error("Bad perm format");
                        }
                    }
                    allRotors.get(allRotors.size() - 1).
                            permutation().setCycles(cycle);
                } else {
                    allRotors.add(readRotor());
                }
            }
            return new Machine(_alphabet, numRotors, pawls, allRotors);
        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }
    }

    /** Return a rotor, reading its description from _config. */
    private Rotor readRotor() {
        try {
            Scanner storedvalue = new Scanner(_config.nextLine());
            String name = storedvalue.next();
            String type = storedvalue.next();
            char[] types = type.toCharArray();
            String cycle = "";
            while (storedvalue.hasNext()) {
                if (storedvalue.hasNext(".*\\)")) {
                    cycle = cycle + storedvalue.next();
                } else {
                    throw error("Bad perm format");
                }
            }
            if (types[0] == 'M') {
                return new MovingRotor(name,
                     new Permutation(cycle, _alphabet), type.substring(1));
            } else if (types[0] == 'N') {
                return new FixedRotor(name, new Permutation(cycle, _alphabet));
            } else {
                return new Reflector(name, new Permutation(cycle, _alphabet));
            }
        } catch (NoSuchElementException excp) {
            throw error("bad rotor description");
        }
    }



    /** Set M according to the specification given on SETTINGS,
     *  which must have the format specified in the assignment. */
    private void setUp(Machine M, String settings) {
        Scanner setting = new Scanner(settings);
        String value = setting.next();
        String[] addingrotors = new String[M.numRotors()];
        String plugboardcycles = "";
        ArrayList<String> addedrotors = new ArrayList<String>();
        int i = 0;
        if (value.equals("*")) {
            while (i < M.numRotors()) {
                addingrotors[i] = setting.next();
                i++;
            }
            for (String rotor : addingrotors) {
                if (addedrotors.contains(rotor)) {
                    throw error("Duplicate rotor name");
                } else {
                    addedrotors.add(rotor);
                }
            }
            M.insertRotors(addingrotors);
            int limit = (M.numRotors() - 1);
            if (setting.hasNext("A-Z{" + limit + "}")) {
                throw error("Too many rotors");
            }
            M.setRotors(setting.next());
            while (setting.hasNext(".+\\)")) {
                plugboardcycles += setting.next();
            }
            M.setPlugboard(new Permutation(plugboardcycles, _alphabet));
        }
        int j = 0;
        for (Rotor rotor : M.rotorConfig()) {
            if (rotor.rotates()) {
                j++;
            }
        }
        if (j > M.numPawls()) {
            throw error("Too many moving rotors");
        }

    }

    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters). */
    private void printMessageLine(String msg) {
        int i = 0;
        char[] output = msg.toCharArray();
        while (i < msg.length()) {
            _output.print(output[i]);
            if (i % 5 == 4 && i != msg.length() - 1) {
                _output.print(" ");
            }
            i++;
        }
        _output.println();

    }

    /** Alphabet used in this machine. */
    private Alphabet _alphabet;

    /** Source of input messages. */
    private Scanner _input;

    /** Source of machine configuration. */
    private Scanner _config;

    /** File for encoded/decoded messages. */
    private PrintStream _output;

    /** Storing names of next rotors.*/
    private String _storedname;
}
