const express = require('express');
const router = express.Router();
const SequenceController = require('../controllers/SequenceController');

router.post('/sequences', SequenceController.createSequence);

router.get('/sequences', SequenceController.getSequences);

router.get('/sequences/random-name', SequenceController.getRandomSequenceName);

module.exports = router;
