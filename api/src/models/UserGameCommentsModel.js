const { PrismaClient } = require('@prisma/client');
const prisma = new PrismaClient();

const UserGameCommentsModel = {
    createUserGameComment: async (comments, image, userId, gameId, latitude, longitude) => {
        return await prisma.userGameComment.create({
            data: {
                comments,
                image,
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


};

module.exports = UserGameCommentsModel;
