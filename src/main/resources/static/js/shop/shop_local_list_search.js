document.addEventListener("DOMContentLoaded", function () {
    // 검색 - 주소와 키워드 검색


    document.getElementById('cityId').addEventListener('change', function() {
        submitFormWithSearchParams();
    });


    document.getElementById('districtsId').addEventListener('change', function() {
        submitFormWithSearchParams();
    });

    // dongId 변경 시 검색 실행
    document.getElementById('dongId').addEventListener('change', function() {
        submitFormWithSearchParams();
    });

    // orderby 변경 시 검색 실행
    document.getElementById('orderby').addEventListener('change', function() {
        submitFormWithSearchParams();
    });

    function submitFormWithSearchParams() {
        var cityId = document.getElementById('cityId').value;
        var districtsId = document.getElementById('districtsId').value;
        var dongId = document.getElementById('dongId').value;
        var keyword = document.getElementById('keywordInput').value;
        var orderby = document.getElementById('orderby').value;
        var form = document.getElementById('searchForm');

        // action URL에 dongId, keyword 및 orderby 값을 추가
        var actionUrl = '/shop/list';
        var queryParams = [];

        if (keyword) {
            queryParams.push('keyword=' + encodeURIComponent(keyword));
        }
        if (cityId) {
            queryParams.push('cityId=' + encodeURIComponent(cityId));
        }
        if (districtsId) {
            queryParams.push('districtsId=' + encodeURIComponent(districtsId));
        }
        if (dongId) {
            queryParams.push('dongId=' + encodeURIComponent(dongId));
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
    const storedCityId = localStorage.getItem('cityId');
    const storedDistrictsId = localStorage.getItem('districtsId');
    const storedDongId = localStorage.getItem('dongId');
    const storedKeyword = localStorage.getItem("keywordInput");
    const storedOrderby = localStorage.getItem('orderby') || 'createdAt';




    if (storedCityId) {
        document.getElementById('cityId').value = storedCityId;
    }
    if (storedDistrictsId) {
        document.getElementById('districtsId').value = storedDistrictsId;
    }
    if (storedDongId) {
        document.getElementById('dongId').value = storedDongId;
    }

    if(storedKeyword){
        document.getElementById('keywordInput').value = storedKeyword;
    }

    if(storedOrderby){
        document.getElementById('orderby').value = storedOrderby;

    }




    // 사용자가 옵션을 선택할 때 선택한 값을 로컬 스토리지에 저장
    document.getElementById('cityId').addEventListener('change', function() {
        localStorage.setItem('cityId', this.value);

    });

    document.getElementById('districtsId').addEventListener('change', function() {
        localStorage.setItem('districtsId', this.value);

    });

    document.getElementById('dongId').addEventListener('change', function() {
        localStorage.setItem('dongId', this.value);
    });

    document.getElementById('keywordInput').addEventListener('change', function() {
        localStorage.setItem('keywordInput', this.value);
    });

    document.getElementById('orderby').addEventListener('change', function() {
        const selectedOrderby = this.value;
        localStorage.setItem('orderby', this.value);
       // submitFormWithSearchParams();
    });





    // 검색한 값들을 초기화
    // 초기화 버튼을 클릭했을 때 로컬 스토리지 값 초기화
    document.getElementById('clearStorageButton').addEventListener('click', function() {
        localStorage.removeItem('cityId');
        localStorage.removeItem('districtsId');
        localStorage.removeItem('dongId');


        document.getElementById('cityId').selectedIndex = 0;
        document.getElementById('districtsId').selectedIndex = 0;
        document.getElementById('dongId').selectedIndex = 0;

        document.getElementById('keywordInput').value = '';
        /*document.getElementById('orderby').value = '';*/

        var form = document.getElementById('searchForm');
        form.action = '/shop/list';


    });


});
