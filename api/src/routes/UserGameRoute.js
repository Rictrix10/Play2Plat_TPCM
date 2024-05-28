const express = require('express');
const router = express.Router();
const UserGameController = require('../controllers/UserGameController');

router.post('/user-game', UserGameController.createUserGame);
router.get('/user-game', UserGameController.getAllUserGames);
router.get('/user-game/:id', UserGameController.getUserGameById);
router.put('/user-game/:id', UserGameController.updateUserGameState);
router.delete('/user-game/:id', UserGameController.deleteUserGame);
router.get('/user-game/user/:userId', UserGameController.getUserGamesByUserId);
module.exports = router;
