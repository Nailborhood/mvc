// const pattern = /^[A-Za-z0-9_\.\-]+@[A-Za-z0-9\-]+\.[A-za-z0-9\-]+$/;
//
// function checkEmailForm() {
//     let emailInput = document.getElementById("email-input").value;
//     let emailMessage = document.getElementById("email-mismatch_message");
//     console.log(emailInput)
//     if(emailValidChk(emailInput)) {
//         emailMessage.style.display = "none";
//     } else {
//         emailMessage.style.display = "table-row";
//     }
// }
// function emailValidChk(email) {
//     return pattern.test(email) !== false;
// }


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

window.onload = function () {
    var passwordInput = document.querySelector("#password-input");
    var patternMessage_tr = document.querySelector("#password-pattern_message");

    passwordInput.addEventListener("focus", function () {
        patternMessage_tr.style.display = "table-row";
    });

    passwordInput.addEventListener("input", function () {
        console.log(passwordInput.value);
        if (checkPasswordForm(passwordInput.value)) {
            patternMessage_tr.style.display = "none";
        } else {
            patternMessage_tr.style.display = "table-row";
        }
    });
};

function checkPasswordForm(input) {
    let reg = /^(?=.*[a-zA-Z])(?=.*[0-9]).{8,20}$/;
    return reg.test(input) !== false;
}

function passwordCheckMatch() {
    var passwordInput = document.querySelector("#password-input").value;
    var passwordCheckInput = document.querySelector(
        "#password-check-input"
    ).value;
    var message_div = document.querySelector("#password-match_div");
    let message_text = document.querySelector("#password-match_message");
    message_div.style.display = "table-row";
    if (passwordInput != passwordCheckInput) {
        message_text.innerHTML = "<div>비밀번호와 일치하지 않습니다.</div>";
    } else {
        message_div.style.display = "none";
    }
}
