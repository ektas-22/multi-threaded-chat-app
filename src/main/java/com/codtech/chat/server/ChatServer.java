package com.codtech.chat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.Map;

public class ChatServer {
	private final int port;
	private final ExecutorService clientPool;
	private final ConcurrentHashMap<String, ClientHandler> clients = new ConcurrentHashMap<>();
	private final ServerShutdownHook shutdownHook = new ServerShutdownHook();
	private volatile boolean running = true;

	public ChatServer(int port, int maxThreads) {
		this.port = port;
		this.clientPool = Executors.newFixedThreadPool(maxThreads);
		Runtime.getRuntime().addShutdownHook(new Thread(shutdownHook));
	}

	public void start() {
		int poolSize = (clientPool instanceof ThreadPoolExecutor)
				? ((ThreadPoolExecutor) clientPool).getMaximumPoolSize()
				: -1;
		System.out.printf("ChatServer: Starting on port %d (thread-pool size=%d)%n", port, poolSize);

		try (ServerSocket serverSocket = new ServerSocket(port)) {
			serverSocket.setReuseAddress(true);
			while (running) {
				Socket socket = serverSocket.accept();
				socket.setSoTimeout(0);
				ClientHandler handler = new ClientHandler(socket, this);
				clientPool.submit(handler);
			}
		} catch (IOException e) {
			if (running)
				System.err.println("ChatServer error: " + e.getMessage());
		} finally {
			shutdown();
		}
	}

	public boolean registerClient(String nick, ClientHandler handler) {
		return clients.putIfAbsent(nick, handler) == null;
	}

	public void unregisterClient(String nick) {
		if (nick != null)
			clients.remove(nick);
	}

	public void broadcast(String from, String message) {
		String full = String.format("[%s]: %s", from, message);
		for (Map.Entry<String, ClientHandler> e : clients.entrySet()) {
			e.getValue().send(full);
		}
		System.out.println(full);
	}

	public boolean privateMessage(String from, String toNick, String message) {
		ClientHandler target = clients.get(toNick);
		if (target == null)
			return false;
		target.send(String.format("[PM from %s]: %s", from, message));
		return true;
	}

	public String listUsers() {
		return String.join(", ", clients.keySet());
	}

	public void shutdown() {
		running = false;
		System.out.println("ChatServer: Shutting down...");
		for (ClientHandler ch : clients.values())
			ch.closeQuietly();
		clients.clear();
		clientPool.shutdownNow();
	}

	private class ServerShutdownHook implements Runnable {
		@Override
		public void run() {
			if (running)
				shutdown();
		}
	}
}
