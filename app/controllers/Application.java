package controllers;

import models.Game;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.WebSocket;

public class Application extends Controller {

	public static Result index() {
		return ok(views.html.main.render());
	}

	public static Result chat() {
		return ok(views.html.chat.render());
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
