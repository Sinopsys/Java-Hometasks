package com.hse.streams;

import java.math.BigInteger;

public class Main {
    public static final int ARGS_NUM = 6;
    public static final int PAR_OR_SEQ = 0;
    public static final int LONG_OR_BIG_INT = 1;
    public static final int N_NUM = 2;
    public static final int M_NUM = 3;
    public static final int C_NUM = 4;
    public static final int FILE_NUM = 5;
    public static final int COMPARE_TO_LESS = 0;


    public static void printUsage() {
        System.out.println("Usage:");
        System.out.println("par/seq, L/B, N, M, C, output file");
        System.out.println("Using type of calculation: par(parallel) or seq(sequential)");
        System.out.println("Using numbers: L - Long, B - BigInteger");
        System.out.println("Program finds all prime numbers in range [N, M]");
        System.out.println("That ends with number C");
        System.out.println("Output is written into file \"output file\", first number is ");
        System.out.println("The quantity of prime numbers and after - all found prime numbers");
    }

    public static void main(String[] args) {
        MyCollection mc = null;
        String path = "";
        boolean parallel = false;
        if (args.length < ARGS_NUM) {
            printUsage();
        } else {

            // if Long was chosen
            if (args[LONG_OR_BIG_INT].equals("L")) {
                System.out.println("Long was chosen");
                mc = new MyCollection(true);
                for (long i = Long.parseLong(args[N_NUM]); i < Long.parseLong(args[M_NUM]); ++i) {
                    MyCollection.getCollection().add(i);
                }
                parallel = args[PAR_OR_SEQ].equals("par");
                path = args[FILE_NUM];
                PrimeCalculator<Long> col = new PrimeCalculator<>(path, Long.parseLong(args[N_NUM]),
                        Long.parseLong(args[M_NUM]), args[C_NUM]);
                col.calculate(parallel);
            }
            //if BigInt was chosen
            else if (args[LONG_OR_BIG_INT].equals("B")) {
                System.out.println("BigInt was chosen");
                mc = new MyCollection(false);
                for (BigInteger i = new BigInteger(args[N_NUM]);
                     i.compareTo(new BigInteger(args[M_NUM])) < COMPARE_TO_LESS; i = i.add(BigInteger.ONE)) {
                    MyCollection.getCollection().add(i);
                }
                parallel = args[PAR_OR_SEQ].equals("par");
                path = args[FILE_NUM];
                PrimeCalculator<BigInteger> col = new PrimeCalculator<>(path, new BigInteger(args[N_NUM]),
                        new BigInteger(args[M_NUM]), args[C_NUM]);
                col.calculate(parallel);
            }

            if (mc == null) {
                printUsage();
                return;
            }
        }
    }
}


// EOF
