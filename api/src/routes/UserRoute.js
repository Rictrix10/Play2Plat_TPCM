const express = require('express');
const router = express.Router();
const UserController = require('../controllers/UserController');

router.post('/users', UserController.createUser);

router.get('/users', UserController.getUsers);

router.get('/users/:id', UserController.getUserById);

router.patch('/users/:id', UserController.updateUser);

router.delete('/users/:id', UserController.deleteUser);

router.post('/users/login', UserController.loginUser);

router.delete('/users/soft-delete/:id', UserController.softDeleteUser);

router.post('/users/verify-password/:id', UserController.verifyPassword);
module.exports = router;
