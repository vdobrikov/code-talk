'use strict';

let socket = null;

// initiate WebSocket connection
function init(type, data) {
    if (socket) return false;
    socket = new WebSocket(((window.location.protocol === "https:") ? "wss://" : "ws://") + window.location.host + "/ws");

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
    else socket.send(`${ type }:${ JSON.stringify(data) }`);
}


// parse incoming WebSocket message and raise custom event
function receive(msg) {
    let p = msg.indexOf(':'), type = '', data = '';

    if (p > 0 && p < msg.length) {
        type = msg.slice(0, p);

        try { data = JSON.parse(msg.slice(p+1)); }
        catch (e) { console.log(e, msg); }
    }

    if (type && data) {
        // raise custom event
        let event = new CustomEvent(`ws:${type}`, { detail: data || {} });
        window.dispatchEvent(event);
    }
}
