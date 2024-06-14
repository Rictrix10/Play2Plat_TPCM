const express = require('express');
const router = express.Router();
const UserGameController = require('../controllers/UserGameController');

router.post('/user-game', UserGameController.createUserGame);
router.get('/user-game', UserGameController.getAllUserGames);
router.get('/user-game/:id', UserGameController.getUserGameById);
router.put('/user-game/:id', UserGameController.updateUserGameState);
router.delete('/user-game/:id', UserGameController.deleteUserGame);
router.get('/user-game/user/:userId', UserGameController.getUserGamesByUserId);
router.delete('/user-game/user/:userId/game/:gameId', UserGameController.deleteUserGameByUserIdAndGameId);
router.patch('/user-game/user/:userId/game/:gameId', UserGameController.updateUserGameByUserIdAndGameId);
router.get('/user-game/user/:userId/state/:state', UserGameController.getUserGamesByUserIdAndState);
router.get('/user-game/user/:userId/game/:gameId/state', UserGameController.getUserGameStateByUserIdAndGameId);
module.exports = router;
