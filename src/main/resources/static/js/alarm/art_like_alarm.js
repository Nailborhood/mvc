document.addEventListener("DOMContentLoaded", function() {
    var socket = new SockJS("/echo");

    socket.onopen = function (){
        console.log("아트판 소켓 연결 완료");
    }
    const clickLikeUrl = "/assets/icons/art/clickLike.svg";
    const emptyLikeUrl = "/assets/icons/art/emptyLike.svg";

    $('.artLike').each(function() {
        var btn = $(this);
        var isFavorite = btn.attr('data-like-status') === 'true';
        var imgSrc = isFavorite ? '/assets/icons/art/clickLike.svg' : '/assets/icons/art/emptyLike.svg';
        btn.find('img').attr('src', imgSrc);

        btn.click(function(e) {
            e.preventDefault();

            var isLoggedIn = btn.attr('data-is-logged-in') === 'true';
            if (!isLoggedIn) {
                alert("로그인 후 이용할 수 있습니다.");
                return;
            }

            var artRefId = btn.attr('data-art-id');
            var receiver = btn.attr('data-receiver');

            if (btn.attr('data-like-status') === 'true') {
                fetch( `/artboard/like/${artRefId}`, {
                    method: 'POST',
                    headers:{
                        'Content-Type': 'application/json',
                    },
                })
                    .then(response => response.json())
                    .then(data => {
                        // 요청이 성공하면 찜 버튼의 상태와 아이콘을 업데이트합니다.
                        if(data.status) {
                            btn.find('img').attr('src', emptyLikeUrl);
                            location.reload();
                        }
                    })
                    .catch(error => console.error('Error:', error));
            } else if (btn.attr('data-like-status') === 'false'){
                fetch( `/artboard/like/${artRefId}`, {
                    method: 'POST',
                    headers:{
                        'Content-Type': 'application/json',
                    },
                })
                    .then(response => response.json())
                    .then(data => {
                        // 요청이 성공하면 찜 버튼의 상태와 아이콘을 업데이트합니다.
                        if(data.status) {
                            btn.find('img').attr('src', clickLikeUrl);
                            sendFavoriteAlarm(artRefId,receiver);
                            location.reload();
                        }
                    })
                    .catch(error => console.error('Error:', error));
            }
        });
    });

    function sendFavoriteAlarm(artRefId,receiver){
        var alarmType = 'LIKE_ART';
        var url = `/artboard/inquiry/${artRefId}`;


        console.log(receiver);
        console.log(alarmType);
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
            .then(response => response.json())  // 응답을 JSON으로 파싱
            .then(data => {
                if (data.alarmId) {
                    // 성공 로직 처리, 서버로 메시지 전송
                    console.log("Alarm sent with ID:", data.alarmId);
                    socket.send(`${alarmType},${receiver},${url},${data.alarmId}`);
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