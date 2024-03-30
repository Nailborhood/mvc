

    document.addEventListener('DOMContentLoaded', function() {
    // 별점 데이터를 가져옵니다. 실제로는 서버로부터 받은 데이터를 사용해야 합니다.
    // 예시에서는 JavaScript 내에 하드코딩되어 있지만, 실제로는 서버로부터 받아와야 합니다.
    var rating = document.querySelector('.rating-number').textContent; // 별점
    var starsContainer = document.querySelector('.stars'); // 별을 표시할 요소
    var fullStar = '★';
    var emptyStar = '☆';
    var stars = '';

    // 별점에 따라 별을 생성합니다.
    for(var i = 1; i <= 5; i++) {
    if(i <= rating) {
    stars += fullStar; // 별점보다 i가 작거나 같으면, 채워진 별을 추가
} else {
    stars += emptyStar; // 그렇지 않으면, 빈 별을 추가
}
}

    // 생성된 별 문자열을 starsContainer 내부에 설정합니다.
    starsContainer.textContent = stars;
});
