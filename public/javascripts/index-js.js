var $d;

$().ready(function() {
	$("#login").on('click touchstart', function(){
		$(".login").hide()
		$("#fields").show()
		$("#menu").show();
		$("#chat").show();
	})
	$("#create").on('click touchstart', function(){
		$(".login").hide()
		$("#fields").show()
		$("#menu").show();
		$("#chat").show();
	})
	
	$("#logout").on('click touchstart', function(){
		$(".login").show()
		$("#fields").hide()
		$("#menu").hide();
		$("#chat").hide();
	})
	
	$d = $(document);
	$d.on('click touchstart','.fields',function(e){
				
		alert(e.target.dataset.number);
	});
	
});