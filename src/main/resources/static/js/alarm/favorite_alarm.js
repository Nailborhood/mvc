document.addEventListener("DOMContentLoaded", function() {

    var socket = new SockJS("/echo");

    socket.onopen = function (){
        console.log("매장 찜 소켓 연결 완료");
    }

    const emptyLikeUrl = "/assets/icons/shop/emptyHeart.svg";
    const clickLikeUrl = "/assets/icons/shop/clickHeart.svg";

    $('.heart-btn').each(function() {
        var btn = $(this);
        var isFavorite = btn.attr('data-favorite-status') === 'true'; // 현재 좋아요 상태를 가져옵니다.
        var imgSrc = isFavorite ? '/assets/icons/shop/clickHeart.svg' : '/assets/icons/shop/emptyHeart.svg'; // 상태에 따라 이미지 경로를 결정합니다.
        btn.find('img').attr('src', imgSrc); // 이미지의 src 속성을 업데이트합니다.

        btn.click(function(e) {
            e.preventDefault(); // 기본 동작 방지

            var isLoggedIn = btn.attr('data-is-logged-in') === 'true';
            if (!isLoggedIn) {
                alert("로그인 후 이용할 수 있습니다.");
                return; // 로그인하지 않았으면 여기서 함수 종료
            }

            // jQuery를 사용하여 데이터를 추출합니다.
            var shopId = btn.attr('data-shop-id');
            var receiver = btn.attr('data-receiver');

            if (btn.attr('data-favorite-status') === 'true') {
                fetch('/favorite/' + shopId, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                })
                    .then(response => response.json())
                    .then(data => {
                        // 요청이 성공하면 찜 버튼의 상태와 아이콘을 업데이트합니다.
                        if (data.status) {
                            btn.find('img').attr('src', emptyLikeUrl);
                            location.reload();

                        }
                    })
                    .catch(error => console.error('Error:', error));
            } else if (btn.attr('data-favorite-status') === 'false'){
                fetch('/favorite/' + shopId, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                })
                    .then(response => response.json())
                    .then(data => {
                        if (data.status) {
                            btn.find('img').attr('src', clickLikeUrl);
                            sendFavoriteAlarm(shopId,receiver);
                            location.reload();

                        }
                    })
                    .catch(error => console.error('Error:', error));
            }
        });
    });


    function sendFavoriteAlarm(shopId,receiver){
        var alarmType = 'FAVORITE';
        var url = `/shopDetail/${shopId}`;

        console.log(shopId);
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

