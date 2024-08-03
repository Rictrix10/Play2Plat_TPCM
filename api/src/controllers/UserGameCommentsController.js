const UserGameCommentsModel = require('../models/UserGameCommentsModel');

const UserGameCommentsController = {
    createUserGameComment: async (req, res) => {
        try {
            const { comments, image, isAnswer, userId, gameId, latitude, longitude, location } = req.body;
            const newComment = await UserGameCommentsModel.createUserGameComment(
                comments,
                image,
                isAnswer,
                userId,
                gameId,
                latitude,
                longitude,
                location
            );
            res.status(201).json(newComment);
        } catch (error) {
            console.error('Erro ao criar comentário:', error);
            res.status(500).json({ error: 'Erro ao criar o comentário' });
        }
    },

    getUserGameComments: async (req, res) => {
        try {
            const comments = await UserGameCommentsModel.getAllComments();
            res.json(comments);
        } catch (error) {
            console.error('Erro ao buscar comentários:', error);
            res.status(500).json({ error: 'Erro ao buscar comentários' });
        }
    },

    getUserGameCommentById: async (req, res) => {
        try {
           const commentId = parseInt(req.params.id, 10); // Converter o ID para número inteiro
           if (isNaN(commentId)) {
                    return res.status(400).json({ error: 'ID inválido' });
           }
           const comment = await UserGameCommentsModel.getCommentById(commentId);
           if (comment) {
                res.json(comment);
           } else {
                res.status(404).json({ error: 'Comentário não encontrado' });
           }
        } catch (error) {
            console.error('Erro ao buscar comentário por ID:', error);
            res.status(500).json({ error: 'Erro ao buscar comentário' });
        }
    },



    getCommentsByGameId: async (req, res) => {
        try {
            const gameId = parseInt(req.params.gameId, 10);
            if (isNaN(gameId)) {
                return res.status(400).json({ error: 'gameId inválido' });
            }
            const comments = await UserGameCommentsModel.getCommentsByGameId(gameId);
            res.json(comments);
        } catch (error) {
            console.error('Erro ao buscar comentários por gameId:', error);
            res.status(500).json({ error: 'Erro ao buscar comentários' });
        }
    },

    getGamePostsPreview: async (req, res) => {
        try {
            const gameId = parseInt(req.params.gameId, 10);
            if (isNaN(gameId)) {
                return res.status(400).json({ error: 'gameId inválido' });
            }
            const comments = await UserGameCommentsModel.getGamePostsPreview(gameId);
            res.json(comments);
        } catch (error) {
            console.error('Erro ao buscar posts de visualização:', error);
            res.status(500).json({ error: 'Erro ao buscar posts de visualização' });
        }
    },

    getPostsByUserIdGameId: async (req, res) => {
        try {
            const userId = parseInt(req.params.userId, 10);
            const gameId = parseInt(req.params.gameId, 10);
            if (isNaN(userId)) {
                return res.status(400).json({ error: 'userId inválido' });
            }
            if (isNaN(gameId)) {
               return res.status(400).json({ error: 'gameId inválido' });
            }
            const comments = await UserGameCommentsModel.getPostsByUserIdGameId(userId, gameId);
            res.json(comments);
        } catch (error) {
            console.error('Erro ao buscar posts por userId:', error);
            res.status(500).json({ error: 'Erro ao buscar posts' });
        }
    },

    getResponsesByPostId: async (req, res) => {
        try {
            const postId = parseInt(req.params.postId, 10);
            if (isNaN(postId)) {
                return res.status(400).json({ error: 'postId inválido' });
            }
            const comments = await UserGameCommentsModel.getResponsesByPostId(postId);
            res.json(comments);
        } catch (error) {
            console.error('Erro ao buscar respostas:', error);
            res.status(500).json({ error: 'Erro ao buscar respostas' });
        }
    },

     updateUserGameCommentById: async (req, res) => {
             try {
                 const commentId = parseInt(req.params.id, 10);
                 const { comments, image, isAnswer, latitude, longitude, location } = req.body;

                 const updatedComment = await UserGameCommentsModel.updateUserGameCommentById(
                     commentId,
                     comments,
                     image,
                     isAnswer,
                     latitude,
                     longitude,
                     location
                 );

                 if (updatedComment) {
                     res.json(updatedComment);
                 } else {
                     res.status(404).json({ error: 'Comentário não encontrado' });
                 }
             } catch (error) {
                 console.error('Erro ao atualizar comentário:', error);
                 res.status(500).json({ error: 'Erro ao atualizar comentário' });
             }
         },

        /*
         deleteUserGameCommentById: async (req, res) => {
                try {
                    const commentId = parseInt(req.params.id, 10);
                    const deleteResult = await UserGameCommentsModel.deleteUserGameCommentById(commentId);

                    if (deleteResult) {
                        res.status(204).send();  // No Content
                    } else {
                        res.status(404).json({ error: 'Comentário não encontrado' });
                    }
                } catch (error) {
                    console.error('Erro ao deletar comentário:', error);
                    res.status(500).json({ error: 'Erro interno ao deletar comentário' });
                }
            }
            */
    deleteUserGameCommentById: async (req, res) => {
        try {
            const commentId = parseInt(req.params.id, 10);
            await UserGameCommentsModel.deleteUserGameCommentById(commentId);
            res.status(204).send();  // No Content
        } catch (error) {
            console.error('Erro ao deletar comentário:', error);
            res.status(500).json({ error: 'Erro interno ao deletar comentário' });
        }
    },

        getLocationCommentsByGameId: async (req, res) => {
            try {
                const gameId = parseInt(req.params.gameId, 10);
                if (isNaN(gameId)) {
                    return res.status(400).json({ error: 'gameId inválido' });
                }
                const comments = await UserGameCommentsModel.getLocationCommentsByGameId(gameId);
                res.json(comments);
            } catch (error) {
                console.error('Erro ao buscar comentários por gameId:', error);
                res.status(500).json({ error: 'Erro ao buscar comentários' });
            }
        },
    };

module.exports = UserGameCommentsController;

