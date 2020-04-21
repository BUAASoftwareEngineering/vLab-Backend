const express = require('express')
const bodyParser = require('body-parser')
const { time } = require('./date.js')
const handler = require('./http_handler.js')
const port = 3000

const app = express()
app.use(bodyParser.json({limit:'100mb'}));
app.use(bodyParser.urlencoded({ limit:'100mb', extended: true }));


app.listen(port, function () {
    console.log('[%s]* service init successfully! Listen at %d', time(), port)
})

app.all('*', function (req, res) {
    // res.setHeader("Access-Control-Allow-Credentials","true")
    // res.setHeader("Access-Control-Allow-Origin", req.headers.origin)
    console.log('[%s]* receive request from %s', time(), req.headers.origin)
    if (req.method == 'GET') {
        let response = {}
        // console.log('[%s]* request url is %s', time(), req.url)
        let data = req.url.split('?')[1]
        let url = req.url.split('?')[0]
        // try {
            switch (url) {
                case '/file/struct':
                    response = handler.file_struct(data)
                    break;
                case '/file/content':
                    response = handler.file_content(data)
                    break;
                default:
                    response = {
                        code: -100,
                        message: 'request path not found!',
                        data : {}
                    }
                    break;
            }
        // } catch (err) {
        //     console.log('[%s]* exception happen when handle request { url: "%s", params: "%s" }', time(), req.url, data)
        //     response.code = 500
        //     response.message = err.message
        //     response.data = {}
        // }
        console.log('[%s]* response message %s', time(), JSON.stringify(response))
        res.end(JSON.stringify(response))
    } else {
        let data = req.body
        console.log('[%s]* receive data from %s', time(), req.headers.origin)
        console.log('[%s]* request url is %s', time(), req.url)
        console.log('[%s]* request data is %s', time(), data)
        let response = {}
        // try {
            switch (req.url) {
                case '/file/update':
                    response = handler.file_update(data)
                    break;
                case '/file/new':
                    response = handler.file_new(data)
                    break;
                case '/file/delete':
                    response = handler.file_delete(data)
                    break;
                case '/file/move':
                    response = handler.file_move(data)
                    break;
                case '/file/copy':
                    response = handler.file_copy(data)
                    break;
                case '/file/rename':
                    response = handler.file_rename(data)
                    break;
                case '/dir/new':
                    response = handler.dir_new(data)
                    break;
                case '/dir/delete':
                    response = handler.dir_delete(data)
                    break;
                case '/dir/move':
                    response = handler.dir_move(data)
                    break;
                case '/dir/copy':
                    response = handler.dir_copy(data)
                    break;
                case '/dir/rename':
                    response = handler.dir_rename(data)
                    break;
                default:
                    response = {
                        code: -100,
                        message: 'request path not found!',
                        data : {}
                    }
                    break;
            }
        // } catch (err) {
        //     console.log('[%s]* exception happen when handle request { url: "%s", params: "%s" }', time(), req.url, data)
        //     response.code = 500
        //     response.message = err.message
        //     response.data = {}
        // }
        console.log('[%s]* response message %s', time(), JSON.stringify(response))
        res.end(JSON.stringify(response))
        
    }
    
})