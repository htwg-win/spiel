var $d;

$().ready(function() {
//	$("#popupuser").dialog({ hide: { effect: "explode", duration: 1000 } });
//	$("#startgame").on('click touchstart', function(){
//		$("#popupuser").dialog( "close" )
//		$("#maincontent").show("explode", "slow");
//	})
	
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
