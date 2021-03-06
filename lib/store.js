// code database storage
/* global cfg */

'use strict';

const
  mongodb = require('mongodb'),
  dbID = mongodb.ObjectID,
  client = new mongodb.MongoClient(
    cfg.dbConn, { useNewUrlParser: true, useUnifiedTopology: true }
  );

// database connection
let db, code;

// connect to database
(async () => {

  try {

    // connect to MongoDB database
    await client.connect();
    db = client.db(cfg.dbName);

    // collections
    code = db.collection('code');

    // add indexes
    await code.createIndexes([
      { key: { created: 1 }},
      { key: { updated: 1 } }
    ]);

  }
  catch (err) {
    console.log('DB error', err);
  }

})();


// create a new code record
async function add() {

  try {

    // insert new record
    let
      now = new Date(),
      rec = await code.insertOne({
        title   : 'untitled',
        mode    : cfg.cmModeDefault,
        code    : '// start coding!',
        created : now,
        updated : now
      });

    return (rec.insertedCount && rec.insertedId ? rec.insertedId : null);

  }
  catch (err) {
    console.log('DB error', err);
  }

  return null;

}


// fetch record
async function fetch(id) {

  try {

    // find record by ID
    return await code.findOne({ _id: new dbID(id) });

  }
  catch (err) {
    console.log('DB error', err);
  }

  return null;

}


// update record
async function update(id, set = {}) {

  try {

    set.updated = new Date();

    let res = await code.updateOne(
      { _id: new dbID(id) },
      { $set: set }
    );

    return (res && res.modifiedCount);

  }
  catch (err) {
    console.log('DB error', err);
  }

  return false;

}


// most recent records
async function list() {

  try {

    return await code
      .find(
        {},
        { projection: { _id: 1, title: 1, mode: 1, created: 1, updated: 1 } }
      )
      .sort({ updated: -1 })
      .limit(cfg.listMax)
      .toArray();

  }
  catch (err) {
    console.log('DB error', err);
  }

  return false;

}

async function remove(id) {
  try {
    const item = await fetch(id);
    if (item) {
      code.deleteOne({_id: new dbID(id)});
    }
    return item;
  } catch(err) {
    console.log('DB error', err);
  }
}


module.exports = {
  add,
  fetch,
  update,
  list,
  remove
};
