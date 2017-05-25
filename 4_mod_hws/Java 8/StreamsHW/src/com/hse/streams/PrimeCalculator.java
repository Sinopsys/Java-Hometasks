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

    public PrimeCalculator(String path, T N, T M, String C) {
        this.path = path;
        this.N = N;
        this.M = M;
        this.C = C;
    }

    private boolean endsWith(Object n, String endsWith) {
        if (n instanceof Long) {
            Long number = (Long) n;
            return Long.toString(number).endsWith(endsWith);
        } else if (n instanceof BigInteger) {
            BigInteger number = (BigInteger) n;
            return String.valueOf(number).endsWith(endsWith);
        }
        return false;
    }

    private boolean isPrime(Object n) {
        if (n instanceof Long) {
            Long num = (Long) n;
            if (num % 2 == 0)
                return false;
            for (int i = 3; i * i <= num; i += 2)
                if (num % i == 0) return false;
            return true;
        } else if (n instanceof BigInteger) {
            BigInteger number = (BigInteger) n;
            if (!number.isProbablePrime(5))
                return false;

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

    public void calculate(boolean parralel) {
        Object[] res;
        long startTime = System.currentTimeMillis();
        if (parralel) {
            res = MyCollection.getCollection().parallelStream()
                    .filter(x -> endsWith(x, C) && isPrime(x)).sorted().toArray();
        } else {
            res = MyCollection.getCollection().stream()
                    .filter(x -> endsWith(x, C) && isPrime(x)).toArray();
        }
        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println("\nelapsed time = " + elapsedTime + "ms");
        printToFile(res);
    }
}


// EOF
