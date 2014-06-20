var $d;

$().ready(function() {

	$d = $(document);
	$d.on('click touchstart', '#login', function() {
		Game.send("user.login", {
			username : $('#username').val(),
			password : $('#password').val()
		})

	})
	$d.on('click touchstart', "#create", function() {
		Game.send("user.create", {
			username : $('#username').val(),
			password : $('#password').val()
		})
	})

	$d.on('click touchstart', "#logout", function() {
		location.href = location.href;
	})
	
});

Game.receive("user.login.success", function() {
	$(".login").hide()
	$("#manual").hide()
	$("#fields").show()
	$("#menu").show();
	$("#chat").show();
	updateHighScore();
});

Game.receive("user.create.success", function() {
	Game.send("user.login", {
		username : $('#username').val(),
		password : $('#password').val()
	})

	updateHighScore();
});

function updateHighScore() {
	GameUI.getHighScore(function(list) {
		var parent = document.querySelector('#highscore');
		var innerHTML = [];
		var player;
		var index;
		for ( var place in list) {
			player = list[place];

			if (player != null) {
				index = player.indexOf(":");
				innerHTML.push('<div>' + (parseInt(place) + 1) + '. ' + player.substr(0, index) + ' (' + player.substr(index + 1) + ')</div>');
			}

		}

		parent.innerHTML = innerHTML.join("");
	});
}
