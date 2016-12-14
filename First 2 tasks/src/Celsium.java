import javax.swing.*;

/**
 * TODO:
 * 1.implement command line application that get input from args;
 * 2.implement the same, but reading input from System.in (and exit by "exit").
 * 3.Do the same with JOptionPane...
 * <p>
 * all that means are covered in Horstman, v2. ch.3
 * TODO: do the same but converting Celseum to Farenheit...
 */
public class Celsium {
    //============================================================== main
    public static void main(String[] args) {
        String choice = "Greetings. Would you like to convert from Cel to Far or visa versa? (1/0)";
        double k = getDouble(choice);
        if (k == 1) {
            double cel = getDouble("Enter number of Cels");
            double fars = ConvertCelToFar(cel);
            displayString(cel + " cels" + " is " + fars + " fars");
        } else if (k == 0) {
            double fars = getDouble("Enter number of Fars");
            double cel = ConvertFarToCel(fars);
            displayString(fars + " fars" + " is " + cel + " cels");
        }
    }

    private static double ConvertCelToFar(double Cel) {
        return Cel * 9 / 5 + 32;
    }

    private static double ConvertFarToCel(double Far) {
        return (Far - 32) * 5 / 9;
    }

    //========================================================= getDouble
    // I/O convenience method to read a double value.
    private static double getDouble(String prompt) {
        String tempStr = "";
        double res;
        try {
            tempStr = JOptionPane.showInputDialog(null, prompt);
            res  = Double.parseDouble(tempStr);
            return res;
        }
        catch (Exception e) {
        displayString("Wrong input!");
            System.exit(0);
        }
        return 0;
    }

    //===================================================== displayString
    // I/O convenience method to display a string in dialog box.
    private static void displayString(String output) {
        JOptionPane.showMessageDialog(null, output);
    }

}
