## 🔍 Visual Proof

### 🔒 Security Blocking an Unauthorized User
When an unauthenticated user attempts to access a protected endpoint,
the system returns a `401 Unauthorized` response.

![401 Unauthorized](docs/401-unauthorized.png)

---

### ⚠️ Failed Integrity Check
When a file has been tampered with, SentinelHash detects the hash mismatch
and returns a `409 Conflict` response.

![Failed Integrity Check](docs/failed-integrity.png)

---

### 🧮 The Math
SentinelHash uses **SHA-256**, where the probability of two different
files producing the same hash (a collision) is:

**1 in 2²⁵⁶ ≈ 1 in 10⁷⁷**

To put that in perspective, there are an estimated 10⁸⁰ atoms in the
observable universe. A SHA-256 collision is effectively impossible.
EOF# SentinelHash
