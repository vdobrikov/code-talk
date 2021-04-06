# Code Talk

Online collaborative code editor based on https://github.com/craigbuckler/codeoperate

A Node.js real-time co-operative code editor. *Code provided for demonstration purposes only!*

Uses [Express.js](https://expressjs.com/) to serve pages, [ws](https://www.npmjs.com/package/ws) for Web Socket communication, [CodeMirror](https://codemirror.net/) for editing, and [MongoDB](https://www.mongodb.com/) with the [native driver](https://www.npmjs.com/package/mongodb) to store data. In development mode, [Docker](https://www.docker.com/) is used to manage systems and [nodemon](https://nodemon.io/) restarts on code changes.

All modern browsers are supported. Older browsers such as Internet Explorer will show a read-only view of previous code.


## Quick start

Ensure Docker is installed then clone the repository in your terminal:

```sh
git clone https://github.com/vdobrikov/code-talk.git
```

Navigate to the `code-talk` directory and start (this can take several minutes on the first launch):

```sh
cd code-talk
docker-compose up
```

Open <http://localhost/> in your browser.

Press `Ctrl|Cmd + C` in your terminal to stop the application.
