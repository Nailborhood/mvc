const pattern = /^[A-Za-z0-9_\.\-]+@[A-Za-z0-9\-]+\.[A-za-z0-9\-]+$/;

function checkEmailForm() {
    let emailInput = document.getElementById("email-input").value;
    let emailMessage = document.getElementById("email-mismatch_message");
    let emailCheck_tr = document.getElementById("email-duplicate-check");
    console.log(emailInput);
    if (emailValidChk(emailInput)) {
        emailMessage.style.display = "none";
        emailCheck_tr.style.display = "table-row";
    } else {
        emailMessage.style.display = "table-row";
    }
}

function emailValidChk(email) {
    return pattern.test(email) !== false;
}

function checkEmailDuplicated() {
    let emailInput = document.getElementById("email-input").value;
    $.ajax({
        url : '/checkEmail',
        type : 'GET',
        dataType:"text",
        data : {"email" : emailInput},
        success : function (exist) {
            if(exist) {
                $("#email-unavailable-message").attr('style','display:inline')
                $("#email-available-message").attr('style','display:none')
            } else {
                $("#email-unavailable-message").attr('style','display:none')
                $("#email-available-message").attr('style','display:inline')
            }
        },
        error: function(xhr, status, error) {
            console.error("Error: " + status + " - " + error);
        }
    })
}

window.onload = function () {
    var passwordInput = document.querySelector("#password-input");
    var patternMessage_tr = document.querySelector("#password-pattern_message");

    passwordInput.addEventListener("focus", function () {
        patternMessage_tr.style.display = "table-row";
    });

    passwordInput.addEventListener("input", function () {
        var password = passwordInput.value;
        if (checkPasswordForm(password)) {
            patternMessage_tr.style.display = "none";
        } else {
            patternMessage_tr.style.display = "table-row";
        }
    });

    // 닉네임 중복 체크
    var nicknameInput = document.querySelector("#nickname-input");
    nicknameInput.addEventListener("input", debounce(nicknameCheck(nicknameInput), 150)); // 150ms 디바운스 지연

    // 전화번호 숫자만 입력 (작업중)
    var phoneNumberInput = document.getElementById("phonenumber-input");
    phoneNumberInput.addEventListener("keydown", onKeyPressHandler);
};

// debounce 함수 정의
function debounce(func, delay) {
    let timer;
    return function() {
        const context = this;
        const args = arguments;
        clearTimeout(timer);
        timer = setTimeout(function() {
            func.apply(context, args);
        }, delay);
    };
}

function nicknameCheck(input) {
    const nickname = input.value.trim();
    console.log(nickname);
    if (nickname === "") { // 입력값 비어있음
        $("#nickname-unavailable-message").attr('style', 'display:none');
        $("#nickname-available-message").attr('style', 'display:none');
        return;
    }
    $.ajax({
        url: '/checkNickname',
        type: 'GET',
        dataType: "text",
        data: {"nickname": nickname},
        success: function (exist) {
            console.log(exist);
            if(exist === 'true') {
                $("#nickname-available_message").attr('style', 'display:none');
                $("#nickname-unavailable_message").attr('style', 'display:table-row');
            } else {
                $("#nickname-unavailable_message").attr('style', 'display:none');
                $("#nickname-available_message").attr('style', 'display:table-row');
            }
        },
        error: function(xhr, status, error) {
            console.error("Error: " + status + " - " + error);
        }
    });
}

function checkPasswordForm(input) {
    let reg = /^(?=.*[a-zA-Z])(?=.*[0-9]).{8,20}$/;
    return reg.test(input) !== false;
}

function passwordCheckMatch() {
    var passwordInput = document.querySelector("#password-input").value;
    var passwordCheckInput = document.querySelector("#password-check-input").value;
    var message_div = document.querySelector("#password-match_div");
    let message_text = document.querySelector("#password-match_message");

    message_div.style.display = "table-row";
    if (passwordInput != passwordCheckInput) {
        message_text.innerHTML = "<div>비밀번호와 일치하지 않습니다.</div>";
    } else {
        message_div.style.display = "none";
    }
}

// 전화번호 숫자만 입력

function isNumericInput(event) {
    var key = event.keyCode;
    return (key >= 48 && key <= 57) || // 0-9 키
        (key >= 96 && key <= 105) || // numpad 0-9 키
        key === 8 || key === 9 || key === 37 || key === 39 || // 백스페이스, 탭, 왼쪽 화살표, 오른쪽 화살표 키
        (event.ctrlKey && key === 65) || // Ctrl+A
        (event.ctrlKey && key === 67) || // Ctrl+C
        (event.ctrlKey && key === 88) || // Ctrl+X
        (event.ctrlKey && key === 86) || // Ctrl+V
        (event.ctrlKey && key === 90) || // Ctrl+Z
        (event.ctrlKey && key === 89); // Ctrl+Y
}

function onKeyPressHandler(event) {
    if (!isNumericInput(event)) {
        event.preventDefault();
    }
}




