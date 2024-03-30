document.addEventListener("DOMContentLoaded", function() {
    document.getElementById("addMenuButton").addEventListener("click", function() {
        var index = document.querySelectorAll('.menu-item').length; // 새 항목의 인덱스를 계산

        // 새로운 메뉴 항목을 위한 컨테이너
        var menuItemContainer = document.createElement("div");
        menuItemContainer.classList.add("menu-item");
        menuItemContainer.id = "menuItem" + index; // 고유 ID 설정

        // 메뉴 이름을 위한 컨테이너 생성
        var shopMenuNameContainer = document.createElement("div");
        shopMenuNameContainer.classList.add("shopMenu");

        // 메뉴 이름 라벨 생성
        var menuNameLabel = document.createElement("label");
        menuNameLabel.htmlFor = "menuName" + index;
        menuNameLabel.innerText = "메뉴 이름";
        shopMenuNameContainer.appendChild(menuNameLabel); // 라벨을 이름 컨테이너에 추가

        menuItemContainer.appendChild(shopMenuNameContainer); // 이름 컨테이너를 메뉴 항목 컨테이너에 추가

        // 메뉴 이름 입력을 위한 컨테이너 생성
        var shopMenuNameInputContainer = document.createElement("div");
        shopMenuNameInputContainer.classList.add("shopMenu-input");

        // 메뉴 이름 입력 필드 생성
        var menuNameInput = document.createElement("input");
        menuNameInput.type = "text";
        menuNameInput.id = "menuName" + index;
        menuNameInput.placeholder ="menu name"
        menuNameInput.name = "shopMenuDtoList[" + index + "].name"; // 이름 필드의 이름 설정
        shopMenuNameInputContainer.appendChild(menuNameInput); // 입력 필드를 입력 컨테이너에 추가

        menuItemContainer.appendChild(shopMenuNameInputContainer); // 입력 컨테이너를 메뉴 항목 컨테이너에 추가

        // 메뉴 가격을 위한 컨테이너 생성
        var shopMenuPriceContainer = document.createElement("div");
        shopMenuPriceContainer.classList.add("shopMenu");

        // 메뉴 가격 라벨 생성
        var menuPriceLabel = document.createElement("label");
        menuPriceLabel.htmlFor = "menuPrice" + index;
        menuPriceLabel.innerText = "메뉴 가격";
        shopMenuPriceContainer.appendChild(menuPriceLabel); // 라벨을 가격 컨테이너에 추가

        menuItemContainer.appendChild(shopMenuPriceContainer); // 가격 컨테이너를 메뉴 항목 컨테이너에 추가

        // 메뉴 가격 입력을 위한 컨테이너 생성
        var shopMenuPriceInputContainer = document.createElement("div");
        shopMenuPriceInputContainer.classList.add("shopMenu-input");

        // 메뉴 가격 입력 필드 생성
        var menuPriceInput = document.createElement("input");
        menuPriceInput.type = "text";
        menuPriceInput.id = "menuPrice" + index;
        menuPriceInput.placeholder="menu price"
        menuPriceInput.name = "shopMenuDtoList[" + index + "].price"; // 가격 필드의 이름 설정
        shopMenuPriceInputContainer.appendChild(menuPriceInput); // 입력 필드를 입력 컨테이너에 추가

        menuItemContainer.appendChild(shopMenuPriceInputContainer); // 입력 컨테이너를 메뉴 항목

        // 메뉴 컨테이너를 DOM에 추가하는 부분
        document.getElementById("menuInputsContainer").appendChild(menuItemContainer);
    });


    document.getElementById("deleteMenuButton").addEventListener("click", function() {
        var menuItems = document.querySelectorAll('.menu-item'); // 모든 메뉴 항목 선택
        var lastItemIndex = menuItems.length - 1; // 가장 마지막 메뉴 항목의 인덱스

        // 메뉴 항목이 존재하는 경우, 가장 마지막 항목 제거
        if(lastItemIndex > 0) {
            menuItems[lastItemIndex].parentNode.removeChild(menuItems[lastItemIndex]);
        }
    });
});
