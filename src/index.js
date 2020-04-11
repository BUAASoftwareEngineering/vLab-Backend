const http_server = require('http')
const server = http_server.createServer()
const { time } = require('./date.js')
const handler = require('./http_handler.js')

server.listen(3000, function () {
    console.log('[%s]* service init successfully!', time())
})

server.on('request', function (req, res) {
    switch (req.url) {
        case '/file/content':
            req.on('data', function (data) {
                let response_message = handler.file_content(data)
                res.end(response_message)
            })
            break;
        
        default:
            res.end()
            break;
    }
})