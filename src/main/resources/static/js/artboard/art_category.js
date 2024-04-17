// 페이지 로드 시 이전 검색 상태 복원
// document.addEventListener("DOMContentLoaded", () => {
//     restoreSearchState();
//     filterArtByCategory();
// });



function toggleCheckbox(checkboxButton) {
    var checkbox = checkboxButton.querySelector(".category-checkbox");
    checkbox.checked = !checkbox.checked;

    filterArtByCategory();
}

function filterArtByCategory() {
    var checkboxes = document.querySelectorAll(".category-checkbox");
    var keywordInput = document.getElementById("search-input");
    var sortBy = document.getElementById('sortBy').value;
    var selectedCategories = [];
    var keyword = keywordInput.value;

    checkboxes.forEach(function(checkbox) {
        if (checkbox.checked) {
            selectedCategories.push(checkbox.value);
        }
    });

    var queryString = selectedCategories.map(function(id) {
        return "category=" + encodeURIComponent(id);
    }).join('&');

    if (keyword) {
        queryString += "&keyword=" + encodeURIComponent(keyword);
    }
    if(sortBy) {
        queryString += "&sortBy=" + encodeURIComponent(sortBy);
    }

    console.log("Keyword:", keyword);
    console.log("sortBy:", sortBy);
    console.log("Query String before fetch:", queryString);

    const currentQueryString = selectedCategories.map(function(id) {
        return "category=" + encodeURIComponent(id);
    }).join('&') + (keyword ? "&keyword=" + encodeURIComponent(keyword) : "") + (sortBy ? "&sortBy=" + encodeURIComponent(sortBy) : "");

    fetch('/artboard/category/inquiry?' + queryString, {
        method: 'GET',
        headers: {}
    })
        .then(response => response.json())
        .then(data => {
            updateArtList(data.data.artResponseDtoList, data.data.paginationDto, currentQueryString);
        })
        .catch(error => {
            console.error('Error:', error);
        });
}

function updateArtList(artList, paginationDto, currentQueryString) {
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

    updatePagination(paginationDto, currentQueryString);
}

function updatePagination(paginationDto, currentQueryString){
    const paginationContainer = document.querySelector('.pagination-ul');
    paginationContainer.innerHTML = '';

    if (paginationDto.pageNo > 1) {
        paginationContainer.innerHTML += createPageItem('Prev', paginationDto.pageNo - 1, currentQueryString);
    }

    for (let i = 1; i <= paginationDto.totalPages; i++) {
        paginationContainer.innerHTML += createPageItem(i, i, currentQueryString);
    }

    if (paginationDto.pageNo < paginationDto.totalPages) {
        paginationContainer.innerHTML += createPageItem('Next', paginationDto.pageNo + 1, currentQueryString);
    }
}

function createPageItem(text, pageNo, currentQueryString) {
    let href = `/artboard/inquiry?page=${pageNo}`;
    if (currentQueryString) {
        href += '&' + currentQueryString;
    }
    return `<li class="page-item">
                <a class="page-link" href="${href}">${text}</a>
            </li>`;
}