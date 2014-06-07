var $d;

$().ready(function() {
	$("#login").on('click touchstart', function(){
		$(".login").hide()
		$("#maincontent").show();
	})
	
	$("#create").on('click touchstart', function(){
		$(".login").hide()
		$("#maincontent").show();
	})
	
	$d = $(document);
	$d.on('click touchstart','.fields',function(e){
				
		alert(e.target.dataset.number);
	});
	
});
