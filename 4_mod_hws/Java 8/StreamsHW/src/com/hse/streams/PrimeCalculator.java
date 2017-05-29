package com.hse.streams;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.Arrays;

/**
 * Created by kirill on 25.05.17.
 */

public class PrimeCalculator<T extends Number> {
    private String path;
    T N, M;
    String C;

    /**
     * Prints formatted output to file.
     *
     * @param res Array of prime numbers.
     */
    public void printToFile(Object[] res) {
        try (PrintWriter pw = new PrintWriter(path)) {
            pw.println(res.length + ": " +
                    Arrays.toString(res)
                            .replace(']', '>')
                            .replace('[', '<'));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Constructor.
     *
     * @param path Path to a file to write to.
     * @param N    N, the lowest bound.
     * @param M    M, the upper bound.
     * @param C    C, a string with which prime numvers must end with.
     */
    public PrimeCalculator(String path, T N, T M, String C) {
        this.path = path;
        this.N = N;
        this.M = M;
        this.C = C;
    }

    /**
     * Determines if n ends with the string endsWith.
     *
     * @param n        Either Long number or BigInteger.
     * @param endsWith A string with which n might end with.
     * @return n ends with endsWith.
     */
    private boolean endsWith(Object n, String endsWith) {
        if (n instanceof Long) {
            Long number = (Long) n;
            return Long.toString(number).endsWith(endsWith);
        } else if (n instanceof BigInteger) {
            BigInteger number = (BigInteger) n;
            return String.valueOf(number).endsWith(endsWith);
        }
        throw new NumberFormatException("Wrong type of object. Must be either Long or BigInteger instatnce");
    }

    /**
     * Determines if n is a prime number.
     *
     * @param n A number. Must be an instance of either Long or BigInteger.
     * @return Is n prime or not.
     */
    private boolean isPrime(Object n) {
        if (n instanceof Long) {
            // Standart algorithm.
            Long num = (Long) n;
            if (num % 2 == 0)
                return false;
            for (int i = 3; i * i <= num; i += 2)
                if (num % i == 0) return false;
            return true;
        } else if (n instanceof BigInteger) {
            BigInteger number = (BigInteger) n;
            // If it is not prime for sure then return false.
            //
            if (!number.isProbablePrime(5))
                return false;

            // Then if the number might possibly be a prime number
            // apply known algorithm to it.
            //
            BigInteger two = new BigInteger("2");
            if (!two.equals(number) && BigInteger.ZERO.equals(number.mod(two)))
                return false;

            for (BigInteger i = new BigInteger("3");
                 i.multiply(i).compareTo(number) < 1; i = i.add(two)) {
                if (BigInteger.ZERO.equals(number.mod(i)))
                    return false;
            }
            return true;
        }
        return false;
    }

    /**
     * Calculates and filters prime numbers in the current collection.
     *
     * @param parralel Type of calculations. true -> parallel; false -> sequential.
     */
    public void calculate(boolean parralel) {
        Object[] res;
        // Start measuring time.
        //
        long startTime = System.currentTimeMillis();
        if (parralel) {
            // Get parallelStream and apply methods to current collection.
            //
            res = MyCollection.getCollection()
                    .parallelStream()
                    .filter(x -> endsWith(x, C) && isPrime(x))
                    .sorted()
                    .toArray();
        } else {
            // Get stream and apply methods to current collection.
            //
            res = MyCollection.getCollection()
                    .stream()
                    .filter(x -> endsWith(x, C) && isPrime(x))
                    .toArray();
        }
        // Stop measuring time.
        //
        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println("\nElapsed time = " + elapsedTime + "ms.");
        printToFile(res);
    }
}


// EOF

