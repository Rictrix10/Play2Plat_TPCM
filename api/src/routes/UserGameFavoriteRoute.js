const express = require('express');
const router = express.Router();
const UserGameFavoriteController = require('../controllers/UserGameFavoriteController');
router.post('/user-game-favorite', UserGameFavoriteController.createUserGameFavorite);
router.get('/user-game-favorite', UserGameFavoriteController.getAllFavorites);
router.get('/user-game-favorite/:id', UserGameFavoriteController.getFavoriteById);
router.delete('/user-game-favorite/:id', UserGameFavoriteController.deleteUserGameFavorite);
router.get('/user-game-favorite/user/:userId', UserGameFavoriteController.getFavoritesByUserId);
router.get('/user-game-favorite/game/:gameId', UserGameFavoriteController.getFavoritesByGameId);

module.exports = router;
