document.addEventListener('DOMContentLoaded', function () {
    // 웹소켓 초기화
    var socket = new SockJS("/echo");
    socket.onopen = function() {
        console.log("Connected to the server.");
    };

    // 초기 알람 카운트 가져오기
    fetch("/alarm/count")
        .then(response => response.json())
        .then(data => {
            var count = data.count;
            var noticeElement = document.getElementById("newNoticeCnt");
            if (noticeElement) {
                noticeElement.innerText = count;
            } else {
                console.log("아직 로그인이 되지 않아서 알림 수를 알 수 없습니다.");
            }
        })
        .catch(error => {
            console.error("Error occurred: ", error);
        });

    // 웹소켓 메시지 수신 처리
    socket.onmessage = function(evt) {
        var message = JSON.parse(evt.data);
        displayToast(message);
    }

    // 토스트 메시지를 화면에 표시
    function displayToast(message) {
        let toastHTML = "<div class='toast' role='alert' aria-live='assertive' aria-atomic='true'>";
        toastHTML += "<div class='toast-header'><i class='fas fa-bell mr-2'></i><strong class='mr-auto'>" + message.alarmType + "</strong>";
        toastHTML += "<small class='text-muted'></small><button type='button' class='ml-2 mb-1 close' data-bs-dismiss='toast' aria-label='Close'>";
        toastHTML += "<span aria-hidden='true'>&times;</span></button></div>";
        toastHTML += "<div class='toast-body'>";
        toastHTML += "<a href='" + message.url + "' data-alarm-id='" + message.alarmId + "' style='color: black; text-decoration: none;'>" + message.alarmType + "가 등록되었습니다.</a>";
        toastHTML += "</div></div>";
        document.getElementById("msgStack").innerHTML += toastHTML;


        // 새로운 Toast를 화면에 보여줌
        var toastElList = [].slice.call(document.querySelectorAll('.toast'));
        var toastList = toastElList.map(function(toastEl) {
            return new bootstrap.Toast(toastEl, { animation: true });
        });
        toastList.forEach(toast => toast.show());

        console.log(message.url);
        console.log(message.alarmId);

        // 알림 카운트 추가
        var currentCount = parseInt(document.getElementById("newNoticeCnt").innerText);
        document.getElementById("newNoticeCnt").innerText = currentCount + 1;
    }

    // 클릭 이벤트 리스너 추가
    document.addEventListener('click', function(e) {
        if (e.target.closest('.toast')) {
            const link = e.target.closest('a');
            if (link) {
                const alarmId = link.getAttribute('data-alarm-id');  // 알람 ID 추출
                console.log("Alarm ID:", alarmId);
                markAlarmAsRead(alarmId);
            }
        }
    });

    // 서버에 알람 확인 요청 보내기
    // var alarmReadState = {};  // 알람 읽기 상태 저장 객체

    function markAlarmAsRead(alarmId) {
        // if (alarmReadState[alarmId]) {
        //     console.log('이미 처리된 알람입니다.');
        //     return;  // 이 알람에 대한 추가 처리 방지
        // }
        //
        // alarmReadState[alarmId] = true;  // 알람 처리 상태 설정

        fetch('/alarm/isChecked', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ alarmId: alarmId })
        })
            .then(response => {
                if (response.ok) {

                    var currentCount = parseInt(document.getElementById("newNoticeCnt").innerText);
                    document.getElementById("newNoticeCnt").innerText = Math.max(currentCount - 1, 0);
                    console.log('알람 상태 변경이 성공했습니다');
                } else {
                    console.error('알림 상태 변경이 실패했습니다');
                }
            })
            .catch(error => {
                console.error('Error marking alarm as read:', error);
            })
            // .finally(() => {
            //     delete alarmReadState[alarmId];  // 처리 완료 후 상태 삭제
            // });
    }

});
