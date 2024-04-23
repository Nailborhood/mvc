function reviewDelete(button) {
    const reviewId = button.getAttribute('data-id');
    const shopId = button.getAttribute('data-shop-id'); // shopId 값을 얻음
    if (confirm("정말로 삭제하시겠습니까?")) {
        fetch(`/mypage/review/${reviewId}?shopId=${shopId}`, {
            method: 'DELETE',
            headers: {
                // 토큰이나 필요한 헤더가 있다면 여기에 추가
            }
        }).then(response => response.json())
            .then(data => {
                alert(data.message);
                if (data.status === 'success') {
                    // 성공했다면 페이지를 새로고침하거나 사용자를 다른 페이지로 리다이렉션
                    window.history.back();
                }
            })
            .catch(error => console.error('Error:', error));
    }
}
