function confirmApproval(isApproval) {
    const message = isApproval ? "매장 등록을 승인하시겠습니까?" : "매장 등록을 반려하시겠습니까?";
    if (confirm(message)) {
        // 사용자가 '확인'을 누른 경우
        const actionMessage = isApproval ? "매장 신청이 승인되었습니다." : "매장 신청이 반려되었습니다.";
        const buttonGroup = document.querySelector('.button-group');
        const shopId = buttonGroup.dataset.shopId;
        const endpoint = isApproval ? `/admin/shop/approve/${shopId}` : `/admin/shop/reject/${shopId}`;

        fetch(endpoint, {
            method: isApproval ? 'PUT' : 'DELETE',
            headers: {
                'Content-Type': 'application/json',
                // 필요하면 헤더 추가
            },
        }).then(response => {
            if (response.ok) {
                alert(actionMessage);
                // 추가적인 성공 후 처리 로직
                window.location.href = "/admin/search/shop";
            } else {
                alert("처리 중 오류가 발생했습니다.");
            }
        });
    }
}