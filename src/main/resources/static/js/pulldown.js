$.getJSON("/merukari/" + "categoryList", null, function (data, textStatus, jqXHR) { // dataにJSONが詰まっている
	// 取得したJSONで行いたい処理を記述できる
	
	data.forEach(category_list => {
		var parent_category_name = Object.values(category_list)[1];
		var parent_category_id = Object.values(category_list)[0];
		$('#parent-category').append($('<option>').html(parent_category_name).val(parent_category_id));
		var parentId = $('input[name=hiddenParent]').val();
		if(parentId == "" || parentId == null){
			parentId = 0;
		}
		$('#parent-category').val([parentId]);
		var childId = $('input[name=hiddenChild]').val();
		var grandChildId = $('input[name=hiddenGrandChild]').val();
		var parent_category_id = Object.values(category_list)[0];
		console.log(parentId)
		if(parentId == parent_category_id){
			Object.values(category_list)[2].forEach(child_category_list =>{
				var child_category_id = Object.values(child_category_list)[0];
				var child_category_name = Object.values(child_category_list)[1];
				$('#child-category').append($('<option>').html(child_category_name).val(child_category_id));
				$('#child-category').val([childId]);
				if(childId == Object.values(child_category_list)[0]){	
					Object.values(child_category_list)[2].forEach(grand_child_category =>{
						var grand_child_category_id = Object.values(grand_child_category)[0];
						var grand_child_category_name = Object.values(grand_child_category)[1];
						$('#grand-child-category').append($('<option>').html(grand_child_category_name).val(grand_child_category_id));
						$('#grand-child-category').val([grandChildId]);
					})
				}
			})
		}
	});
	
	
	
	$('#parent-category').change(function(){
		var parent_val = parseInt($('#parent-category').val());
		$('#child-category').children().remove();
		$('#child-category').append($('<option>').html("- childCategory -").val(0));
		$('#grand-child-category').children().remove();
		$('#grand-child-category').append($('<option>').html("- grandChild -").val(0));
		data.forEach(category_list =>{
			var parent_category_id = Object.values(category_list)[0];
			if(parent_val == parent_category_id){
				Object.values(category_list)[2].forEach(child_category_list =>{
					var child_category_id = Object.values(child_category_list)[0];
					var child_category_name = Object.values(child_category_list)[1];
					$('#child-category').append($('<option>').html(child_category_name).val(child_category_id));
				})
			}
		})
		// 中カテゴリが選べるようになる時に大は操作してるので、その時に大は決まってる
		// 選択した大が確定しているので、大の中カテゴリを取り出しているはずなので、pulldownにセットしたときに同時に貶すにも持たせる
		$('#child-category').change(function(){
			var child_val = parseInt($('#child-category').val());
			$('#grand-child-category').children().remove();
			$('#grand-child-category').append($('<option>').html("- grandChild -").val(0));
			data.forEach(category_list =>{
				var parent_category_id = Object.values(category_list)[0];
				if(parent_val == parent_category_id){
					Object.values(category_list)[2].forEach(child_category_list =>{
						if(child_val == Object.values(child_category_list)[0]){	
							Object.values(child_category_list)[2].forEach(grand_child_category =>{
								var grand_child_category_id = Object.values(grand_child_category)[0];
								var grand_child_category_name = Object.values(grand_child_category)[1];
								$('#grand-child-category').append($('<option>').html(grand_child_category_name).val(grand_child_category_id));
							})
						}
					})
				}
			})
		});
	});
});