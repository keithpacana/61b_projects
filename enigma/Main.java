package enigma;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

import static enigma.EnigmaException.*;

/** Enigma simulator.
 *  @author keith pacana
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
    private String setng;
    /** set up machine and rotor. */
    private void process() {
        Machine lol = readConfig();
        if (_input.hasNext("\\*")) {
            _input.next();
            setng = _input.nextLine();
            setUp(lol, setng);
        } else {
            throw error("not initialized");
        }
        while (_input.hasNextLine()) {
            String msg = _input.nextLine();
            if (msg.isEmpty()) {
                _output.println(msg);
            } else if (msg.startsWith("*")) {
                setng = msg.substring(2).trim();
                setUp(lol, setng);
            } else {
                printMessageLine(lol.convert(msg));
            }
        }
    }

    /** Return an Enigma machine configured from the contents of configuration
     *  file _config. */
    /** @param intialize # of rotors */private int numrotors;
    /** @param intialize # of pawls */private int pawls;
    /** @param intialize alphabet */private String xalphabet;
    /** all rotors. */private ArrayList<Rotor> allrotors;
    /** @return Machine*/
    private Machine readConfig() {
        try {
            _alphabet = new Alphabet(_config.nextLine());
            numrotors = _config.nextInt();
            pawls = _config.nextInt();
            allrotors = new ArrayList<Rotor>();
            while (_config.hasNext()) {
                Rotor b = readRotor();
                allrotors.add(b);
            }
            return new Machine(_alphabet, numrotors, pawls, allrotors);
        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }
    }

    /** Return a rotor, reading its description from _config. */
    private Rotor readRotor() {
        try {
            String rotorname = _config.next();
            String moving = _config.next();
            String cycles = "";
            Permutation x;
            int i = 0;
            while (_config.hasNext("\\(.*")) {
                String a = _config.next();
                if (a.contains(")")) {
                    cycles = cycles + a;
                } else {
                    throw error("bad conf");
                }
            }
            x = new Permutation(cycles, _alphabet);
            String m = Character.toString(moving.charAt(0));
            if (m.contentEquals("M")) {
                MovingRotor movrot = new MovingRotor(rotorname, x,
                    moving.substring(1));
                return movrot;
            } else if (m.contentEquals("N")) {
                FixedRotor fixrot = new FixedRotor(rotorname, x);
                return fixrot;
            } else if (m.contentEquals("R")) {
                Reflector ref = new Reflector(rotorname, x);
                return ref;
            } else {
                throw error("no description");
            }
        } catch (NoSuchElementException excp) {
            throw error("bad rotor description");
        }
    }
    /** Set M according to the specification given on SETTINGS,
     *  which must have the format specified in the assignment. */
    private void setUp(Machine M, String settings) {
        Scanner y = new Scanner(settings);
        try {
            String[] a = new String[numrotors];
            int nummoving;
            for (int i = 0; numrotors > i; i++) {
                a[i] = y.next();
            }
            boolean x = a[1].equals("BETA");
            M.insertRotors(a);
            String cycles = "";
            String setting = y.next();
            M.setRotors(setting);
            while (y.hasNext("\\(.*")) {
                cycles += y.next();
            }
            Permutation in = new Permutation(cycles, _alphabet);
            M.setPlugboard(in);
        } catch (NoSuchElementException excp) {
            throw error("bad rotor type");
        }
    }
    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters). */
    private void printMessageLine(String msg) {
        String z = "";
        int i = 5;
        for (char x: msg.toCharArray()) {
            if (i == 0) {
                _output.print(" ");
                i = 5;
            }
            _output.print(x);
            i -= 1;
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
}
