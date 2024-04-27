const { PrismaClient } = require('@prisma/client');
const prisma = new PrismaClient();

const PlatformGameModel = {
    createPlatformGame: async (platformId, gameId) => {
        // Cria uma nova relação entre uma plataforma e um jogo
        return await prisma.platformGame.create({
            data: {
                platformId,
                gameId
            }
        });
    },

    getAllPlatformGames: async () => {
        // Retorna todas as relações entre plataformas e jogos
        return await prisma.platformGame.findMany();
    },

    getPlatformGameById: async (id) => {
        // Retorna uma relação específica entre uma plataforma e um jogo por ID
        return await prisma.platformGame.findUnique({
            where: {
                id: id,
            }
        });
    },

    deletePlatformGameById: async (id) => {
        // Exclui uma relação específica entre uma plataforma e um jogo por ID
        return await prisma.platformGame.delete({
            where: {
                id: id,
            }
        });
    },

    getPlatformGamesByPlatformId: async (platformId) => {
        // Retorna todas as relações de uma plataforma específica com jogos
        return await prisma.platformGame.findMany({
            where: {
                platformId: platformId,
            }
        });
    },

    getPlatformGamesByGameId: async (gameId) => {
        // Retorna todas as relações de um jogo específico com plataformas
        return await prisma.platformGame.findMany({
            where: {
                gameId: gameId,
            }
        });
    }
};

module.exports = PlatformGameModel;
