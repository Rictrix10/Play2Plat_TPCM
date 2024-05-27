const express = require('express');
const router = express.Router();
const GameController = require('../controllers/GameController');

router.post('/games', GameController.createGame);
router.get('/games', GameController.getGames);
router.get('/games/:id', GameController.getGameById);
router.patch('/games/:id', GameController.updateGame);
router.delete('/games/:id', GameController.deleteGame);
router.get('/games/platform/:platformName', GameController.getGamesByPlatformName);
module.exports = router;
