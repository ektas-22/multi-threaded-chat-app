package com.codtech.chat.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public class ClientHandler implements Runnable {
	private static final Pattern VALID_NICK = Pattern.compile("^[A-Za-z0-9_\\-]{3,20}$");

	private final Socket socket;
	private final ChatServer server;
	private volatile boolean running = true;

	private BufferedReader in;
	private PrintWriter out;
	private String nick;

	public ClientHandler(Socket socket, ChatServer server) {
		this.socket = socket;
		this.server = server;
		this.nick = "User" + socket.getPort();
	}

	@Override
	public void run() {
		try {
			in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
			out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);

			// ensure unique nick
			synchronized (server) {
				String base = nick;
				int suffix = 1;
				while (!server.registerClient(nick, this)) {
					nick = base + "_" + (suffix++);
				}
			}

			send("Welcome! Your nick is: " + nick);
			server.broadcast("SERVER", nick + " has joined.");

			String line;
			while (running && (line = in.readLine()) != null) {
				line = line.trim();
				if (line.isEmpty())
					continue;

				if (line.startsWith("/"))
					handleCommand(line);
				else
					server.broadcast(nick, line);
			}
		} catch (IOException e) {
			// client disconnected or IO error
		} finally {
			cleanup();
		}
	}

	private void handleCommand(String line) {
		String[] parts = line.split("\\s+", 3);
		String cmd = parts[0].toLowerCase();
		switch (cmd) {
		case "/nick":
			if (parts.length < 2) {
				send("Usage: /nick <newNick>");
				return;
			}
			changeNick(parts[1].trim());
			break;
		case "/w":
			if (parts.length < 3) {
				send("Usage: /w <nick> <message>");
				return;
			}
			String target = parts[1];
			String msg = parts[2];
			if (!server.privateMessage(nick, target, msg))
				send("User not found: " + target);
			else
				send("[PM to " + target + "]: " + msg);
			break;
		case "/list":
			send("Users: " + server.listUsers());
			break;
		case "/quit":
			send("Goodbye!");
			running = false;
			break;
		default:
			send("Unknown command. Available: /nick /w /list /quit");
		}
	}

	private void changeNick(String newNick) {
		if (!VALID_NICK.matcher(newNick).matches()) {
			send("Invalid nick. Use 3-20 chars: letters, digits, '_' or '-'.");
			return;
		}
		if (newNick.equals(nick)) {
			send("You already have that nick.");
			return;
		}
		synchronized (server) {
			if (server.registerClient(newNick, this)) {
				server.unregisterClient(nick);
				server.broadcast("SERVER", nick + " is now " + newNick);
				nick = newNick;
				send("Nick changed to " + nick);
			} else {
				send("Nick already taken: " + newNick);
			}
		}
	}

	public void send(String message) {
		try {
			out.println(message);
		} catch (Exception ignored) {
		}
	}

	public void closeQuietly() {
		running = false;
		cleanup();
	}

	private void cleanup() {
		try {
			server.unregisterClient(nick);
			server.broadcast("SERVER", nick + " has left.");
			if (in != null)
				in.close();
			if (out != null)
				out.close();
			if (socket != null && !socket.isClosed())
				socket.close();
		} catch (IOException ignored) {
		}
	}

	public String getNick() {
		return nick;
	}
}
