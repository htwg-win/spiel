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
		$(".login").hide()
		$("#fields").show()
		$("#menu").show();
		$("#chat").show();
	})

	$d.on('click touchstart', "#logout", function() {
		location.href = location.href;
	})


});

Game.receive("user.login.success", function() {
	$(".login").hide()
	$("#fields").show()
	$("#menu").show();
	$("#chat").show();
});
