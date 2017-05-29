package com.hse.streams;

import com.sun.javaws.exceptions.InvalidArgumentException;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kirill on 25.05.17.
 */

/**
 * A singleton class which holds either a collection of Longs or BigIntegers.
 */
public class MyCollection {
    private static List<Long> collectionLong;
    private static List<BigInteger> collectionBigInt;
    private static boolean mIsLong;

    /**
     * Singleton factory.
     * Initializes a collection of a chosen type.
     *
     * @param isLong true -> initialization of Longs collection;
     *               false -> initialization of BigInts collection.
     */
    public static void initCollection(boolean isLong) {
        mIsLong = isLong;
        if (isLong) {
            collectionLong = new ArrayList<>();
        } else {
            collectionBigInt = new ArrayList<>();
        }
    }

    /**
     * Gets an instance of an initialized collection.
     *
     * @return Initialized collection.
     */
    public static List getCollection() {
        if (mIsLong && collectionLong != null) {
            return collectionLong;
        } else if (collectionBigInt != null) {
            return collectionBigInt;
        }
        return null;
    }

    /**
     * Appends a given number to the current collection.
     *
     * @param num Either a Long or BigInteger.
     */
    public static void add(Object num) throws NumberFormatException {
        if (mIsLong && collectionLong != null && num instanceof Long) {
            collectionLong.add((Long) num);
            return;
        } else if (collectionBigInt != null && num instanceof BigInteger) {
            collectionBigInt.add((BigInteger) num);
            return;
        }
        throw new NumberFormatException("Parameter must be either Long or BigInteger.");
    }

    public static boolean getIsLong() {
        return mIsLong;
    }
}


// EOF

