const express = require('express');
const router = express.Router();
const GameGenreController = require('../controllers/GameGenreController');

router.post('/game-genre', GameGenreController.createGameGenre);
router.get('/game-genre', GameGenreController.getAllGameGenres);
router.get('/game-genre/:id', GameGenreController.getGameGenreById);
router.delete('/game-genre/:id', GameGenreController.deleteGameGenre);
router.get('/game-genre/game/:gameId', GameGenreController.getGameGenresByGameId);
router.get('/game-genre/genre/:genreId', GameGenreController.getGameGenresByGenreId);
router.get('/game-genre/games-by-genre/:genreId', GameGenreController.getGamesByGenreId);
router.get('/game-genre/name/:genreName', GameGenreController.getGamesByGenreName);
router.delete('/game-genre/game/:gameId/genre/:genreId', GameGenreController.deleteGameGenreByGameIdAndGenreId);
router.delete('/game-genre/game/:gameId', GameGenreController.deleteGameGenreByGameId);
module.exports = router;

