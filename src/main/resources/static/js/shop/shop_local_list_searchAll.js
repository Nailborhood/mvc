document.addEventListener("DOMContentLoaded", function () {
    // 도시 선택 시 구 목록 업데이트 및 검색 폼 제출
    document.getElementById('cityId').addEventListener('change', function() {

        updateDistrictsOptions();
        submitFormAndMaintainCity();
    });

    // 구 선택 시 동 목록 업데이트 및 검색 폼 제출
    document.getElementById('districtsId').addEventListener('change', function() {
        updateDongOptions();
        localStorage.setItem('districtsId', this.value); // 구 선택값을 저장
        submitFormWithSearchParams();
    });

    document.getElementById('dongId').addEventListener('change', function() {

        localStorage.setItem('dongId', this.value); // 구 선택값을 저장
        submitFormWithSearchParams();
    });

    // 초기화 버튼 클릭 시 로컬 스토리지 값 초기화 및 폼 초기화
    document.getElementById('clearStorageButton').addEventListener('click', function() {
        localStorage.removeItem('cityId');
        localStorage.removeItem('districtsId');
        localStorage.removeItem('dongId');
        document.getElementById('cityId').selectedIndex = 0;
        document.getElementById('districtsId').style.display = 'none';
        document.getElementById('districtsId').selectedIndex = 0;
        document.getElementById('dongId').style.display = 'none';
        document.getElementById('dongId').selectedIndex = 0;
        document.getElementById('keywordInput').value = '';
        var form = document.getElementById('searchForm');
        form.action = '/shop/list';
    });

    // 검색 폼 제출 함수 (도시 선택 시)
    function submitFormAndMaintainCity() {
        var cityId = document.getElementById('cityId').value;
        localStorage.setItem('cityId', cityId);
        var form = document.getElementById('searchForm');
        form.submit();
    }

    // 검색 폼 제출 함수 (구 선택 시)
    function submitFormWithSearchParams() {
        var form = document.getElementById('searchForm');
        form.submit();
    }

    // 페이지 로드 시 로컬 스토리지 값으로 드롭다운 설정
    var storedCityId = localStorage.getItem('cityId');
    if (storedCityId) {
        document.getElementById('cityId').value = storedCityId;
        updateDistrictsOptions();
        var storedDistrictsId = localStorage.getItem('districtsId');
        if (storedDistrictsId) {
            document.getElementById('districtsId').value = storedDistrictsId;
            updateDongOptions();
        }
    }

    // 도시 선택 시 해당 도시에 따른 구 목록 업데이트
    function updateDistrictsOptions() {
        var cityId = document.getElementById('cityId').value;
        var districtsSelect = document.getElementById('districtsId');
        var districtsOptions = districtsSelect.getElementsByTagName('option');


        for (var i = 1; i < districtsOptions.length; i++) {
            if (districtsOptions[i].getAttribute('data-city-id') === cityId /*|| districtsOptions[i].value === ""*/) {
                districtsOptions[i].style.display = 'block';
            } else if (districtsOptions[i].value === ""){
                districtsOptions[i].style.display = 'none';
            }
        }




        //districtsSelect.style.display = 'block';
        districtsSelect.value = localStorage.getItem('districtsId'); // 저장된 districtsId 선택
    }

    // 구 선택 시 해당 구에 따른 동 목록 업데이트
    function updateDongOptions() {
        var districtsId = document.getElementById('districtsId').value;
        var dongSelect = document.getElementById('dongId');
        var dongOptions = dongSelect.getElementsByTagName('option');
        for (var i = 0; i < dongOptions.length; i++) {
            if (dongOptions[i].getAttribute('data-districts-id') === districtsId || dongOptions[i].value === "") {
                dongOptions[i].style.display = 'block';
            } else {
                dongOptions[i].style.display = 'none';
            }
        }
        //dongSelect.style.display = 'block';
        dongSelect.value = localStorage.getItem('dongId');
    }
});
