package com.codtech.chat.server;

public class ChatServerMain {
	public static void main(String[] args) {
		int port = 12345;
		int maxThreads = 100;
		if (args.length >= 1) {
			try {
				port = Integer.parseInt(args[0]);
			} catch (NumberFormatException ignored) {
			}
		}
		if (args.length >= 2) {
			try {
				maxThreads = Integer.parseInt(args[1]);
			} catch (NumberFormatException ignored) {
			}
		}

		ChatServer server = new ChatServer(port, maxThreads);
		server.start(); // blocking call
	}
}
