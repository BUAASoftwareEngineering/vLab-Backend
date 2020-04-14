const fs = require('fs')
const spawnSync = require('child_process').spawnSync

function solve_data(data) {
    let obj = ''
    let params = data.split('&')
    for (let i = 0; i < params.length; i = i + 1) {
        let key_value = params[i].split('=')
        obj += decodeURIComponent(key_value[0]).trim() + ':"' + decodeURIComponent(key_value[1]).trim() + '"'
        if (i != params.length - 1) {
            obj += ','
        }
    }
    obj = '{' + obj + '}'
    obj = eval('(' + obj + ')')
    return obj
}

function read_dir(path) {
    let obj = []
    fs.readdirSync(path).forEach(function (file) {
        var pathname = path + file
        let sub_obj = {
            title: file
        }
        if (fs.statSync(pathname).isDirectory()) {
            sub_obj.children = read_dir(pathname + '/')
        }
        obj.push(sub_obj)
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
    
    return {
        code: 0,
        message: 'file content success!',
        data: {
            path: path,
            content: fs.readFileSync(path, 'utf-8')
        }
    }
        
}

function file_update(data) {
    let obj = solve_data(data)
    let path = obj.file_path
    
    fs.writeFileSync(path, obj.file_content, 'utf-8')
    return {
        code: 0,
        message: 'file update success!',
        data: {}
    }
        
}

function file_new(data) {
    let obj = solve_data(data)
    let path = obj.file_path
    if (fs.existsSync(path)) {
        return {
            code: -301,
            message: '存在同名文件或文件夹，操作失败！',
            data: {}
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
    let obj = solve_data(data)
    let path = obj.file_path
    
    fs.unlinkSync(path)
    return {
        code: 0,
        message: 'delete file success!',
        data: {}
    }
        
}

function file_move(data) {
    let obj = solve_data(data)
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
        let ret = spawnSync('rm',['-rf',new_path])
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
    let obj = solve_data(data)
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
        let ret = spawnSync('rm',['-rf',new_path])
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
    let obj = solve_data(data)
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

function dir_new(data) {
    let obj = solve_data(data)
    let dir_path = obj.dir_path
    if (fs.existsSync(dir_path)) {
        return {
            code: -301,
            message: '存在同名文件夹或文件，操作失败！',
            data: {}
        }
    }
    fs.mkdirSync(dir_path)
    return {
        code: 0,
        message: 'mkdir success!',
        data: {}
    }
}

function dir_delete(data) {
    let obj = solve_data(data)
    let dir_path = obj.dir_path
    
    let ret = spawnSync('rm',['-rf',dir_path])
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
    let obj = solve_data(data)
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
        let ret = spawnSync('rm',['-rf',new_path])
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
    let ret = spawnSync('mv',['-f',old_path,new_path])
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
    let obj = solve_data(data)
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
        let ret = spawnSync('rm',['-rf',new_path])
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
    let ret = spawnSync('cp',['-rf',old_path,new_path])
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
    let obj = solve_data(data)
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
    dir_new,
    dir_delete,
    dir_copy,
    dir_move,
    dir_rename
}