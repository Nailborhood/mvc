document.addEventListener("DOMContentLoaded", function(){
    // 모든 .chart-item 요소에 대해 클릭 이벤트 리스너 추가
    document.querySelectorAll('.chart-item').forEach(item => {
        item.addEventListener('click', function() {
            // 데이터 속성에서 URL을 가져와서 페이지를 이동
            const href = this.getAttribute('data-href');
            window.location.href = href;
        });
    });
});