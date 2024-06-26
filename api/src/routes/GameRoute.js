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
router.get('/games/searchByName/:name', GameController.getGamesByPartialName);
router.get('/games/free/:isFree', GameController.getGamesByFreeStatus);
router.get('/gamess/descending', GameController.getGamesByDescendingId);
router.get('/games/same-company/:gameId', GameController.getGamesBySameCompanyId);
router.get('/games/same-sequence/:gameId', GameController.getGamesBySameSequenceId);
router.post('/games/filter', GameController.getFilteredGames);
router.delete('/games/soft-delete/:id', GameController.softDeleteGame);
module.exports = router;

