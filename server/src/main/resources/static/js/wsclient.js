'use strict';

let socket = null;

// initiate WebSocket connection
function init(type, data) {
    if (socket) return false;
    socket = new WebSocket(((window.location.protocol === "https:") ? "wss://" : "ws://") + window.location.host + "/ws?documentId=" + cfg.documentId);

    // client start
    socket.onopen = function () {
        console.log("Socket opened");
        send(type, data);
    }

    // client close
    socket.onclose = function () {
        console.log('Socket closed');
        socket = null;
    }

    // receive message
    socket.onmessage = function (event) {
        console.log('Socket onMessage: ' + event);
        receive(event.data);
    }
}


// send WebSocket message
export function send(type = '', data = '') {
    if (!socket) init(type, data);
    else socket.send(JSON.stringify({type: type, data: data}));
}


// parse incoming WebSocket message and raise custom event
function receive(msg) {
    let message;
    try {
        message = JSON.parse(msg);
    } catch (e) {
        console.log(e, msg);
        return;
    }
    let type = message.type;
    let userId = message.userId;
    let data = message.data;

    if (type && data) {
        // raise custom event
        let event = new CustomEvent(`ws:${type}`, { detail: {
            userId: userId,
            data: data
        } || {} });
        window.dispatchEvent(event);
    }
}
