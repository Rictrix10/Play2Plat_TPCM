const express = require('express');
const router = express.Router();
const UserController = require('../controllers/UserController');

router.post('/users', UserController.createUser);

router.get('/users', UserController.getUsers);

router.get('/users/:id', UserController.getUserById);

router.patch('/users/:id', UserController.updateUser);

router.delete('/users/:id', UserController.deleteUser);

router.post('/users/login', UserController.loginUser);

router.get('/user-details/:id', UserController.getUserDetailsById);

module.exports = router;
