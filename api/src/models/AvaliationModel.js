const { PrismaClient } = require('@prisma/client');
const prisma = new PrismaClient();

const AvaliationModel = {
    createAvaliation: async (stars, userId, gameId) => {
        return await prisma.avaliation.create({
            data: {
                stars,
                userId,
                gameId
            }
        });
    },
    getAvaliations: async () => {
        return await prisma.avaliation.findMany();
    },
    getAvaliationByUserId: async (userId) => {
        return await prisma.avaliation.findMany({
            where: {
                userId: userId
            }
        });
    },
    updateAvaliationByUserIdAndGameId: async (userId, gameId, data) => {
        return await prisma.avaliation.updateMany({
            where: {
                userId: userId,
                gameId: gameId
            },
            data: data
        });
    },
    deleteAvaliationByUserIdAndGameId: async (userId, gameId) => {
        return await prisma.avaliation.deleteMany({
            where: {
                userId: userId,
                gameId: gameId
            }
        });
    }
};

module.exports = AvaliationModel;
