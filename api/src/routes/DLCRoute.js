const express = require('express');
const router = express.Router();
const DLCController = require('../controllers/DLCController');

router.post('/dlc', DLCController.createDLC);
router.get('/dlc', DLCController.getDLCs);

module.exports = router;