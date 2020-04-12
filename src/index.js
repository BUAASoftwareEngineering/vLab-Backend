const http_server = require('http')
const server = http_server.createServer()
const { time } = require('./date.js')
const handler = require('./http_handler.js')
const port = 3000

server.listen(port, function () {
    console.log('[%s]* service init successfully! Listen at %d', time(), port)
})

server.on('request', function (req, res) {
    res.setHeader("Access-Control-Allow-Origin", req.headers.origin)
    req.on('data', function(data) {
        console.log('[%s]* receive data from %s', time(), req.ip)
        console.log('[%s]* request path is %s', time(), req.url)
        let response = {}
        switch (req.url) {
            case '/file/file_struct':
                response = handler.file_file_struct(data)
                break;
            case '/file/content':
                response = handler.file_content(data)
                break;
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
            case '/dir/new':
                response = handler.dir_new(data)
                break;
            case '/dir/delete':
                response = handler.dir_delete(data)
                break;
            default:
                response = {
                    code: -100,
                    message: 'request path not found!',
                    data : {}
                }
                break;
        }
        console.log('[%s]* response message %s', time(), JSON.stringify(response))
        res.end(JSON.stringify(response))
    })
    
})