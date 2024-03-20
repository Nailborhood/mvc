document.addEventListener("DOMContentLoaded", function() {
    // 도시 선택 시 이벤트 리스너
    document.getElementById('cityName').addEventListener('change', function() {
        var cityId = this.value;
        console.log('1111');
        filterDistricts(cityId);
    });

    // 구 선택 시 이벤트 리스너
    document.getElementById('districtsName').addEventListener('change', function() {
        var districtsId = this.value;
        filterDongs(districtsId);
    });
});

// 도시에 따른 구 필터링
function filterDistricts(cityId) {
    console.log(cityId);

    var districts = document.getElementById('districtsName').options;
    console.log(districts);
    for (var i = 0; i < districts.length; i++) {
        if (districts[i].getAttribute('data-city-id') == cityId || districts[i].value == "") {
            districts[i].style.display = 'block';
        } else {
            districts[i].style.display = 'none';
        }
    }
    // 구 선택 초기화
    document.getElementById('districtsName').value = '';
    // 동 선택 초기화 및 숨김 처리
    filterDongs('');
}

// 구에 따른 동 필터링
function filterDongs(districtsId) {
    var dongs = document.getElementById('dongName').options;
    console.log(districtsId);
    console.log(dongs);
    for (var i = 0; i < dongs.length; i++) {
        if (dongs[i].getAttribute('data-districts-id') == districtsId ) {
            dongs[i].style.display = 'block';
        } else {
            dongs[i].style.display = 'none';
        }
    }
    // 동 선택 초기화
    document.getElementById('dongName').value = '';
}