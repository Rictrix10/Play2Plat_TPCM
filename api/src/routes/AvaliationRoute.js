const express = require('express');
const router = express.Router();
const AvaliationController = require('../controllers/AvaliationController');

router.post('/avaliation', AvaliationController.createAvaliation);
router.get('/avaliation', AvaliationController.getAvaliations);
router.get('/avaliation/user/:userId', AvaliationController.getAvaliationByUserId);
router.patch('/avaliation/user/:userId/game/:gameId', AvaliationController.patchAvaliationByUserIdAndGameId);
router.delete('/avaliation/user/:userId/game/:gameId', AvaliationController.deleteAvaliationByUserIdAndGameId);

module.exports = router;
