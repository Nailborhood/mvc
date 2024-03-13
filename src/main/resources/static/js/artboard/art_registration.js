document.addEventListener('DOMContentLoaded', (event) => {
    const imageInput = document.getElementById('image-input');
    if (imageInput) {
        imageInput.addEventListener('change', function(event) {
            const files = event.target.files;
            const imageBoxes = document.querySelectorAll('.image-upload-box');
            let fileIndex = 0;

            for (const box of imageBoxes) {
                if (!box.style.backgroundImage && fileIndex < files.length) {
                    const reader = new FileReader();
                    reader.onload = (e) => {
                        box.style.backgroundImage = `url(${e.target.result})`;
                        box.style.backgroundSize = 'cover';
                        box.style.backgroundPosition = 'center';

                        // 텍스트 요소를 숨깁니다.
                        const textElement = box.querySelector('.upload-text');
                        if (textElement) {
                            textElement.style.display = 'none';
                        }
                    };
                    reader.readAsDataURL(files[fileIndex++]);
                }
            }
        });
    }
});