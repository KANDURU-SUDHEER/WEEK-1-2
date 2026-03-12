import java.util.*;
import java.util.concurrent.*;

class FlashSaleInventory {

    private ConcurrentHashMap<String, Integer> stockMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Queue<Integer>> waitingList = new ConcurrentHashMap<>();
    public void addProduct(String productId, int stock) {
        stockMap.put(productId, stock);
        waitingList.put(productId, new ConcurrentLinkedQueue<>());
    }

    public int checkStock(String productId) {
        return stockMap.getOrDefault(productId, 0);
    }
    public synchronized String purchaseItem(String productId, int userId) {

        int stock = stockMap.getOrDefault(productId, 0);

        if (stock > 0) {
            stockMap.put(productId, stock - 1);

            return "Success: User " + userId +
                    " purchased item. Remaining stock: " + (stock - 1);
        }
        Queue<Integer> queue = waitingList.get(productId);
        queue.add(userId);

        return "Stock unavailable. User " + userId +
                " added to waiting list. Position #" + queue.size();
    }
    public void showWaitingList(String productId) {

        Queue<Integer> queue = waitingList.get(productId);

        System.out.println("Waiting list for " + productId + ":");

        for (Integer user : queue) {
            System.out.println("User " + user);
        }
    }
    public static void main(String[] args) throws InterruptedException {

        FlashSaleInventory system = new FlashSaleInventory();

        String product = "IPHONE15_256GB";

        system.addProduct(product, 5); // limited stock

        ExecutorService executor = Executors.newFixedThreadPool(10);

        for (int i = 1; i <= 10; i++) {

            int userId = i;

            executor.submit(() -> {
                String result = system.purchaseItem(product, userId);
                System.out.println(result);
            });
        }

        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        System.out.println("\nRemaining Stock: " + system.checkStock(product));

        system.showWaitingList(product);
    }
}