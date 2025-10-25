package com.codtech.chat.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class ChatClient {
	public static void main(String[] args) {
		String host = "localhost";
		int port = 12345;
		if (args.length >= 1)
			host = args[0];
		if (args.length >= 2) {
			try {
				port = Integer.parseInt(args[1]);
			} catch (NumberFormatException ignored) {
			}
		}

		try (Socket socket = new Socket(host, port);
				BufferedReader in = new BufferedReader(
						new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
				PrintWriter out = new PrintWriter(
						new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
				Scanner console = new Scanner(System.in, StandardCharsets.UTF_8)) {

			// Thread: read messages from server
			Thread reader = new Thread(() -> {
				try {
					String s;
					while ((s = in.readLine()) != null) {
						System.out.println(s);
					}
				} catch (IOException e) {
					System.out.println("Disconnected from server.");
				}
			});
			reader.setDaemon(true);
			reader.start();

			// Send user input to server
			while (console.hasNextLine()) {
				String line = console.nextLine();
				out.println(line);
				if (line.equalsIgnoreCase("/quit"))
					break;
			}

		} catch (IOException e) {
			System.err.println("Could not connect to server: " + e.getMessage());
		}
	}
}
