var Event = new (function() {
	var isReady = false;
	var events = {};
	var params = {};
	this.ready = function(f) {
		if (isReady) {
			f.call(this, params['ready']);
		} else {
			this.on('ready', f);
		}
	}

	this.readyTrigger = function() {
		isReady = true;
		this.trigger('ready');
	}

	this.trigger = function(e, p) {

		if (typeof events[e] == "object") {

			var stack = events[e];
			var i = stack.length;
			var P = [];
			if (typeof params[e] == "object") {
				P = params[e];
			}
			if (typeof p == "object") {
				P = p;
			}

			while (i--) {
				events[e][i].call(this, P);
			}
		}
	}

	this.on = function(e, f) {
		if (typeof events[e] != "object") {
			events[e] = [];
		}
		events[e].push(f);
	}

	this.off = function(e) {
		events[e] = null;
	}

});
Event.readyTrigger();

/**
 * 
 */
var GameAPI = (function() {
	var self = this;
	var keepalive_timeout = null;
	var hearbeat_interval = 15 * 1000;
	var heartbeat_interval_p = null;
	/**
	 * 
	 */
	const
	URL = "ws://" + location.hostname + (location.port ? ':' + location.port : '') + "/socket";

	var Socket;

	/**
	 * 
	 */
	function SocketInit() {
		Socket = new WebSocket(URL);

		Socket.onopen = function() {
			console.log("WS Connection Opened");
			Event.trigger('socket.open', [ Socket ]);

			window.clearInterval(heartbeat_interval_p);
			heartbeat_interval_p = window.setInterval(function() {
				console.log("heartbeat");
				Socket.send('{"name":"heartbeat","data":{}}');
			}, hearbeat_interval);

		}

		Socket.onmessage = function(evt) {
			console.log("Data received: " + evt.data);
			var data = JSON.parse(evt.data);
			var name = data.name;
			var d = data.data;
			Event.trigger(name, d);
		};

		Socket.onerror = function(err) {

			console.log(err);
		}

		Socket.onclose = function() {
			console.log("WS Connection Closed");
			Event.trigger('socket.close', [ Socket ]);

			// retry
			SocketInit();
		}

		self.socket = Socket;

		clearInterval(keepalive_timeout);
		keepalive_timeout = window.setInterval(function() {
			if (Socket.readyState != 1) {
				console.log("NO WS CON");
				SocketInit();
			}
		}, 1000);

	}

	/**
	 * 
	 */
	this.init = function() {

		// Init Socket
		SocketInit();
	}

	this.send = function(Event, Data) {
		var raw = {
			name : Event,
			data : Data
		};
		Socket.send(JSON.stringify(raw));
	}

	this.receive = function(name, Callback) {
		Event.on(name, Callback);
	}

});

var Alert = new function() {
	var element = document.querySelector('#notify');
	var messageQueue = [];

	this.show = function(type, str) {
		var f = document.createElement("div");

		element.insertBefore(f, element.firstChild);
		f.className = type;
		f.innerHTML = str;
		element.className = "active";
		window.setTimeout(function() {
			element.removeChild(f);

			if (element.childNodes.length == 0) {
				element.className = "";
			}

		}, 5000);

		window.setTimeout(function() {
			f.className = "disable";

		}, 3000);

	}

}

var Game = new GameAPI();
Game.init();

Game.receive("notify.error", function(d) {
	Alert.show("error", d.message);
});

Game.receive("notify.info", function(d) {
	Alert.show("info", d.message);
});

Game.receive("notify.success", function(d) {
	Alert.show("success", d.message);
});
