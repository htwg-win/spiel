package models;

import java.util.HashMap;
import java.util.Map;

import play.libs.F.Callback;
import play.libs.F.Callback0;
import play.mvc.Http;
import play.mvc.WebSocket;

public class Game {
	Map<String, WebSocket.Out<String>> members = new HashMap<String, WebSocket.Out<String>>();

	public static void join(String username, WebSocket.In<String> in, WebSocket.Out<String> out) {

		// For each event received on the socket,
		in.onMessage(new Callback<String>() {
			public void invoke(String event) {

				// Log events to the console
				System.out.println(event);

			}
		});

		// When the socket is closed.
		in.onClose(new Callback0() {
			public void invoke() {

				System.out.println("Disconnected");

			}
		});

		// Send a single 'Hello!' message
		out.write("{\"name\":\"test\",\"data\":{}}");

	}

	public void notifyAll(String text) {
		for (WebSocket.Out<String> channel : members.values()) {
			channel.write(text);
		}
	}
}
