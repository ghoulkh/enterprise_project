'use strict';

var messageArea = document.querySelector('#messageArea');
var stompClient = null;
var username = null;

function connect(event) {
    var socket = new SockJS('http://34.16.132.4:8080/ws');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, onConnected, onError);
    event.preventDefault();
}


function onConnected() {
    console.log("adadaddasdas")
    stompClient.subscribe('/post/117', onMessageReceived);
}


function onError(error) {

}



function onMessageReceived(payload) {
    console.log("asasadsadsads")
    console.log(payload);
    var messageElement = document.createElement('li');
    messageElement.classList.add('event-message');

    var textElement = document.createElement('p');
    var messageText = document.createTextNode(payload.body);
    textElement.appendChild(messageText);

    messageElement.appendChild(textElement);

    messageArea.appendChild(messageElement);
    messageArea.scrollTop = messageArea.scrollHeight;
}


usernameForm.addEventListener('submit', connect, true)
