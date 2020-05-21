const pty = require('node-pty');
const os = require('os');
const WebSocket = require('ws');
const { time } = require('./date')
const spawnSync = require('child_process').spawnSync
const spawn = require('child_process').spawn
const shell = os.platform() === 'win32' ? 'powershell.exe' : 'bash';


const wss = new WebSocket.Server({port: 4000});
const counter = {}
counter.count = 0
counter.connect = false
var timer = setInterval(function() {
  // if (counter.connect == false) {
  //   spawnSync('/home/terminal/close.sh')
  // }
  if (wss.clients.size == 0) {
    //   spawnSync('sh', ['/home/terminal/close.sh'])
  }
}, 1800000)

wss.on('listening', function () {
    console.log('[%s]* terminal service init success!', time())
})

wss.on('close', function () {
    console.log('wtf')
})

wss.on('connection', (ws) => {
  console.log('[%s]* socket connection success', time());
  counter.connect = true
  const ptyProcess = pty.spawn(shell, [], {
    name: 'xterm-color',
    cols: 500,
    rows: 100,
    cwd: process.env.HOME,
    env: process.env
  });
  spawn('sh', ['/home/terminal/restart.sh'])
  counter.count += 1
    
  ws.on('message', (res) => {
    ptyProcess.write(res)
    // ptyProcess.write(wss.clients.size)
    // ws.send(wss.clients.size)
  });
  
  ptyProcess.on('data', function (data) {
    process.stdout.write(data);
    ws.send(data)
  });

  ws.on('close', function(err) {
    counter.count -= 1
    setTimeout(function() {
        if (wss.clients.size == 0) {
            // console.log('close docker now!!')
            // spawnSync('sh', ['/home/terminal/close.sh'])
          }
    }, 30000)
  })
});

process.on('uncaughtException', function (err) {
  // console.log('close docker now!')
//   spawnSync('sh', ['/home/terminal/close.sh'])
});

process.on('SIGINT', function() {
    // console.log('I am exit!')
    process.exit(0)
})