import java.util.*;

class PlagiarismDetector {

    // n-gram size
    private static final int N = 5;

    // ngram -> set of document IDs
    private HashMap<String, Set<String>> index = new HashMap<>();

    // documentId -> total ngrams count
    private HashMap<String, Integer> docNgramCount = new HashMap<>();

    // Add document to database
    public void addDocument(String docId, String text) {

        List<String> ngrams = generateNgrams(text);

        docNgramCount.put(docId, ngrams.size());

        for (String gram : ngrams) {

            index.putIfAbsent(gram, new HashSet<>());
            index.get(gram).add(docId);
        }
    }

    // Generate n-grams
    private List<String> generateNgrams(String text) {

        String[] words = text.toLowerCase().split("\\s+");

        List<String> grams = new ArrayList<>();

        for (int i = 0; i <= words.length - N; i++) {

            StringBuilder sb = new StringBuilder();

            for (int j = 0; j < N; j++) {
                sb.append(words[i + j]).append(" ");
            }

            grams.add(sb.toString().trim());
        }

        return grams;
    }

    // Analyze new document
    public void analyzeDocument(String docId, String text) {

        List<String> ngrams = generateNgrams(text);

        System.out.println("Extracted " + ngrams.size() + " n-grams");

        HashMap<String, Integer> matchCount = new HashMap<>();

        for (String gram : ngrams) {

            if (index.containsKey(gram)) {

                for (String existingDoc : index.get(gram)) {

                    matchCount.put(existingDoc,
                            matchCount.getOrDefault(existingDoc, 0) + 1);
                }
            }
        }

        for (String doc : matchCount.keySet()) {

            int matches = matchCount.get(doc);

            double similarity =
                    (matches * 100.0) / Math.max(ngrams.size(), docNgramCount.get(doc));

            System.out.println(
                    "Found " + matches + " matching n-grams with \"" + doc + "\"");

            System.out.printf("Similarity: %.2f%%", similarity);

            if (similarity > 50) {
                System.out.println(" (PLAGIARISM DETECTED)");
            } else if (similarity > 10) {
                System.out.println(" (Suspicious)");
            } else {
                System.out.println();
            }

            System.out.println();
        }
    }

    public static void main(String[] args) {

        PlagiarismDetector detector = new PlagiarismDetector();

        // Existing essays
        String essay1 = "Artificial intelligence is transforming the world by enabling machines to learn from data and improve performance automatically";

        String essay2 = "Machine learning is a branch of artificial intelligence that allows computers to learn patterns from data";

        detector.addDocument("essay_089.txt", essay1);
        detector.addDocument("essay_092.txt", essay2);

        // New submission
        String newEssay = "Artificial intelligence allows machines to learn from data and improve automatically using machine learning techniques";

        System.out.println("Analyzing essay_123.txt\n");

        detector.analyzeDocument("essay_123.txt", newEssay);
    }
}