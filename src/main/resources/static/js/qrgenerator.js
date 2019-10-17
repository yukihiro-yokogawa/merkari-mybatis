$.getJSON("/merukari/" + "passGenerator", null, function(data, textStatus,	jqXHR) {
	console.log(data);
	$('#qrcode').qrcode({
		width : 150,
		height : 150,
		text : data
	})
});

var start = new Date();

//初期化
var hour = 0;
var min = 0;
var sec = 0;
var now = 0;
var datet = 0;

function disp(){

	now = new Date();

	datet = parseInt((now.getTime() - start.getTime()) / 1000);

	sec = datet % 60;

	// 数値が1桁の場合、頭に0を付けて2桁で表示する指定

	// フォーマットを指定（不要な行を削除する）
	var remaining_time = 20 - sec; // パターン1

	// テキストフィールドにデータを渡す処理（不要な行を削除する）
	var timer = document.getElementById('timer')
	timer.innerHTML="このページは"+ remaining_time +"秒後にログインページに自動でジャンプします。"
	
	if(remaining_time != 0){
		setTimeout("disp()", 1000);
	}
}

window.onload = disp;