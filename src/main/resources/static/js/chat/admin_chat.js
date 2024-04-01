document.addEventListener('DOMContentLoaded', function () {
    var stompClient = null;
    var roomId = document.getElementById('roomId').value;

    function connect() {
        var socket = new SockJS('/stomp/chat');
        stompClient = Stomp.over(socket);
        stompClient.connect({}, function (frame) {
            console.log('Connected: ' + frame);
            stompClient.subscribe('/sub/chatroom/' + roomId, function (messageOutput) {
                showMessageOutput(JSON.parse(messageOutput.body));
            });
        });
    }

    function sendMessage() {

        var messageContent = document.getElementById('message').value.trim();
        // TODO: writer는 서버 측에서 세션 정보를 통해 가져오게 바꾸기
        var writer = document.getElementById('writer').value;
        if (messageContent && stompClient) {
            var chatMessage = {
                roomId: roomId,
                contents: messageContent,
                // TODO: writer는 서버 측에서 세션 정보를 통해 가져오게 바꾸기
                writer: writer,
            };
            stompClient.send("/pub/chatroom/" + roomId, {}, JSON.stringify(chatMessage));

            chatMessage.messageDate = new Date().toISOString(); // 현재 시간 설정

/*            // 화면에 메시지 표시
            showMessageOutput(chatMessage); */

            document.getElementById('message').value = '';
        }
    }

// 기존 채팅 내역 불러오기
    function showInitialMessages() {

        var messageDtoData = document.getElementById('messageDto').getAttribute('data-message-dto');
        var messageDto = JSON.parse(messageDtoData);
        var previousDate = null;

        messageDto.forEach(function (message) {
            var messageDate = new Date(message.messageDate);
            var currentDate = messageDate.toISOString().split('T')[0];
            if(previousDate !== currentDate){
                showDateDivider(currentDate); // 날짜 구분자 표시 함수 호출
                previousDate = currentDate; // 이전 날짜 업데이트
            }
            showMessageOutput(message); // 메세지 출력함수 호출
        });

    }

    // 날짜
    function showDateDivider(dateString) {
        var chatArea = document.querySelector('.chatArea');
        var dateElement = document.createElement('div');
        dateElement.classList.add('dateDivider');
        var formattedDate = new Date(dateString).toLocaleDateString('ko-KR', {
            year: 'numeric', month: 'long', day: 'numeric', weekday: 'long'
        });
        dateElement.innerHTML = formattedDate; // 예: '2024년 3월 28일 목요일'
        chatArea.appendChild(dateElement);
    }

// admin -mychat
    function showMessageOutput(messageOutput) {
        var chatArea = document.querySelector('.chatArea');

        // 최상위 요소로 'userChat' 또는 'myChat' 클래스를 가진 div 생성
        var chatWrapper = document.createElement('div');
        chatWrapper.classList.add(messageOutput.writer === 'ADMIN' ? 'myChat' : 'userChat');

        // 'chatBox'를 포함하는 중간 요소 생성
        var messageElement = document.createElement('div');
        messageElement.classList.add('chatBox');

        // 메시지 내용을 담을 요소 생성
        var contentElement = document.createElement('div');
        contentElement.classList.add('chat-content');
        contentElement.innerHTML = messageOutput.contents;

        // 메시지 시간을 담을 요소 생성
/*        var timeElement = document.createElement('div');
        timeElement.classList.add('chatTime');
        var messageDate = new Date(messageOutput.messageDate);
        // 예: '오후 3:45' 형태로 시간 표시
        timeElement.innerHTML = messageDate.toLocaleTimeString('ko-KR');*/

        // 메시지 시간을 담을 요소 생성 및 조건부로 시간 표시
        var timeElement = document.createElement('div');
        timeElement.classList.add('chatTime');
        // 메시지 날짜가 유효한지 확인
        if (messageOutput.messageDate && !isNaN(new Date(messageOutput.messageDate).getTime())) {
            var messageDate = new Date(messageOutput.messageDate);
            // 예: '오후 3:45' 형태로 시간 표시
            timeElement.innerHTML = messageDate.toLocaleTimeString('ko-KR', { hour: '2-digit', minute: '2-digit' });
        } else {
            // 유효하지 않은 경우, 시간을 표시하지 않음
            timeElement.innerHTML = '';
        }

        // 중간 요소에 메시지 내용과 시간을 추가
        messageElement.appendChild(contentElement);
        messageElement.appendChild(timeElement);

        // 최상위 요소에 중간 요소를 추가
        chatWrapper.appendChild(messageElement);

        // 최상위 요소를 채팅 영역에 추가
        chatArea.appendChild(chatWrapper);
        chatArea.scrollTop = chatArea.scrollHeight; // 스크롤을 가장 아래로
    }


// '전송' 버튼 이벤트 리스너
    document.getElementById('chatRoomForm').addEventListener('submit', function (event) {
        event.preventDefault(); // 폼의 기본 제출을 방지
        sendMessage();
        window.location.reload();
    });

    // 웹소켓 연결
    connect();

    // 페이지 로드 시 초기 메시지 표시
    showInitialMessages();



});