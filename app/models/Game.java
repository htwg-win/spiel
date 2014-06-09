package models;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import play.libs.F.Callback;
import play.libs.F.Callback0;
import play.mvc.WebSocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Game {
	static Map<String, WebSocket.Out<String>> members = new HashMap<String, WebSocket.Out<String>>();

	public static void register(final WebSocket.In<String> in, final WebSocket.Out<String> out) {

		// For each event received on the socket,
		in.onMessage(new Callback<String>() {
			public void invoke(String JSON_DATA) {

				ObjectMapper mapper = new ObjectMapper();
				try {
					JsonNode actualObj = mapper.readTree(JSON_DATA);
					String event = actualObj.get("name").textValue();
					execEvent(event, actualObj.get("data"), in, out);

				} catch (JsonProcessingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		// When the socket is closed.
		in.onClose(new Callback0() {
			public void invoke() {

				System.out.println("Disconnected");

			}
		});

		// JsonNode data = new JsonNode();

		// Send a single 'Hello!' message
		// out.write("{\"name\":\"test\",\"data\":{}}");

	}

	private static void execEvent(String event, JsonNode Data, WebSocket.In<String> in, WebSocket.Out<String> out) {
		switch (event) {
		case "login":

			// do DB login test

			// dummy, always login
			members.put(Data.get("username").asText(), out);
			notifyAll("join", Data.get("username").asText() + " has entered the game");

			break;

		case "message":

			break;
		}
	}

	public static void notifyAll(String Event, String data) {

		JSONObject raw = new JSONObject();
		JSONObject d = new JSONObject();
		
		try {
			raw.put("name", Event);
			d.put("text", data);
			raw.put("data", d);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (WebSocket.Out<String> channel : members.values()) {
			channel.write(raw.toString());
		}
	}
}
