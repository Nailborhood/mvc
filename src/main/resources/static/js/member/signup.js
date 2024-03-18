const pattern = /^[A-Za-z0-9_\.\-]+@[A-Za-z0-9\-]+\.[A-za-z0-9\-]+$/;

function checkEmailForm() {
    let emailInput = document.getElementById("email-input").value;
    let emailMessage = document.getElementById("email-mismatch_message");
    console.log(emailInput)
    if(emailValidChk(emailInput)) {
        emailMessage.style.display = "none";
    } else {
        emailMessage.style.display = "table-row";
    }
}
function emailValidChk(email) {
    return pattern.test(email) !== false;
}
