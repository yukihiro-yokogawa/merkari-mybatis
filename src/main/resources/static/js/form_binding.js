$(document).ready(function(){
	var itemCondition = $("input[name='itemCondition']").val();
	$("input[name=condition][value=" + itemCondition + "]").prop("checked",true);
});