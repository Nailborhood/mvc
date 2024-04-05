document.addEventListener("DOMContentLoaded", function() {

    var ownerPageButton = document.getElementById("ownerPageButton");


    if (ownerPageButton) {
        ownerPageButton.addEventListener("click", function() {

            window.location.href = '/owner/shop/update';
        });
    }
});
