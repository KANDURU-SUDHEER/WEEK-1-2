import java.util.*;

class UsernameChecker {

    private HashMap<String, Integer> usernameToUserId;
    private HashMap<String, Integer> usernameAttempts;
    public UsernameChecker() {
        usernameToUserId = new HashMap<>();
        usernameAttempts = new HashMap<>();
    }

    public boolean registerUser(String username, int userId) {
        if (usernameToUserId.containsKey(username)) {
            System.out.println("Username already taken");
            return false;
        }

        usernameToUserId.put(username, userId);
        return true;
    }


    public boolean checkAvailability(String username) {

        usernameAttempts.put(username,
                usernameAttempts.getOrDefault(username, 0) + 1);

        return !usernameToUserId.containsKey(username);
    }

    public List<String> suggestAlternatives(String username) {

        List<String> suggestions = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {
            String candidate = username + i;

            if (!usernameToUserId.containsKey(candidate)) {
                suggestions.add(candidate);
            }
        }

        String alt = username.replace("_", ".");
        if (!usernameToUserId.containsKey(alt)) {
            suggestions.add(alt);
        }

        return suggestions;
    }

    public String getMostAttempted() {

        String most = null;
        int max = 0;

        for (Map.Entry<String, Integer> entry : usernameAttempts.entrySet()) {

            if (entry.getValue() > max) {
                max = entry.getValue();
                most = entry.getKey();
            }
        }

        return most + " (" + max + " attempts)";
    }

    public static void main(String[] args) {

        UsernameChecker checker = new UsernameChecker();

        checker.registerUser("john_doe", 101);
        checker.registerUser("admin", 102);
        checker.registerUser("test_user", 103);

        System.out.println("checkAvailability('john_doe') → " + checker.checkAvailability("john_doe"));
        System.out.println("checkAvailability('jane_smith') → " + checker.checkAvailability("jane_smith"));

        System.out.println("suggestAlternatives('john_doe') → " + checker.suggestAlternatives("john_doe"));

        for (int i = 0; i < 10; i++) {
            checker.checkAvailability("admin");
        }
        for (int i = 0; i < 5; i++) {
            checker.checkAvailability("john_doe");
        }
        System.out.println("getMostAttempted() → " + checker.getMostAttempted());
    }
}
