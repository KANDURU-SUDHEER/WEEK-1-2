import java.util.*;

class VideoData {
    String videoId;
    String content;

    public VideoData(String videoId, String content) {
        this.videoId = videoId;
        this.content = content;
    }
}

class MultiLevelCache {

    private LinkedHashMap<String, VideoData> L1;
    private final int L1_CAPACITY = 10000;

    private HashMap<String, VideoData> L2;
    private final int L2_CAPACITY = 100000;

    private HashMap<String, Integer> accessCount;
    private final int PROMOTION_THRESHOLD = 3;

    private int L1_hits = 0, L1_misses = 0;
    private int L2_hits = 0, L2_misses = 0;
    private int L3_hits = 0, L3_misses = 0;
    private int totalRequests = 0;
    private double totalTime = 0;

    public MultiLevelCache() {
        L1 = new LinkedHashMap<>(L1_CAPACITY, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, VideoData> eldest) {
                return size() > L1_CAPACITY;
            }
        };

        L2 = new HashMap<>();
        accessCount = new HashMap<>();
    }

    public VideoData getVideo(String videoId) {

        totalRequests++;

        if (L1.containsKey(videoId)) {
            L1_hits++;
            totalTime += 0.5;
            System.out.println(videoId + " → L1 HIT (0.5ms)");
            return L1.get(videoId);
        } else {
            L1_misses++;
            totalTime += 0.5;
            System.out.println(videoId + " → L1 MISS (0.5ms)");
        }

        if (L2.containsKey(videoId)) {
            L2_hits++;
            totalTime += 5;
            System.out.println(videoId + " → L2 HIT (5ms)");

            accessCount.put(videoId, accessCount.getOrDefault(videoId, 0) + 1);
            if (accessCount.get(videoId) >= PROMOTION_THRESHOLD) {
                L1.put(videoId, L2.get(videoId));
                System.out.println(videoId + " → Promoted to L1");
            }

            return L2.get(videoId);
        } else {
            L2_misses++;
            totalTime += 5;
            System.out.println(videoId + " → L2 MISS (5ms)");
        }
        L3_hits++;
        totalTime += 150;
        System.out.println(videoId + " → L3 Database HIT (150ms)");
        VideoData video = new VideoData(videoId, "Content of " + videoId);
        if (L2.size() >= L2_CAPACITY) {
            // Simple eviction: remove random entry (could use LRU)
            String evictKey = L2.keySet().iterator().next();
            L2.remove(evictKey);
            accessCount.remove(evictKey);
        }
        L2.put(videoId, video);
        accessCount.put(videoId, 1);

        return video;
    }

    public void getStatistics() {

        double L1_hit_rate = L1_hits * 100.0 / totalRequests;
        double L2_hit_rate = L2_hits * 100.0 / totalRequests;
        double L3_hit_rate = L3_hits * 100.0 / totalRequests;
        double overall_hit_rate = ((L1_hits + L2_hits + L3_hits) * 100.0) / totalRequests;
        double avg_time = totalTime / totalRequests;

        System.out.println("\nCache Statistics:");
        System.out.println(String.format("L1: Hit Rate %.2f%%, Avg Time: 0.5ms", L1_hit_rate));
        System.out.println(String.format("L2: Hit Rate %.2f%%, Avg Time: 5ms", L2_hit_rate));
        System.out.println(String.format("L3: Hit Rate %.2f%%, Avg Time: 150ms", L3_hit_rate));
        System.out.println(String.format("Overall: Hit Rate %.2f%%, Avg Time: %.2fms", overall_hit_rate, avg_time));
    }

    public static void main(String[] args) {

        MultiLevelCache cache = new MultiLevelCache();
        cache.getVideo("video_123");
        cache.getVideo("video_123");
        cache.getVideo("video_123");
        cache.getVideo("video_999");

        cache.getStatistics();
    }
}