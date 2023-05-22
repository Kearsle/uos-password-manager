const express = require('express')
const RandomWord = require('../models/RandomWord')
const auth = require('../middleware/auth')
const router = express.Router()

router.get('/generateThreeRandomWords', auth.authenticateToken, async (req, res) => {
	try {
		const randomWords = await RandomWord.getRandomWords(3)
		console.log(`Status 200: Generated Three Random Word Password.`)
		res.status(200).send({randomWords: randomWords})
	} catch (error) {
		console.log(`Status 400: Failed to Generated Three Random Word Password.`)
		res.status(400).send(error)
	}
})

module.exports = router