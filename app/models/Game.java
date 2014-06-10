package models;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import play.libs.F.Callback;
import play.libs.F.Callback0;
import play.mvc.WebSocket;

public class Game {
	static Map<String, WebSocket.Out<String>> members = new HashMap<String, WebSocket.Out<String>>();

	public static void register(final WebSocket.In<String> in, final WebSocket.Out<String> out) {

		in.onMessage(new Callback<String>() {
			public void invoke(String JSON_DATA) {

				try {
					JSONObject raw = new JSONObject(JSON_DATA);
					raw.get("name");
					execEvent(raw.get("name").toString(), (JSONObject) raw.get("data"), in, out);
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				
			}
		});

		// When the socket is closed.
		in.onClose(new Callback0() {
			public void invoke() {
				System.out.println("Disconnected Client");
				
			}
		});

		// JsonNode data = new JsonNode();

		// Send a single 'Hello!' message
		// out.write("{\"name\":\"test\",\"data\":{}}");

	}

	private static int execEvent(String event, JSONObject Data, WebSocket.In<String> in, WebSocket.Out<String> out) {
		int returnCode = 0;
		System.out.println(event);

		switch (event) {
		case "login":

			// do DB login test

			// dummy, always login

			try {
				members.put(Data.get("username").toString(), out);
				notifyAll(Data.get("username").toString() + " has entered the game " + members.size());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			returnCode = -1;
			break;

		case "message":

			break;

		case "heartbeet":

			returnCode = -1;
			break;
		}

		return returnCode;
	}

	public static void notifyAll(String data) {
		JSONObject d = new JSONObject();

		try {
			d.put("text", data);
			trigger("notify", d);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void trigger(String Event, JSONObject Data) {

		JSONObject raw = new JSONObject();

		try {
			raw.put("data", Data);
			raw.put("name", Event);

			for (WebSocket.Out<String> channel : members.values()) {
				channel.write(raw.toString());
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
