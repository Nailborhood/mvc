document.addEventListener("DOMContentLoaded", function () {
    document.getElementById('cityId').addEventListener('change', function () {
        var cityId = this.value;
        var districts = document.getElementById('districtsId').options;
        document.getElementById('districtsId').value = '';
        for (var i = 0; i < districts.length; i++) {
            if (districts[i].getAttribute('data-city-id') === cityId) {
                districts[i].style.display = 'block';
            } else {
                districts[i].style.display = 'none';
            }
        }
        // 초기화

        document.getElementById('dongId').value = '';
        var dongs = document.getElementById('dongId').options;
        for (var i = 0; i < dongs.length; i++) {
            dongs[i].style.display = 'none';
        }
    });

    document.getElementById('districtsId').addEventListener('change', function () {
        var districtsId = this.value;
        var dongs = document.getElementById('dongId').options;
        for (var i = 0; i < dongs.length; i++) {
            if (dongs[i].getAttribute('data-districts-id') === districtsId) {
                dongs[i].style.display = 'block';
            } else {
                dongs[i].style.display = 'none';
            }
        }
        document.getElementById('dongId').value = '';
    });

});