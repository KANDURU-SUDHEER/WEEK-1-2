import java.util.*;

class DNSCache {
    static class DNSEntry {
        String domain;
        String ipAddress;
        long expiryTime;

        DNSEntry(String domain, String ipAddress, long ttlSeconds) {
            this.domain = domain;
            this.ipAddress = ipAddress;
            this.expiryTime = System.currentTimeMillis() + (ttlSeconds * 1000);
        }

        boolean isExpired() {
            return System.currentTimeMillis() > expiryTime;
        }
    }

    private final int capacity;
    private int hits = 0;
    private int misses = 0;

    private LinkedHashMap<String, DNSEntry> cache;

    public DNSCache(int capacity) {
        this.capacity = capacity;

        cache = new LinkedHashMap<String, DNSEntry>(capacity, 0.75f, true) {
            protected boolean removeEldestEntry(Map.Entry<String, DNSEntry> eldest) {
                return size() > DNSCache.this.capacity;
            }
        };
    }

    public String resolve(String domain) {

        long start = System.nanoTime();

        if (cache.containsKey(domain)) {

            DNSEntry entry = cache.get(domain);

            if (!entry.isExpired()) {
                hits++;
                System.out.println("Cache HIT → " + entry.ipAddress);
                return entry.ipAddress;
            } else {
                System.out.println("Cache EXPIRED → removing entry");
                cache.remove(domain);
            }
        }

        misses++;
        String ip = queryUpstreamDNS(domain);
        DNSEntry newEntry = new DNSEntry(domain, ip, 5);

        cache.put(domain, newEntry);

        long end = System.nanoTime();
        double timeMs = (end - start) / 1_000_000.0;

        System.out.println("Cache MISS → Query upstream → " + ip + " (" + timeMs + " ms)");

        return ip;
    }

    private String queryUpstreamDNS(String domain) {

        Random rand = new Random();

        return "172.217.14." + (rand.nextInt(200) + 1);
    }

    public void getCacheStats() {

        int total = hits + misses;

        double hitRate = total == 0 ? 0 : ((double) hits / total) * 100;

        System.out.println("\nCache Stats:");
        System.out.println("Hits: " + hits);
        System.out.println("Misses: " + misses);
        System.out.println("Hit Rate: " + String.format("%.2f", hitRate) + "%");
    }

    public void startCleanupThread() {

        Thread cleaner = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(3000);

                    Iterator<Map.Entry<String, DNSEntry>> iterator = cache.entrySet().iterator();

                    while (iterator.hasNext()) {
                        Map.Entry<String, DNSEntry> entry = iterator.next();

                        if (entry.getValue().isExpired()) {
                            iterator.remove();
                            System.out.println("Removed expired entry: " + entry.getKey());
                        }
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        cleaner.setDaemon(true);
        cleaner.start();
    }

    public static void main(String[] args) throws Exception {

        DNSCache dnsCache = new DNSCache(3);

        dnsCache.startCleanupThread();

        dnsCache.resolve("google.com");
        dnsCache.resolve("openai.com");
        dnsCache.resolve("github.com");

        Thread.sleep(1000);

        dnsCache.resolve("google.com");

        Thread.sleep(6000);

        dnsCache.resolve("google.com");

        dnsCache.getCacheStats();
    }
}

