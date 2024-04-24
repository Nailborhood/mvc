document.addEventListener('DOMContentLoaded', function() {
    var link = document.querySelector('#sidebar-link-owner');

    link.addEventListener('click', function(event) {
        event.preventDefault(); // 기본 동작 중단
        var provider = this.getAttribute('data-provider');
        console.log(provider);
        if (provider === 'Nail') {
            window.location.href = this.href;

        } else if (provider === 'google') {
            // 확인 대화 상자를 표시
            if (confirm('Google 계정으로는 사업자 신청을 할 수 없습니다. 내동네일 로그인 페이지로 이동하시겠습니까?')) {
                // window.location.href = '/login';
                logoutAndRedirect();
            }
        } else {
            alert('지원하지 않는 계정 유형입니다.');
        }
    });

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