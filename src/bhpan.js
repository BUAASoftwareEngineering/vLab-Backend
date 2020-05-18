const https = require('https')
var iconv = require("iconv-lite");
var fs = require('fs');
var FormData = require('form-data')

var Accept = 'text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9'
var User_Agent = 'Mozilla/5.0 AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36'
var Accept_Encoding = 'gzip, deflate, br'
var Accept_Language = 'zh-CN,zh;q=0.9'
var Connection = 'keep-alive'
var Content_Type = 'application/x-www-form-urlencoded'
var Referer = 'https://bhpan.buaa.edu.cn/'

var message = JSON.parse(fs.readFileSync('./message.json', 'utf-8'))
var username = message['username']
var password = message['password']
var rootgns = message['rootgns']

var files = {}


async function tryLogin() {
    //   console.log('begin')
    let Cookie = ''
    let JSESSIONID = ''
    let tokenId = ''
    let success = false
    let location = ''
    let ticket = ''
    let lt = ''
    let execution = ''
    let _eventId = ''

    // 200 == success
    var res = await new Promise((resolve) => {
        let datas = []
        let size = 0
        let req = https.request({
            host: 'sso.buaa.edu.cn',
            method: 'GET',
            path: '/login?service=https%3a%2f%2fbhpan.buaa.edu.cn%2fsso',
            headers: {
                'Accept': Accept,
                'User-Agent': User_Agent,
                'Accept-Encoding': Accept_Encoding,
                'Accept-Language': Accept_Language,
                'Connection': Connection,
                'Referer': Referer
            },
            timeout: 10000
        }, (res) => {
            console.log('send')
            console.log(res.headers)
            console.log(res.statusCode)
            res.on('data', (chunk) => {
                // console.log('data:')
                // console.log(chunk)
                datas.push(chunk)
                size += chunk.length
            })
            res.on('end', function () {
                var buff = Buffer.concat(datas, size);
                var result = iconv.decode(buff, "utf8");//转码//var result = buff.toString();//不需要转编码,直接tostring  
                let index1 = result.indexOf('<input type="hidden" name="lt" value="')
                index1 += '<input type="hidden" name="lt" value="'.length
                lt = ''
                while (result[index1] != '"') {
                    lt += result[index1]
                    index1 += 1
                }
                let index2 = result.indexOf('<input type="hidden" name="execution" value="')
                index2 += '<input type="hidden" name="execution" value="'.length
                execution = ''
                while (result[index2] != '"') {
                    execution += result[index2]
                    index2 += 1
                }
                let index3 = result.indexOf('<input type="hidden" name="_eventId" value="')
                index3 += '<input type="hidden" name="_eventId" value="'.length
                _eventId = ''
                while (result[index3] != '"') {
                    _eventId += result[index3]
                    index3 += 1
                }
                console.log(lt)
                console.log(execution)
                console.log(_eventId)
                if (res.statusCode == 200) {
                    success = true
                }
                resolve(res)
            })
            res.on('error', function(err) {
                console.log(err)
                resolve(res)
            })
            // console.log(res.headers.expires)
            if (res.headers['set-cookie']) {
                for (let i = 0; i < res.headers['set-cookie'].length; i = i + 1) {
                    Cookie += res.headers['set-cookie'][i].split(';')[0] + '; '
                    if (res.headers['set-cookie'][i].indexOf('JSESSIONID') != -1) {
                        JSESSIONID = res.headers['set-cookie'][i].split(';')[0].split('=')[1]
                    }
                }
            }

        })
        req.on('timeout', () => {
            if (req.res) {
                req.res.abort()
            }
            req.abort()
            success = false
            resolve(req)
        })
        req.on('error', function(err) {
            success = false
            console.log(err)
            resolve(res)
        })
        req.end()
    }).catch((err)=>{
        console.log(err)
        success = false
    })

    console.log(Cookie)
    console.log(JSESSIONID)
    if (success) {
        success = false
    } else {
        return success
    }

    content = 'username=' + encodeURIComponent(username)
        + '&password=' + encodeURIComponent(password)
        + '&code=&lt=' + encodeURIComponent(lt)
        + '&execution=' + encodeURIComponent(execution)
        + '&_eventId=' + encodeURIComponent(_eventId)
        + '&submit=%E7%99%BB%E5%BD%95'
    console.log(content)

    // have location and ticket means success
    res = await new Promise((resolve) => {
        let datas = []
        let size = 0
        let req = https.request({
            host: 'sso.buaa.edu.cn',
            method: 'POST',
            path: '/login;jsessionid=' + JSESSIONID + '?service=https%3a%2f%2fbhpan.buaa.edu.cn%2fsso',
            headers: {
                'Accept': Accept,
                'User-Agent': User_Agent,
                'Accept-Encoding': Accept_Encoding,
                'Accept-Language': Accept_Language,
                'Connection': Connection,
                'Content-Type': Content_Type,
                'Cache-Control': 'max-age=0',
                'Referer': 'https://sso.buaa.edu.cn/login?service=https%3a%2f%2fbhpan.buaa.edu.cn%2fsso',
                'Cookie': Cookie,
                'Content-Length': content.length,
                'Sec-Fetch-Site': 'same-origin',
                'Sec-Fetch-Mode': 'navigate',
                'Sec-Fetch-User': '?1',
                'Sec-Fetch-Dest': 'document'
            },
            timeout: 10000
        }, (res) => {
            console.log('post')
            console.log(res.headers)
            console.log(res.statusCode)
            // let location = res.headers.location
            if (res.headers['set-cookie']) {
                for (let i = 0; i < res.headers['set-cookie'].length; i = i + 1) {
                    Cookie += res.headers['set-cookie'][i].split(';')[0] + '; '
                    if (res.headers['set-cookie'][i].indexOf('JSESSIONID') != -1) {
                        JSESSIONID = res.headers['set-cookie'][i].split(';')[0].split('=')[1]
                    }
                }
            }
            Cookie += 'lastVisitedOrigin=https%3A%2F%2Fbhpan.buaa.edu.cn; '
            if (res.headers.location) {
                location = res.headers.location
                ticket = location.split('?')[1].split('=')[1]
                if ((ticket != undefined) && (ticket != '')) {
                    success = true
                }
            }
            
            res.on('data', function (chunk) {
                console.log(chunk)
                datas.push(chunk)
                size += chunk.length
            })
            res.on('end', function () {
                var buff = Buffer.concat(datas, size);
                var result = iconv.decode(buff, "utf8");//转码//var result = buff.toString();//不需要转编码,直接tostring  
                console.log(result);
                resolve(res)
            })
            res.on('error', function(err) {
                console.log(err)
                resolve(res)
            })
        })

        // console.log('here is checking')
        // console.log(req.res)
        req.on('timeout', () => {
            if (req.res) {
                req.res.abort()
            }
            req.abort()
            success = false
            resolve(req)
        })
        req.on('error', function(err) {
            success = false
            console.log(err)
            resolve(res)
        })
        console.log('here')
        req.write(content)
        req.end()
    }).catch((err)=>{
        console.log(err)
        success = false
    })

    console.log(Cookie)
    // let beres = res
    console.log(location)
    if (success) {
        success = false
    } else {
        return success
    }

    // Accept means success
    res = await new Promise((resolve) => {
        let datas = []
        let size = 0
        let req = https.request({
            host: 'bhpan.buaa.edu.cn',
            method: 'GET',
            path: location.substring('https://bhpan.buaa.edu.cn'.length, location.length),
            headers: {
                'Accept': '*/*',
                'User-Agent': User_Agent,
                'Accept-Encoding': Accept_Encoding,
                'Accept-Language': Accept_Language,
                'Cache-Control': 'max-age=0',
                'Cookie': Cookie,
                'Connection': Connection,
                'Referer': 'https://sso.buaa.edu.cn/login?service=https%3a%2f%2fbhpan.buaa.edu.cn%2fsso',
                'Sec-Fetch-Site': 'same-origin',
                'Sec-Fetch-Mode': 'navigate',
                'Sec-Fetch-User': '?1',
                'Sec-Fetch-Dest': 'document'
            },
            timeout: 10000
        }, (res) => {
            console.log('GET')
            console.log(res.headers)
            console.log(res.statusCode)
            // let location = res.headers.location
            if (res.headers['set-cookie']) {
                for (let i = 0; i < res.headers['set-cookie'].length; i = i + 1) {
                    Cookie += res.headers['set-cookie'][i].split(';')[0] + '; '
                    if (res.headers['set-cookie'][i].indexOf('JSESSIONID') != -1) {
                        JSESSIONID = res.headers['set-cookie'][i].split(';')[0].split('=')[1]
                    }
                }
            }
            if (res.headers.vary) {
                if (res.headers.vary == 'Accept') {
                    success = true
                }
            }
            res.on('data', function (chunk) {
                console.log(chunk)
                datas.push(chunk)
                size += chunk.length
            })
            res.on('end', function () {
                var buff = Buffer.concat(datas, size);
                var result = iconv.decode(buff, "utf8");//转码//var result = buff.toString();//不需要转编码,直接tostring  
                console.log(result);
                resolve(res)
            })
            res.on('error', function(err) {
                console.log(err)
                resolve(res)
            })
        })

        req.on('timeout', () => {
            if (req.res) {
                req.res.abort()
            }
            req.abort()
            success = false
            resolve(req)
        })
        req.on('error', function(err) {
            success = false
            console.log(err)
            resolve(res)
        })
        req.end()
    }).catch((err)=>{
        console.log(err)
        success = false
    })

    if (success) {
        success = false
    } else {
        return success
    }

    // got userid and tokenid means success
    res = await new Promise((resolve) => {
        let datas = []
        let size = 0
        let content = JSON.stringify({
            "thirdpartyid": "BHMoudle",
            "params": {
                "ticket": ticket
            },
            "deviceinfo": {
                "ostype": 6
            }
        })
        let req = https.request({
            host: 'bhpan.buaa.edu.cn',
            method: 'POST',
            path: '/api/v1/auth1?method=getbythirdparty',
            headers: {
                'Accept': Accept,
                'User-Agent': User_Agent,
                'Accept-Encoding': Accept_Encoding,
                'Accept-Language': Accept_Language,
                'Cache-Control': 'max-age=0',
                'Cookie': Cookie,
                'Connection': Connection,
                'Referer': Referer,
                'Sec-Fetch-Site': 'same-origin',
                'Sec-Fetch-Mode': 'cors',
                'Sec-Fetch-Dest': 'empty',
                'Content-Type': 'text/plain;charset=UTF-8',
                'Content-Length': content.length
            },
            timeout: 10000
        }, (res) => {
            console.log('POST')
            console.log(res.headers)
            console.log(res.statusCode)
            // let location = res.headers.location
            if (res.headers['set-cookie']) {
                for (let i = 0; i < res.headers['set-cookie'].length; i = i + 1) {
                    Cookie += res.headers['set-cookie'][i].split(';')[0] + '; '
                    if (res.headers['set-cookie'][i].indexOf('JSESSIONID') != -1) {
                        JSESSIONID = res.headers['set-cookie'][i].split(';')[0].split('=')[1]
                    }
                }
            }
            res.on('data', function (chunk) {
                console.log(chunk)
                datas.push(chunk)
                size += chunk.length
            })
            res.on('end', function () {
                var buff = Buffer.concat(datas, size);
                var result = iconv.decode(buff, "utf8");//转码//var result = buff.toString();//不需要转编码,直接tostring  
                console.log(result);
                tokenId = JSON.parse(result)['tokenid']
                console.log('tokenid is')
                console.log(tokenId)
                Cookie += 'tokenid=' + encodeURIComponent(tokenId) + '; '
                if ((JSON.parse(result)['tokenid']) && (JSON.parse(result)['userid'])) {
                    success = true
                }
                resolve(res)
            })
            res.on('error', function(err) {
                console.log(err)
                resolve(res)
            })
        })

        req.on('timeout', () => {
            if (req.res) {
                req.res.abort()
            }
            req.abort()
            success = false
            resolve(req)
        })
        req.on('error', function(err) {
            success = false
            console.log(err)
            resolve(res)
        })
        req.write(content)
        req.end()
    }).catch((err)=>{
        console.log(err)
        success = false
    })

    if (success) {
        success = false
    } else {
        return success
    }

    res = await new Promise((resolve) => {
        let datas = []
        let size = 0
        let content = JSON.stringify({
            by: "name",
            docid: rootgns,
            sort: "asc"
        })
        let req = https.request({
            host: 'bhpan.buaa.edu.cn',
            method: 'POST',
            path: '/api/v1/dir?method=list&tokenid=' + encodeURIComponent(tokenId),
            headers: {
                'Accept': Accept,
                'User-Agent': User_Agent,
                'Accept-Encoding': Accept_Encoding,
                'Accept-Language': Accept_Language,
                'Cookie': Cookie,
                'Connection': Connection,
                'Referer': Referer,
                'Sec-Fetch-Site': 'same-origin',
                'Sec-Fetch-Mode': 'cors',
                'Sec-Fetch-Dest': 'empty',
                'Content-Type': 'text/plain;charset=UTF-8',
                'Content-Length': content.length
            }
        }, (res) => {
            console.log('POST')
            console.log(res.headers)
            console.log(res.statusCode)
            // let location = res.headers.location
            if (res.headers['set-cookie']) {
                for (let i = 0; i < res.headers['set-cookie'].length; i = i + 1) {
                    Cookie += res.headers['set-cookie'][i].split(';')[0] + '; '
                    if (res.headers['set-cookie'][i].indexOf('JSESSIONID') != -1) {
                        JSESSIONID = res.headers['set-cookie'][i].split(';')[0].split('=')[1]
                    }
                }
            }
            if (res.statusCode == 200) {
                success = true
            }
            res.on('data', function (chunk) {
                console.log(chunk)
                datas.push(chunk)
                size += chunk.length
            })
            res.on('end', function () {
                var buff = Buffer.concat(datas, size);
                var result = iconv.decode(buff, "utf8");//转码//var result = buff.toString();//不需要转编码,直接tostring  
                console.log(result);
                result = JSON.parse(result)
                if (result) {
                    // let tmpfiles = {}
                    // for (let i = 0; i < result['dirs'].length; i = i + 1) {
                    //     files[result['dirs'][i]['name']] = result['dirs'][i]
                    // }
                    for (let i = 0; i < result['files'].length; i = i + 1) {
                        files[result['files'][i]['name']] = result['files'][i]["docid"]
                    }
                }
                console.log(files)
                resolve(res)
            })
            res.on('error', function(err) {
                console.log(err)
                resolve(err)
            })
        })

        req.on('timeout', () => {
            if (req.res) {
                req.res.abort()
            }
            req.abort()
            success = false
            resolve(req)
        })
        req.on('error', function(err) {
            success = false
            console.log(err)
            resolve(res)
        })
        req.write(content)
        req.end()

    }).catch((err)=>{
        console.log(err)
        success = false
    })

    if (success) {
        return {
            Cookie : Cookie,
            tokenId : tokenId,
            JSESSIONID : JSESSIONID
        }
    } else {
        return success
    }
}

async function tryDownload(Cookie, tokenId, filename, savename, savepath) {
    let success = false
    let download_link = ''
    if (files[filename] == undefined) {
        res = await new Promise((resolve) => {
            let datas = []
            let size = 0
            let content = JSON.stringify({
                by: "name",
                docid: rootgns,
                sort: "asc"
            })
            let req = https.request({
                host: 'bhpan.buaa.edu.cn',
                method: 'POST',
                path: '/api/v1/dir?method=list&tokenid=' + encodeURIComponent(tokenId),
                headers: {
                    'Accept': Accept,
                    'User-Agent': User_Agent,
                    'Accept-Encoding': Accept_Encoding,
                    'Accept-Language': Accept_Language,
                    'Cookie': Cookie,
                    'Connection': Connection,
                    'Referer': Referer,
                    'Sec-Fetch-Site': 'same-origin',
                    'Sec-Fetch-Mode': 'cors',
                    'Sec-Fetch-Dest': 'empty',
                    'Content-Type': 'text/plain;charset=UTF-8',
                    'Content-Length': content.length
                }
            }, (res) => {
                console.log('POST')
                console.log(res.headers)
                console.log(res.statusCode)
                // let location = res.headers.location
                if (res.headers['set-cookie']) {
                    for (let i = 0; i < res.headers['set-cookie'].length; i = i + 1) {
                        Cookie += res.headers['set-cookie'][i].split(';')[0] + '; '
                        
                    }
                }
                if (res.statusCode == 200) {
                    success = true
                }
                res.on('data', function (chunk) {
                    console.log(chunk)
                    datas.push(chunk)
                    size += chunk.length
                })
                res.on('end', function () {
                    var buff = Buffer.concat(datas, size);
                    var result = iconv.decode(buff, "utf8");//转码//var result = buff.toString();//不需要转编码,直接tostring  
                    console.log(result);
                    result = JSON.parse(result)
                    if (result) {
                        // files = {}
                        // for (let i = 0; i < result['dirs'].length; i = i + 1) {
                        //     files[result['dirs'][i]['name']] = result['dirs'][i]
                        // }
                        for (let i = 0; i < result['files'].length; i = i + 1) {
                            files[result['files'][i]['name']] = result['files'][i]["docid"]
                        }
                    }
                    console.log(files)
                    resolve(res)
                })
                res.on('error', function(err) {
                    console.log(err)
                    resolve(err)
                })
            })
    
            req.on('timeout', () => {
                if (req.res) {
                    req.res.abort()
                }
                req.abort()
                success = false
                resolve(req)
            })
            req.on('error', function(err) {
                success = false
                console.log(err)
                resolve(res)
            })
            req.write(content)
            req.end()
    
        }).catch((err)=>{
            console.log(err)
            success = false
        })

        if (success) {
            success = false
        } else {
            return success
        }
    }
    res = await new Promise((resolve) => {
        let datas = []
        let size = 0
        let content = JSON.stringify({
            authtype: "QUERY_STRING",
            docid: files[filename],
            reqhost: "bhpan.buaa.edu.cn",
            rev: "",
            savename: savename,
            usehttps: true
        })
        let req = https.request({
            host: 'bhpan.buaa.edu.cn',
            method: 'POST',
            path: '/api/v1/file?method=osdownload&tokenid=' + encodeURIComponent(tokenId),
            headers: {
                'Accept': Accept,
                'User-Agent': User_Agent,
                'Accept-Encoding': Accept_Encoding,
                'Accept-Language': Accept_Language,
                'Cookie': Cookie,
                'Connection': Connection,
                'Referer': Referer,
                'Sec-Fetch-Site': 'same-origin',
                'Sec-Fetch-Mode': 'cors',
                'Sec-Fetch-Dest': 'empty',
                'Content-Type': 'text/plain;charset=UTF-8',
                'Content-Length': content.length
            }
        }, (res) => {
            console.log('POST')
            console.log(res.headers)
            console.log(res.statusCode)
            // let location = res.headers.location
            if (res.headers['set-cookie']) {
                for (let i = 0; i < res.headers['set-cookie'].length; i = i + 1) {
                    Cookie += res.headers['set-cookie'][i].split(';')[0] + '; '
                }
            }
            res.on('data', function (chunk) {
                console.log(chunk)
                datas.push(chunk)
                size += chunk.length
            })
            res.on('error', function(err) {
                success = false
                console.log(err)
                resolve(res)
            })
            res.on('end', function () {
                var buff = Buffer.concat(datas, size);
                var result = iconv.decode(buff, "utf8");//转码//var result = buff.toString();//不需要转编码,直接tostring  
                console.log(result);
                result = JSON.parse(result)
                console.log(result)
                if (result['authrequest']) {
                    download_link = result['authrequest'][1]
                    if ((res.statusCode == 200) && (download_link != undefined) && (download_link != '')) {
                        success = true
                    }
                }
                resolve(res)
            })
            
        })
        req.on('error', function(err) {
            success = false
            console.log(err)
            resolve(res)
        })
        req.write(content)
        console.log('content is')
        console.log(content)
        req.end()
    }).catch((err)=>{
        console.log(err)
        success = false
    })


    console.log('link is')
    console.log(download_link)
    console.log('host is ')
    console.log(download_link.substring('https://'.length, download_link.indexOf('/bhpan_bucket')))
    console.log('path is ')
    console.log(download_link.substring(download_link.indexOf('/bhpan_bucket'), download_link.length))

    if (success) {
        success = false
    } else {
        return success
    }

    res = await new Promise((resolve) => {
        let datas = []
        let size = 0
        let req = https.request({
            host: download_link.substring('https://'.length, download_link.indexOf('/bhpan_bucket')).split(':')[0],
            method: 'GET',
            port: download_link.substring('https://'.length, download_link.indexOf('/bhpan_bucket')).split(':')[1],
            path: download_link.substring(download_link.indexOf('/bhpan_bucket'), download_link.length),
            headers: {
                'Accept': Accept,
                'User-Agent': User_Agent,
                'Accept-Encoding': Accept_Encoding,
                'Accept-Language': Accept_Language,
                'Cookie': Cookie,
                'Connection': Connection,
                'Referer': Referer,
                'Sec-Fetch-Site': 'same-site',
                'Sec-Fetch-Mode': 'navigate',
                'Sec-Fetch-User': '?1',
                'Sec-Fetch-Dest': 'iframe'
            }
        }, (res) => {
            console.log('DOWNLOAD')
            console.log(res.headers)
            console.log(res.statusCode)
            // let location = res.headers.location
            if (res.headers['set-cookie']) {
                for (let i = 0; i < res.headers['set-cookie'].length; i = i + 1) {
                    Cookie += res.headers['set-cookie'][i].split(';')[0] + '; '
                }
            }
            if (res.statusCode == 200) {
                success = true
                var file = fs.createWriteStream(savepath)
                res.on('data', function (chunk) {
                    console.log(chunk)
                    file.write(chunk)
                })
                res.on('end', function () {
                    file.end()
                    file.on('finish', function() {
                        resolve(res)
                    })
                })
            } else {
                res.on('data', function (chunk) {
                    console.log(chunk)
                    datas.push(chunk)
                    size += chunk.length
                })
                res.on('end', function () {
                    var buff = Buffer.concat(datas, size);
                    var result = iconv.decode(buff, "utf8");//转码//var result = buff.toString();//不需要转编码,直接tostring  
                    console.log(result);
                    resolve(res)
                })
            }
            res.on('error', function(err) {
                console.log(err)
                resolve(res)
            })
        })
        req.on('error', function(err) {
            success = false
            console.log(err)
            resolve(res)
        })
        req.end()
    }).catch((err)=>{
        console.log(err)
        success = false
    })

    return success
}

async function tryUpload(Cookie, tokenId, filepath, uploadname) {
    let success = false
    let upload_link = ''
    let upload_param = {}
    let upload_all_param = {}
    res = await new Promise((resolve) => {
        let datas = []
        let size = 0
        let content = JSON.stringify({
            client_mtime: Date.now(),
            docid: rootgns,
            length: fs.statSync(filepath).size,
            name: uploadname,
            ondup: 3,
            reqhost: "bhpan.buaa.edu.cn",
            reqmethod: "POST",
            usehttps: true
        })
        let req = https.request({
            host: 'bhpan.buaa.edu.cn',
            method: 'POST',
            path: '/api/v1/file?method=osbeginupload&tokenid=' + tokenId,
            headers: {
                'Accept': '*/*',
                'User-Agent': User_Agent,
                'Accept-Encoding': Accept_Encoding,
                'Accept-Language': Accept_Language,
                'Cookie': Cookie,
                'Connection': Connection,
                'Referer': Referer,
                'Sec-Fetch-Site': 'same-origin',
                'Sec-Fetch-Mode': 'cors',
                'Sec-Fetch-Dest': 'empty',
                'Content-Type': 'text/plain;charset=UTF-8',
                'Content-Length': content.length
            }
        }, (res) => {
            console.log('UPLOAD')
            console.log(res.headers)
            console.log(res.statusCode)
            // let location = res.headers.location
            if (res.headers['set-cookie']) {
                for (let i = 0; i < res.headers['set-cookie'].length; i = i + 1) {
                    Cookie += res.headers['set-cookie'][i].split(';')[0] + '; '
                }
            }
            // var file = fs.createWriteStream('./download.png')
            res.on('data', function (chunk) {
                console.log(chunk)
                datas.push(chunk)
                size += chunk.length
            })
            res.on('end', function () {
                var buff = Buffer.concat(datas, size);
                var result = iconv.decode(buff, "utf8");//转码//var result = buff.toString();//不需要转编码,直接tostring  
                console.log(result);
                result = JSON.parse(result)
                console.log(result)
                if (result['authrequest']) {
                    upload_link = result['authrequest'][1]
                    upload_param = result['authrequest']
                    upload_all_param = result
                    if ((res.statusCode == 200) && (upload_link != undefined) && (upload_link != '')) {
                        success = true
                    }
                }
                
                resolve(res)
            })
            res.on('error', function(err) {
                console.log(err)
                resolve(res)
            })
        })
        req.on('error', function(err) {
            success = false
            console.log(err)
            resolve(res)
        })
        req.write(content)
        req.end()
    }).catch((err)=>{
        console.log(err)
        success = false
    })
    // console.log(Date.now())

    console.log('link is')
    console.log(upload_link)
    console.log('host is ')
    console.log(upload_link.substring('https://'.length, upload_link.indexOf('/bhpan_bucket')))
    console.log('path is ')
    console.log(upload_link.substring(upload_link.indexOf('/bhpan_bucket'), upload_link.length))

    if (success) {
        success = false
    } else {
        return success
    }

    let form = new FormData()
    for (let i = 2; i < upload_param.length; i = i + 1) {
        form.append(upload_param[i].split(': ')[0], upload_param[i].split(': ')[1])
    }
    form.append('file', fs.createReadStream(filepath))
    let header = form.getHeaders()
    header.Cookie = Cookie
    header.Referer = Referer
    header["Sec-Fetch-Dest"] = "empty"
    header["Sec-Fetch-Mode"] = "cors"
    header["Sec-Fetch-Site"] = "same-site"
    header["Connection"] = Connection
    header["Accept-Encoding"] = Accept_Encoding
    header["Accept-Language"] = Accept_Language
    header["Accept"] = '*/*'
    res = await new Promise((resolve) => {
        let datas = []
        let size = 0
        let req = https.request({
            method: 'POST',
            host: upload_link.substring('https://'.length, upload_link.indexOf('/bhpan_bucket')).split(':')[0],
            port: upload_link.substring('https://'.length, upload_link.indexOf('/bhpan_bucket')).split(':')[1],
            path: upload_link.substring(upload_link.indexOf('/bhpan_bucket'), upload_link.length),
            headers: header
        }, (res) => {
            console.log('REALUPLOAD')
            console.log(res.headers)
            console.log(res.statusCode)
            // let location = res.headers.location
            if (res.headers['set-cookie']) {
                for (let i = 0; i < res.headers['set-cookie'].length; i = i + 1) {
                    Cookie += res.headers['set-cookie'][i].split(';')[0] + '; '
                }
            }
            // var file = fs.createWriteStream('./download.png')
            res.on('data', function (chunk) {
                console.log(chunk)
                datas.push(chunk)
                size += chunk.length
            })
            res.on('end', function () {
                var buff = Buffer.concat(datas, size);
                var result = iconv.decode(buff, "utf8");//转码//var result = buff.toString();//不需要转编码,直接tostring  
                console.log(result);
                resolve(res)
            })
            res.on('error', function(err) {
                console.log(err)
                resolve(res)
            })
        })
        req.on('error', function(err) {
            success = false
            console.log(err)
            resolve(res)
        })
        form.pipe(req)
        // req.end()
    }).catch((err)=>{
        console.log(err)
        success = false
    })


    console.log('id is')
    console.log(upload_all_param.docid)
    res = await new Promise((resolve) => {
        let datas = []
        let size = 0
        let content = JSON.stringify({
            "docid": upload_all_param.docid,
            "rev": upload_all_param.rev,
            "csflevel": 0
        })
        let req = https.request({
            host: 'bhpan.buaa.edu.cn',
            method: 'POST',
            path: '/api/v1/file?method=osendupload&tokenid=' + tokenId,
            headers: {
                'Accept': '*/*',
                'User-Agent': User_Agent,
                'Accept-Encoding': Accept_Encoding,
                'Accept-Language': Accept_Language,
                'Cookie': Cookie,
                'Connection': Connection,
                'Referer': Referer,
                'Sec-Fetch-Site': 'same-origin',
                'Sec-Fetch-Mode': 'cors',
                'Sec-Fetch-Dest': 'empty',
                'Content-Type': 'text/plain;charset=UTF-8',
                'Content-Length': content.length
            }
        }, (res) => {
            console.log('UPLOADEND')
            console.log(res.headers)
            console.log(res.statusCode)
            // let location = res.headers.location
            if (res.headers['set-cookie']) {
                for (let i = 0; i < res.headers['set-cookie'].length; i = i + 1) {
                    Cookie += res.headers['set-cookie'][i].split(';')[0] + '; '
                }
            }
            // var file = fs.createWriteStream('./download.png')
            if (res.statusCode == 200) {
                success = true
            }
            res.on('data', function (chunk) {
                console.log(chunk)
                datas.push(chunk)
                size += chunk.length
            })
            res.on('end', function () {
                var buff = Buffer.concat(datas, size);
                var result = iconv.decode(buff, "utf8");//转码//var result = buff.toString();//不需要转编码,直接tostring  
                console.log(result);
                result = JSON.parse(result)
                console.log(result)

                resolve(res)
            })
        })
        req.on('error', function(err) {
            success = false
            console.log(err)
            resolve(res)
        })
        req.write(content)
        req.end()
    }).catch((err)=>{
        console.log(err)
        success = false
    })

    if (success) {
        if ((upload_all_param.docid != undefined) && (upload_all_param.docid != '')) {
            files[uploadname] = upload_all_param.docid
            return success
        }
    }
    return false
}

async function tryDelete(Cookie, tokenId, filename) {
    if (files[filename] == undefined) {
        res = await new Promise((resolve) => {
            let datas = []
            let size = 0
            let content = JSON.stringify({
                by: "name",
                docid: rootgns,
                sort: "asc"
            })
            let req = https.request({
                host: 'bhpan.buaa.edu.cn',
                method: 'POST',
                path: '/api/v1/dir?method=list&tokenid=' + encodeURIComponent(tokenId),
                headers: {
                    'Accept': Accept,
                    'User-Agent': User_Agent,
                    'Accept-Encoding': Accept_Encoding,
                    'Accept-Language': Accept_Language,
                    'Cookie': Cookie,
                    'Connection': Connection,
                    'Referer': Referer,
                    'Sec-Fetch-Site': 'same-origin',
                    'Sec-Fetch-Mode': 'cors',
                    'Sec-Fetch-Dest': 'empty',
                    'Content-Type': 'text/plain;charset=UTF-8',
                    'Content-Length': content.length
                }
            }, (res) => {
                console.log('POST')
                console.log(res.headers)
                console.log(res.statusCode)
                // let location = res.headers.location
                if (res.headers['set-cookie']) {
                    for (let i = 0; i < res.headers['set-cookie'].length; i = i + 1) {
                        Cookie += res.headers['set-cookie'][i].split(';')[0] + '; '
                        if (res.headers['set-cookie'][i].indexOf('JSESSIONID') != -1) {
                            JSESSIONID = res.headers['set-cookie'][i].split(';')[0].split('=')[1]
                        }
                    }
                }
                if (res.statusCode == 200) {
                    success = true
                }
                res.on('data', function (chunk) {
                    console.log(chunk)
                    datas.push(chunk)
                    size += chunk.length
                })
                res.on('end', function () {
                    var buff = Buffer.concat(datas, size);
                    var result = iconv.decode(buff, "utf8");//转码//var result = buff.toString();//不需要转编码,直接tostring  
                    console.log(result);
                    result = JSON.parse(result)
                    if (result) {
                        // files = {}
                        // for (let i = 0; i < result['dirs'].length; i = i + 1) {
                        //     files[result['dirs'][i]['name']] = result['dirs'][i]
                        // }
                        for (let i = 0; i < result['files'].length; i = i + 1) {
                            files[result['files'][i]['name']] = result['files'][i]["docid"]
                        }
                    }
                    console.log(files)
                    resolve(res)
                })
                res.on('error', function(err) {
                    console.log(err)
                    resolve(err)
                })
            })
    
            req.on('timeout', () => {
                if (req.res) {
                    req.res.abort()
                }
                req.abort()
                success = false
                resolve(req)
            })
            req.on('error', function(err) {
                success = false
                console.log(err)
                resolve(res)
            })
            req.write(content)
            req.end()
    
        }).catch((err)=>{
            console.log(err)
            success = false
        })

        if (success) {
            success = false
        } else {
            return success
        }
    }

    res = await new Promise((resolve) => {
        let datas = []
        let size = 0
        let content = JSON.stringify({
            docid: files[filename]
        })
        let req = https.request({
            host: 'bhpan.buaa.edu.cn',
            method: 'POST',
            path: '/api/v1/file?method=delete&tokenid=' + encodeURIComponent(tokenId),
            headers: {
                'Accept': Accept,
                'User-Agent': User_Agent,
                'Accept-Encoding': Accept_Encoding,
                'Accept-Language': Accept_Language,
                'Cookie': Cookie,
                'Connection': Connection,
                'Referer': Referer,
                'Sec-Fetch-Site': 'same-origin',
                'Sec-Fetch-Mode': 'cors',
                'Sec-Fetch-Dest': 'empty',
                'Content-Type': 'text/plain;charset=UTF-8',
                'Content-Length': content.length
            }
        }, (res) => {
            console.log('POST')
            console.log(res.headers)
            console.log(res.statusCode)
            // let location = res.headers.location
            if (res.headers['set-cookie']) {
                for (let i = 0; i < res.headers['set-cookie'].length; i = i + 1) {
                    Cookie += res.headers['set-cookie'][i].split(';')[0] + '; '
                }
            }
            if (res.statusCode == 200) {
                success = true
            }
            res.on('data', function (chunk) {
                console.log(chunk)
                datas.push(chunk)
                size += chunk.length
            })
            res.on('end', function () {
                var buff = Buffer.concat(datas, size);
                var result = iconv.decode(buff, "utf8");//转码//var result = buff.toString();//不需要转编码,直接tostring  
                console.log(result);
                resolve(res)
            })
            res.on('error', function(err) {
                console.log(err)
                resolve(err)
            })
        })

        req.on('timeout', () => {
            if (req.res) {
                req.res.abort()
            }
            req.abort()
            success = false
            resolve(req)
        })
        req.on('error', function(err) {
            success = false
            console.log(err)
            resolve(res)
        })
        req.write(content)
        req.end()

    }).catch((err)=>{
        console.log(err)
        success = false
    })

    if (success) {
        success = false
    } else {
        return success
    }

    res = await new Promise((resolve) => {
        let datas = []
        let size = 0
        let content = JSON.stringify({
            docid: files[filename]
        })
        let req = https.request({
            host: 'bhpan.buaa.edu.cn',
            method: 'POST',
            path: '/api/v1/file?method=attribute&tokenid=' + encodeURIComponent(tokenId),
            headers: {
                'Accept': Accept,
                'User-Agent': User_Agent,
                'Accept-Encoding': Accept_Encoding,
                'Accept-Language': Accept_Language,
                'Cookie': Cookie,
                'Connection': Connection,
                'Referer': Referer,
                'Sec-Fetch-Site': 'same-origin',
                'Sec-Fetch-Mode': 'cors',
                'Sec-Fetch-Dest': 'empty',
                'Content-Type': 'text/plain;charset=UTF-8',
                'Content-Length': content.length
            }
        }, (res) => {
            console.log('POST')
            console.log(res.headers)
            console.log(res.statusCode)
            // let location = res.headers.location
            if (res.headers['set-cookie']) {
                for (let i = 0; i < res.headers['set-cookie'].length; i = i + 1) {
                    Cookie += res.headers['set-cookie'][i].split(';')[0] + '; '
                }
            }
            
            res.on('data', function (chunk) {
                console.log(chunk)
                datas.push(chunk)
                size += chunk.length
            })
            res.on('end', function () {
                var buff = Buffer.concat(datas, size);
                var result = iconv.decode(buff, "utf8");//转码//var result = buff.toString();//不需要转编码,直接tostring  
                console.log(result);
                if ((res.statusCode == 404) && (JSON.parse(result).errcode == 404006)) {
                    files[filename] = undefined
                    success = true
                }
                resolve(res)
            })
            res.on('error', function(err) {
                console.log(err)
                resolve(err)
            })
        })

        req.on('timeout', () => {
            if (req.res) {
                req.res.abort()
            }
            req.abort()
            success = false
            resolve(req)
        })
        req.on('error', function(err) {
            success = false
            console.log(err)
            resolve(res)
        })
        req.write(content)
        req.end()

    }).catch((err)=>{
        console.log(err)
        success = false
    })

    return success
}

async function login(times=0) {
    if (times == 0) {
        times = -1
    }
    while (times != 0) {
        try {
            let ret = await tryLogin().catch((err)=>console.log(err))
            if ((ret.Cookie) && (ret.tokenId) && (ret.JSESSIONID)) {
                return ret
            }
        } catch (err) {
            console.log(err)
        } finally {
            times -= 1
        }
    }
    return undefined
}

async function test() {
    let ret = await tryLogin()
    console.log(ret)
    ret = await tryDownload(ret.Cookie, ret.tokenId, 'vscode_bug_eat_char.png', 'mydownload.png', './mydownload.png')
    console.log(ret)
    // ret = await tryUpload(ret.Cookie, ret.tokenId, './mydownload.png', 'newupload.png')
    // console.log(ret)
}

// test()

module.exports = {
    tryLogin,
    tryDownload,
    tryUpload,
    tryDelete,
    login
}