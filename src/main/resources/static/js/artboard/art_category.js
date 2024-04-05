function toggleCheckbox(checkboxButton) {
    var checkbox = checkboxButton.querySelector(".category-checkbox");
    checkbox.checked = !checkbox.checked;

    filterArtByCategory();
}

function filterArtByCategory() {
    var checkboxes = document.querySelectorAll(".category-checkbox");
    var selectedCategories = [];

    checkboxes.forEach(function(checkbox) {
        if (checkbox.checked) {
            selectedCategories.push(checkbox.value);
        }
    });

    var queryString = selectedCategories.map(function(id) {
        return "category=" + encodeURIComponent(id);
    }).join('&');

    fetch('/artboard/category/inquiry?' + queryString, {
        method: 'GET',
        headers: {}
    })
        .then(response => response.json())
        .then(data => {
            updateArtList(data.data.artResponseDtoList);
        })
        .catch(error => {
            console.error('Error:', error);
        });
}

function updateArtList(artList) {
    var artListContainer = document.getElementById('art-list');
    artListContainer.innerHTML = '';

    if (artList.length > 0) {

        artListContainer.style.display = '';
        artListContainer.style.justifyContent = '';
        artListContainer.style.alignItems = '';
        artListContainer.style.height = '';

        artList.forEach(function(art) {
            var artElement = document.createElement('div');
            artElement.className = 'art-list-block';
            artElement.innerHTML = `
            <a href="/artboard/inquiry/${art.id}" class="art-link" role="link">
                <div class="art-img">
                    <img src="${art.mainImgPath}" alt="Art Image">
                </div>
            </a>
            `;

            artListContainer.appendChild(artElement);
        });
    } else {

        artListContainer.style.display = 'flex';
        artListContainer.style.justifyContent = 'center';
        artListContainer.style.alignItems = 'center';
        artListContainer.style.height = '30vh';

        var emptyMessageElement = document.createElement('div');
        emptyMessageElement.className = 'empty-art-message';
        emptyMessageElement.textContent = '아트판이 없습니다.';
        artListContainer.appendChild(emptyMessageElement);
    }
}