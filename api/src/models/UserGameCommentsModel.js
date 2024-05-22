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

    getCommentsByGameId: async (gameId) => {
        console.log('gameId:', gameId); // Adicione este log para verificar gameId
        return await prisma.userGameComment.findMany({
            where: {
                gameId: gameId,
            },
            include: {
                user: {
                    select: {
                        username: true,
                        avatar: true
                    }
                },
                game: {
                    select: {
                        name: true
                    }
                }
            }
        });
    }
};

module.exports = UserGameCommentsModel;
