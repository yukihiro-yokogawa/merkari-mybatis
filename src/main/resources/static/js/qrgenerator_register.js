$(function(){
	$("#jQueryBox").css("display", "none");
	$('#register_success').change(function(){
		$("#jQueryBox").toggle();
		$('#register_success').each(function(){
			var r = $(this).prop('checked');
			if(r == true){
				$(function(){
					$('#onetime_password').get(0).type = 'text';
					$('#onetime_password').get(0).value = '';
					$.getJSON("/merukari/" + "passGenerator", null, function(data, textStatus,	jqXHR) {
						$('#qrcode').qrcode({
							width : 150,
							height : 150,
							text : data
						})
					})
				});
			}else if(r == false){
				var qrcode = document.getElementById('qrcode')
				qrcode.innerHTML=""
			}
		})
	})
})