# 🖥️ Java RMI – Distributed User Database

## 📌 Project Description

This project is a **distributed client–server application** built using **Java RMI (Remote Method Invocation)**.  
The system implements a simple **CRUD user database** stored entirely on the server side and accessed remotely by a console client.

The goal of the application is to demonstrate:

- remote method calls via RMI,
- transmission of serialized objects (`User`) between JVMs,
- server-side validation and auditing,
- simple user interaction through a text-based menu interface.

All data is stored **in memory** for the duration of server runtime.

---

## ⚙️ How the System Works

The application consists of **three logical modules**:

### 🗄️ Server

- Hosts the remote object implementing `HelloInterface`.
- Maintains a thread-safe in-memory collection of `User` objects.
- Generates unique user IDs.
- Performs business validation of incoming data.
- Logs all operations (**ADD / UPDATE / DELETE / SHUTDOWN**) for audit purposes.
- Exposes remote CRUD methods to clients.

---

### 💻 Client

- Connects to the server via the RMI registry.
- Uses a numeric **console menu** for interaction.
- Collects and **validates all input locally** before sending requests to server.
- Sends only valid requests to the RMI server.
- Displays results and error messages returned from remote calls.

---

### 🔁 Communication

- Implemented using **Java RMI**.
- Transferred objects implement `Serializable`.
- Business exceptions (`UserValidationException`) are propagated to the client.

---

## 🧩 User Interface

The application uses a **menu-based CLI user interface**:

USER DATABASE MENU  
1 - Add user  
2 - Remove user  
3 - Get user by ID  
4 - List users  
5 - Modify user  
9 - Shutdown server  
0 - Exit

---

### ✅ Input Validation

All fields are validated on the client side:

| Field        | Validation rules |
|----------------|-------------------|
| **First name** | Letters only, minimum 2 characters |
| **Last name**  | Letters only, minimum 2 characters |
| **Birth date** | Valid `yyyy-MM-dd` format, not future dated |
| **Salary**     | Numeric value ≥ 0 |
| **Gender**     | `MALE` or `FEMALE` only |
| **Department**| Required, minimum 2 characters |
| **Position**  | Required, minimum 2 characters |

Invalid input blocks further execution until corrected.

---

## 🗃️ Business Operations (CRUD)

All operations are executed remotely on the server:

- **CREATE** – add new user
- **READ** – fetch single user or list all users
- **UPDATE**
    - update salary
    - update department & position
- **DELETE** – remove user by ID
- **SHUTDOWN** – stops the RMI server remotely

Each operation modifying data is logged server-side.

---

## 🔨 Compilation

Compile all sources:

```bash
javac -d out src/**/*.java
```
---

## ▶️ Running the Application

### 1️⃣ Start the RMI server
```bash
java -cp out server.MyRMI
```
Expected output:

RMI SERVER STARTED SUCCESSFULLY  
Registry port : 5001  
Bind name     : Hello  

---

### 2️⃣ Run the client
```bash
java -cp out client.HelloClient
```
Optional remote address:
```bash
java -cp out client.HelloClient 192.168.0.10
```
---

## 🧪 Example Server Log

[ADD] User created -> User{id=1, firstName='Adam', lastName='Nowak', ...}

[UPDATE-SALARY] User id=1 | 15000.0 -> 17000.0

[UPDATE-DEPT] User id=1 | Department: IT -> Finance | Position: Developer -> Lead

[DELETE] User removed -> User{id=1, ...}

[SHUTDOWN] Remote server shutdown requested.

---

## 🛠️ Technologies

- Java SE
- Java RMI
- Concurrent collections
- Object serialization
- Console‐based UI

---
