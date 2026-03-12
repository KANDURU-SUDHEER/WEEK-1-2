import java.util.*;

class AutocompleteSystem {

    // Trie Node
    class TrieNode {
        Map<Character, TrieNode> children = new HashMap<>();
        Map<String, Integer> sentences = new HashMap<>();
    }

    private TrieNode root;

    public AutocompleteSystem() {
        root = new TrieNode();
    }

    // Global frequency storage
    private HashMap<String, Integer> frequencyMap = new HashMap<>();

    // Add query to system
    public void addQuery(String query, int freq) {

        frequencyMap.put(query, frequencyMap.getOrDefault(query, 0) + freq);

        TrieNode node = root;

        for (char c : query.toCharArray()) {

            node.children.putIfAbsent(c, new TrieNode());
            node = node.children.get(c);

            node.sentences.put(query, frequencyMap.get(query));
        }
    }

    // Search autocomplete suggestions
    public List<String> search(String prefix) {

        TrieNode node = root;

        for (char c : prefix.toCharArray()) {

            if (!node.children.containsKey(c)) {
                return new ArrayList<>();
            }

            node = node.children.get(c);
        }

        // Min heap for top 10 results
        PriorityQueue<Map.Entry<String, Integer>> pq =
                new PriorityQueue<>((a, b) -> a.getValue() - b.getValue());

        for (Map.Entry<String, Integer> entry : node.sentences.entrySet()) {

            pq.offer(entry);

            if (pq.size() > 10) {
                pq.poll();
            }
        }

        List<String> result = new ArrayList<>();

        while (!pq.isEmpty()) {
            result.add(pq.poll().getKey());
        }

        Collections.reverse(result);

        return result;
    }

    // Update frequency when new search happens
    public void updateFrequency(String query) {
        addQuery(query, 1);
    }

    public static void main(String[] args) {

        AutocompleteSystem system = new AutocompleteSystem();

        // preload queries
        system.addQuery("java tutorial", 1234567);
        system.addQuery("javascript", 987654);
        system.addQuery("java download", 456789);
        system.addQuery("java 21 features", 1);
        system.addQuery("java spring boot", 250000);
        system.addQuery("java interview questions", 400000);

        System.out.println("Search results for 'jav':");

        List<String> results = system.search("jav");

        int rank = 1;

        for (String r : results) {
            System.out.println(rank + ". " + r);
            rank++;
        }

        // simulate new searches
        system.updateFrequency("java 21 features");
        system.updateFrequency("java 21 features");
        system.updateFrequency("java 21 features");

        System.out.println("\nAfter new searches:");

        System.out.println(system.search("java"));
    }
}
