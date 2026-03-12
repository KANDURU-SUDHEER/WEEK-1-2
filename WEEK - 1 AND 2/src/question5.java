import java.util.*;
import java.util.concurrent.*;

class RealTimeAnalytics {
    private ConcurrentHashMap<String, Integer> pageViews = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Set<String>> uniqueVisitors = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Integer> trafficSources = new ConcurrentHashMap<>();
    public void processEvent(String url, String userId, String source) {
        pageViews.merge(url, 1, Integer::sum);

        uniqueVisitors.putIfAbsent(url, ConcurrentHashMap.newKeySet());
        uniqueVisitors.get(url).add(userId);

        trafficSources.merge(source, 1, Integer::sum);
    }
    public List<Map.Entry<String, Integer>> getTopPages() {

        PriorityQueue<Map.Entry<String, Integer>> pq =
                new PriorityQueue<>(Map.Entry.comparingByValue());

        for (Map.Entry<String, Integer> entry : pageViews.entrySet()) {

            pq.offer(entry);

            if (pq.size() > 10) {
                pq.poll();
            }
        }

        List<Map.Entry<String, Integer>> result = new ArrayList<>(pq);

        result.sort((a, b) -> b.getValue() - a.getValue());

        return result;
    }

    public void getDashboard() {

        System.out.println("\n===== REAL-TIME DASHBOARD =====");

        System.out.println("\nTop Pages:");

        List<Map.Entry<String, Integer>> topPages = getTopPages();

        int rank = 1;

        for (Map.Entry<String, Integer> entry : topPages) {

            String page = entry.getKey();
            int views = entry.getValue();
            int unique = uniqueVisitors.get(page).size();

            System.out.println(rank + ". " + page +
                    " - " + views + " views (" + unique + " unique)");

            rank++;
        }

        System.out.println("\nTraffic Sources:");

        for (Map.Entry<String, Integer> source : trafficSources.entrySet()) {
            System.out.println(source.getKey() + " → " + source.getValue());
        }

        System.out.println("===============================\n");
    }

    public static void main(String[] args) throws Exception {

        RealTimeAnalytics analytics = new RealTimeAnalytics();

        String[] pages = {
                "/article/breaking-news",
                "/sports/championship",
                "/tech/ai-future",
                "/politics/election",
                "/entertainment/movie-review"
        };

        String[] sources = {"google", "facebook", "direct", "twitter"};

        Random rand = new Random();
        Thread eventGenerator = new Thread(() -> {

            for (int i = 0; i < 100; i++) {

                String url = pages[rand.nextInt(pages.length)];
                String userId = "user_" + rand.nextInt(50);
                String source = sources[rand.nextInt(sources.length)];

                analytics.processEvent(url, userId, source);

                try {
                    Thread.sleep(50); // simulate incoming traffic
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        Thread dashboardThread = new Thread(() -> {

            while (true) {

                analytics.getDashboard();

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        eventGenerator.start();
        dashboardThread.start();
        eventGenerator.join();
    }
}
