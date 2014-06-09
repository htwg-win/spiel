package controllers;

import org.apache.http.auth.UsernamePasswordCredentials;

import models.Game;

import com.fasterxml.jackson.databind.JsonNode;

import play.libs.F.Callback;
import play.libs.F.Callback0;
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
		final String username = Http.Context.current().session().get("username");
		return new WebSocket<String>() {

			// Called when the Websocket Handshake is done.
			public void onReady(WebSocket.In<String> in, WebSocket.Out<String> out) {

				System.out.println(username);
				Game.join(username, in, out);
			}

		};
	}
}
