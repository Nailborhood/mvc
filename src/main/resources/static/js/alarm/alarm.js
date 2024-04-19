document.addEventListener('DOMContentLoaded', function () {
// 웹소켓 연결
var socket  = null;



$(document).ready(function(){
    sock = new SockJS("/echo");
    socket = sock;

    // 데이터를 전달 받았을때
    sock.onmessage = onMessage;




    // 알림 카운트 받아오기
    $.ajax({
        type: "get",
        async: true,
        dataType: "text",
        url: "/alarm/count",
        success: function (data, textStatus) {
            if(data != '0'){
                $("#newNoticeCnt").text(data);
            }
        }
    });




});

// 실시간 알림 받았을 시
function onMessage(evt){
    var data = evt.data;
    // toast
    let toast = "<div class='toast' role='alert' aria-live='assertive' aria-atomic='true'>";
    toast += "<div class='toast-header'><i class='fas fa-bell mr-2'></i><strong class='mr-auto'>알림</strong>";
    toast += "<small class='text-muted'></small><button type='button' class='ml-2 mb-1 close' data-dismiss='toast' aria-label='Close'>";
    toast += "<span aria-hidden='true'>&times;</span></button>";
    toast += "</div> <div class='toast-body'>" + data + "</div></div>";
    $("#msgStack").append(toast);
    $(".toast").toast({"animation": true, "autohide": false});
// 		$(".toast").toast({"animation": true, "autohide": true, "delay": 5000});
    $('.toast').toast('show');
    // 알림 카운트 추가
    $("#newNoticeCnt").text($("#newNoticeCnt").text()*1+1);
}

});