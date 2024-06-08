const { PrismaClient } = require('@prisma/client');
const prisma = new PrismaClient();

const AvaliationModel = {
    createAvaliation: async (stars, userId, gameId) => {
        const newAvaliation = await prisma.avaliation.create({
            data: {
                stars,
                userId,
                gameId
            }
        });
        await AvaliationModel.updateAverageStars(gameId);
        return newAvaliation;
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
        const updatedAvaliation = await prisma.avaliation.updateMany({
            where: {
                userId: userId,
                gameId: gameId
            },
            data: data
        });
        await AvaliationModel.updateAverageStars(gameId);
        return updatedAvaliation;
    },
    deleteAvaliationByUserIdAndGameId: async (userId, gameId) => {
        const deletedAvaliation = await prisma.avaliation.deleteMany({
            where: {
                userId: userId,
                gameId: gameId
            }
        });
        await AvaliationModel.updateAverageStars(gameId);
        return deletedAvaliation;
    },
    getAverageStarsByGameId: async (gameId) => {
        const result = await prisma.avaliation.aggregate({
            where: { gameId },
            _avg: { stars: true }
        });
        return result._avg.stars !== null ? result._avg.stars : 0; // Se não houver avaliações, retorna 0
    },
    updateAverageStars: async (gameId) => {
        const averageStars = await AvaliationModel.getAverageStarsByGameId(gameId);
        await prisma.game.update({
            where: { id: gameId },
            data: { averageStars: averageStars }
        });
    }
};

module.exports = AvaliationModel;
