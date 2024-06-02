const express = require('express');
const router = express.Router();
const UserGameCommentsController = require('../controllers/UserGameCommentsController');

router.post('/userGameComments', UserGameCommentsController.createUserGameComment);
router.get('/userGameComments', UserGameCommentsController.getUserGameComments);
router.get('/userGameComments/:id', UserGameCommentsController.getUserGameCommentById);
router.delete('/userGameComments/:id', UserGameCommentsController.deleteUserGameComment);
router.get('/userGameComments/game/:gameId', UserGameCommentsController.getCommentsByGameId);
router.get('/userGameComments/postsPreview', UserGameCommentsController.getPostsPreview);
router.get('/userGameComments/user/:userId', UserGameCommentsController.getPostsByUserId);
router.get('/userGameComments/responses/:postId', UserGameCommentsController.getResponsesByPostId);
router.patch('/userGameComments/:userId/:gameId', UserGameCommentsController.updateUserGameComment);
router.delete('/userGameComments/:userId/:gameId', UserGameCommentsController.deletingUserGameComment);

module.exports = router;



