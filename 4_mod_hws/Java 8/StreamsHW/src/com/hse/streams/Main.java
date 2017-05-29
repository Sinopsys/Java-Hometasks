package com.hse.streams;

import java.math.BigInteger;

/**
 * Created by kirill on 25.05.17.
 */

public class Main {
    static int argCount = 0;
    // Quantity of arguments.
    //
    public static final int ARGS_NUM = 6;
    // Indices of arguments, to get rid of "magic constants".
    //
    public static final int PAR_OR_SEQ = argCount++;
    public static final int LONG_OR_BIG_INT = argCount++;
    public static final int N_NUM = argCount++;
    public static final int M_NUM = argCount++;
    public static final int C_NUM = argCount++;
    public static final int FILE_NUM = argCount++;
    public static final int COMPARE_TO_LESS = 0;
    // Length of C, in this particular task it is 1.
    //
    public static final int ENDS_WITH_QTY = 1;

    /**
     * Outputs a short reminder of how to use the program.
     */
    public static void printUsage() {
        System.out.println("*****\t*****\t*****");
        System.out.println("Usage:");
        System.out.println("par/seq, L/B, N, M, C, output file");
        System.out.println("Using type of calculation: par(parallel) or seq(sequential)");
        System.out.println("Using numbers: L - Long, B - BigInteger");
        System.out.println("Program finds all prime numbers in range [N, M]");
        System.out.println("That ends with number C");
        System.out.println("N, M, C should be whole numbers and N must be less than M!");
        System.out.println("Output is written into file \"output file\", first number is ");
        System.out.println("The quantity of prime numbers and after - all found prime numbers");
        System.out.println("*****\t*****\t*****");
    }

    public static void main(String[] args) {
        // Placeholders for parameters.
        //
        String path;
        boolean parallel;
        boolean isLong = false;
        long NL = 0, ML = 0, CL = 0;
        BigInteger NB = BigInteger.ZERO,
                MB = BigInteger.ZERO,
                CB = BigInteger.ZERO;

        // Parsing command line args.
        //
        if (args.length < ARGS_NUM) {
            printUsage();
            return;
        } else {
            switch (args[PAR_OR_SEQ]) {
                case "par":
                    parallel = true;
                    break;
                case "seq":
                    parallel = false;
                    break;
                default:
                    printUsage();
                    return;
            }

            switch (args[LONG_OR_BIG_INT]) {
                // If Long was chosen.
                //
                case "L":
                    isLong = true;
                    break;

                // If BigInt was chosen.
                //
                case "B":
                    isLong = false;
                    break;

                // By default just remind user of good input and return.
                //
                default:
                    printUsage();
                    return;
            }

            // Parsing numbers.
            //
            try {
                if (isLong) {
                    NL = Long.parseLong(args[N_NUM]);
                    ML = Long.parseLong(args[M_NUM]);
                    // Check if N < M and C has given length.
                    //
                    if (NL < ML && args[C_NUM].length() == ENDS_WITH_QTY) {
                        CL = Long.parseLong(args[C_NUM]);
                    } else
                        throw new NumberFormatException();
                } else {
                    NB = new BigInteger(args[N_NUM]);
                    MB = new BigInteger(args[M_NUM]);
                    // Check if N < M and C has given length.
                    //
                    if (NB.compareTo(MB) < COMPARE_TO_LESS && args[C_NUM].length() == ENDS_WITH_QTY) {
                        CB = new BigInteger(args[C_NUM]);
                    } else {
                        throw new NumberFormatException();
                    }
                }
            } catch (NumberFormatException e) {
                // If something is wrong print usage and return.
                //
                printUsage();
                return;
            }

            if (isLong) {
                // Initialize collection pupulated with Longs.
                //
                MyCollection.initCollection(true);
                for (long i = NL; i < ML; ++i) {
                    MyCollection.add(i);
                }
                path = args[FILE_NUM];
                PrimeCalculator<Long> colL = new PrimeCalculator<>(path, NL, ML, args[C_NUM]);

                // Calculate prime numbers and output evetything to a file.
                //
                colL.calculate(parallel);
            } else {
                // Initialize collection pupulated with BigIntegers.
                //
                MyCollection.initCollection(false);
                for (BigInteger i = NB; i.compareTo(MB) < COMPARE_TO_LESS; i = i.add(BigInteger.ONE)) {
                    MyCollection.add(i);
                }
                path = args[FILE_NUM];
                PrimeCalculator<BigInteger> colB = new PrimeCalculator<>(path, NB, MB, args[C_NUM]);

                // Calculate prime numbers and output evetything to a file.
                //
                colB.calculate(parallel);
            }
            if (MyCollection.getCollection().equals(null)) {
                printUsage();
                return;
            }
        }
    }
}


// EOF

