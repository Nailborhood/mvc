function submitSearch() {
    document.getElementById('search-form').submit();
}

document.getElementById('search-input').addEventListener('keypress', function(event) {
    if (event.key === 'Enter') {
        event.preventDefault();
        submitSearch();
    }
});