$(document).ready(function () {
    $('td i').on('click', function () {
        $('input').toggleClass('active');
        if ($('input').hasClass('active')) {
            $(this).attr('class', "bi bi-eye-slash")
                .prev('input').attr('type', "text");
        } else {
            $(this).attr('class', "bi bi-eye")
                .prev('input').attr('type', 'password');
        }
    });
});

