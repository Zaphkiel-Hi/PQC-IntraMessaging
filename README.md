# PQC Messenger

> A proof-of-concept secure messenger app using **Post-Quantum Cryptography (PQC)** — implemented for academic research.  
> 
**"Post-Quantum Cryptography: Implementation of McEliece in the Example of a Messenger App"**  
 
---
   
## 📌 Project Structure 
 
- `app/src/main/java/org/niklasunrau/pqcmessenger/`   
  → Main application source code  
  
- `domain/crypto/`      
  → Core cryptographic components and PQC logic (including McEliece implementation) 
   
- `gradle/`    
  → Gradle wrapper and build system  
 
---
 
## 🔐 Features

- Uses **McEliece cryptosystem**, a PQC algorithm designed to be secure against quantum computers.
- Encrypted messaging between users.
- Lightweight Android-based architecture.
- Separation of concerns via `domain/crypto`.

---

## 🧪 Status

This project is a **research-grade prototype** and not intended for production use.  
Security has not been audited, and the app was built primarily for academic purposes.

---

## 🚀 Getting Started

To build and run the app:
 
1. Clone the repository:
   ```bash
   git clone https://github.com/zaphkiel-hi/pqc-messenger.git 
   cd pqc-messenger
