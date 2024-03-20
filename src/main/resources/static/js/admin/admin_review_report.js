function changeReportStatus(reportId, status) {
    if(confirm("신고 처리 하시겠습니까?")) {
        $.ajax({
            type: "POST",
            url: `/admin/search/review/report`,
            data: { reportId: reportId, status: status },
            success: function(response) {
                location.reload();
            },
            error: function(xhr, status, error) {
                console.error("Error: " + status + " - " + error);
            }
        });
    }
}