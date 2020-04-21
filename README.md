# Container-server

## Environment

```
yum -y install nodejs npm
npm install pm2
```

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

## Error code

- -301：路径已存在（文件或文件夹重名）
- -300：其他错误

## 使用规范

- 请使用绝对路径
- 文件路径末尾不要有`/`，文件夹路径末尾务必带`/`
- 路径均使用完整路径，即对于move和copy的new_path，请使用结果路径
    - 例如将`./a.txt`移动到`./A/`目录下，则new_path为`./A/a.txt`

## log

- `2020-04-11`：项目初始化
- `2020-04-12`：项目结构构建，index.js分配处理请求以及response，http_handler.js处理具体请求
- `2020-04-14`：file接口设计完成，错误码定义完成，本地测试通过
- `2020-04-15`：测试完成，添加`setup.sh`作为构建项目脚本
- `2020-04-21`：修复file_update和file_content的bug

## TODO

- 和服务器对接，制作镜像