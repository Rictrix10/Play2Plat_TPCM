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

getPlatformByName: async (platformName) => {
        try {
            const platform = await prisma.platform.findUnique({
                where: { name: platformName }
            });
            return platform;
        } catch (error) {
            console.error('Erro ao buscar plataforma por nome:', error);
            throw error;
        }
    },

    getGamesByPlatformId: async (platformId) => {
        try {
            const games = await prisma.game.findMany({
                where: {
                    platformGames: {
                        some: { platformId }
                    }
                },
                include: {
                    sequence: true,
                    company: true,
                    platforms: true
                }
            });
            return games;
        } catch (error) {
            console.error('Erro ao buscar jogos por ID da plataforma:', error);
            throw error;
        }
    }
};

module.exports = GameModel;

module.exports = PlatformGameModel;
