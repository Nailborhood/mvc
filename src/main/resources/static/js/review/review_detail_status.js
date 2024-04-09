    /* 페이지 로드 시 실행되는 함수 */
window.onload = function() {
    var body = document.body;
    var isDeleted = body.getAttribute('data-review-isDeleted') === 'true'; // 문자열 "true"를 불리언 true로 변환
    var reviewReportStatus = body.getAttribute('data-review-status');
    var shopStatus = body.getAttribute('data-shop-status');

    // 조건 검사
    if (isDeleted) {
        alert('삭제된 리뷰입니다.');
        window.location.href = '/mypage/review/inquiry';

    } else if (reviewReportStatus === '신고 처리됨') {
        alert('리뷰가 신고되었습니다.');
        window.location.href = '/mypage/review/inquiry';

    } else if (shopStatus === 'BEFORE_OPEN') {
        alert('매장영업이 중지되었습니다.');
        window.location.href = '/mypage/review/inquiry';
    }
}
