const express = require('express');
const router = express.Router();
const GameController = require('../controllers/GameController');

router.post('/games', GameController.createGame);
router.get('/games', GameController.getGames);
router.get('/games/:id', GameController.getGameById);
router.patch('/games/:id', GameController.updateGame);
router.delete('/games/:id', GameController.deleteGame);
router.get('/games/company/:companyName', GameController.getGamesByCompany);
router.get('/games/sequence/:sequenceName', GameController.getGamesBySequence);
router.get('/games/search', GameController.getGamesByParams);

module.exports = router;
