const express = require('express');
const router = express.Router();
const UserGameCommentsController = require('../controllers/UserGameCommentsController');

router.post('/user-game-comment', UserGameCommentsController.createUserGameComment);

router.get('/user-game-comment', UserGameCommentsController.getUserGameComments);

router.get('/user-game-comment/:id', UserGameCommentsController.getUserGameCommentById);


router.get('/user-game-comments/:gameId', UserGameCommentsController.getCommentsByGameId);

router.get('/user-game-comment-preview', UserGameCommentsController.getPostsPreview);

router.get('/user-game-comments-by-user-game/:userId/:gameId', UserGameCommentsController.getPostsByUserIdGameId);

router.get('/user-game-comments-responses/:postId', UserGameCommentsController.getResponsesByPostId);

router.patch('/user-game-comments/:id', UserGameCommentsController.updateUserGameCommentById);

router.delete('/user-game-comments/:id', UserGameCommentsController.deleteUserGameCommentById);

module.exports = router;



