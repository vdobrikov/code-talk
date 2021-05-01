'use strict';

const cm = CodeMirror(document.getElementById('editor'), {
        value: document.getElementById('code').textContent,
        mode: cfg.defaultMode,
        tabSize: 2,
        theme: 'darcula',
        styleActiveLine: true,
        matchBrackets: true,
        lineNumbers: true
    });

// user list
const operatorListNode = document.getElementById('userList');

// set theme and name
setOption('theme', localStorage.getItem('theme') || 'default');
setOption('userName', localStorage.getItem('userName') || 'somebody');


// fetch all content
export function get() {
    return cm.getValue();
}


// set all content
export function set(content) {
    cm.setValue(content);
}


// user edit event
cm.on('change', (i, change) => {

    if (change.origin === 'gen' || change.origin === 'setValue') return;

    raiseEvent('edit', {
        text: change.text,
        from: { line: change.from.line, ch: change.from.ch },
        to:   { line: change.to.line, ch: change.to.ch }
    });

});


// programmatic edit
export function edit(change) {
    cm.replaceRange(
        change.text,
        change.from,
        change.to,
        'gen'
    );
}


// change title, mode, theme, or operator
document.body.addEventListener('change', e => {
    let target = e.target, type = target.id, value = target.value.trim();

    if (!type || !value) return;

    // set option
    setOption(type, value);

    // raise event
    raiseEvent(type, value);
});

// set an option
export function setOption(name, value) {

    let node = document.getElementById(name);
    if (node) node.value = value;

    localStorage.setItem(name, value);
    // cm.setOption(name, value);
}


// get an option
export function getOption(name) {
    return localStorage.getItem(name);
}


// define all users
export function setUsers(idSelf, user) {
    user.forEach((u, idx) => addUser(idx, u));
    operatorListNode.children[idSelf].classList.add('self');
}


// add an individual user
export function addUser(id, name) {
    while (operatorListNode.children.length <= id) {
        let uItem = document.createElement('li');
        operatorListNode.appendChild(uItem);
    }
    operatorListNode.children[id].textContent = name;
}


// raise custom event
function raiseEvent(type, detail) {
    let event = new CustomEvent(`cm:${type}`, { detail });
    window.dispatchEvent(event);
}
