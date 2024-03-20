document.addEventListener('DOMContentLoaded', (event) => {
    const imageInput = document.getElementById('input-file2'); // 'input-file2'로 변경
    const deleteButton = document.querySelector('.delete-file-button2'); // 삭제 버튼 선택

    if (imageInput) {
        imageInput.addEventListener('change', function(event) {
            const files = event.target.files;
            const imageBoxes = document.querySelectorAll('.image-certificate-upload-box'); // '.image-certificate-upload-box'로 변경
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
    // 삭제 버튼에 대한 이벤트 리스너 추가
    if (deleteButton) {
        deleteButton.addEventListener('click', function() {
            // input file 요소의 파일 선택을 초기화합니다.
            imageInput.value = '';

            // 모든 이미지 박스의 배경 이미지를 제거하고, 업로드 텍스트를 다시 표시합니다.
            const imageBoxes = document.querySelectorAll('.image-certificate-upload-box');
            imageBoxes.forEach(box => {
                box.style.backgroundImage = '';
                const textElement = box.querySelector('.upload-text');
                if (textElement) {
                    textElement.style.display = 'block'; // 텍스트 요소를 다시 표시합니다.
                }
            });
        });
    }

});

