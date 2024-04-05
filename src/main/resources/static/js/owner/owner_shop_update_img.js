document.addEventListener('DOMContentLoaded', (event) => {
    const imageInput = document.getElementById('input-file');
    const imageBoxes = document.querySelectorAll('.image-upload-box');
    const uploadTextElements = document.querySelectorAll('.upload-text');
    const deleteButton = document.querySelector('.delete-file-button'); // 삭제 버튼 선택

    // 새 이미지를 선택했을 때 기존 이미지를 대체하는 함수
    const updateImages = (files) => {
        for (let i = 0; i < files.length; i++) {
            if (i < imageBoxes.length) {
                const reader = new FileReader();
                reader.onload = ((index) => (e) => {
                    imageBoxes[index].style.backgroundImage = `url(${e.target.result})`;
                    imageBoxes[index].style.backgroundSize = 'cover';
                    imageBoxes[index].style.backgroundPosition = 'center';
                    uploadTextElements[index].style.display = 'none';
                })(i);
                reader.readAsDataURL(files[i]);
            }
        }
    };

    // 파일 입력 변경 감지
    imageInput.addEventListener('change', function(event) {
        const files = Array.from(event.target.files);
        updateImages(files);
    });

    // 기존 이미지 로드 로직
    imageBoxes.forEach((box, index) => {
        const imagePath = box.getAttribute('data-image-path');
        if (imagePath) {
            box.style.backgroundImage = `url(${imagePath})`;
            box.style.backgroundSize = 'cover';
            box.style.backgroundPosition = 'center';
            uploadTextElements[index].style.display = 'none';
        }
    });

    // 삭제 버튼에 대한 이벤트 리스너 추가
    if (deleteButton) {
        deleteButton.addEventListener('click', function() {
            // input file 요소의 파일 선택을 초기화합니다.
            imageInput.value = '';

            // 모든 이미지 박스의 배경 이미지를 제거하고, 업로드 텍스트를 다시 표시합니다.
            const imageBoxes = document.querySelectorAll('.image-upload-box');
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
