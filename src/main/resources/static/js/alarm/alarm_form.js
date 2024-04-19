document.addEventListener('DOMContentLoaded', function () {
    var socket = new SockJS("/echo");

    socket.onopen = function() {
        console.log("Socket connection established.");
        // hidden input에서 리뷰 ID와 매장 ID를 추출
        var reviewId = document.getElementById("reviewId").value;
        var shopId = document.getElementById("shopId").value;
        var receiverEmail = document.getElementById("receiver").value; // 이메일 추출

        var alarmSent = new URLSearchParams(window.location.search).get('alarmSent');

        if (alarmSent === 'true') {
            sendReviewAlarm(reviewId, shopId, receiverEmail);
        }
    };


    function sendReviewAlarm(reviewId, shopId, receiverEmail) {
        var alarmType = '리뷰';
        var url = `/review/inquiry/${reviewId}?shopId=${shopId}`;

        console.log(receiverEmail);
        console.log(url);

        fetch("/alarm/save", {
            method: "POST",
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                receiver: receiverEmail,
                alarmType: alarmType,
                url: url
            })
        })
            .then(response => response.json())  // 응답을 JSON으로 파싱
            .then(data => {
                if (data.alarmId) {
                    // 성공 로직 처리, 서버로 메시지 전송
                    console.log("Alarm sent with ID:", data.alarmId);
                    socket.send(`${alarmType},${receiverEmail},${url},${data.alarmId}`);
                } else {
                    throw new Error("Failed to retrieve alarm ID.");
                }
            })
            .catch(error => {
                console.error("Alarm sending failed:", error);
                alert("알람 발송에 실패했습니다.");
            });
    }

});
