import java.util.concurrent.*;
import java.util.*;

class RateLimiter {

    // Token Bucket class
    static class TokenBucket {

        int maxTokens;
        double refillRate; // tokens per second
        double tokens;
        long lastRefillTime;

        public TokenBucket(int maxTokens, double refillRate) {
            this.maxTokens = maxTokens;
            this.refillRate = refillRate;
            this.tokens = maxTokens;
            this.lastRefillTime = System.currentTimeMillis();
        }

        // refill tokens based on time
        private void refill() {

            long now = System.currentTimeMillis();
            double seconds = (now - lastRefillTime) / 1000.0;

            double refill = seconds * refillRate;

            tokens = Math.min(maxTokens, tokens + refill);

            lastRefillTime = now;
        }

        // attempt request
        public synchronized boolean allowRequest() {

            refill();

            if (tokens >= 1) {
                tokens -= 1;
                return true;
            }

            return false;
        }

        public int remainingTokens() {
            return (int) tokens;
        }
    }

    // clientId -> TokenBucket
    private ConcurrentHashMap<String, TokenBucket> clients = new ConcurrentHashMap<>();

    private static final int MAX_REQUESTS = 1000;

    private static final int WINDOW_SECONDS = 3600;

    private static final double REFILL_RATE =
            (double) MAX_REQUESTS / WINDOW_SECONDS;

    // check rate limit
    public String checkRateLimit(String clientId) {

        clients.putIfAbsent(clientId,
                new TokenBucket(MAX_REQUESTS, REFILL_RATE));

        TokenBucket bucket = clients.get(clientId);

        if (bucket.allowRequest()) {

            return "Allowed (" + bucket.remainingTokens()
                    + " requests remaining)";
        }

        int retry = (int) (1 / REFILL_RATE);

        return "Denied (0 requests remaining, retry after "
                + retry + " seconds)";
    }

    // rate limit status
    public String getRateLimitStatus(String clientId) {

        TokenBucket bucket = clients.get(clientId);

        if (bucket == null) {
            return "No usage yet";
        }

        int used = MAX_REQUESTS - bucket.remainingTokens();

        return "{used: " + used +
                ", limit: " + MAX_REQUESTS +
                ", remaining: " + bucket.remainingTokens() +
                "}";
    }

    public static void main(String[] args) {

        RateLimiter limiter = new RateLimiter();

        String client = "abc123";

        // simulate requests
        for (int i = 0; i < 5; i++) {

            System.out.println(
                    limiter.checkRateLimit(client)
            );
        }

        System.out.println(
                limiter.getRateLimitStatus(client)
        );
    }
}
