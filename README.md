This repository contains 10 system design and algorithm problems demonstrating advanced hash table applications, caching, concurrency, and data processing.

1. Social Media Username Availability Checker

Goal: Check username availability in O(1), suggest alternatives, track popularity.

Techniques: HashMap<String, Integer> for username → userId & attempt frequency.

Use Cases: Twitter/Instagram registration, email checks.

Sample Function: checkAvailability("john_doe") → false.

2. DNS Cache with TTL

Goal: Cache domain → IP with TTL, LRU eviction, track hits/misses.

Techniques: HashMap<String, DNSEntry>, background cleanup thread.

Use Cases: Browser DNS caching, CDN edge servers.

Feature: Cache HIT/MISS metrics.

3. Inventory Management during Flash Sale

Goal: Prevent overselling for limited stock items under high concurrency.

Techniques: HashMap<productId, stockCount>, synchronized/atomic decrement, waiting list via LinkedHashMap.

Use Cases: Amazon deals, concert tickets.

4. Plagiarism Detector

Goal: Detect matching n-grams between documents.

Techniques: HashMap<String, Set<DocumentId>>, 5-7 word n-grams, string hashing.

Use Cases: Turnitin, MOSS code similarity.

5. Real-Time Analytics Dashboard

Goal: Track top pages, unique visitors, traffic sources.

Techniques: Multiple hash tables, PriorityQueue for top N, batch updates.

Use Cases: Google Analytics, Twitter trends, e-commerce metrics.

6. Distributed API Rate Limiter

Goal: Enforce per-client request limits in <1ms.

Techniques: HashMap<clientId, TokenBucket>, sliding/fixed window, atomic operations.

Use Cases: AWS API Gateway, Stripe API throttling.

7. Autocomplete System

Goal: Suggest top queries for a prefix, handle typos and popularity.

Techniques: Trie + HashMap<String, Integer> for frequency, min-heap for top K.

Use Cases: Google search, Amazon product search.

8. Parking Lot Management (Open Addressing)

Goal: Assign parking spots using license plate hash, linear probing for collisions.

Techniques: Array-based hash table, Status {EMPTY, OCCUPIED, DELETED}, probe counting, entry/exit tracking.

Use Cases: Airport/mall parking, street parking apps.

9. Two-Sum Variants for Transactions

Goal: Detect transaction pairs summing to target, K-Sum, duplicates.

Techniques: Hash maps for complement lookup, time window filtering, recursive K-Sum.

Use Cases: Fraud detection, crypto analytics, tax compliance.

10. Multi-Level Video Cache System

Goal: L1 memory → L2 SSD → L3 DB caching, LRU eviction, promotion, hit statistics.

Techniques: LinkedHashMap (access-order) for L1 LRU, HashMap for L2, access counts for promotion.

Use Cases: Netflix streaming, CDN content delivery, database query caching.