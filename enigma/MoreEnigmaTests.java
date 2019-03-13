/** Some extra tests for Enigma.
 *  @author Roland Li
 *
 */
package enigma;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.Test;
import static org.junit.Assert.*;


public class MoreEnigmaTests {
    @Test
    public void testInvertChar() {
        Permutation p = new Permutation("(PNH) (ABDFIKLZYXW) (JC)",
                new CharacterRange('A', 'Z'));
        assertEquals(p.invert('B'), 'A');
        assertEquals(p.invert('G'), 'G');
    }

    @Test
    public void testPermuteChar() {

    }

    @Test
    public void testDerangement() {

    }

    @Test
    public void testDoubleStep() {
        Alphabet ac = new CharacterRange('A', 'D');
        Rotor one = new Reflector("R1",
                new Permutation("(AC) (BD)", ac));
        Rotor two = new MovingRotor("R2",
                new Permutation("(ABCD)", ac), "C");
        Rotor three = new MovingRotor("R3",
                new Permutation("(ABCD)", ac), "C");
        Rotor four = new MovingRotor("R4",
                new Permutation("(ABCD)", ac), "C");
        String setting = "AAA";
        Rotor[] machineRotors = {one, two, three, four};
        String[] rotors = {"R1", "R2", "R3", "R4"};
        Machine mach = new Machine(ac, 4, 3,
                new ArrayList<>(Arrays.asList(machineRotors)));
        mach.insertRotors(rotors);
        mach.setRotors(setting);
        assertEquals("AAAA", getSetting(ac, machineRotors));
        mach.convert('a');
        assertEquals("AAAB", getSetting(ac, machineRotors));
        mach.convert('a');
        assertEquals("AAAC", getSetting(ac, machineRotors));
        mach.convert('a');
        assertEquals("AABD", getSetting(ac, machineRotors));
        mach.convert('a');
        assertEquals("AABA", getSetting(ac, machineRotors));
        mach.convert('a');
        assertEquals("AABB", getSetting(ac, machineRotors));
        mach.convert('a');
        assertEquals("AABC", getSetting(ac, machineRotors));
        mach.convert('a');
        assertEquals("AACD", getSetting(ac, machineRotors));
        mach.convert('a');
        assertEquals("ABDA", getSetting(ac, machineRotors));
    }
    @Test
    public void testConvert() {
        Alphabet ac = new CharacterRange('A', 'Z');
        Rotor one = new Reflector("R1",
                new Permutation("(AE) (BN) (CK) (DQ) (FU) (GY) (HW) "
                        + "(IJ) (LO) (MP) (RX) (SZ) (TV)", ac));
        Rotor two = new FixedRotor("R2",
                new Permutation("(ALBEVFCYODJWUGNMQTZSKPR)"
                        + "(HIX)", ac));
        Rotor three = new MovingRotor("R3",
                new Permutation("(ABDHPEJT) "
                        + "(CFLVMZOYQIRWUKXSG) (N)", ac),
                "V");
        Rotor four = new MovingRotor("R4",
                new Permutation("(AEPLIYWCOXMRFZBSTGJQNH) "
                        + "(DV) (KU)", ac),
                "J");
        Rotor five = new MovingRotor("R5",
                new Permutation("(AELTPHQXRU) (BKNW) "
                        + "(CMOY) (DFG) (IV) (JZ) (S)", ac),
                "Q");
        String setting = "AXLE";
        Rotor[] machineRotors = {one, two, three, four, five};
        String[] rotors = {"R1", "R2", "R3", "R4", "R5"};
        Machine mach = new Machine(ac, 5, 3,
                new ArrayList<>(Arrays.asList(machineRotors)));
        mach.insertRotors(rotors);
        mach.setRotors(setting);
        mach.setPlugboard(new Permutation("(YF) (HZ)", ac));
        assertEquals("AAXLE", getSetting(ac, machineRotors));
        assertEquals(25, mach.convert(24));

    }

    @Test
    public void testMsg() {
        Alphabet ac = new CharacterRange('A', 'Z');
        Rotor one = new Reflector("R1",
                new Permutation("(AE) (BN) (CK) (DQ) (FU) "
                        + "(GY) (HW) (IJ) (LO) (MP) (RX) (SZ) (TV)", ac));
        Rotor two = new FixedRotor("R2",
                new Permutation("(ALBEVFCYODJWUGNMQTZSKPR)(HIX)", ac));
        Rotor three = new MovingRotor("R3",
                new Permutation("(ABDHPEJT) (CFLVMZOYQIRWUKXSG) (N)", ac), "V");
        Rotor four = new MovingRotor("R4",
                new Permutation("(AEPLIYWCOXMRFZBSTGJQNH) (DV) (KU)", ac), "J");
        Rotor five = new MovingRotor("R5",
                new Permutation("(AELTPHQXRU) (BKNW) (CMOY)"
                        + " (DFG) (IV) (JZ) (S)", ac), "Q");
        String setting = "AXLE";
        Rotor[] machineRotors = {one, two, three, four, five};
        String[] rotors = {"R1", "R2", "R3", "R4", "R5"};
        Machine mach = new Machine(ac, 5, 3,
                new ArrayList<>(Arrays.asList(machineRotors)));
        mach.insertRotors(rotors);
        mach.setRotors(setting);
        assertEquals("AAXLE", getSetting(ac, machineRotors));
    }

    /** Helper method to get the String representation of the
     * current Rotor settings. */
    private String getSetting(Alphabet alph, Rotor[] machineRotors) {
        String currSetting = "";
        for (Rotor r : machineRotors) {
            currSetting += alph.toChar(r.setting());
        }
        return currSetting;
    }
}
