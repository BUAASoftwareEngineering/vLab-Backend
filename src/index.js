const express = require('express')
const bodyParser = require('body-parser')
const bhpan = require('./bhpan')


const app = express()
app.use(bodyParser.json({limit:'100mb'}));
app.use(bodyParser.urlencoded({ limit:'100mb', extended: true }));
app.use(function (req, res, next) {
    res.setTimeout(60*1000, function () {
        console.log("Request has timed out.");
        return res.status(408).send("请求超时")
    });
    next();
});

var Cookie = ''
var tokenId = ''
var JSESSIONID = ''
const port = 6000

async function init() {
    let ret = await bhpan.login()
    Cookie = ret.Cookie
    tokenId = ret.tokenId
    JSESSIONID = ret.JSESSIONID
    setInterval(async function() {
        let ret = await bhpan.login()
        Cookie = ret.Cookie
        tokenId = ret.tokenId
        JSESSIONID = ret.JSESSIONID
    }, 1800000)
}

init()
app.listen(port, function() {
    console.log('Listen at %d', port)
})

app.all('*', async function(req, res) {
    if ((req.path == '/download') && (req.method == 'GET')) {
        let filename = 'newupload.png'
        let savename = 'newdownload.png'
        let savepath = './newdownload.png'
        console.log('path is %s', req.path)
        console.log('param is')
        console.log(req.query)

        let ret = undefined
        let error = false
        try {
            ret = await bhpan.tryDownload(Cookie, tokenId, filename, savename, savepath)
            if (ret) {
                res.end(JSON.stringify({code:0, message:'ok'}))
            } else {
                throw Error()
            }
        } catch (err) {
            console.log(err)
            error = true
        }
        if (error) {
            ret = await bhpan.login(5)
            Cookie = ret.Cookie
            tokenId = ret.tokenId
            JSESSIONID = ret.JSESSIONID
            ret = await bhpan.tryDownload(Cookie, tokenId, filename, savename, savepath)
            if (ret) {
                res.end(JSON.stringify({code:0, message:'ok'}))
            } else {
                res.statusCode = 500
                res.end(JSON.stringify({code:-1, message:'error'}))
            }
        } 
    } else if ((req.path == '/upload') && (req.method == 'POST')) {
        let filepath = './README.md'
        let uploadname = 'upload.md'
        console.log('path is %s', req.path)
        console.log('param is')
        console.log(JSON.stringify(req.body))
        console.log(req.body.projectId)
        console.log(req.body.filepath)

        let ret = undefined
        let error = false
        try {
            ret = await bhpan.tryUpload(Cookie, tokenId, filepath, uploadname)
            if (ret) {
                res.end(JSON.stringify({code:0, message:'ok'}))
            } else {
                throw Error()
            }
        } catch (err) {
            console.log(err)
            error = true
        }
        if (error) {
            ret = await bhpan.login(5)
            Cookie = ret.Cookie
            tokenId = ret.tokenId
            JSESSIONID = ret.JSESSIONID
            ret = await bhpan.tryUpload(Cookie, tokenId, filepath, uploadname)
            if (ret) {
                res.end(JSON.stringify({code:0, message:'ok'}))
            } else {
                res.statusCode = 500
                res.end(JSON.stringify({code:-1, message:'error'}))
            }
        }  
    }
})




