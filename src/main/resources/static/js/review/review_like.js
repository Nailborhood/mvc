document.addEventListener("DOMContentLoaded", function() {

    const clickLikeUrl = "/assets/icons/review/clickThunbsUp.svg";
    const emptyLikeUrl = "/assets/icons/review/emptyThumbsUp.svg";

    $('.like').each(function() {
        var btn = $(this);
        var isFavorite = btn.attr('data-like-status') === 'true'; // 현재 좋아요 상태를 가져옵니다.
        var imgSrc = isFavorite ? '/assets/icons/review/clickThunbsUp.svg' : '/assets/icons/review/emptyThumbsUp.svg'; // 상태에 따라 이미지 경로를 결정합니다.
        btn.find('img').attr('src', imgSrc); // 이미지의 src 속성을 업데이트합니다.

        btn.click(function(e) {
            e.preventDefault(); // 기본 동작 방지

            var isLoggedIn = btn.attr('data-is-logged-in') === 'true';
            if (!isLoggedIn) {
                alert("로그인 후 이용할 수 있습니다.");
                return; // 로그인하지 않았으면 여기서 함수 종료
            }

            var shopId = btn.attr('data-shop-id');
            var reviewId = btn.attr('data-review-id');

            if (btn.attr('data-like-status') === 'true') {
                fetch( `/like/review/${reviewId}?shopId=${shopId}`, {
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
                fetch( `/like/review/${reviewId}?shopId=${shopId}`, {
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
                            location.reload();
                        }
                    })
                    .catch(error => console.error('Error:', error));
            }


        });
    });


});