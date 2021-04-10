// Express.js routing
/* global cfg express */

'use strict';

const
  lib = require('./lib'),
  store = require('./store'),

  router = express.Router();

// new
router.get('/new', async (req, res) => {

  const id = await store.add();

  if (id) {
    res.redirect(`/${id}`);
  }
  else {
    res.status(500).send('unable to create code record');
  }

});


// editor
router.get('/[a-f0-9]{24}', async (req, res, next) => {

  // code ID
  const
    id = req.path.slice(1),
    edit = await store.fetch(id);

  if (edit) {

    // recorded code found
    cfg.hostname = req.headers.host.replace(/:\d+$/, '');
    edit.created = lib.formatDate(edit.created);
    edit.updated = lib.formatDate(edit.updated);

    res.render('editor', { cfg, edit });

  }
  else {

    // code not found
    next();

  }

});

// delete document
router.delete('/[a-f0-9]{24}', async (req, res, next) => {
  const id = req.path.slice(1);
  const item = await store.fetch(id);
  if (item) {
    await store.remove(id);
    res.status(204).send('Success');
  } else {
    next();
  }
});

// home page
router.get('/', async (req, res) => {

  let recent = await store.list();
  recent.forEach(r => {
    r.created = lib.formatDate(r.created);
    r.updated = lib.formatDate(r.updated);
  });

  res.render('index', { cfg, recent });

});

// health
router.get('/health', (req, res) => {
  res.status(200).send('OK');
});

// export
module.exports = router;
