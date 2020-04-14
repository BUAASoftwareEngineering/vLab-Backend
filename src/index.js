const http_server = require('http')
const server = http_server.createServer()
const { time } = require('./date.js')
const handler = require('./http_handler.js')
const port = 3000

server.listen(port, function () {
    console.log('[%s]* service init successfully! Listen at %d', time(), port)
})

server.on('request', function (req, res) {
    res.setHeader("Access-Control-Allow-Credentials","true")
    res.setHeader("Access-Control-Allow-Origin", req.headers.origin)
    console.log('[%s]* receive request from %s', time(), req.headers.origin)
    if (req.method == 'GET') {
        let response = {}
        console.log('[%s]* request url is %s', time(), req.url)
        let data = req.url.split('?')[1]
        let url = req.url.split('?')[0]
        try {
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
        } catch (err) {
            console.log('[%s]* exception happen when handle request { url: "%s", params: "%s" }', time(), req.url, data)
            response.code = 500
            response.message = err.message
            response.data = {}
        }
        console.log('[%s]* response message %s', time(), JSON.stringify(response))
        res.end(JSON.stringify(response))
    } else {
        req.on('data', function(data) {
            console.log('[%s]* receive data from %s', time(), req.headers.origin)
            console.log('[%s]* request url is %s', time(), req.url)
            console.log('[%s]* request data is %s', time(), data.toString())
            let response = {}
            try {
                switch (req.url) {
                    case '/file/update':
                        response = handler.file_update(data.toString())
                        break;
                    case '/file/new':
                        response = handler.file_new(data.toString())
                        break;
                    case '/file/delete':
                        response = handler.file_delete(data.toString())
                        break;
                    case '/file/move':
                        response = handler.file_move(data.toString())
                        break;
                    case '/file/copy':
                        response = handler.file_copy(data.toString())
                        break;
                    case '/file/rename':
                        response = handler.file_rename(data.toString())
                        break;
                    case '/dir/new':
                        response = handler.dir_new(data.toString())
                        break;
                    case '/dir/delete':
                        response = handler.dir_delete(data.toString())
                        break;
                    case '/dir/move':
                        response = handler.dir_move(data.toString())
                        break;
                    case '/dir/copy':
                        response = handler.dir_copy(data.toString())
                        break;
                    case '/dir/rename':
                        response = handler.dir_rename(data.toString())
                        break;
                    default:
                        response = {
                            code: -100,
                            message: 'request path not found!',
                            data : {}
                        }
                        break;
                }
            } catch (err) {
                console.log('[%s]* exception happen when handle request { url: "%s", params: "%s" }', time(), req.url, data)
                response.code = 500
                response.message = err.message
                response.data = {}
            }
            console.log('[%s]* response message %s', time(), JSON.stringify(response))
            res.end(JSON.stringify(response))
        })
    }
    
})