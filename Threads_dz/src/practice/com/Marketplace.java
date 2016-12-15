package practice.com;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by kirill on 14.12.16.
 */

public class Marketplace {
    static BlockingQueue<String> storage;
    //    static final Object lockObj = new Object();
    private int numFarmers, numSellers, numItems, timeToCheckAnItem,
             sellersTakeAmount, sellerTimeToLeave, sellerReturnTime;
    private double p;

    private String tmpFarmersStorage[], tmpSellersStorage[];
    private Random rd = new Random();
    private volatile boolean _hasItemsToDeliver = false, _storageOpened = false;

    public Marketplace(int numFarmers, int numSellers, int timeToCheckAnItem,
                       int sellerTimeToLeave,
                       int sellerReturnTime, double p) {
        this.numFarmers = numFarmers;
        this.numSellers = numSellers;
        this.timeToCheckAnItem = timeToCheckAnItem;
        this.sellerTimeToLeave = sellerTimeToLeave;
        this.sellerReturnTime = sellerReturnTime;
        this.p = p;
        storage = new LinkedBlockingQueue<>();
    }

    boolean workWithFarmer = false;

    class Farmer {
        void generateSomeItems() {
//            numItems = 10 + rd.nextInt(991);
            numItems = 2 + rd.nextInt(10);
            String[] arr = new String[numItems];
            for (int i = 0; i < numItems; ++i) {
                arr[i] = "Item #" + i;
            }
            tmpFarmersStorage = arr;
        }

        void deliverSomeItems() throws InterruptedException {
            synchronized (storage) {
                if (_hasItemsToDeliver) {
                    storage.wait();
                }
                generateSomeItems();
                System.out.printf("Farmer brought %d items %n", tmpFarmersStorage.length);
                workWithFarmer = true;
                _hasItemsToDeliver = true;
                storage.notifyAll();
            }
        }
    }

    class Inspector {
        void checkSomeItems() throws InterruptedException {
            synchronized (storage) {
                if (!_hasItemsToDeliver) {
                    storage.wait();
                }
                filterItems();
                System.out.printf("Inspector filtered farmer's items. %n");
                Arrays.fill(tmpFarmersStorage, null);
                _hasItemsToDeliver = false;
                if (storage.size() > 0) {
                    _storageOpened = true;
                    System.out.printf("Storage opened for sellers. %n");
                    workWithFarmer = false;
                }
                storage.notifyAll();
            }
        }

        private void filterItems() throws InterruptedException {
            for (String item : tmpFarmersStorage) {
                if (generateProbabilityCheck(p)) {
                    TimeUnit.SECONDS.sleep(timeToCheckAnItem);
                    storage.put(item);
                    System.out.printf("%s is good. Put in a storage.%n", item);
                }
            }
        }

        private boolean generateProbabilityCheck(double p) {
            int num = 1 + rd.nextInt(100);
            return !(num <= p * 100);
        }
    }

    class Seller {
        volatile boolean took = false;

        void takeItems() throws InterruptedException {
            synchronized (storage) {
//                sellersTakeAmount = 10 + rd.nextInt(91);
                sellersTakeAmount = 1 + rd.nextInt(5);
                if (!_storageOpened || workWithFarmer) {
                    storage.wait();
                }
                System.out.printf("Seller wants %d items %n", sellersTakeAmount);
                if (storage.size() < sellersTakeAmount) {
                    System.out.printf("Not enough items. Seller waits. %n");
                    storage.wait();
                }
                if (workWithFarmer)
                    storage.wait();
                tmpSellersStorage = new String[sellersTakeAmount];
                for (int i = 0; i < sellersTakeAmount; ++i) {
                    tmpSellersStorage[i] = storage.take();
                    System.out.printf("Seller took %s %n", tmpSellersStorage[i]);
                }
                System.out.printf("Seller took %d items. %n", sellersTakeAmount);
                TimeUnit.SECONDS.sleep(sellerTimeToLeave);
                _storageOpened = false;
                System.out.printf("Storage closed for sellers. %n");
                took = true;
                storage.notifyAll();
            }
        }

        void sellItems() throws InterruptedException {
            Arrays.fill(tmpSellersStorage, null);
            System.out.printf("Seller selled items. %n");
            TimeUnit.SECONDS.sleep(sellerReturnTime);
            took = false;
        }
    }
}

// EOF
