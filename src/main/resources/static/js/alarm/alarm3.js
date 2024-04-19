document.addEventListener('DOMContentLoaded', function () {
    var socket = new SockJS("/echo");
    socket.onopen = function() {
        console.log("Connected to the server.");
    };

    // Fetch initial alarm count
    fetch("/alarm/count")
        .then(response => response.json())
        .then(data => {
            var count = data.count;
            // document.getElementById("newNoticeCnt").innerText = count;
            var noticeElement = document.getElementById("newNoticeCnt");
            if(noticeElement){
                noticeElement.innerText = count;
            }else {
                console.log("아직 로그인이 되지 않아서 알림 수를 알 수 없습니다")
            }
        })
        .catch(error => {
            console.error("Error occurred: ", error);
        });

    socket.onmessage = function(evt) {
        var data = evt.data;

        // Toast 구성
        let toastHTML = "<div class='toast' role='alert' aria-live='assertive' aria-atomic='true'>";
        toastHTML += "<div class='toast-header'><i class='fas fa-bell mr-2'></i><strong class='mr-auto'>알림</strong>";
        toastHTML += "<small class='text-muted'></small><button type='button' class='ml-2 mb-1 close' data-bs-dismiss='toast' aria-label='Close'>";
        toastHTML += "<span aria-hidden='true'>&times;</span></button>";
        toastHTML += "</div> <div class='toast-body'>" + data + "</div></div>";

        document.getElementById("msgStack").innerHTML += toastHTML;

        // 새로운 Toast를 화면에 보여줌
        var toastElList = [].slice.call(document.querySelectorAll('.toast'));
        var toastList = toastElList.map(function(toastEl) {
            return new bootstrap.Toast(toastEl, {
                animation: true,
                // autohide: true,
                // delay: 5000
            });
        });
        toastList.forEach(toast => toast.show());

        // 알림 카운트 추가
        var currentCount = parseInt(document.getElementById("newNoticeCnt").innerText);
        document.getElementById("newNoticeCnt").innerText = currentCount + 1;
    }
});
