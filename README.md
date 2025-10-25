# 💬 Multi-Threaded Chat Application

## 📘 Project Overview
This project is a **real-time client-server chat application** built using **Java Sockets** and **Multithreading**.  
It enables **multiple clients** to connect to a single chat server simultaneously and exchange messages in real time.

Each client runs in its own thread, ensuring smooth communication between multiple users.  
The server manages all active clients and handles message broadcasting, private chats, nickname changes, and graceful disconnections.

---

## 🎯 Objective
> **Build a client-server chat application using Java sockets and multithreading to handle multiple users.**

This project was developed as part of the **CodTech Internship Program** to demonstrate proficiency in:
- Java Networking
- Thread synchronization
- Concurrent client handling
- Clean coding and exception management

---

## ⚙️ Technologies Used
| Component | Description |
|------------|-------------|
| **Language** | Java |
| **Framework** | None (Core Java project) |
| **IDE** | Spring Tool Suite / Eclipse |
| **Networking** | TCP (Socket, ServerSocket) |
| **Concurrency** | Multithreading using ExecutorService |
| **Collections** | ConcurrentHashMap |
| **Encoding** | UTF-8 for reliable text communication |

---

## 🧱 Project Structure
com.codtech.chat.server
├── ChatServer.java # Main server logic, handles clients and broadcasting
├── ClientHandler.java # Manages individual client connections
└── ChatServerMain.java # Entry point for starting the server

com.codtech.chat.client
└── ChatClient.java # Client-side program for connecting and chatting

yaml
Copy code

---

## 🚀 How to Run the Application

### 🖥️ Step 1: Compile the Code
Open terminal or STS console inside your project folder:
```bash
javac com/codtech/chat/server/*.java com/codtech/chat/client/*.java
🖧 Step 2: Start the Server
Run the following command to start the chat server:

bash
Copy code
java com.codtech.chat.server.ChatServerMain
Expected output:

arduino
Copy code
ChatServer: Starting on port 12345 (thread-pool size=100)
Chat server running. Press Ctrl+C to stop.
💻 Step 3: Start the Clients
Open multiple terminals (or STS Run Configurations) and execute:

bash
Copy code
java com.codtech.chat.client.ChatClient
You’ll see:

csharp
Copy code
Welcome! Your nick is: User60123
Now you can start chatting! 🎉

💬 Available Commands
Command	Example	Description
/nick <newNick>	/nick Alex	Change your nickname
/w <user> <message>	/w John hello!	Send a private message
/list	/list	Display all connected users
/quit	/quit	Exit the chat

🧠 Features Implemented
✅ Real-time group chat (broadcast)
✅ Private messaging between users
✅ Nickname management (/nick command)
✅ List connected users (/list command)
✅ Thread-safe handling of multiple clients
✅ Graceful server shutdown
✅ UTF-8 encoding for reliable message exchange
✅ Command-based client interaction

🧩 Example Console Output
Client 1:

markdown
Copy code
Welcome! Your nick is: User60123
[SERVER]: User60124 has joined.
[User60124]: Hello everyone!
/w User60124 Hi, private message!
[PM to User60124]: Hi, private message!
Client 2:

markdown
Copy code
Welcome! Your nick is: User60124
[User60123]: Hi there!
[PM from User60123]: Hi, private message!
🧹 Exception Handling and Edge Cases
Handles abrupt client disconnections gracefully.

Prevents duplicate nicknames.

Detects invalid commands and prints friendly help messages.

Closes sockets and streams safely using try-with-resources.

Automatically removes inactive clients from server memory.

📄 How It Works (Architecture)
The server listens on a port (ServerSocket) for incoming connections.

When a client connects, a new thread (ClientHandler) is spawned for that client.

The handler reads messages and relays them to all connected clients via broadcast.

The server maintains a list of active users in a ConcurrentHashMap.

The system supports both group and private chats in real time.

📚 Learning Outcomes
Implementing TCP/IP socket communication in Java.

Managing concurrency using ExecutorService and threads.

Ensuring thread-safe operations using concurrent data structures.

Understanding client-server architecture design.

Building console-based real-time applications.

👨‍💻 Author
Name: [Your Full Name]
Internship: CodTech IT Solutions Pvt. Ltd.
Project Title: Multi-Threaded Chat Application
Date: October 2025
