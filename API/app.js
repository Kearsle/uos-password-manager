const express = require('express')
const userRouter = require('./routers/user')
const passwordRouter = require('./routers/password')
const randomWordRouter = require('./routers/randomWord')

const port = process.env.PORT
require('./db/db')

const app = express()

app.use(express.urlencoded({extended: true}))
app.use(express.json())
app.use(userRouter)
app.use(passwordRouter)
app.use(randomWordRouter)

module.exports = app








