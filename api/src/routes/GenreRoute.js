const express = require('express');
const router = express.Router();
const GenreController = require('../controllers/GenreController');

router.post('/genres', GenreController.createGenre);

router.get('/genres', GenreController.getGenres);

module.exports = router;