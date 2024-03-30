document.addEventListener("DOMContentLoaded", function () {
    // 검색 - 키워드 검색

    // orderby 변경 시 검색 실행
    document.getElementById('orderby').addEventListener('change', function() {
        submitFormWithSearchParams();
    });

    function submitFormWithSearchParams() {
        var keyword = document.getElementById('keywordInput').value;
        var orderby = document.getElementById('orderby').value;
        var form = document.getElementById('searchForm');

        // action URL에 dongId, keyword 및 orderby 값을 추가
        var actionUrl = '/review/inquiry';
        var queryParams = [];

        if (keyword) {
            queryParams.push('keyword=' + encodeURIComponent(keyword));
        }
        if (orderby) {
            queryParams.push('orderby=' + encodeURIComponent(orderby));
        }

        if (queryParams.length > 0) {
            actionUrl += '?' + queryParams.join('&');
        }

        form.action = actionUrl;
        form.submit();
    }

    // 검색 - 주소 와 키워드 동시 검색
    // 검색되어 지고 나서 주소값을 유지 ( 주소 검색 후 키워드 검색 시 주소 값이 남아있게 하기 위함)
    // 페이지 로드 시 로컬 스토리지에 저장된 값으로 드롭다운 설정
    const storedKeyword = localStorage.getItem("keywordInput");
    const storedOrderby = localStorage.getItem('orderby') || 'likeCnt';





    if(storedKeyword){
        document.getElementById('keywordInput').value = storedKeyword;
    }

    if(storedOrderby){
        document.getElementById('orderby').value = storedOrderby;

    }




    // 사용자가 옵션을 선택할 때 선택한 값을 로컬 스토리지에 저장

    document.getElementById('keywordInput').addEventListener('change', function() {
        localStorage.setItem('keywordInput', this.value);
    });

    document.getElementById('orderby').addEventListener('change', function() {
        const selectedOrderby = this.value;
        localStorage.setItem('orderby', this.value);
        // submitFormWithSearchParams();
    });



});