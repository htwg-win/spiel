package models;

import static org.apache.commons.lang3.StringEscapeUtils.escapeHtml4;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import play.libs.F.Callback;
import play.libs.F.Callback0;
import play.mvc.WebSocket;

public class Game {
	static Map<String, WebSocket.Out<String>> members = new HashMap<String, WebSocket.Out<String>>();
	static Map<String, String> usernames = new HashMap<String, String>();
	static boolean playing = false;

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
				if(!(user.equals(null))){
				String msg = (user + " has quit");
				Game.notifyAll(msg);
				members.remove(user);
				usernames.remove(out.toString());
				System.out.println(":out: " + user);
				}
			}
		});

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
		String username = null;
		String password = null;
		JSONObject d = new JSONObject();
		System.out.println(event);

		switch (event) {
		case "user.login":

			try {
				username = escapeHtml4(Data.get("username").toString());
				password = Data.get("password").toString();
				boolean login = Db.login(username, password);
				if (!username.equals("") && !members.containsKey(username) && login) {
					members.put(username, out);
					usernames.put(out.toString(), username);

					triggerOne("user.login.success", d, out);
					notifyAll(username + " has entered the game (" + members.size() + " players)", "success");
					startGame();
				} else {
					if (!login) {
						notifyOne("Login failed.", out, "error");
					} else {
						notifyOne("'" + username + "' is already logged in or empty", out, "error");
					}

				}

			} catch (JSONException e) {
				notifyOne("Username not specified", out, "error");
			}

			returnCode = -1;
			break;

		case "user.create":
			try {
				username = escapeHtml4(Data.get("username").toString());
				password = Data.get("password").toString();

				if (Db.create(username, password)) {
					triggerOne("user.create.success", d, out);
					notifyOne("Account created", out, "sucess");
					return -1;
				}
			} catch (JSONException e1) {
				notifyOne("Username not specified", out, "error");
			}

			notifyOne("Create Failed", out, "error");
			break;
		case "user.getInfo":

			try {
				d.put("id", out.toString());
				triggerOne("user.info", d, out);
			} catch (JSONException e) {
				notifyOne("ups..Something went wrong...", out, "error");
			}

			break;

		case "message":

			break;
		case "game.finish":
			try {
				finish(Data.get("from").toString());
				trigger(event, Data);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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

	/**
	 * 
	 * @param data
	 */
	public static void notifyAll(String data) {
		notifyAll(data, "info");
	}

	/**
	 * 
	 * @param data
	 * @param type
	 */
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

	/**
	 * 
	 * @param data
	 * @param out
	 * @param type
	 */
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

	/**
	 * 
	 * @param data
	 * @param out
	 */
	public static void notifyOne(String data, WebSocket.Out<String> out) {
		notifyOne(data, out, "info");
	}

	/**
	 * 
	 * @param Event
	 * @param Data
	 * @param out
	 */
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

	/**
	 * 
	 * @param Event
	 * @param Data
	 */
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

	public static void finish(String username) {
		if (playing) {
			playing = false;
			Db.win(username);
			startGame();
		}
	}

	public static boolean startGame() {
		if (members.size() > 1 && !playing) {
			int[][] seq = generateSequence(10);

			JSONObject raw = new JSONObject();
			try {
				raw.put("sequence", seq);
				raw.put("startIn", 10);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			playing = true;
			trigger("game.start", raw);
			return true;
		}

		return false;
	}

	/**
	 * 
	 * @param rounds
	 * @return
	 */
	public static int[][] generateSequence(int rounds) {
		int[][] sequence = new int[rounds][5];
		int buttons = 0;
		int number = 0;
		for (int i = 0; i < rounds; ++i) {
			buttons = 5;

			for (int j = 0; j < buttons; ++j) {

				while (true) {
					number = randomInt(1, 9);
					if (Arrays.binarySearch(sequence[i], number) >= 0) {
						continue;
					}

					break;
				}

				sequence[i][4 - j] = number;
				Arrays.sort(sequence[i]);
			}
		}

		return sequence;

	}

	/**
	 * 
	 * @param Min
	 * @param Max
	 * @return
	 */
	public static int randomInt(int Min, int Max) {
		return Min + (int) (Math.random() * ((Max - Min) + 1));
	}

}
