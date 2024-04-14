const express = require('express');
const router = express.Router();
const UserController = require('../controllers/userController');

router.post('/users', UserController.createUser);

router.get('/users', UserController.getUsers);

module.exports = router;
