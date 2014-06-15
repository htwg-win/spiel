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

	$d.on('click touchstart', '.fields', function(e) {
		e.preventDefault();
		piano.play(this.dataset.number);
		this.classList.toggle("active");

	});

});

Game.receive("user.login.success", function() {
	$(".login").hide()
	$("#fields").show()
	$("#menu").show();
	$("#chat").show();
});
