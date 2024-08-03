const MessageModel = require('../models/MessageModel');

const MessageController = {
    createMessage: async (req, res) => {
        try {
            const { message, image, isAnswer, userOneId, userTwoId, date } = req.body;
            const newMessage = await MessageModel.createMessage(
                message,
                image,
                isAnswer,
                userOneId,
                userTwoId,
                date
            );
            res.status(201).json(newMessage);
        } catch (error) {
            console.error('Erro ao criar mensagem:', error);
            res.status(500).json({ error: 'Erro ao criar a mensagem' });
        }
    },

    getMessages: async (req, res) => {
        try {
            const messages = await MessageModel.getAllMessages();
            res.json(messages);
        } catch (error) {
            console.error('Erro ao buscar mensagens:', error);
            res.status(500).json({ error: 'Erro ao buscar mensagens' });
        }
    },

    getMessageById: async (req, res) => {
        try {
           const messageId = parseInt(req.params.id, 10); // Converter o ID para número inteiro
           if (isNaN(messageId)) {
                    return res.status(400).json({ error: 'ID inválido' });
           }
           const message = await MessageModel.getMessageById(messageId);
           if (message) {
                res.json(message);
           } else {
                res.status(404).json({ error: 'Mensagem não encontrada' });
           }
        } catch (error) {
            console.error('Erro ao buscar mensagem por ID:', error);
            res.status(500).json({ error: 'Erro ao buscar mensagem' });
        }
    },



        getMessagesByUserId: async (req, res) => {
            try {
                const userId = parseInt(req.params.userId, 10);
                if (isNaN(userId)) {
                    return res.status(400).json({ error: 'userId inválido' });
                }
                const messages = await MessageModel.getMessagesByUserId(userId);
                res.json(messages);
            } catch (error) {
                console.error('Erro ao buscar mensagens por userId:', error);
                res.status(500).json({ error: 'Erro ao buscar mensagens' });
            }
        },

        getMessagesByUsers: async (req, res) => {
            try {
                const userOneId = parseInt(req.params.userOneId, 10);
                const userTwoId = parseInt(req.params.userTwoId, 10);
                if (isNaN(userOneId) || isNaN(userTwoId)) {
                    return res.status(400).json({ error: 'userOneId ou userTwoId inválido' });
                }
                const messages = await MessageModel.getMessagesByUsers(userOneId, userTwoId);
                res.json(messages);
            } catch (error) {
                res.status(500).json({ error: 'Erro ao buscar mensagens' });
            }
        },

    getResponsesByMessageId: async (req, res) => {
        try {
            const messageId = parseInt(req.params.messageId, 10);
            if (isNaN(messageId)) {
                return res.status(400).json({ error: 'messageId inválido' });
            }
            const messages = await MessageModel.getResponsesByMessageId(messageId);
            res.json(messages);
        } catch (error) {
            console.error('Erro ao buscar respostas:', error);
            res.status(500).json({ error: 'Erro ao buscar respostas' });
        }
    },

     updateMessageById: async (req, res) => {
             try {
                 const messageId = parseInt(req.params.id, 10);
                 const { message, image, isAnswer, date } = req.body;

                 const updatedMessage = await MessageModel.updateMessageById(
                     message,
                     image,
                     isAnswer,
                     date
                 );

                 if (updatedMessage) {
                     res.json(updatedMessage);
                 } else {
                     res.status(404).json({ error: 'Mensagem não encontrada' });
                 }
             } catch (error) {
                 console.error('Erro ao atualizar mensagem:', error);
                 res.status(500).json({ error: 'Erro ao atualizar mensagem' });
             }
         },

    deleteMessageById: async (req, res) => {
        try {
            const messageId = parseInt(req.params.id, 10);
            await MessageModel.deleteMessageById(messageId);
            res.status(204).send();  // No Content
        } catch (error) {
            console.error('Erro ao deletar mensagem:', error);
            res.status(500).json({ error: 'Erro interno ao deletar mensagem' });
        }
    },

    // ENDPOINT DE LISTAR USERS QUE SE ENVIOU MENSAGENS

       getUsersByMessageId: async (req, res) => {
            try {
                const userId = parseInt(req.params.userId, 10);
                if (isNaN(userId)) {
                    return res.status(400).json({ error: 'userId inválido' });
                }
                const users = await MessageModel.getUsersByMessageId(userId);
                res.json(users);
            } catch (error) {
                console.error('Erro ao buscar usuários por mensagens:', error);
                res.status(500).json({ error: 'Erro ao buscar usuários' });
            }
        }
    };

module.exports = MessageController;

