document.addEventListener('DOMContentLoaded', (event) => {
    document.querySelector('.search-icon').addEventListener('click', function() {
        var searchInput = document.getElementById('search-input');
        if(searchInput.value.trim() !== '') {
            document.getElementById('search-form').submit();
        } else {
            alert('검색어를 입력해주세요.');
        }
    });
});