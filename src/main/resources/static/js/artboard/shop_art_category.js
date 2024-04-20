
document.addEventListener("DOMContentLoaded", function() {
    restoreSearchState();
    filterShopArtByCategory();  // 페이지 로드 시 자동으로 검색 실행


    var searchInput = document.getElementById('search-input');
    if (searchInput) {
        searchInput.addEventListener('keypress', function(event) {
            if (event.key === 'Enter') {
                event.preventDefault();
                filterShopArtByCategory();
            }
        });
    } else {
        console.log('search-input element not found');
    }

    var searchIcon = document.querySelector('.search-icon');
    if (searchIcon) {
        searchIcon.addEventListener('click', submitSearch);
    }

    var sortBySelect = document.getElementById('sortBy');
    if (sortBySelect) {
        sortBySelect.addEventListener('change', function() {
            filterShopArtByCategory();
        });
    }

    function submitSearch() {
        filterShopArtByCategory();
    }

    // 검색 상태 복원
    function restoreSearchState() {
        const keyword = localStorage.getItem('keywordInput');
        const sortBy = localStorage.getItem('sortBy');
        const selectedCategories = JSON.parse(localStorage.getItem('selectedCategories') || '[]');

        if (keyword) {
            document.getElementById('keywordInput').value = keyword;
        }
        if (sortBy) {
            document.getElementById('sortBy').value = sortBy;
        }
        const checkboxes = document.querySelectorAll(".category-checkbox");
        checkboxes.forEach(checkbox => {
            checkbox.checked = selectedCategories.includes(checkbox.value);
        });
    }

    function toggleCheckbox(event) {
        var checkbox = event.currentTarget.querySelector(".category-checkbox");
        checkbox.checked = !checkbox.checked;

        filterShopArtByCategory();
    }

    const checkboxButtons = document.querySelectorAll(".checkbox-button");
    checkboxButtons.forEach(button => {
        button.addEventListener('click', toggleCheckbox);
    });

    function filterShopArtByCategory() {
        var checkboxes = document.querySelectorAll(".category-checkbox");
        var keyword = document.getElementById("search-input").value;
        var sortBy = document.getElementById('sortBy').value;
        var selectedCategories = [];
        // var keyword = keywordInput.value;
        const bodyElement = document.querySelector('body');
        const shopId = bodyElement.dataset.shopId;

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

        fetch(`/art/category/${shopId}?` + queryString, {
            method: 'GET',
            headers: {}
        })
            .then(response => response.json())
            .then(data => {
                updateShopArtList(data.data.shopArtBoardLookupResponseDtoList, data.data.paginationDto, currentQueryString);
            })
            .catch(error => {
                console.error('Error:', error);
            });
    }

    function updateShopArtList(artList, paginationDto, currentQueryString) {
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
                <a href="/artboard/inquiry/${art.artRefId}" class="art-link" role="link">
                    <div class="art-img">
                        <img src="${art.artImgPath}" alt="Art Image">
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
        const bodyElement = document.querySelector('body');
        const shopId = bodyElement.dataset.shopId;
        let href = `/art/${shopId}?page=${pageNo}`;
        if (currentQueryString) {
            href += '&' + currentQueryString;
        }
        return `<li class="page-item">
                    <a class="page-link" href="${href}">${text}</a>
                </li>`;
    }
});