const express = require('express');
const router = express.Router();
const AvaliationController = require('../controllers/AvaliationController');

router.post('/avaliation', AvaliationController.createAvaliation);
router.get('/avaliation', AvaliationController.getAvaliations);

module.exports = router;