const express = require('express');
const router = express.Router();
const PlatformGameController = require('../controllers/PlatformGameController');
router.post('/platform-game', PlatformGameController.createPlatformGame);
router.get('/platform-game', PlatformGameController.getAllPlatformGames);
router.get('/platform-game/:id', PlatformGameController.getPlatformGameById);
router.delete('/platform-game/:id', PlatformGameController.deletePlatformGame);
router.get('/platform-game/platform/:platformId', PlatformGameController.getPlatformGamesByPlatformId);
router.get('/platform-game/game/:gameId', PlatformGameController.getPlatformGamesByGameId);

module.exports = router;
