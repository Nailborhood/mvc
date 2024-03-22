document.addEventListener("DOMContentLoaded", function () {
    document.getElementById('dongId').addEventListener('change', function() {
        var dongId = this.value;
        var keyword = document.getElementById('keywordInput').value;
        var form = document.getElementById('searchForm');

        form.action = '/shop/list' + '?keyword=' + encodeURIComponent(keyword);
        form.submit();
    });


});
