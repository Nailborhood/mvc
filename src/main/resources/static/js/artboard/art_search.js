function submitSearch() {
    filterArtByCategory();
}

window.onload = function() {
    var searchInput = document.getElementById('search-input');
    if (searchInput) {
        searchInput.addEventListener('keypress', function(event) {
            if (event.key === 'Enter') {
                event.preventDefault();
                filterArtByCategory();
            }
        });
    } else {
        console.log('search-input element not found');
    }
};