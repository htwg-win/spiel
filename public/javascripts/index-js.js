var $d;

$().ready(function() {
	$("#popupuser").dialog({ hide: { effect: "explode", duration: 1000 } });
	$("#startgame").on('click touchstart', function(){
		$("#popupuser").dialog( "close" )
		$("#maincontent").show("explode", "slow");
//		$("#maincontent").css("visibility", "visible");
	})
	
	$d = $(document);
	
	$d.on('click touchstart','.fields',function(e){
				
		alert(e.target.dataset.number);
	});
	
	
	
});
