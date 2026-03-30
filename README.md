## 🔍 Visual Proof

### 🔒 Security Blocking an Unauthorized User
When an unauthenticated user attempts to access a protected endpoint,
the system returns a `401 Unauthorized` response.

!<img width="570" height="293" alt="Screenshot 2026-03-30 at 5 33 57 PM" src="https://github.com/user-attachments/assets/be0b33cc-3333-44fa-ae61-8ecc56b00aaf" />



---

### ⚠️ Failed Integrity Check
When a file has been tampered with, SentinelHash detects the hash mismatch
and returns a `409 Conflict` response.

!<img width="574" height="313" alt="Screenshot 2026-03-30 at 5 39 31 PM" src="https://github.com/user-attachments/assets/1f7fec3c-080d-4990-ae1c-9b6d4732ecfa" />


---

### 🧮 The Math
SentinelHash uses **SHA-256**, where the probability of two different
files producing the same hash (a collision) is:

**1 in 2²⁵⁶ ≈ 1 in 10⁷⁷**

To put that in perspective, there are an estimated 10⁸⁰ atoms in the
observable universe. A SHA-256 collision is effectively impossible.
EOF# SentinelHash
