const express = require('express');
const router = express.Router();
const PlatformController = require('../controllers/PlatformController');

router.post('/platforms', PlatformController.createPlatform);

router.get('/platforms', PlatformController.getPlatforms);

router.get('/platforms/random-name', PlatformController.getRandomPlatformName);

module.exports = router;
