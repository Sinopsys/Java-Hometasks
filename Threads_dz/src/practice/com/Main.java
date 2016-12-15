package practice.com;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        int numFarmers, timeToCheckAnItem, farmerSleepTime,
                numSellers, sellerTimeToLeave, sellerReturnTime;
        double p;

        Scanner sc = new Scanner(System.in);
        System.out.println("Number of Farmers (N): ");
        numFarmers = sc.nextInt();
        System.out.println("Number of Sellers (M): ");
        numSellers = sc.nextInt();
        System.out.println("Time to check one item (t1): ");
        timeToCheckAnItem = sc.nextInt();
        System.out.println("Time for seller to leave marketplace (t2): ");
        sellerTimeToLeave = sc.nextInt();
        System.out.println("Farmer sleep time (T1): ");
        farmerSleepTime = sc.nextInt();
        System.out.println("Seller return time (T2): ");
        sellerReturnTime = sc.nextInt();
        System.out.println("Probability of item to be spoiled: ");
        p = sc.nextDouble();

        Marketplace mp = new Marketplace(numFarmers, numSellers,
                timeToCheckAnItem, sellerTimeToLeave, sellerReturnTime, p);
        Marketplace.Farmer farmer = mp.new Farmer();
        Marketplace.Inspector inspector = mp.new Inspector();
        Marketplace.Seller seller = mp.new Seller();

        Thread[] farmersThreads = new Thread[numFarmers];
        Thread[] sellersThreads = new Thread[numSellers];

        for (int i = 0; i < numFarmers; ++i) {
            farmersThreads[i] = new Thread(() -> {
                try {
                    farmer.deliverSomeItems();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }, "F_" + i);
        }

//        Thread f = new Thread(() -> {
//            try {
//                for (int i = 0; i < numFarmers; ++i) {
////                    TimeUnit.SECONDS.sleep(2);
//                    farmer.deliverSomeItems();
//                }
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        });


        Thread insp = new Thread(() -> {
            try {
                for (int j = 0; j < numFarmers; ++j) {
//                    TimeUnit.SECONDS.sleep(3);
                    inspector.checkSomeItems();
                    System.out.printf("Storage contains %d items %n%n", Marketplace.storage.size());
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        for (int j = 0; j < numSellers; ++j) {
            sellersThreads[j] = new Thread(() -> {
                try {
                    if (seller.took)
                        seller.sellItems();
                    seller.takeItems();
                    farmer.deliverSomeItems();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }, "S_" + j);
        }

//        Thread s = new Thread(() -> {
//            try {
//                for (int j = 0; j < numSellers; ++j) {
////                    TimeUnit.SECONDS.sleep(3);
//                    if (seller.took)
//                        seller.sellItems();
//                    seller.takeItems();
//                }
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        });

//        f.start();
        insp.start();
//        s.start();
        for (int i = 0; i < farmersThreads.length; ++i) {
            farmersThreads[i].start();
            TimeUnit.SECONDS.sleep(farmerSleepTime);
        }
        for (int i = 0; i < sellersThreads.length; ++i) {
            sellersThreads[i].start();
        }
    }
}

// EOF
