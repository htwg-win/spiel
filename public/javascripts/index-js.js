var $d;

$().ready(function() {
	$("#login").on('click touchstart', function() {
		Game.send("user.login", {
			username : $('#username').val(),
			password : $('#password').val()
		})

	})
	$("#create").on('click touchstart', function() {
		$(".login").hide()
		$("#fields").show()
		$("#menu").show();
		$("#chat").show();
	})

	$("#logout").on('click touchstart', function() {
		$(".login").show()
		$("#fields").hide()
		$("#menu").hide();
		$("#chat").hide();
	})

	$d = $(document);
	$d.on('click touchstart', '.fields', function(e) {
		e.preventDefault();
		SoundBank.play(this.dataset.number);

	});
	
	

});

Game.receive("user.login.success", function() {
	$(".login").hide()
	$("#fields").show()
	$("#menu").show();
	$("#chat").show();
});