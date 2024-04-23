document.addEventListener("DOMContentLoaded", function() {

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
                            location.reload();
                        }
                    })
                    .catch(error => console.error('Error:', error));
            }
        });
    });
});