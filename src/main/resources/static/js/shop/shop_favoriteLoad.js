// document.addEventListener("DOMContentLoaded", function() {
//     // 가정: shopHeartStatus 값은 서버로부터 받아와야 합니다.
//     // 이 예제에서는 단순화를 위해 true로 설정합니다. 실제로는 서버로부터 이 값을 동적으로 받아와야 합니다.
//     var shopHeartStatus = true; // 이 값은 서버로부터 동적으로 할당받아야 합니다.
//
//     // shopHeartStatus 값에 따라 적절한 이미지 경로를 결정합니다.
//     var heartIconSrc = shopHeartStatus ? '/assets/icons/shop/emptyHeart.svg' : '/assets/icons/shop/clickHeart.svg';
//
//     // 이미지의 src 속성을 동적으로 설정합니다.
//     document.getElementById('heartIcon').setAttribute('src', heartIconSrc);
// });
document.addEventListener("DOMContentLoaded", function() {
    $('.heart-btn').each(function() {
        var btn = $(this);
        var isFavorite = btn.attr('data-favorite-status') === 'true'; // 현재 좋아요 상태를 가져옵니다.
        var imgSrc = isFavorite ? '/assets/icons/shop/clickHeart.svg' : '/assets/icons/shop/emptyHeart.svg'; // 상태에 따라 이미지 경로를 결정합니다.
        btn.find('img').attr('src', imgSrc); // 이미지의 src 속성을 업데이트합니다.
    });
});
