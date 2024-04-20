document.addEventListener('DOMContentLoaded', function () {
    $(document).ready(function() {
        $('.alarm-link').click(function() {
            var alarmId = $(this).data('alarm-id');
            $.ajax({
                url: '/alarm/isChecked',
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify({ alarmId: alarmId }),
                success: function(response) {
                    console.log(alarmId+"번 알람을 확인했습니다")
                    // 여기에 추가적인 UI 업데이트 로직 구현
                },
                error: function(xhr, status, error) {
                    alert('알람 상태 업데이트에 실패했습니다: ' + error);
                }
            });
        });
    });
});
