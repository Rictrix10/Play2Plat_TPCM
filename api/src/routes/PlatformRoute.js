const express = require('express');
const router = express.Router();
const PlatformController = require('../controllers/PlatformController');

router.post('/platforms', PlatformController.createPlatform);

router.get('/platforms', PlatformController.getPlatforms);

module.exports = router;
