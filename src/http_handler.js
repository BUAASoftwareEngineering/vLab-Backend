const fs = require('fs')

function solve_data(data) {
    let obj = ''
    let params = data.split('&')
    for (let i = 0; i < params.length; i = i + 1) {
        let key_value = params[i].split('=')
        obj += key_value[0] + ':"' + key_value[1] + '"'
        if (i != params.length - 1) {
            obj += ','
        }
    }
    obj = '{' + obj + '}'
    obj = eval('(' + obj + ')')
    return obj
}

function file_file_struct(data) {
    let obj = solve_data(data)
}

function file_content(data) {
    let obj = solve_data(data)
}

function file_update(data) {
    let obj = solve_data(data)
}

function file_new(data) {
    let obj = solve_data(data)
}

function file_delete(data) {
    let obj = solve_data(data)
}

function file_move(data) {
    let obj = solve_data(data)
}

function file_copy(data) {
    let obj = solve_data(data)
}

function dir_new(data) {
    let obj = solve_data(data)
}

function dir_delete(data) {
    let obj = solve_data(data)
}

module.exports = {
    file_file_struct,
    file_content,
    file_update,
    file_new,
    file_delete,
    file_move,
    file_copy,
    dir_new,
    dir_delete
}