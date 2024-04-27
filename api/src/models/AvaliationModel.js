const { PrismaClient } = require('@prisma/client');
const prisma = new PrismaClient();

const AvaliationModel = {
    createAvaliation: async (stars, userId, gameId ) => {
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

};

module.exports = AvaliationModel;