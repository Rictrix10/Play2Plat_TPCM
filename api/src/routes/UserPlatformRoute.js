const express = require('express');
const router = express.Router();
const UserPlatformController = require('../controllers/UserPlatformController');
router.post('/user-platform', UserPlatformController.createUserPlatform);
router.get('/user-platform', UserPlatformController.getAllUserPlatforms);
router.get('/user-platform/:id', UserPlatformController.getUserPlatformById);
router.delete('/user-platform/:id', UserPlatformController.deleteUserPlatform);
router.get('/user-platform/user/:userId', UserPlatformController.getUserPlatformsByUserId);
router.get('/user-platform/platform/:platformId', UserPlatformController.getUserPlatformsByPlatformId);

module.exports = router;
