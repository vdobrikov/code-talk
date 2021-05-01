import * as editor from './editor.js';
import * as ws from './wsclient.js';

// register this user
ws.send('connect', { documentId: cfg.documentId, userName: editor.getOption('userName') });


// register all users
window.addEventListener('ws:register', e => {
    editor.setUsers(e.detail.userId, e.detail.user);
});


// code editing events
let save;
window.addEventListener('beforeunload', editCode);
window.addEventListener('cm:edit', editCode);
window.addEventListener('ws:edit', editCode);
window.addEventListener('ws:code', editCode);

function editCode(e) {

    // unloading: save all content
    if (e.type === 'beforeunload' && save) saveAll();

    clearTimeout(save);
    save = null;

    switch (e.type) {

        // user edit, with throttled save
        case 'cm:edit':
            ws.send('edit', e.detail);
            save = setTimeout(saveAll, cfg.saveWait);
            break;

        // incoming edit
        case 'ws:edit':
            editor.edit(e.detail.data);
            break;

        // incoming saved content
        case 'cm:code':
            editor.set(e.detail.data);
            break;

    }

    // send all content
    function saveAll() {
        ws.send('content', editor.get());
    }

}


// option events
window.addEventListener('cm:title', editOption);
window.addEventListener('ws:title', editOption);
window.addEventListener('cm:mode', editOption);
window.addEventListener('ws:mode', editOption);
window.addEventListener('cm:userName', editOption);

function editOption(e) {
    let
        type = e.type,
        from = type.slice(0, 2),
        opt = type.slice(3);

    if (from === 'cm') ws.send(opt, e.detail);
    else editor.setOption(opt, e.detail);

}


// add, remove, or rename user
window.addEventListener('ws:userName', e => {
    editor.addUser(e.detail.userId, e.detail.data);
});