document.addEventListener("DOMContentLoaded", function() {

    var ownerPageButton = document.getElementById("ownerPageButton");


    if (ownerPageButton) {
        ownerPageButton.addEventListener("click", function() {

            // window.location.href = '/login';
            logoutAndRedirect();
        });
    }

    function logoutAndRedirect() {
        fetch('/logout', { method: 'POST' })  // 로그아웃을 처리할 서버의 URL과 메소드를 설정
            .then(response => {
                if (response.ok) {
                    console.log('로그아웃 성공');
                    window.location.href = '/login';  // 로그아웃 후 로그인 페이지로 이동
                } else {
                    throw new Error('로그아웃 실패');
                }
            })
            .catch(error => {
                console.error('로그아웃 처리 중 오류 발생:', error);
            });
    }
});
