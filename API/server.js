const app = require('./app.js')
const port = process.env.PORT

/*app.listen(port, () => {
	console.log(`Server running on port ${port}`)
}) */



const path = require('path')
const fileSystem = require('fs')
const https = require('https')

const sslServer = https.createServer({
	key: fileSystem.readFileSync(path.join(__dirname, 'cert', 'key.pem')),
	cert: fileSystem.readFileSync(path.join(__dirname, 'cert', 'cert.pem'))
}, app)

sslServer.listen(port, () => {
	console.log(`Server running on port ${port}`)
})
