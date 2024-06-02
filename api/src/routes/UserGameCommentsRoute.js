const express = require('express');
const router = express.Router();
const UserGameCommentsController = require('../controllers/UserGameCommentsController');

router.post('/user-game-comment', UserGameCommentsController.createUserGameComment);

router.get('/user-game-comment', UserGameCommentsController.getUserGameComments);

router.get('/user-game-comment/:id', UserGameCommentsController.getUserGameCommentById);

router.delete('/user-game-comment/:id', UserGameCommentsController.deleteUserGameComment);

router.get('/user-game-comments/:gameId', UserGameCommentsController.getCommentsByGameId);

router.get('/user-game-comment-preview', UserGameCommentsController.getPostsPreview);

module.exports = router;

