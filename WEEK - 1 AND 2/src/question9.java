import java.util.*;
import java.time.*;
import java.time.format.DateTimeFormatter;

class Transaction {
    int id;
    int amount;
    String merchant;
    String account;
    LocalDateTime time;

    public Transaction(int id, int amount, String merchant, String account, String timeStr) {
        this.id = id;
        this.amount = amount;
        this.merchant = merchant;
        this.account = account;
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm");
        this.time = LocalDateTime.of(LocalDate.now(), LocalTime.parse(timeStr, fmt));
        this.merchant = merchant;
        this.account = account;
    }

    @Override
    public String toString() {
        return "id:" + id + ", amount:" + amount + ", merchant:" + merchant + ", account:" + account;
    }
}

class TransactionAnalyzer {

    List<Transaction> transactions;

    public TransactionAnalyzer(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public List<List<Transaction>> findTwoSum(int target) {

        List<List<Transaction>> result = new ArrayList<>();
        Map<Integer, Transaction> map = new HashMap<>();

        for (Transaction t : transactions) {
            int complement = target - t.amount;
            if (map.containsKey(complement)) {
                result.add(Arrays.asList(map.get(complement), t));
            }
            map.put(t.amount, t);
        }
        return result;
    }

    public List<List<Transaction>> findTwoSumTimeWindow(int target, int minutes) {

        List<List<Transaction>> result = new ArrayList<>();
        Map<Integer, List<Transaction>> map = new HashMap<>();

        for (Transaction t : transactions) {
            int complement = target - t.amount;
            if (map.containsKey(complement)) {
                for (Transaction prev : map.get(complement)) {
                    Duration diff = Duration.between(prev.time, t.time).abs();
                    if (diff.toMinutes() <= minutes) {
                        result.add(Arrays.asList(prev, t));
                    }
                }
            }
            map.putIfAbsent(t.amount, new ArrayList<>());
            map.get(t.amount).add(t);
        }

        return result;
    }


    public List<List<Transaction>> findKSum(int k, int target) {
        List<List<Transaction>> result = new ArrayList<>();
        Collections.sort(transactions, Comparator.comparingInt(t -> t.amount));
        kSumHelper(transactions, k, target, 0, new ArrayList<>(), result);
        return result;
    }

    private void kSumHelper(List<Transaction> txs, int k, int target, int start,
                            List<Transaction> path, List<List<Transaction>> result) {

        if (k == 2) { // two-sum
            Map<Integer, Transaction> map = new HashMap<>();
            for (int i = start; i < txs.size(); i++) {
                Transaction t = txs.get(i);
                int complement = target - t.amount;
                if (map.containsKey(complement)) {
                    List<Transaction> combo = new ArrayList<>(path);
                    combo.add(map.get(complement));
                    combo.add(t);
                    result.add(combo);
                }
                map.put(t.amount, t);
            }
            return;
        }

        for (int i = start; i < txs.size(); i++) {
            path.add(txs.get(i));
            kSumHelper(txs, k - 1, target - txs.get(i).amount, i + 1, path, result);
            path.remove(path.size() - 1);
        }
    }

    public List<Map<String, Object>> detectDuplicates() {

        Map<String, Map<String, Set<String>>> map = new HashMap<>();

        for (Transaction t : transactions) {
            map.putIfAbsent(t.merchant, new HashMap<>());
            Map<String, Set<String>> amountMap = map.get(t.merchant);
            amountMap.putIfAbsent(String.valueOf(t.amount), new HashSet<>());
            amountMap.get(String.valueOf(t.amount)).add(t.account);
        }

        List<Map<String, Object>> result = new ArrayList<>();

        for (String merchant : map.keySet()) {
            for (String amt : map.get(merchant).keySet()) {
                Set<String> accounts = map.get(merchant).get(amt);
                if (accounts.size() > 1) {
                    Map<String, Object> entry = new HashMap<>();
                    entry.put("amount", Integer.parseInt(amt));
                    entry.put("merchant", merchant);
                    entry.put("accounts", accounts);
                    result.add(entry);
                }
            }
        }

        return result;
    }

    public static void main(String[] args) {

        List<Transaction> txs = Arrays.asList(
                new Transaction(1, 500, "Store A", "acc1", "10:00"),
                new Transaction(2, 300, "Store B", "acc2", "10:15"),
                new Transaction(3, 200, "Store C", "acc3", "10:30"),
                new Transaction(4, 500, "Store A", "acc2", "11:00"),
                new Transaction(5, 200, "Store D", "acc4", "11:05")
        );

        TransactionAnalyzer analyzer = new TransactionAnalyzer(txs);

        System.out.println("Classic Two-Sum (target=500):");
        List<List<Transaction>> twoSum = analyzer.findTwoSum(500);
        for (List<Transaction> pair : twoSum) {
            System.out.println(pair);
        }

        System.out.println("\nTwo-Sum within 60 mins (target=500):");
        List<List<Transaction>> twoSumWindow = analyzer.findTwoSumTimeWindow(500, 60);
        for (List<Transaction> pair : twoSumWindow) {
            System.out.println(pair);
        }

        System.out.println("\nK-Sum k=3, target=1000:");
        List<List<Transaction>> ksum = analyzer.findKSum(3, 1000);
        for (List<Transaction> combo : ksum) {
            System.out.println(combo);
        }

        System.out.println("\nDuplicate detection:");
        List<Map<String, Object>> duplicates = analyzer.detectDuplicates();
        for (Map<String, Object> dup : duplicates) {
            System.out.println(dup);
        }
    }
}
