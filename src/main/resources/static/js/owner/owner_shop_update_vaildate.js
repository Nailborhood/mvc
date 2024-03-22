document.addEventListener('DOMContentLoaded', function() {
    document.getElementById('requestForm').addEventListener('submit', function(e) {    var inputFile = document.getElementById('input-file');
    var inputFile2 = document.getElementById("input-file2");
    var isFormValid = true; // 폼이 유효한지 추적하는 변수

    if (inputFile.files.length !== 3) {
        alert('매장 사진을 등록해 주세요. 3장 다 올려주셔야 합니다');
        isFormValid = false;
    }


    /*document.getElementById('error-message').textContent = '';

    const name = document.getElementById('name').value;
    const opentime =document.getElementById('opentime').value;
    const phone =document.getElementById('phone').value;
    const city =document.getElementById('city').value;
    const districts =document.getElementById('districts').value;
    const dong =document.getElementById('dong').value;
    const address =document.getElementById('address').value;
    const content =document.getElementById('content').value;
    const website =document.getElementById('website').value;
    const menuName0 =document.getElementById('menuName0').value;
    const menuPrice0 =document.getElementById('menuPrice0').value;

    if (!name  ) {
        document.getElementById('error-message').textContent = '매장 이름을 입력해주세요';
        e.preventDefault();
    }

    if (!opentime) {
        document.getElementById('error-message').textContent = '매장 운영 시간을 입력해주세요';
        e.preventDefault();
    }

    if (!phone) {
        document.getElementById('error-message').textContent = '매장 전화번호을 입력해주세요';
        e.preventDefault();
    }

    if (!city) {
        document.getElementById('error-message').textContent = '매장 주소를 입력해주세요';
        e.preventDefault();
    }

    if (!districts) {
        document.getElementById('error-message').textContent = '매장 주소를 입력해주세요';
        e.preventDefault();
    }

    if (!dong) {
        document.getElementById('error-message').textContent = '매장 주소를 입력해주세요';
        e.preventDefault();
    }

    if (!address) {
        document.getElementById('error-message').textContent = '매장 상세 주소를 입력해주세요';
        e.preventDefault();
    }

    if (!content) {
        document.getElementById('error-message').textContent = '매장 정보를 입력해주세요';
        e.preventDefault();
    }

    if (!website) {
        document.getElementById('error-message').textContent = '매장 웹사이트를 입력해주세요';
        e.preventDefault();
    }

    if (!menuName0) {
        document.getElementById('error-message').textContent = '매장 메뉴를 입력해주세요';
        e.preventDefault();
    }

    if (!menuPrice0) {
        document.getElementById('error-message').textContent = '매장 가격을 입력해주세요';
        e.preventDefault();
    }*/

    });
});