const { PrismaClient } = require('@prisma/client');
const prisma = new PrismaClient();

const UserGameCommentsModel = {
    createUserGameComment: async (comments, image, isAnswer, userId, gameId, latitude, longitude) => {
        return await prisma.userGameComment.create({
            data: {
                comments,
                image,
                isAnswer,
                userId,
                gameId,
                latitude,
                longitude
            }
        });
    },

    getAllComments: async () => {
        return await prisma.userGameComment.findMany();
    },

    getCommentById: async (id) => {
        return await prisma.userGameComment.findUnique({
            where: {
                id: id,
            }
        });
    },

    deleteCommentById: async (id) => {
        return await prisma.userGameComment.delete({
            where: {
                id: id,
            }
        });
    },
    getCommentsByGameId: async (req, res) => {
        try {
            // Convertendo gameId para inteiro
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

module.exports = UserGameCommentsModel;
