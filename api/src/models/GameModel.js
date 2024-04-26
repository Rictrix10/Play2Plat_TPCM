const { PrismaClient } = require('@prisma/client');
const prisma = new PrismaClient();

const GameModel = {
    createGame: async (name, description, isFree, releaseDate, pegiInfo, coverImage, sequenceId, companyId ) => {
        return await prisma.game.create({
            data: {
                name,
                description,
                isFree,
                releaseDate,
                pegiInfo,
                coverImage,
                sequenceId,
                companyId
            }
        });
    },
    getGames: async () => {
            return await prisma.game.findMany();
    },
    updateGame: async (id, data) => {
       return await prisma.game.update({
              where: { id },
              data,
            });
        },
    deleteGame: async (id) => {
         return await prisma.game.delete({
            where: { id },
            });
        },
};

module.exports = GameModel;
