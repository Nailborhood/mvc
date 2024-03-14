/*async function changeReportStatus(reportId, status) {
    console.log(reportId);
    console.log(status);

    // 사용자에게 상태 변경을 확인
    if (confirm("상태를 변경하시겠습니까?")) {
        try {
            const response = await fetch(`/admin/search/reviewReport/change?reportId=${reportId}&status=${status}`, {
                method: 'POST', // HTTP 메소드 지정
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
            });

            if (response.ok) {
                // 성공적으로 상태가 변경되면 페이지를 새로고침하여 변경된 상태를 반영
                location.reload();
            } else {
                // 응답이 성공적이지 않을 경우 오류 메시지 표시
                alert("상태 변경에 실패했습니다. 다시 시도해주세요.");
            }
        } catch (error) {
            // 네트워크 오류 또는 요청 중단 같은 예외 처리
            alert("상태 변경 요청 중 오류가 발생했습니다.");
            console.error("Error during fetch:", error);
        }
    }
}*/
/*function changeReportStatus(reportId, status) {
    console.log(reportId);
    console.log(status);

    // 사용자에게 상태 변경을 확인
    if (confirm("상태를 변경하시겠습니까?")) {
        $.ajax({
            url: `/admin/search/reviewReport/change?reportId=${reportId}&status=${status}`,
            type: 'POST',
            contentType: 'application/x-www-form-urlencoded',
            success: function(response) {
                // 성공적으로 상태가 변경되면 페이지를 새로고침하여 변경된 상태를 반영
                location.reload();
            },
            error: function(xhr, status, error) {
                // 응답이 성공적이지 않을 경우 오류 메시지 표시
                alert("상태 변경에 실패했습니다. 다시 시도해주세요.");
                console.error("Error during fetch:", error);
            }
        });
    }
}*/
function changeReportStatus(reportId, status) {
    // if(confirm("신고 처리 하시겠습니까?")){
    //     location.href='/admin/search/reviewReport/change?reportId='+reportId +'&status='+status;
    // }
    if(confirm("신고 처리 하시겠습니까?")) {
        $.ajax({
            type: "POST",
            url: `/admin/search/reviewReport`,
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