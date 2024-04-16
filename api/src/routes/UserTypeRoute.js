const express = require('express');
const router = express.Router();
const UserTypeController = require('../controllers/UserTypeController');

router.post('/userTypes', UserTypeController.createUserType);

router.get('/userTypes', UserTypeController.getUserTypes);

module.exports = router;
