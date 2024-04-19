
document.addEventListener('DOMContentLoaded', function () {
    var socket = new SockJS("/echo");
    socket.onopen = function() {
        console.log("Connected to the server.");
    };

    // Fetch initial alarm count
    $.ajax({
        type: "get",
        url: "/alarm/count",
        dataType: "json", // 명시적으로 응답 데이터 타입을 JSON으로 지정
        success: function (response) {
            var count = response.count;
            $("#newNoticeCnt").text(count);
        },
        error: function(xhr, status, error) {
            // 오류 발생 시 오류 상세 정보를 콘솔에 출력
            console.error("Error occurred: " + status + ", " + error);
            console.error("Response status: " + xhr.status);
            console.error("Response text: " + xhr.responseText);

            // 서버 응답이 JSON 형식으로 오류 메시지를 포함하고 있다면, 해당 내용을 파싱하여 출력
            try {
                var responseJson = JSON.parse(xhr.responseText);
                console.error("Error details:", responseJson);
            } catch(e) {
                console.error("Error parsing response text.");
            }
        }
    });

    // Handle incoming messages
    // socket.onmessage = function(evt) {
    //     var data = evt.data;
    //     console.log("New message:", data);
    //     $("#newNoticeCnt").text(parseInt($("#newNoticeCnt").text()) + 1);
    //     displayToast(data);
    // };
    //
    // function displayToast(message) {
    //     var toastHTML = "<div class='toast' role='alert'><strong>알림:</strong> " + message + "</div>";
    //     $("#msgStack").append(toastHTML);
    //     $(".toast").last().toast("show");
    // }

    socket.onmessage = function(evt) {
        var data = evt.data;
        // toast
        let toast = "<div class='toast' role='alert' aria-live='assertive' aria-atomic='true'>";
        toast += "<div class='toast-header'><i class='fas fa-bell mr-2'></i><strong class='mr-auto'>알림</strong>";
        toast += "<small class='text-muted'></small><button type='button' class='ml-2 mb-1 close' data-dismiss='toast' aria-label='Close'>";
        toast += "<span aria-hidden='true'>&times;</span></button>";
        toast += "</div> <div class='toast-body'>" + data + "</div></div>";
        $("#msgStack").append(toast);
        $(".toast").toast({"animation": true, "autohide": false});
 		$(".toast").toast({"animation": true, "autohide": true, "delay": 5000});
        $('.toast').toast('show');
        // 알림 카운트 추가
        $("#newNoticeCnt").text($("#newNoticeCnt").text()*1+1);
    }
});
