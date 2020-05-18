const express = require('express')
const bodyParser = require('body-parser')
const bhpan = require('./bhpan')
const spawn = require('child_process').spawn
const spawnSync = require('child_process').spawnSync
const fs = require('fs')


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
function errorHandler(err, req, res, next) {
    console.error(err.stack);
}
app.use(errorHandler);

var Cookie = ''
var tokenId = ''
var JSESSIONID = ''
const rootpath = '/ProjectFiles/'
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

app.get('/download', async function(req, res) {
    
    console.log('path is %s', req.path)
    console.log('param is')
    console.log(req.query)
    console.log(req.query.projectId)
    let filename = 'project' + req.query.projectId.toString() + '.zip'
    let savename = filename
    let savepath = req.query.projectId.toString()

    console.log('filename is %s', filename)
    console.log('savename is %s', savename)
    console.log('savepath is %s', savepath)
    let ret = undefined
    let error = false
    let q1 = ''
    let q2 = ''
    try {
        ret = await bhpan.tryDownload(Cookie, tokenId, filename, savename, savepath)
        if (ret) {
            error = false
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
            error = false
        } else {
            res.statusCode = 500
            res.end(JSON.stringify({code:-1, message:'error'}))
        }
    } 
    if (error == false) {
        console.log(fs.existsSync(savepath))
        while (!fs.existsSync(savepath)) {

        }
        q1 = spawnSync('unzip', [savepath, '-d', rootpath])
        // console.log('savepath is %s', savepath)
        q2 = spawnSync('rm', ['-rf', savepath])
        res.end(JSON.stringify({code:0, message:'ok'}))
    }
    console.log('q1 is')
    console.log(q1.stdout.toString())
    console.log('wtf')
    console.log(q1.stderr.toString())
    console.log('q2 is')
    console.log(q2.stdout.toString())
    console.log(q2.stderr.toString())
})

app.post('/upload', async function(req, res) {
    
    console.log('path is %s', req.path)
    console.log('param is')
    console.log(req.body)
    
    let dirpath = rootpath + req.body.projectId.toString() + '/'
    let filepath = rootpath + 'project' + req.body.projectId.toString() + '.zip'
    let uploadname = 'project' + req.body.projectId.toString() + '.zip'
    spawnSync('rm', ['-rf', filepath])
    let wy = spawnSync('sh', ['./src/zipfile.sh', rootpath, 'project' + req.body.projectId.toString() + '.zip', req.body.projectId.toString() + '/\*'])
    
    console.log(wy.stdout.toString())
    console.log(wy.stderr.toString())
    let ret = undefined
    let error = false
    try {
        ret = await bhpan.tryUpload(Cookie, tokenId, filepath, uploadname)
        if (ret) {
            error = false
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
            error = false
        } else {
            res.statusCode = 500
            res.end(JSON.stringify({code:-1, message:'error'}))
        }
    } 
    
    spawn('rm', ['-rf', filepath])
    if (error == false) {
        spawn('rm', ['-rf', dirpath])
        res.end(JSON.stringify({code:0, message:'ok'}))
    }
})

app.post('/delete', async function(req, res) {
    
    console.log('path is %s', req.path)
    console.log('param is')
    console.log(req.body)
    let filename = 'project' + req.body.projectId.toString() + '.zip'
    // console.log(req.body.projectId)
    // console.log(req.body.filepath)
    spawn('rm', ['-rf', rootpath + filename])
    spawn('rm', ['-rf', req.body.projectId.toString()])
    let ret = undefined
    let error = false
    try {
        ret = await bhpan.tryDelete(Cookie, tokenId, filename)
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
        ret = await bhpan.tryDelete(Cookie, tokenId, filename)
        if (ret) {
            res.end(JSON.stringify({code:0, message:'ok'}))
        } else {
            res.statusCode = 500
            res.end(JSON.stringify({code:-1, message:'error'}))
        }
    }
})




