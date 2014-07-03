var $d;

$(document).ready(function() {


	console.log('document loaded');
	
	
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


