document.addEventListener('DOMContentLoaded', function() {
    // 모든 별점 요소를 선택합니다.
    var ratings = document.querySelectorAll('.rating-number');
    var fullStar = '★';
    var emptyStar = '☆';

    ratings.forEach(function(rating) {
        var stars = '';
        var ratingValue = parseInt(rating.textContent); // 별점 숫자로 변환
        var starsContainer = rating.closest('.review-info_under').querySelector('.stars'); // 해당 별점의 별을 표시할 요소를 찾습니다.

        // 별점에 따라 별을 생성합니다.
        for(var i = 1; i <= 5; i++) {
            if(i <= ratingValue) {
                stars += fullStar; // 별점보다 i가 작거나 같으면, 채워진 별을 추가
            } else {
                stars += emptyStar; // 그렇지 않으면, 빈 별을 추가
            }
        }

        // 생성된 별 문자열을 starsContainer 내부에 설정합니다.
        starsContainer.textContent = stars;
    });
});