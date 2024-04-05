// $(document).ready(function() {
//     $('.heart-btn').click(function(event) {
//         event.preventDefault();
//
//         // 데이터를 추출합니다.
//         var shopId = $(this).attr('data-shop-id');
//
//         // AJAX 요청을 보냅니다.
//         $.ajax({
//             url: '/favorite/' + shopId  ,
//             type: 'POST',
//             success: function(response) {
//                 // 성공적으로 요청이 처리되었을 때의 동작을 정의합니다.
//                 console.log('매장 저장 성공:', response);
//             },
//             error: function(xhr, status, error) {
//                 // 오류가 발생했을 때의 동작을 정의합니다.
//                 console.error('매장 저장 실패:', error);
//             }
//         });
//     });
// });

// document.addEventListener("DOMContentLoaded", function() {
//     // jQuery를 사용하여 모든 찜 버튼에 접근합니다.
//     $('.heart-btn').each(function() {
//         $(this).click(function(e) {
//             e.preventDefault(); // 기본 동작 방지
//
//             // jQuery를 사용하여 데이터를 추출합니다.
//             var shopId = $(this).attr('data-shop-id');
//             var isFavorite = $(this).attr('data-favorite-status') === 'true';
//
//             // AJAX 요청을 보냅니다. 여기서는 jQuery의 $.ajax 대신 fetch API를 사용합니다.
//             fetch('/favorite/' + shopId , {
//                 method: 'POST',
//                 // 필요한 경우 헤더 추가
//                 headers: {
//                     'Content-Type': 'application/json',
//                     // 추가적으로 필요한 헤더가 있다면 여기에 추가
//                 },
//                 // body: JSON.stringify({ 예시 데이터 }),
//             })
//                 .then(response => response.json())
//                 .then(data => {
//                     // 요청이 성공하면 찜 버튼의 상태와 아이콘을 업데이트합니다.
//                     if(data.status) {
//                         $(this).attr('data-favorite-status', data.favorite.status.toString());
//                         var imgSrc = data.favorite.status ? '/assets/icons/shop/emptyHeart.svg' : '/assets/icons/shop/clickHeart.svg';
//                         $(this).find('img').attr('src', imgSrc);
//                     }
//                 })
//                 .catch(error => console.error('Error:', error));
//         });
//     });
// });
// document.addEventListener("DOMContentLoaded", function() {
//     // jQuery를 사용하여 모든 찜 버튼에 접근합니다.
//     $('.heart-btn').each(function() {
//         var btn = $(this); // 클릭 이벤트 내에서 this를 사용하기 위해 변수에 저장합니다.
//         btn.click(function(e) {
//             e.preventDefault(); // 기본 동작 방지
//
//             // jQuery를 사용하여 데이터를 추출합니다.
//             var shopId = btn.attr('data-shop-id');
//             var isFavorite = btn.attr('data-favorite-status') === 'true';
//
//             // AJAX 요청을 보냅니다. 여기서는 jQuery의 $.ajax 대신 fetch API를 사용합니다.
//             fetch('/favorite/' + shopId , {
//                 method: 'POST',
//                 // 필요한 경우 헤더 추가
//                 headers: {
//                     'Content-Type': 'application/json',
//                     // 추가적으로 필요한 헤더가 있다면 여기에 추가
//                 },
//                 // body: JSON.stringify({ 예시 데이터 }),
//             })
//                 .then(response => response.json())
//                 .then(data => {
//                     // 요청이 성공하면 찜 버튼의 상태와 아이콘을 업데이트합니다.
//                     if(data.status) {
//                         btn.attr('data-favorite-status', data.shopDetailLookupResponseDto.heartStatus.toString());
//                         var imgSrc = data.shopDetailLookupResponseDto.heartStatus ? '/assets/icons/shop/clickHeart.svg' : '/assets/icons/shop/emptyHeart.svg';
//                         btn.find('img').attr('src', imgSrc);
//                     }
//                 })
//                 .catch(error => console.error('Error:', error));
//         });
//     });
// });

// $(document).ready(function() {
//     $('.heart-btn').click(function(event) {
//         event.preventDefault();
//
//         // 데이터를 추출합니다.
//         var shopId = $(this).attr('data-shop-id');
//         var isFavorite = $(this).attr('data-favorite-status') === 'true';
//
//         // AJAX 요청을 보냅니다.
//         $.ajax({
//             url: '/favorite/' + shopId,
//             type: 'POST',
//             dataType: 'json', // 응답을 JSON 형태로 받기 위해 설정
//             success: function(response) {
//                 console.log('매장 저장 성공:', response);
//                 // 성공적으로 요청이 처리되었을 때의 동작을 정의합니다.
//                 if(response.status) {
//                     var button = $('.heart-btn[data-shop-id="' + shopId + '"]');
//                     button.attr('data-favorite-status', response.data.shopDetailLookupResponseDto.heartStatus.toString());
//                     var imgSrc = response.favorite.status ? '/assets/icons/shop/filledHeart.svg' : '/assets/icons/shop/clickHeart.svg';
//                     button.find('img').attr('src', imgSrc);
//                 }
//             },
//             error: function(xhr, status, error) {
//                 // 오류가 발생했을 때의 동작을 정의합니다.
//                 console.error('매장 저장 실패:', error);
//             }
//         });
//     });
// });


// const emptyLikeUrl = "/assets/icons/shop/emptyHeart.svg";
// const clickLikeUrl = "/assets/icons/shop/clickHeart.svg";
// $(function(){
//
//     // 현재 로그인한 유저가 해당 게시물을 좋아요 했다면 likeVal = true,
//     // 좋아요하지 않았다면 false
//     // let likeVal = $('#like_check').val(); // 데이터가 있으면 true
//     var isFavorite = $(this).attr('data-favorite-status') === 'true';
//     const heartImg = $('#heartImg');
//
//     console.log("isFavorite : " + isFavorite);
//
//     if(isFavorite === 'true'){
//         // 데이터가 존재하면 화면에 채워진 하트 보여줌
//         $('#heartImg').attr("src", clickLikeUrl);
//     } else if(isFavorite === 'false'){
//         // 데이터가 없으면 화면에 빈 하트 보여줌
//         $('#heartImg').attr("src", emptyLikeUrl);
//     }
// });

/** 좋아요 클릭 시 실행 **/
// $(document).ready(function() {
//     $('.heart-btn').click(function() {
//
//         var shopId = $(this).attr('data-shop-id');
//         var isFavorite = $(this).attr('data-favorite-status') === 'true';
//
//         console.log(isFavorite);
//         if (isFavorite === 'true') {
//             console.log("추천 취소 진입");
//             $.ajax({
//                 type: 'POST',
//                 url: '/favorite/' + shopId,
//                 // contentType: 'application/json; charset=utf-8'
//             }).done(function () {
//                 $('#heartImg').attr("src", emptyLikeUrl);
//             }).fail(function (error) {
//                 alert(JSON.stringify(error));
//             })
//
//         } else if(isFavorite === 'false'){
//                 console.log("추천 진입");
//                 $.ajax({
//                     type: 'POST',
//                     url: '/favorite/' + shopId,
//                 }).done(function () {
//                     $('#likeImg').attr("src", clickLikeUrl);
//                 }).fail(function (error) {
//                     alert(JSON.stringify(error));
//                 })
//
//         }
//     });
// });

// document.addEventListener("DOMContentLoaded", function() {
//     // jQuery를 사용하여 모든 찜 버튼에 접근합니다.
//     $('.heart-btn').each(function() {
//         var btn = $(this); // 클릭 이벤트 내에서 this를 사용하기 위해 변수에 저장합니다.
//         btn.click(function(e) {
//             e.preventDefault(); // 기본 동작 방지
//
//             // jQuery를 사용하여 데이터를 추출합니다.
//             var shopId = btn.attr('data-shop-id');
//             var isFavorite = btn.attr('data-favorite-status') === 'true';
//
//             // AJAX 요청을 보냅니다. 여기서는 jQuery의 $.ajax 대신 fetch API를 사용합니다.
//             fetch('/favorite/' + shopId , {
//                 method: 'POST',
//                 // 필요한 경우 헤더 추가
//                 headers: {
//                     'Content-Type': 'application/json',
//                     // 추가적으로 필요한 헤더가 있다면 여기에 추가
//                 },
//                 // body: JSON.stringify({ 예시 데이터 }),
//             })
//                 .then(response => response.json())
//                 .then(data => {
//                     // 요청이 성공하면 찜 버튼의 상태와 아이콘을 업데이트합니다.
//                     if(data.status) {
//                         btn.attr('data-favorite-status', data.favorite.status.toString());
//                         var imgSrc = data.favorite.status ? '/assets/icons/shop/clickHeart.svg' : '/assets/icons/shop/emptyHeart.svg';
//                         btn.find('img').attr('src', imgSrc);
//                     }
//                 })
//                 .catch(error => console.error('Error:', error));
//         });
//     });
// });

// 눌렀을 때 색깔이 바뀌진않지만 새로고침하면 바뀜
document.addEventListener("DOMContentLoaded", function() {
    // jQuery를 사용하여 모든 찜 버튼에 접근합니다.
    $('.heart-btn').each(function() {
        var btn = $(this); // 클릭 이벤트 내에서 this를 사용하기 위해 변수에 저장합니다.
        btn.click(function(e) {
            e.preventDefault(); // 기본 동작 방지

            var isLoggedIn = btn.attr('data-is-logged-in') === 'true';
            if (!isLoggedIn) {
                alert("로그인 후 이용할 수 있습니다.");
                return; // 로그인하지 않았으면 여기서 함수 종료
            }

            // jQuery를 사용하여 데이터를 추출합니다.
            var shopId = btn.attr('data-shop-id');
            var isFavorite = btn.attr('data-favorite-status') === 'true';

            // AJAX 요청을 보냅니다. 여기서는 jQuery의 $.ajax 대신 fetch API를 사용합니다.
            fetch('/favorite/' + shopId , {
                method: 'POST',
                // 필요한 경우 헤더 추가
                headers: {
                    'Content-Type': 'application/json',
                    // 추가적으로 필요한 헤더가 있다면 여기에 추가
                },
                // body: JSON.stringify({ 예시 데이터 }),
            })
                .then(response => response.json())
                .then(data => {
                    // 요청이 성공하면 찜 버튼의 상태와 아이콘을 업데이트합니다.
                    if(data.status) {
                        btn.attr('data-favorite-status', data.favorite.status.toString());
                        var imgSrc = data.favorite.status ? '/assets/icons/shop/clickHeart.svg' : '/assets/icons/shop/emptyHeart.svg';
                        btn.find('img').attr('src', imgSrc);
                    }
                })
                .catch(error => console.error('Error:', error));
        });
    });
});

