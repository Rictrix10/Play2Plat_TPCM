const express = require('express');
const router = express.Router();
const GenreController = require('../controllers/GenreController');

router.post('/genres', GenreController.createGenre);

router.get('/genres', GenreController.getGenres);

router.get('/genres/random-name', GenreController.getRandomGenreName);

router.get('/genres/random-name-excluding', GenreController.getRandomGenreNameExcluding);

router.get('/genres/exclude-genres/:excludeName1/:excludeName2', GenreController.getRandomGenreNameExcludingUrl);

router.get('/genres/random-names', GenreController.getRandomGenreNames);
module.exports = router;
