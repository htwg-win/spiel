package controllers;

import models.Db;
import models.Game;
import play.Play;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.WebSocket;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class Application extends Controller {
	public static String file = Play.application().getFile("/../../../SQLite/game.sqlite").toString();
	
	
	public static Result index() {
		return ok(views.html.main.render());
	}

	public static Result highscore() {
		ObjectNode result = Json.newObject();
		String[] high = Db.highscores();

		for (int i = 0; i < high.length; ++i) {
			result.put(String.valueOf(i), high[i]);
		}

		return ok(result);
	}

	public static WebSocket<String> socket() {
		return new WebSocket<String>() {

			// Called when the Websocket Handshake is done.
			public void onReady(WebSocket.In<String> in, WebSocket.Out<String> out) {
				Game.register(in, out);
			}

		};
	}
}
