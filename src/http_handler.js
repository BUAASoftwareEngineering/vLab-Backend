const fs = require('fs')
const child_process = require('child_process')
const TextDecoder = require('util').TextDecoder
const TextEncoder = require('util').TextEncoder
const spawnSync = require('child_process').spawnSync

function solve_data(data) {
    let obj = ''
    let params = data.split('&')
    for (let i = 0; i < params.length; i = i + 1) {
        let key_value = params[i].split('=')
        if (decodeURIComponent(key_value[0]).trim() != 'file_content') {
            // console.log('www')
            obj += '"' + decodeURIComponent(key_value[0]).trim() + '":"' + decodeURIComponent(key_value[1]).trim() + '"'
        } else {
            console.log('here!')
            obj += '"' + decodeURIComponent(key_value[0]).trim() + '":' + decodeURIComponent(key_value[1]).trim()
            console.log(decodeURIComponent(key_value[1]).trim())
        }
        if (i != params.length - 1) {
            obj += ','
        }
    }
    obj = '{' + obj + '}'
    console.log(obj)
    obj = JSON.parse(obj)
    // console.log(obj)
    return obj
}

function read_dir(path) {
    let obj = []
    fs.readdirSync(path).forEach(function (file) {
        let pathname = path + file
        if ((file[0] == '.') || ((file == '__pycache__') && (fs.statSync(pathname).isDirectory()))) {

        } else {
            let sub_obj = {
                title: file
            }
            if (fs.statSync(pathname).isDirectory()) {
                sub_obj.children = read_dir(pathname + '/')
            }
            obj.push(sub_obj)
        }

    });
    return obj
}

function file_struct(data) {
    let obj = solve_data(data)
    let path = obj.root_path
    if (path[path.length - 1] != '/') {
        path = path + '/'
    }
    let struct = read_dir(path)
    return {
        code: 0,
        message: 'get file struct success!',
        data: struct
    }
}

function file_content(data) {
    let obj = solve_data(data)
    let path = obj.file_path
    console.log(new TextEncoder('utf-8').encode(fs.readFileSync(path, 'utf-8')).buffer)
    return {
        code: 0,
        message: 'file content success!',
        data: {
            path: path,
            content: Buffer.from(new TextEncoder('utf-8').encode(fs.readFileSync(path, 'utf-8')))
        }
    }

}

function file_update(data) {
    let obj = data
    let path = obj.file_path
    console.log(obj)
    // fs.writeFileSync()
    fs.writeFileSync(path, Buffer.from(JSON.parse(obj.file_content)))
    return {
        code: 0,
        message: 'file update success!',
        data: {}
    }

}

function file_new(data) {
    let obj = data
    let path = obj.file_path
    if (fs.existsSync(path)) {
        return {
            code: -301,
            message: '存在同名文件或文件夹，操作失败！',
            data: {}
        }
    }
    let dir_path = ''
    // console.log('aaa')
    for (let i = path.length - 1; i >= 0; i = i - 1) {
        if (path[i] == '/') {
            // console.log(i)
            dir_path = path.substring(0, i)
            break
        }
    }
    // console.log(dir_path)
    if (fs.existsSync(dir_path)) {
        if (!fs.statSync(dir_path).isDirectory()) {
            return {
                code: -301,
                message: '存在同名文件或文件夹，操作失败！',
                data: {}
            }
        }
    } else if (dir_path != '') {
        let ret = spawnSync('mkdir', ['-p', dir_path])
        if (ret.error != undefined) {
            console.log(ret.error)
            return {
                code: 500,
                message: ret.error.message,
                data: {}
            }
        } else if (ret.stderr != undefined) {
            if (ret.stderr.toString().length != 0) {
                return {
                    code: -300,
                    message: ret.stderr.toString(),
                    data: {}
                }
            }
        }
    }
    fs.writeFileSync(path, '', 'utf-8')
    return {
        code: 0,
        message: 'file new success!',
        data: {}
    }
}

function file_delete(data) {
    let obj = data
    let path = obj.file_path

    fs.unlinkSync(path)
    return {
        code: 0,
        message: 'delete file success!',
        data: {}
    }

}

function file_move(data) {
    let obj = data
    let old_path = obj.old_path
    let new_path = obj.new_path
    let force = JSON.parse(obj.force)

    if (old_path != new_path && fs.existsSync(new_path) && !force) {
        return {
            code: -301,
            message: '存在同名文件或文件夹，操作失败！',
            data: {}
        }
    }

    if (fs.existsSync(new_path) && old_path != new_path) {
        let ret = spawnSync('rm', ['-rf', new_path])
        if (ret.error != undefined) {
            return {
                code: 500,
                message: ret.error.message,
                data: {}
            }
        } else if (ret.stderr != undefined) {
            if (ret.stderr.toString().length != 0) {
                return {
                    code: -300,
                    message: ret.stderr.toString(),
                    data: {}
                }
            }
        }
    }
    fs.renameSync(old_path, new_path)
    return {
        code: 0,
        message: 'file move success!',
        data: {}
    }
}

function file_copy(data) {
    let obj = data
    let old_path = obj.old_path
    let new_path = obj.new_path
    let force = JSON.parse(obj.force)

    if (old_path != new_path && fs.existsSync(new_path) && !force) {
        return {
            code: -301,
            message: '存在同名文件或文件夹，操作失败！',
            data: {}
        }
    }

    if (fs.existsSync(new_path) && old_path != new_path) {
        let ret = spawnSync('rm', ['-rf', new_path])
        if (ret.error != undefined) {
            console.log(ret.error)
            return {
                code: 500,
                message: ret.error.message,
                data: {}
            }
        } else if (ret.stderr != undefined) {
            if (ret.stderr.toString().length != 0) {
                return {
                    code: -300,
                    message: ret.stderr.toString(),
                    data: {}
                }
            }
        }
    }
    fs.copyFileSync(old_path, new_path)
    return {
        code: 0,
        message: 'file copy success!',
        data: {}
    }
}

function file_rename(data) {
    let obj = data
    let old_path = obj.old_path
    let new_path = obj.new_path

    if (old_path != new_path && fs.existsSync(new_path)) {
        return {
            code: -301,
            message: '存在同名文件夹或文件，操作失败！',
            data: {}
        }
    }

    fs.renameSync(old_path, new_path)
    return {
        code: 0,
        message: 'file rename success!',
        data: {}
    }
}

function file_download(obj, res) {
    
    console.log(obj.query)
    // console.log(data)
    if ((obj.path == undefined) || (obj.path == '')) {
        try {
            child_process.spawnSync('rm', ['/code.zip'])
        } catch (err) {}
        let ret = child_process.spawnSync('zip', ['-r', '/code.zip', '/code/', '-x', '"*/\\.*"', '-x', '"\\.*"'])
            // console.log(ret)
            // console.log(ret.error)
            // console.log(ret.stdout.toString())
        let name = 'code.zip'
        let path = '/code.zip';
        var size = fs.statSync(path).size;
        var f = fs.createReadStream(path);
        res.writeHead(200, {
          'Content-Type': 'application/force-download',
          'Content-Disposition': 'attachment; filename=' + name,
          'Content-Length': size
        });
        f.pipe(res);
    } else {
        let path = obj.path
        if (fs.existsSync(path)) {
            if (fs.statSync(path).isDirectory()) {
                try {
                    child_process.spawnSync('rm', ['/code.zip'])
                } catch (err) {}
                let ret = child_process.spawnSync('zip', ['-r', '/code.zip', path, '-x', '"*/\\.*"', '-x', '"\\.*"'])
                    // console.log(ret)
                    // console.log(ret.error)
                    // console.log(ret.stdout.toString())
                let name = 'code.zip'
                let zippath = '/code.zip';
                var size = fs.statSync(zippath).size;
                var f = fs.createReadStream(zippath);
                res.writeHead(200, {
                  'Content-Type': 'application/force-download',
                  'Content-Disposition': 'attachment; filename=' + name,
                  'Content-Length': size
                });
                f.pipe(res);
            } else {
                var size = fs.statSync(path).size
                var f = fs.createReadStream(path)
                let name = ''
                for (let i = path.length - 1; i >= 0; i = i - 1) {
                    if (path[i] != '/') {
                        name = path[i] + name
                    } else {
                        break
                    }
                }
                res.writeHead(200, {
                  'Content-Type': 'application/force-download',
                  'Content-Disposition': 'attachment; filename=' + name,
                  'Content-Length': size
                })
                f.pipe(res)
            }
        } else {
            res.end(JSON.stringify({
                code: 500,
                message: "file path not exists!",
                data: {}
            }))
        }
    }
    
    // f.on('end', )
    
}

function dir_new(data) {
    let obj = data
    let dir_path = obj.dir_path
    if (fs.existsSync(dir_path)) {
        return {
            code: -301,
            message: '存在同名文件夹或文件，操作失败！',
            data: {}
        }
    }
    let ret = spawnSync('mkdir', ['-p', dir_path])
    if (ret.error != undefined) {
        console.log(ret.error)
        return {
            code: 500,
            message: ret.error.message,
            data: {}
        }
    } else if (ret.stderr != undefined) {
        if (ret.stderr.toString().length != 0) {
            return {
                code: -300,
                message: ret.stderr.toString(),
                data: {}
            }
        }
    }
    // fs.spawnSync('mkdir', ['-p', dir_path])
    // fs.mkdirSync(dir_path)
    return {
        code: 0,
        message: 'mkdir success!',
        data: {}
    }
}

function dir_delete(data) {
    let obj = data
    let dir_path = obj.dir_path

    let ret = spawnSync('rm', ['-rf', dir_path])
    if (ret.error != undefined) {
        return {
            code: 500,
            message: ret.error.message,
            data: {}
        }
    } else if (ret.stderr != undefined) {
        if (ret.stderr.toString().length != 0) {
            return {
                code: -300,
                message: ret.stderr.toString(),
                data: {}
            }
        }
    }
    return {
        code: 0,
        message: 'remove dir success!',
        data: {}
    }

}

function dir_move(data) {
    let obj = data
    let old_path = obj.old_path
    let new_path = obj.new_path
    let force = JSON.parse(obj.force)

    if (old_path != new_path && fs.existsSync(new_path) && !force) {
        return {
            code: -301,
            message: '存在同名文件或文件夹，操作失败！',
            data: {}
        }
    }

    if (fs.existsSync(new_path)) {
        let ret = spawnSync('rm', ['-rf', new_path])
        if (ret.error != undefined) {
            return {
                code: 500,
                message: ret.error.message,
                data: {}
            }
        } else if (ret.stderr != undefined) {
            if (ret.stderr.toString().length != 0) {
                return {
                    code: -300,
                    message: ret.stderr.toString(),
                    data: {}
                }
            }
        }
    }
    let ret = spawnSync('mv', ['-f', old_path, new_path])
    if (ret.error != undefined) {
        return {
            code: 500,
            message: ret.error.message,
            data: {}
        }
    } else if (ret.stderr != undefined) {
        if (ret.stderr.toString().length != 0) {
            return {
                code: -300,
                message: ret.stderr.toString(),
                data: {}
            }
        }
    }

    return {
        code: 0,
        message: 'dir move success!',
        data: {}
    }
}

function dir_copy(data) {
    let obj = data
    let old_path = obj.old_path
    let new_path = obj.new_path
    let force = JSON.parse(obj.force)

    if (old_path != new_path && fs.existsSync(new_path) && !force) {
        return {
            code: -301,
            message: '存在同名文件或文件夹，操作失败！',
            data: {}
        }
    }

    if (fs.existsSync(new_path)) {
        let ret = spawnSync('rm', ['-rf', new_path])
        if (ret.error != undefined) {
            return {
                code: 500,
                message: ret.error.message,
                data: {}
            }
        } else if (ret.stderr != undefined) {
            if (ret.stderr.toString().length != 0) {
                return {
                    code: -300,
                    message: ret.stderr.toString(),
                    data: {}
                }
            }
        }
    }
    let ret = spawnSync('cp', ['-rf', old_path, new_path])
    if (ret.error != undefined) {
        return {
            code: 500,
            message: ret.error.message,
            data: {}
        }
    } else if (ret.stderr != undefined) {
        if (ret.stderr.toString().length != 0) {
            return {
                code: -300,
                message: ret.stderr.toString(),
                data: {}
            }
        }
    }

    return {
        code: 0,
        message: 'dir copy success!',
        data: {}
    }
}

function dir_rename(data) {
    let obj = data
    let old_path = obj.old_path
    let new_path = obj.new_path

    if (old_path != new_path && fs.existsSync(new_path)) {
        return {
            code: -301,
            message: '存在同名文件夹或文件，操作失败！',
            data: {}
        }
    }

    fs.renameSync(old_path, new_path)
    return {
        code: 0,
        message: 'dir rename success!',
        data: {}
    }
}

module.exports = {
    file_struct,
    file_content,
    file_update,
    file_new,
    file_delete,
    file_move,
    file_copy,
    file_rename,
    file_download,
    dir_new,
    dir_delete,
    dir_copy,
    dir_move,
    dir_rename
}
