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
            farmerSleepTime, sellersTakeAmount, sellerTimeToLeave, sellerReturnTime;
    private double p;

    private String tmpFarmersStorage[], tmpSellersStorage[];
    private Random rd = new Random();
    private boolean _hasItemsToDeliver, _storageOpened;

    public Marketplace(int numFarmers, int numSellers, int timeToCheckAnItem,
                       int sellerTimeToLeave, int farmerSleepTime,
                       int sellerReturnTime, double p) {
        this.numFarmers = numFarmers;
        this.numSellers = numSellers;
        this.timeToCheckAnItem = timeToCheckAnItem;
        this.sellerTimeToLeave = sellerTimeToLeave;
        this.farmerSleepTime = farmerSleepTime;
        this.sellerReturnTime = sellerReturnTime;
        this.p = p;
        storage = new LinkedBlockingQueue<>();
    }

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
                System.out.printf("Farmer generated %d items %n", tmpFarmersStorage.length);
                _hasItemsToDeliver = true;
                storage.notifyAll();
            }
        }
    }

    class Inspector {
        void checkSomeItems() throws InterruptedException {
            synchronized (storage) {
                if (!_hasItemsToDeliver) {
                    if (storage.size() > 0)
                        _storageOpened = true;
                    storage.wait();
                }
                filterItems();
                System.out.printf("Inspector filtered farmer's items. %n");
                Arrays.fill(tmpFarmersStorage, null);
                _hasItemsToDeliver = false;
                storage.notifyAll();
            }
        }

        private void filterItems() throws InterruptedException {
            if (tmpFarmersStorage.length != 0)
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
        public void takeItems() throws InterruptedException {
            synchronized (storage) {
                if (!_storageOpened) {
                    storage.wait();
                }
//                sellersTakeAmount = 10 + rd.nextInt(91);
                sellersTakeAmount = 1 + rd.nextInt(5);
                System.out.printf("Seller wants %d items %n", sellersTakeAmount);
                if (storage.size() < sellersTakeAmount) {
                    System.out.printf("Not enough items. Seller waits. %n");
                    storage.wait();
                }
                tmpSellersStorage = new String[sellersTakeAmount];
                for (int i = 0; i < sellersTakeAmount; ++i) {
                    tmpSellersStorage[i] = storage.take();
                }
                System.out.printf("Seller took items. %n");
                TimeUnit.SECONDS.sleep(sellerTimeToLeave);
                _storageOpened = false;
                storage.notifyAll();
                sellItems();
            }
        }

        private void sellItems() throws InterruptedException {
            Arrays.fill(tmpSellersStorage, null);
            TimeUnit.SECONDS.sleep(sellerReturnTime);
        }
    }
}
