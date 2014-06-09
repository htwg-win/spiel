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
		}

		Socket.onmessage = function(evt) {
			console.log("Data received: " + evt.data);
			var data = JSON.parse(evt.data);
			var name = data.name;
			var d = data.data;
			Event.trigger('socket.on' + name, d);
		};

		Socket.onerror = function(err) {

			console.log(err);
		}

		Socket.onclose = function() {
			console.log("WS Connection Closed");
			Event.trigger('socket.close', [ Socket ]);
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

	this.receive = function(Event, Callback) {
		Event.on('socket.on' + Event, Callback);
	}

});

var Game = new GameAPI();
Game.init();
