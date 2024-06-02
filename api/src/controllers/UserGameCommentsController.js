const UserGameCommentsModel = require('../models/UserGameCommentsModel');

const UserGameCommentsController = {
    createUserGameComment: async (req, res) => {
        try {
            const { comments, image, isAnswer, userId, gameId, latitude, longitude } = req.body;
            const newComment = await UserGameCommentsModel.createUserGameComment(
                comments,
                image,
                isAnswer,
                userId,
                gameId,
                latitude,
                longitude
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
            const commentId = req.params.id;
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

    deleteUserGameComment: async (req, res) => {
        try {
            const commentId = req.params.id;
            const deleted = await UserGameCommentsModel.deleteCommentById(commentId);
            if (deleted) {
                res.status(204).end();
            } else {
                res.status(404).json({ error: 'Comentário não encontrado' });
            }
        } catch (error) {
            console.error('Erro ao excluir comentário:', error);
            res.status(500).json({ error: 'Erro ao excluir comentário' });
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
    }
};

module.exports = UserGameCommentsController;
