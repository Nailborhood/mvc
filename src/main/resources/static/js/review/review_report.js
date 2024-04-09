function reviewReport(button) {
    const reviewId = button.getAttribute('data-id');
    const shopId = button.getAttribute('data-shop-id'); // shopId 값을 얻음
    const reportReason = document.getElementById('report-reason').value;
    if (confirm("정말로 신고하시겠습니까?")) {
        fetch(`/review/report/${reviewId}?shopId=${shopId}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ reportReason }),
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
