# Container-server

## Usage

### Release

```
pm2 start src/index.js
pm2 logs
```

### Debug

```
node src/index.js
```

### log

- `2020-04-11`：项目初始化
- `2020-04-12`：项目结构构建，index.js分配处理请求以及response，http_handler.js处理具体请求

### TODO

- 规范各项请求API，包括错误处理和内容
- 内部处理使用fs完成