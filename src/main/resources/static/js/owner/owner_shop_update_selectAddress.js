document.addEventListener("DOMContentLoaded", function () {
    document.getElementById('cityName').addEventListener('change', function () {
        var cityId = this.value;
        var districts = document.getElementById('districtsName').options;

        for (var i = 0; i < districts.length; i++) {
            if (districts[i].getAttribute('data-city-id') == cityId) {
                districts[i].style.display = 'block';
            } else {
                districts[i].style.display = 'none';
            }
        }
        // 초기화
        document.getElementById('districtsName').value = '';
        document.getElementById('dongName').value = '';
        var dongs = document.getElementById('dongName').options;
        for (var i = 0; i < dongs.length; i++) {
            dongs[i].style.display = 'none';
        }
    });

    document.getElementById('districtsName').addEventListener('change', function () {
        var districtsId = this.value;
        var dongs = document.getElementById('dongName').options;
        for (var i = 0; i < dongs.length; i++) {
            if (dongs[i].getAttribute('data-districts-id') == districtsId) {
                dongs[i].style.display = 'block';
            } else {
                dongs[i].style.display = 'none';
            }
        }
        document.getElementById('dongName').value = '';
    });

});