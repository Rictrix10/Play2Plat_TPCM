const UserGameCommentsModel = require('../models/UserGameCommentsModel');

const UserGameCommentsController = {
    createUserGameComment: async (req, res) => {
        try {
            // Obtendo dados do corpo da requisição
            const { comments, image, isAnswer, userId, gameId, latitude, longitude } = req.body;

            // Criando um novo comentário
            const newComment = await UserGameCommentsModel.createUserGameComment(
                comments,
                image,
                isAnswer,
                userId,
                gameId,
                latitude,
                longitude
            );

            // Retornando o novo comentário com status 201 (Criado)
            res.status(201).json(newComment);
        } catch (error) {
            console.error('Erro ao criar comentário:', error);
            // Retornando um erro com status 500 (Erro Interno do Servidor)
            res.status(500).json({ error: 'Erro ao criar o comentário' });
        }
    },

    getUserGameComments: async (req, res) => {
        try {
            // Obtendo todos os comentários
            const comments = await UserGameCommentsModel.getAllComments();

            // Retornando todos os comentários
            res.json(comments);
        } catch (error) {
            console.error('Erro ao buscar comentários:', error);
            // Retornando um erro com status 500 (Erro Interno do Servidor)
            res.status(500).json({ error: 'Erro ao buscar comentários' });
        }
    },

    getUserGameCommentById: async (req, res) => {
        try {
            // Obtendo o ID do comentário da URL
            const commentId = req.params.id;

            // Buscando o comentário pelo ID
            const comment = await UserGameCommentsModel.getCommentById(commentId);

            if (comment) {
                // Se o comentário foi encontrado, retorná-lo
                res.json(comment);
            } else {
                // Se o comentário não foi encontrado, retornar 404 (Não Encontrado)
                res.status(404).json({ error: 'Comentário não encontrado' });
            }
        } catch (error) {
            console.error('Erro ao buscar comentário por ID:', error);
            // Retornando um erro com status 500 (Erro Interno do Servidor)
            res.status(500).json({ error: 'Erro ao buscar comentário' });
        }
    },

    deleteUserGameComment: async (req, res) => {
        try {
            // Obtendo o ID do comentário a ser excluído da URL
            const commentId = req.params.id;

            // Excluindo o comentário
            const deleted = await UserGameCommentsModel.deleteCommentById(commentId);

            if (deleted) {
                // Se o comentário foi excluído, retornando 204 (Sem Conteúdo)
                res.status(204).end();
            } else {
                // Se o comentário não foi encontrado, retornar 404 (Não Encontrado)
                res.status(404).json({ error: 'Comentário não encontrado' });
            }
        } catch (error) {
            console.error('Erro ao excluir comentário:', error);
            // Retornando um erro com status 500 (Erro Interno do Servidor)
            res.status(500).json({ error: 'Erro ao excluir comentário' });
        }
    }
};

module.exports = UserGameCommentsController;
