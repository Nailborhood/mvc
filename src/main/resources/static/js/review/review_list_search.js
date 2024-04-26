
document.addEventListener("DOMContentLoaded", function() {
    restoreSearchState();
    filterReviewByCategory();

    var searchInput = document.getElementById('keywordInput');
    if (searchInput) {
        searchInput.addEventListener('keypress', function(event) {
            if (event.key === 'Enter') {
                event.preventDefault();
                filterReviewByCategory();
            }
        });
    } else {
        console.log('search-input element not found');
    }

    var searchIcon = document.querySelector('.search-button');
    if (searchIcon) {
        searchIcon.addEventListener('click', submitSearch);
    }

    var sortBySelect = document.getElementById('orderby');
    if (sortBySelect) {
        sortBySelect.addEventListener('change', function() {
            filterReviewByCategory();
        });
    }

    function submitSearch() {
        filterReviewByCategory();
    }

    function restoreSearchState() {
        const keyword = localStorage.getItem('keywordInput');
        const orderby = localStorage.getItem('orderby');
        const selectedCategories = JSON.parse(localStorage.getItem('selectedCategories') || '[]');

        if (keyword) {
            document.getElementById('keywordInput').value = keyword;
        }
        if (orderby) {
            document.getElementById('orderby').value = orderby;
        }
        const checkboxes = document.querySelectorAll(".category-checkbox");
        checkboxes.forEach(checkbox => {
            checkbox.checked = selectedCategories.includes(checkbox.value);
        });
    }

    document.getElementById('clearStorageButton').addEventListener('click', function() {
        // localStorage에서 관련 데이터 삭제
        localStorage.removeItem('keywordInput');
        localStorage.removeItem('orderby');
        localStorage.removeItem('selectedCategories');

        // 입력 필드 초기화
        document.getElementById('keywordInput').value = '';

        // 셀렉트 박스 초기화
        document.getElementById('orderby').selectedIndex = 0;

        // 카테고리 체크박스 모두 초기화
        const checkboxes = document.querySelectorAll(".category-checkbox");
        checkboxes.forEach(checkbox => {
            checkbox.checked = false;
        });

        // 필터링 함수 재호출 (선택적)
        filterReviewByCategory();
    });

    function toggleCheckbox(event) {
        var checkbox = event.currentTarget.querySelector(".category-checkbox");
        checkbox.checked = !checkbox.checked;

        filterReviewByCategory();
    }

    const checkboxButtons = document.querySelectorAll(".checkbox-button");
    checkboxButtons.forEach(button => {
        button.addEventListener('click', toggleCheckbox);
    });

    function filterReviewByCategory() {
        var checkboxes = document.querySelectorAll(".category-checkbox");
        var keywordInput = document.getElementById("keywordInput");
        var orderby = document.getElementById('orderby').value;
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
        if(orderby) {
            queryString += "&orderby=" + encodeURIComponent(orderby);
        }

        console.log("Keyword:", keyword);
        console.log("orderby:", orderby);
        console.log("Query String before fetch:", queryString);

        const currentQueryString = selectedCategories.map(function(id) {
            return "category=" + encodeURIComponent(id);
        }).join('&') + (keyword ? "&keyword=" + encodeURIComponent(keyword) : "") + (orderby ? "&orderby=" + encodeURIComponent(orderby) : "");

        fetch('/review/category/inquiry?' + queryString, {
            method: 'GET',
            headers: {}
        })
            .then(response => response.json())
            .then(data => {
                updateReviewList(data.data.reviewResponseDtoList, data.data.paginationDto, currentQueryString);
            })
            .catch(error => {
                console.error('Error:', error);
            });
    }

    function updateReviewList(reviewList, paginationDto, currentQueryString) {
        var reviewListContainer = document.getElementById('review-list');
        reviewListContainer.innerHTML = '';

        if (reviewList.length > 0) {

            reviewListContainer.style.display = '';
            reviewListContainer.style.justifyContent = '';
            reviewListContainer.style.alignItems = '';
            reviewListContainer.style.height = '';

            reviewList.forEach(function(review) {
                var reviewElement = document.createElement('div');
                reviewElement.className = 'review-list-block';
                reviewElement.innerHTML = `
                <a href="/review/inquiry/${review.reviewId}?shopId=${review.shopId}" class="review-link" role="link">
                    <div class="review-img">
                        <img src="${review.mainImgPath}" alt="review Image">
                    </div>
                </a>
                `;

                reviewListContainer.appendChild(reviewElement);
            });
        } else {

            reviewListContainer.style.display = 'flex';
            reviewListContainer.style.justifyContent = 'center';
            reviewListContainer.style.alignItems = 'center';
            reviewListContainer.style.height = '30vh';

            var emptyMessageElement = document.createElement('div');
            emptyMessageElement.className = 'empty-review-message';
            emptyMessageElement.textContent = '해당 리뷰가 없습니다.';
            reviewListContainer.appendChild(emptyMessageElement);
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
        let href = `/review/inquiry?page=${pageNo}`;
        if (currentQueryString) {
            href += '&' + currentQueryString;
        }
        return `<li class="page-item">
                    <a class="page-link" href="${href}">${text}</a>
                </li>`;
    }
});
