const http = require('http')
var iconv = require("iconv-lite");

const ip = '120.53.27.31'

function test1() {
    let datas = []
    let size = 0
    http.request({
        host: ip,
        path: '/download?projectId=33',
        port: 6000,
        method: 'GET'
    }, (res) => {
        console.log(res.statusCode)
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
        host: ip,
        path: '/upload',
        port: 6000,
        method: 'POST',
        headers: {
            'Content-Type' : 'application/x-www-form-urlencoded'
        }
    }, (res) => {
        console.log(res.statusCode)
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

    req.write('projectId=33')
    req.end()
}

function test3() {
    let datas = []
    let size = 0
    let req = http.request({
        host: ip,
        path: '/delete',
        port: 6000,
        method: 'POST',
        headers: {
            'Content-Type' : 'application/x-www-form-urlencoded'
        }
    }, (res) => {
        console.log(res.statusCode)
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

    req.write('projectId=33')
    req.end()
}

test1()
