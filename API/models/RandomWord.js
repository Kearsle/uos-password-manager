const mongoose = require('mongoose')

const randomWordSchema = mongoose.Schema({
	words: [{
        type: String
	}],
    length: {
        type: String
    }
})

randomWordSchema.statics.getRandomWords = async (length) => {
	const wordList = await RandomWord.findOne({"length" : "2285"})
    var words = ""
    for (let i = 0; i < length; i++) {
        const randomWord = wordList.words[Math.floor(Math.random() * wordList.words.length)];
        words += randomWord[0].toUpperCase() + randomWord.substring(1)
    }

    return words
}

const RandomWord = mongoose.model('RandomWord', randomWordSchema)

module.exports = RandomWord