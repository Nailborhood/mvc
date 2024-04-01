$(document).ready(function() {
    $('.heart-btn').click(function(event) {
        event.preventDefault();

        // 데이터를 추출합니다.
        var shopId = $(this).attr('data-shop-id');

        // AJAX 요청을 보냅니다.
        $.ajax({
            url: '/favorite/' + shopId  ,
            type: 'POST',
            success: function(response) {
                // 성공적으로 요청이 처리되었을 때의 동작을 정의합니다.
                console.log('매장 저장 성공:', response);
            },
            error: function(xhr, status, error) {
                // 오류가 발생했을 때의 동작을 정의합니다.
                console.error('매장 저장 실패:', error);
            }
        });
    });
});