const mongoose = require('mongoose')

const passwordSchema = mongoose.Schema({
	userID: {
		type: String,
		required: true,
		trim: true
	},
	programName: {
		type: String,
		required: true,
		trim: true
	},
	programUsername: {
		type: String,
		required: true,
		trim: true
	},
	programPassword: {
		type: String,
		required: true,
		trim: true
	},
	createdOn: {
		type: Date,
		default: Date.now
	},
	updatedOn: {
		type: Date,
		default: Date.now
	}
})

passwordSchema.statics.editPassword = async (password) => {
	// Finding and updating the password

	const id = new mongoose.Types.ObjectId(password._id)
	const updatedPassword = await Password.updateOne({"_id": id}, {"programName": password.programName, "programUsername": password.programUsername, "programPassword": password.programPassword, "updatedOn": Date.now()})
		
	if (!updatedPassword) {
		return false
	}

	return updatedPassword
}

passwordSchema.statics.deletePassword = async (passwordID) => {
	var id = new mongoose.Types.ObjectId(passwordID)

	const password = await Password.deleteOne({"_id": id})

	if(!password) {
		return false
	}

	return password
}

passwordSchema.statics.deleteAllPasswordsByUserID = async (userID) => {
	const password = await Password.deleteMany({"userID": userID})

	if(!password) {
		return false
	}

	return password
}

passwordSchema.statics.getPasswordsbyUserID = async (userID) => {
	const passwords = await Password.find({"userID": userID})

	return passwords
}

passwordSchema.statics.getPasswordbyPasswordID = async (passwordID) => {
	const id = new mongoose.Types.ObjectId(passwordID)
	const password = await Password.findOne({"_id": id})

	return password
}

const Password = mongoose.model('Password', passwordSchema)

module.exports = Password