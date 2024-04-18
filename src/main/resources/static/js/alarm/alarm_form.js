/*document.addEventListener('DOMContentLoaded', function () {
    var socket = null;
    var sock = new SockJS("/echo");
    socket = sock;

    var shopId = document.getElementById("shopId").value;

    document.getElementById("review-form").addEventListener("submit", function(event) {
        event.preventDefault(); // 폼 기본 제출 이벤트 방지
        let formData = new FormData(this);

        // 리뷰 등록 AJAX 요청
        fetch("/" + shopId + "/review/registration", {
            method: "POST",
            body: formData
        })
            .then(response => {
                if (response.ok) {
                    sendReviewAlarm();
                } else {
                    throw new Error("리뷰 등록에 실패했습니다.");
                }
            })
            .catch(error => {
                console.error(error);
                alert("리뷰 등록에 실패했습니다.");
            });
    });

    function sendReviewAlarm() {
        let receiver = document.getElementById("receiverName").value;
        let alarmType = 'REVIEW';
        let url = '/alarm/list';

        fetch("/alarm/save", {
            method: "POST",
            headers: {
                'Content-Type': 'application/json'  // JSON 데이터 전송을 위한 헤더 설정
            },
            body: JSON.stringify({
                receiver: receiver,
                alarmType: alarmType,
                url: url
            })
        })
            .then(response => {
                if (response.ok) {
                    // 성공 로직 처리
                    socket.send(alarmType+","+receiver+","+url);
                } else {
                    throw new Error("알람 발송에 실패했습니다.");
                }
            })
            .catch(error => {
                console.error(error);
                alert("알람 발송에 실패했습니다.");
            });
    }
});*/

/*
document.addEventListener('DOMContentLoaded', function () {

    var socket = null;
    var sock = new SockJS("/echo");
    socket = sock;


    sendReviewAlarm();

    function sendReviewAlarm() {
        var receiverDiv = document.getElementById('receiver');
         // 이메일 값을 가져옴
        // let receiver = document.getElementById("receiverName").value;
        let receiver = receiverDiv.getAttribute('data-receiver-email');
        let alarmType = 'REVIEW';
        let url = '/alarm/list';

        fetch("/alarm/save", {
            method: "POST",
            headers: {
                'Content-Type': 'application/json'  // JSON 데이터 전송을 위한 헤더 설정
            },
            body: JSON.stringify({
                receiver: receiver,
                alarmType: alarmType,
                url: url
            })
        })
            .then(response => {
                if (response.ok) {
                    // 성공 로직 처리
                    socket.send(alarmType+","+receiver+","+url);
                } else {
                    throw new Error("알람 발송에 실패했습니다.");
                }
            })
            .catch(error => {
                console.error(error);
                alert("알람 발송에 실패했습니다.");
            });
    }
});
*/

/*document.addEventListener('DOMContentLoaded', function () {
    var sock = new SockJS("/echo");

    sock.onopen = function() {
        console.log("Socket connection established.");
        // URL에서 쿼리 파라미터를 추출하여 알람 조건 확인
        var urlParams = new URLSearchParams(window.location.search);
        var alarmSent = urlParams.get('alarmSent');
        var reviewId = urlParams.get('reviewId');
        var shopId = urlParams.get('shopId');

        if (alarmSent === 'true') {
            sendReviewAlarm(reviewId, shopId);
        }
    };

    function sendReviewAlarm(reviewId, shopId) {
        var receiver = document.getElementById("receiver").dataset.receiverEmail;
        var alarmType = 'REVIEW';
        var url = `/review/inquiry/${reviewId}?shopId=${shopId}`;

        console.log(receiver);
        console.log(url);

        fetch("/alarm/save", {
            method: "POST",
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                receiver: receiver,
                alarmType: alarmType,
                url: url
            })
        })
            .then(response => {
                if (response.ok) {
                    // 성공 로직 처리, 서버로 메시지 전송
                    //sock.send(`${alarmType},${receiver},${url}`);
                    sock.send(alarmType+","+receiver+","+url);
                } else {
                    throw new Error("Failed to send alarm.");
                }
            })
            .catch(error => {
                console.error("Alarm sending failed:", error);
                alert("알람 발송에 실패했습니다.");
            });
    }
});*/

document.addEventListener('DOMContentLoaded', function () {
    var sock = new SockJS("/echo");

    sock.onopen = function() {
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

    /*function sendReviewAlarm(reviewId, shopId, receiverEmail) {
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
            .then(response => {
                if (response.ok) {
                    // 성공 로직 처리, 서버로 메시지 전송
                    sock.send(`${alarmType},${receiverEmail},${url}`);
                } else {
                    throw new Error("Failed to send alarm.");
                }
            })
            .catch(error => {
                console.error("Alarm sending failed:", error);
                alert("알람 발송에 실패했습니다.");
            });
    }*/

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
                    sock.send(`${alarmType},${receiverEmail},${url},${data.alarmId}`);
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
