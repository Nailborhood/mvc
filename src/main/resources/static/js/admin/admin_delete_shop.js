function deleteShop(shopId) {
    if(confirm("매장을 삭제 하시겠습니까?")) {
        $.ajax({
            type: "POST",
            url: `/admin/delete/shop`,
            data: { shopId: shopId },
            success: function(response) {
                location.reload();
            },
            error: function(xhr, status, error) {
                console.error("Error: " + status + " - " + error);
            }
        });
    }
}