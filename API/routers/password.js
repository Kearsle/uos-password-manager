const express = require('express')
const Password = require('../models/Password')
const auth = require('../middleware/auth')
const router = express.Router()

router.post('/passwords/create', auth.authenticateToken, async (req, res) => {
	// Creating a new Password

	var passwordData = req.body
	passwordData['userID'] = req.userToken.userID
	
	try {
		const password = new Password(passwordData)
		await password.save()
		console.log("Status 201: Successfully created a new password!")
		res.status(201).send(password)
	} catch (error) {
		console.log("Status 400: Failed to create new password")
		res.status(400).send(error)
	}
})

router.put('/passwords/edit', auth.authenticateToken, async (req, res) => {
	// Editing a Password

	try {
		// Password Validation
		const password = await Password.getPasswordbyPasswordID(req.body._id)

		if (!password) {
			console.log("Status 401: No password with that ID")
			return res.status(401).send("Failed to update password")
		}
		
		if (password.userID != req.userToken.userID) {
			console.log("Status 403: Failed to delete ")
			return res.status(403).send("You cannot edit other users passwords")
		}

		// Edit the password
		const passwordUpdate = req.body
		const editedPassword = await Password.editPassword(passwordUpdate)

		if (!editedPassword) {
			console.log("Status 400: Failed to update password")
			return res.status(401).send({error: "Failed to update password"})
		}

		console.log("Status 201: Updated password")
		res.status(201).send(editedPassword)
	} catch (errors) {
		console.log("Status 400: Failed to update password")
		res.status(400).send({error: "Failed to update password"})
	}
})

router.delete('/passwords/delete', auth.authenticateToken, async (req, res) => {
	// Deleting a Password
	try {
		// Password Validation
		// Get the auth token user ID and see if it matches the stored password user ID
		const password = await Password.getPasswordbyPasswordID(req.body._id)

		if (!password) {
			console.log("Status 401: No password with that ID")
			return res.status(401).send("Failed to delete password")
		}
		
		if (password.userID != req.userToken.userID) {
			console.log("Status 403: Failed to delete ")
			return res.status(403).send("You cannot delete other users passwords")
		}

		// Delete Password
		const passwordID = req.body._id
		const passwordDeleted = await Password.deletePassword(passwordID)

		if (!passwordDeleted) {
			console.log("Status 401: Failed to delete password")
			return res.status(401).send({error: "Failed to delete password"})
		}

		console.log("Status 200: Password Deleted")
		res.status(200).send(passwordDeleted)
	} catch (errors) {
		console.log("Status 400: Failed to delete Password")
		res.status(400).send({error: "Failed to delete password"})
	}
})

router.delete('/passwords/deleteAll', auth.authenticateToken, async (req, res) => {
	try {
		// Delete All Passwords
		const passwordsDeleted = await Password.deleteAllPasswordsByUserID(req.userToken.userID)

		if (!passwordsDeleted) {
			console.log("Status 401: Failed to delete all passwords")
			return res.status(401).send({error: "Failed to delete all passwords"})
		}

		console.log("Status 200: All Passwords Deleted")
		res.status(200).send(passwordsDeleted)
	} catch (errors) {
		console.log("Status 400: Failed to delete all passwords")
		res.status(400).send({error: "Failed to delete all passwords"})
	}
})

router.get('/passwords/', auth.authenticateToken, async (req, res) => {
	// Get all passwords by User

	try {
		const userID = req.userToken.userID
		const passwords = await Password.getPasswordsbyUserID(userID)

		if (!passwords) {
			return res.status(401).send({error: "Failed to find passwords"})
		}

		res.status(200).send(passwords)
	} catch (error) {
		res.status(400).send(error)
	}
})

module.exports = router