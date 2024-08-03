const express = require('express');
const router = express.Router();
const MessageController = require('../controllers/MessageController');

router.post('/message', MessageController.createMessage);

router.get('/message', MessageController.getMessages);

router.get('/message/:id', MessageController.getMessageById);


// Buscar mensagens por ID de usuário (considerando userOneId e userTwoId)
router.get('/messages/:userId', MessageController.getMessagesByUserId);

// Buscar mensagens entre dois usuários
router.get('/messages-between/:userOneId/:userTwoId', MessageController.getMessagesByUsers);


router.get('/message-responses/message/:messageId', MessageController.getResponsesByMessageId);

router.patch('/message/:id', MessageController.updateMessageById);

router.delete('/message/:id', MessageController.deleteMessageById);

// Novo endpoint para buscar usuários que têm mensagens com o userId
router.get('/users-by-message/:userId', MessageController.getUsersByMessageId);


module.exports = router;



