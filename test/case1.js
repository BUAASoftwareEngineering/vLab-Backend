const http = require('http')
var iconv = require("iconv-lite");

function test1() {
    let datas = []
    let size = 0
    http.request({
        host: 'localhost',
        path: '/download?projectId=200',
        port: 6000,
        method: 'GET'
    }, (res) => {
        res.on('data', (chunk) => {
            datas.push(chunk)
            size += chunk.length
        })
        res.on('end', function () {
            var buff = Buffer.concat(datas, size);
            var result = iconv.decode(buff, "utf8");//转码//var result = buff.toString();//不需要转编码,直接tostring  
            console.log(result);
            
        })
        res.on('error', function(err) {
            console.log(err)
            
        })
    }).end()
}

function test2() {
    let datas = []
    let size = 0
    let req = http.request({
        host: 'localhost',
        path: '/upload',
        port: 6000,
        method: 'POST',
        headers: {
            'Content-Type' : 'application/x-www-form-urlencoded'
        }
    }, (res) => {
        res.on('data', (chunk) => {
            datas.push(chunk)
            size += chunk.length
        })
        res.on('end', function () {
            var buff = Buffer.concat(datas, size);
            var result = iconv.decode(buff, "utf8");//转码//var result = buff.toString();//不需要转编码,直接tostring  
            console.log(result);
            
        })
        res.on('error', function(err) {
            console.log(err)
            
        })
    })

    req.write('projectId=200&filepath='+encodeURIComponent('./index.js'))
    req.end()
}

test2()
