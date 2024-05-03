// 소켓 연결 파일 로드
document.addEventListener('DOMContentLoaded', function () {
    // 소켓 연결 코드가 이미 socket_connection.js에 포함되어 있음
    // 따라서 여기서는 소켓을 직접 초기화할 필요 없음
    var reviewId = document.getElementById("reviewId").value;
    var shopId = document.getElementById("shopId").value;
    var receiverEmail = document.getElementById("receiver").value; // 이메일 추출

    var alarmSent = new URLSearchParams(window.location.search).get('alarmSent');

    if (alarmSent === 'true') {
        sendReviewAlarm(reviewId, shopId, receiverEmail);
    }
});


function sendReviewAlarm(reviewId, shopId, receiverEmail) {
    var alarmType = 'REVIEW';
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
