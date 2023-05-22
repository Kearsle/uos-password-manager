const express = require('express')
const User = require('../models/User')
const Password = require('../models/Password')
const auth = require('../middleware/auth')
const jwt = require('jsonwebtoken')
const router = express.Router()
const Redis = require('ioredis')
const rateLimit = require('express-rate-limit')

const redis = new Redis()
const loginRateLimit = rateLimit({
	windowMs: 2 * 60 * 1000,
	max: 10,
	message: {error: "Too many login attempts, please try again later."}
})
const registerRateLimit = rateLimit({
	windowMs: 5 * 60 * 1000,
	max: 5,
	message: {error: "Too many register attempts, please try again later."}
})
const maxNumberOfFailedLogins = 5 // Max number of failed login attempts on a user
const timeWindowFailedLogins = 5 * 60 // 5 min window of failed logins

router.get('/', async(req, res) => {
	console.log("Status 200: Ping")
	res.status(200).send({message: "pong"})
})

router.post	('/createUser', registerRateLimit, async(req, res) => {
	// Create a new user
	try {
		const user = new User(req.body)
		await user.save()
		console.log("Status 201: Created a new user!")
		res.status(201).send({success: "User Registered"})
	} catch(errors) {
		console.log("Status 400: Failed to create a new user!")
		res.status(400).send({error: "Failed to create a new user!"})
	}
})

router.post('/user', auth.authenticateToken, async (req, res) => {
	// Get user information

	try {
		const userID = req.userToken.userID
		const user = await User.getUserbyUserID(userID)

		if (!user) {
			return res.status(401).send({error: "Failed to find user!"})
		}

		res.status(200).send(user)
	} catch (errors) {
		res.status(400).send({error: "Failed to find user!"})
	}
})

router.post('/userLogin', loginRateLimit, async (req, res) => {
	// Checks if there is a user with the username then checks the passwords
	try {
		const username = req.body.username
		const password = req.body.password
		// Brute Force Check
		let userAttempts = await redis.get(username)
		if (userAttempts > maxNumberOfFailedLogins) {
			return res.status(429).send({error: "Too many login attempts, please try again later."})
		}
		
		// User authentication
		const user = await User.findByCredentials(username.toLowerCase(), password)
		if (!user) {
			// If there is as failed login for a user, store it in the tempory database with the login attempts which expires 
			await redis.set(username, ++userAttempts, 'EX', timeWindowFailedLogins)
			return res.status(401).send({error: "Login Failed! Check your Credentials"})
		}
		
		// Tokens
		const userID = user._id
		const accessToken = await User.createAccessToken(userID)
		const refreshToken = await User.createRefreshToken(userID)

		await redis.del(username)
		console.log(`Status 200: ${username} successfully logged in!`)
		res.status(200).send({userID, accessToken, refreshToken})
	} catch (error) {
		console.log(`Status 400: Login failed for ${req.body.username}`)
		console.log(error)
		res.status(400).send({error})
	}
})

router.delete('/userLogout', auth.authenticateToken, async (req, res) => {
	try {
		const refreshToken = req.body.refreshToken
		const userID = req.userToken.userID
		if (!refreshToken) {
			return res.status(401).send({error: "No token sent"})
		}
		if (!await User.checkRefreshToken(userID, refreshToken)) {
			return res.status(403).send({error: "Incorrect information"})
		}
		await User.deleteRefreshToken(userID, refreshToken)
		res.status(200).send({success: "User logged out"})
	} catch (errors) {
		res.status(400).send({error})
	}
})

router.delete('/userLogoutAll', auth.authenticateToken, async (req, res) => {
	try {
		const userID = req.userToken.userID
		const deletedRefreshToken = await User.deleteAllRefreshToken(userID)
		if (!deletedRefreshToken) {
			return res.status(403).send({error: "Incorrect information"})
		}
		res.status(200).send({success: "User logged out of all devices"})
	} catch (error) {
		console.log(errors)
		res.status(400).send({error: "Failed to logout"})
	}
})

router.delete('/userDelete', auth.authenticateToken, async (req, res) => {
	try {
		// Delete All Passwords
		const passwordsDeleted = await Password.deleteAllPasswordsByUserID(req.userToken.userID)

		if (!passwordsDeleted) {
			console.log("Status 401: Failed to delete all passwords")
			return res.status(401).send({error: "Failed to delete all passwords"})
		}

		// Delete User
		const userID = req.userToken.userID
		const userDeleted = await User.deleteUser(userID)

		if (!userDeleted) {
			console.log("Status 401: Failed to delete user")
			return res.status(401).send({error: "Failed to delete user"})
		}

		console.log("Status 200: Account Deleted")
		res.status(200).send({success: "Account Deleted"})
	} catch (errors) {
		console.log("Status 400: Failed to delete user")
		res.status(400).send({error: "Failed to delete user"})
	}
})

router.put('/userPasswordReset', auth.authenticateToken, async (req, res) => {
	// Password Reset
	try {
		const userID = req.userToken.userID
		const newPassword = req.body.password
		const oldPassword = req.body.oldPassword

		if (!newPassword || !oldPassword) {
			console.log("Status 400: Failed to reset password")
			return res.status(400).send({err: "Failed to reset password"})
		}
		
		const user = await User.findByCredentialsUserID(userID, oldPassword)
		
		if (!user) {
			return res.status(401).send({error: "Incorrect Password"})
		}

		const editedUser = await User.editPassword(userID, newPassword)

		if (!editedUser) {
			console.log("Status 400: Failed to reset password")
			return res.status(400).send({error: "Failed to reset password"})
		}

		console.log("Status 201: Password reset")
		res.status(201).send({success: "Password reset"})
	} catch (errors) {
		console.log("Status 400: Failed to update password")
		res.status(400).send({error: "Failed to update password"})
	}
})

router.post('/token', async (req, res) => {
	try {
		const refreshToken = req.body.refreshToken
		const userID = req.body.userID
		if (!refreshToken) {
			return res.status(401).send("No token sent")
		}
		if (!await User.checkRefreshToken(userID, refreshToken)) {
			return res.status(403).send("Authorization Failed")
		}
		jwt.verify(refreshToken, process.env.REFRESH_TOKEN_SECRET, async (error, userToken) => {
			if (error) {
				return res.status(403).send("Authorization denied")
			}
			const accessToken = await User.createAccessToken(userToken.user)
			res.status(200).send({accessToken})
		})
	} catch (error) {
		res.status(400).send({error})
	}

})

module.exports = router