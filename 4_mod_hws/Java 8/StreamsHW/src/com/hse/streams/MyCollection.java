package com.hse.streams;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kirill on 25.05.17.
 */

public class MyCollection {
    private static List<MyNumber<Long>> collectionLong;
    private static List<MyNumber<BigInteger>> collectionBigInt;
    private static boolean isLong;

    public MyCollection(boolean isLong) {
        this.isLong = isLong;
        if (isLong) {
            collectionLong = new ArrayList<>();
        } else {
            collectionBigInt = new ArrayList<>();
        }
    }

    public static List getCollection() {
        if (isLong && collectionLong != null) {
            return collectionLong;
        } else if (collectionBigInt != null) {
            return collectionBigInt;
        }
        return null;
    }

    public static boolean getIsLong() {
        return isLong;
    }
}


// EOF
