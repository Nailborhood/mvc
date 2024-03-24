function favCancel(button) {
    const shopId = button.getAttribute('data-id');
    if (confirm("관심 매장 목록에서 삭제하시겠습니까?")) {
        fetch(`/favorite/${shopId}`, {
            method: 'POST',
            headers: {
                // 토큰이나 필요한 헤더가 있다면 여기에 추가
            }
        }).then(response => response.json())
            .then(data => {
                alert(data.message);
                if (data.status === 'success') {
                    // 성공했다면 페이지를 새로고침하거나 사용자를 다른 페이지로 리다이렉션
                    location.reload();
                    // location.href = '/mypage/shop/favorite/inquiry';
                }
            })
            .catch(error => console.error('Error:', error));
    }
}
