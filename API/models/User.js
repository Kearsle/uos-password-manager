const mongoose = require('mongoose')
const validator = require('validator')
const bcrypt = require('bcryptjs')
const jwt = require('jsonwebtoken')

const userSchema = mongoose.Schema({
	username: {
		type: String,
		unique: true,
		minLength: 3,
		required: true,
		trim: true
	},
	password: {
		type: String,
		required: true,
		minLength: 5,
		trim: true
	},
	email: {
		type: String,
		required: true,
		unique: true,
		lowercase: true,
		validate: value => {
			if (!validator.isEmail(value)) {
				throw new Error({error: 'Invalid Email Address'})
			}
		}
	},
	// Array isn't required but if a token is present it needs a value
	tokens: [{
		token: {
			type: String,
			required: true
		}
	}]
})

userSchema.pre('save', async function (next){
	//hashing the password
	const user = this
	if (user.isModified('password')) {
		user.username = user.username.toLowerCase()
		// the await ensures the encryption happens before movng forward
		user.password = await bcrypt.hash(user.password, 8) 
	}
})

userSchema.statics.findByCredentials = async (username, password) => {
	
	const user = await User.findOne({username})
	if(!user) {
		return null
	}
	const isPasswordMatch = await bcrypt.compare(password, user.password)
	if(!isPasswordMatch){
		return null
	}

	return user
}

userSchema.statics.findByCredentialsUserID = async (userID, password) => {
	var id = new mongoose.Types.ObjectId(userID)
	const user = await User.findOne({"_id": id}).select("-tokens")
	if(!user) {
		return false
	}

	const isPasswordMatch = await bcrypt.compare(password, user.password)
	if(!isPasswordMatch){
		return false
	}

	return true
}

userSchema.statics.getUserbyUserID = async (userID) => {
	const user = await User.findOne({"_id": userID}).select("-tokens -password")

	return user
}

userSchema.statics.deleteUser = async (userID) => {
	var id = new mongoose.Types.ObjectId(userID)

	const user = await User.deleteOne({"_id": id})

	if(!user) {
		throw new Error({error: "Failed to delete password"})
	}

	return user
}

userSchema.statics.editPassword = async (userID, newPassword) => {
	// Finding and updating the password
	const hashedNewPass = (await hashPass(newPassword)).toString()
	const resetPassword = await User.updateOne({"_id": userID}, {"password": hashedNewPass})
		
	if (!resetPassword) {
		throw new Error({error: "Failed to reset password"})
	}

	return resetPassword
}

// Tokens


userSchema.statics.createAccessToken = async (userID) => {
	const accessToken = jwt.sign({
		userID: userID
	}, process.env.ACCESS_TOKEN_SECRET, {
		expiresIn: '1d'
	})
	return accessToken
}

userSchema.statics.createRefreshToken = async (userID) => {
	const refreshToken = jwt.sign({
		userID: userID
	}, process.env.REFRESH_TOKEN_SECRET, {
		expiresIn: '10m'
	})

	const id = userID
	try{
		await User.updateOne(
			{"_id": id},
			{ $push: {"tokens": {"token": refreshToken}}})
	} catch(error)
	{
		throw new Error({error: "Failed to create refresh token"})
	}
	return refreshToken
}

userSchema.statics.deleteRefreshToken = async (userID, refreshToken) => {
	try{
		return await User.updateOne(
			{"_id": userID},
			{ $pull: {"tokens": {"token": refreshToken}}})
	} catch(error)
	{
		throw new Error({error: "Failed to delete refresh token"})
	}
}

userSchema.statics.deleteAllRefreshToken = async (userID) => {
	try {
		return await User.updateOne(
			{"_id": userID},
			{ $set: {"tokens": []}})
	} catch (error)
	{
		throw new Error({error: "Failed to delete refresh token"})
	}
}

userSchema.statics.checkRefreshToken = async (userID, refreshToken) => {
	const user = await User.findOne({"_id": userID})
	if (!user) {
		return null
	}
	const tokenIncludes = await user.tokens.some(token => token.token === refreshToken)
	return tokenIncludes
}

function hashPass(password) {
	const hashedPassword = bcrypt.hash(password, 8) 
	return hashedPassword
}

const User = mongoose.model('User', userSchema)

module.exports = User