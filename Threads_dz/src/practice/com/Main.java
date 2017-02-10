package practice.com;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;


public class Main {

    public static void main(String[] args) throws InterruptedException {
        // some default values
        int numFarmers = 3, numSellers = 2;
        float timeToCheckAnItem = 1, farmerSleepTime = 1,
                sellerTimeToLeave = 1, sellerReturnTime = 1;
        double p = 0.5;
        Scanner sc = new Scanner(System.in);
        if (args.length == 0) {
            System.out.println("Arguments should go in this order: ");
            System.out.println("Number of Farmers (N): ");
//        numFarmers = sc.nextInt();
            System.out.println("Number of Sellers (M): ");
//        numSellers = sc.nextInt();
            System.out.println("Time to check one item (t1): ");
//        timeToCheckAnItem = sc.nextInt();
            System.out.println("Time for seller to leave marketplace (t2): ");
//        sellerTimeToLeave = sc.nextInt();
            System.out.println("Farmer sleep time (T1): ");
//        farmerSleepTime = sc.nextInt();
            System.out.println("Seller return time (T2): ");
//        sellerReturnTime = sc.nextInt();
            System.out.println("Probability of item to be spoiled: ");
//        p = sc.nextDouble();
        } else {
            numFarmers = Integer.parseInt(args[0]);
            numSellers = Integer.parseInt(args[1]);
            timeToCheckAnItem = Integer.parseInt(args[2]);
            sellerTimeToLeave = Integer.parseInt(args[3]);
            farmerSleepTime = Integer.parseInt(args[4]);
            sellerReturnTime = Integer.parseInt(args[5]);
            p = Double.parseDouble(args[6]);
        }

        System.out.println("Launch all threads at once (1 - default) or choose which to run (2)?");
        int choice = sc.nextInt();
        int[] chosenFarmerThreads = null, chosenSellerThreads = null;
        int len;
        if (choice == 2) {
            System.out.print("Enter number of Farmer threads to run: ");
            if ((len = sc.nextInt()) > numFarmers) {
                System.out.println("Invalid value. It must be leq to numFarmers");
                return;
            }
            chosenFarmerThreads = new int[len];
            System.out.println("Enter which Farmer threads to run (e.g. 4 or 7, starts from 0!)");
            for (int i = 0; i < len; ++i) {
                chosenFarmerThreads[i] = sc.nextInt();
            }
            System.out.print("Enter number of Seller threads to run: ");
            if ((len = sc.nextInt()) > numSellers) {
                System.out.println("Invalid value. It must be leq to numSellers");
                return;
            }
            chosenSellerThreads = new int[len];
            System.out.println("Enter which Seller threads to run (e.g. 4 or 7, starts from 0!)");
            for (int i = 0; i < len; ++i) {
                chosenSellerThreads[i] = sc.nextInt();
            }
        }

        if (chosenFarmerThreads != null)
            numFarmers = chosenFarmerThreads.length;
        if (chosenSellerThreads != null)
            numSellers = chosenSellerThreads.length;

        Marketplace mp = new Marketplace(numFarmers, numSellers,
                timeToCheckAnItem, sellerTimeToLeave, sellerReturnTime, p);
        Marketplace.Farmer farmer = mp.new Farmer();
        Marketplace.Inspector inspector = mp.new Inspector();
        Marketplace.Seller seller = mp.new Seller();

        Thread[] farmersThreads = new Thread[numFarmers];
        Thread[] sellersThreads = new Thread[numSellers];

        for (int i = 0; i < numFarmers; ++i) {
            final int kk = i;
            if (chosenFarmerThreads != null && IntStream.of(chosenFarmerThreads).anyMatch(x -> x == kk)) {
                farmersThreads[i] = new Thread(() -> {
                    try {
                        farmer.deliverSomeItems();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }, "F_" + i);
            } else {
                farmersThreads[i] = new Thread(() -> {
                    try {
                        farmer.deliverSomeItems();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }, "F_" + i);
            }
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
                for (int j = 0; j < farmersThreads.length; ++j) {
//                    TimeUnit.SECONDS.sleep(3);
                    inspector.checkSomeItems();
                    System.out.printf("Storage contains %d items %n%n", Marketplace.storage.size());
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        for (int j = 0; j < numSellers; ++j) {
            final int kk = j;
            if (chosenSellerThreads != null && IntStream.of(chosenSellerThreads).anyMatch(x -> x == kk)) {
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
            } else {
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
            TimeUnit.SECONDS.sleep((long) farmerSleepTime);
        }
        for (int i = 0; i < sellersThreads.length; ++i) {
            sellersThreads[i].start();
        }
    }
}

// EOF
