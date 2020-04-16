const pty = require('node-pty');
const os = require('os');
const WebSocket = require('ws');
const { time } = require('./date')
const shell = os.platform() === 'win32' ? 'powershell.exe' : 'bash';

const wss = new WebSocket.Server({port: 4001});

// console.log('begin')

wss.on('listening', function () {
    console.log('[%s]* terminal service init success!', time())
})

wss.on('close', function () {
    console.log('wtf')
})

wss.on('connection', (ws) => {
  console.log('[%s]* socket connection success', time());
  const ptyProcess = pty.spawn(shell, [], {
    name: 'xterm-color',
    cols: 150,
    rows: 100,
    cwd: process.env.HOME,
    env: process.env
  });
  
  ws.on('message', (res) => {
    ptyProcess.write(res)
  });
  
  ptyProcess.on('data', function (data) {
    process.stdout.write(data);
    ws.send(data)
  });
});