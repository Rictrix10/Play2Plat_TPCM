const express = require('express');
const router = express.Router();
const FriendRequestController = require('../controllers/FriendRequestController');

router.post('/friend-request', FriendRequestController.createFriendRequest);
router.get('/friend-request', FriendRequestController.getAllFriendRequests);
router.get('/friend-request/:id', FriendRequestController.getFriendRequestById);
router.delete('/friend-request/:id', FriendRequestController.deleteFriendRequest);

router.get('/friend-request/all-sent-user/:userId', FriendRequestController.getAllFriendRequestBySentUserId);
router.get('/friend-request/accepted-sent-user/:userId', FriendRequestController.getAcceptedFriendRequestBySentUserId);
router.get('/friend-request/not-accepted-sent-user/:userId', FriendRequestController.getNotAcceptedFriendRequestBySentUserId);

router.get('/friend-request/all-received-user/:userId', FriendRequestController.getAllFriendRequestByReceivedUserId);
router.get('/friend-request/accepted-received-user/:userId', FriendRequestController.getAcceptedFriendRequestByReceivedUserId);
router.get('/friend-request/not-accepted-received-user/:userId', FriendRequestController.getNotAcceptedFriendRequestByReceivedUserId);

router.delete('/friend-request/sent-user/:sentUserId/received-user/:receivedUserId', FriendRequestController.deleteFriendRequestBySentUserIdAndReceivedUserId);
router.get('/friend-request/sent-user/:sentUserId/received-user/:receivedUserId', FriendRequestController.getFriendRequestBySentUserIdAndReceivedUserId);


router.patch('/friend-request/sent-user/:sentUserId/received-user/:receivedUserId', FriendRequestController.updateFriendRequestBySentUserIdAndReceivedUserId);
router.put('/friend-request/:id', FriendRequestController.updateFriendRequestAccepted);
module.exports = router;
