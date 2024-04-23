function changeReportStatus(reportId, status) {
    var message = "";
    if(status === "accept"){
        message = "신고를 승인하시겠습니까?"
    }else {
        message = "신고를 반려하시겠습니까?"
    }
    if(confirm(message)) {
        $.ajax({
            type: "POST",
            url: `/admin/search/review/report`,
            data: { reportId: reportId, status: status },
            success: function(response) {
                if (response.redirect) {
                    window.location.href = response.redirect;
                }
            },
            error: function(xhr, status, error) {
                console.error("Error: " + status + " - " + error);
            }
        });
    }
}
