package models;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import play.libs.F.Callback;
import play.libs.F.Callback0;
import play.mvc.WebSocket;

import static org.apache.commons.lang3.StringEscapeUtils.escapeHtml4;

public class Game {
	static Map<String, WebSocket.Out<String>> members = new HashMap<String, WebSocket.Out<String>>();
	static Map<String, String> usernames = new HashMap<String, String>();

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
				String user = usernames.get(out.toString());
				String msg = (user + " has quit");
				Game.notifyAll(msg);
				members.remove(user);
				usernames.remove(out.toString());
				System.out.println(":out: " + user);
			}
		});

		// JsonNode data = new JsonNode();
		// Send a single 'Hello!' message
		// out.write("{\"name\":\"test\",\"data\":{}}");

	}

	/**
	 * s
	 * 
	 * @param event
	 * @param Data
	 * @param in
	 * @param out
	 * @return
	 */
	private static int execEvent(String event, JSONObject Data, WebSocket.In<String> in, WebSocket.Out<String> out) {
		int returnCode = 0;

		JSONObject d = new JSONObject();
		System.out.println(event);

		switch (event) {
		case "user.login":
			String username = null;
			boolean login = true;

			try {
				username = escapeHtml4(Data.get("username").toString());

				if (!username.equals("") && !members.containsKey(username) && login) {
					members.put(username, out);
					usernames.put(out.toString(), username);

					triggerOne("user.login.success", d, out);
					notifyAll(username + " has entered the game (" + members.size() + " players)", "success");
				} else {

					notifyOne("'" + username + "' is already logged in or empty", out, "error");
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();

				notifyOne("Username not specified", out, "error");

			}
			// do DB login test

			// dummy, always login

			returnCode = -1;
			break;
		case "user.getInfo":

			try {
				d.put("id", out.toString());
				triggerOne("user.info", d, out);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			break;

		case "message":

			break;

		case "heartbeat":

			returnCode = -1;
			break;

		default:

			// dispatch to all clients s default
			trigger(event, Data);
			break;
		}

		return returnCode;
	}

	public static void notifyAll(String data) {
		notifyAll(data, "info");
	}

	public static void notifyAll(String data, String type) {
		JSONObject d = new JSONObject();

		try {
			d.put("message", data);
			trigger("notify." + type, d);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void notifyOne(String data, WebSocket.Out<String> out, String type) {
		JSONObject d = new JSONObject();

		try {
			d.put("message", data);
			triggerOne("notify." + type, d, out);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void notifyOne(String data, WebSocket.Out<String> out) {
		notifyOne(data, out, "info");
	}

	public static void triggerOne(String Event, JSONObject Data, WebSocket.Out<String> out) {
		JSONObject raw = new JSONObject();
		try {
			raw.put("data", Data);
			raw.put("name", Event);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		out.write(raw.toString());
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
