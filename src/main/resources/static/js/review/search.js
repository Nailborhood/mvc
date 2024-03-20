
function toggleCheckbox(checkboxButton) {
    var checkbox = checkboxButton.querySelector(".category-checkbox");
    checkbox.checked = !checkbox.checked; // 체크박스의 상태를 반전시킵니다.
}

function clickSearchIcon() {
    var searchInput = document.getElementById("search-input");
    searchInput.style.display = "inline-block";
}
