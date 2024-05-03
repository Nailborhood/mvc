var socket = new SockJS("/echo");
socket.onopen = function() {
    console.log("Socket connection established.");
};