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

    getGamesByPlatformId: async (platformId) => {
        try {
            return await prisma.platformGame.findMany({
                where: {
                    platformId: platformId
                },
                include: {
                    game: {
                        include: {
                            sequence: true,
                            company: true,
                            platforms: true
                        }
                    }
                }
            });
        } catch (error) {
            console.error('Erro ao buscar jogos por ID da plataforma:', error);
            throw error;
        }
    },

    deletePlatformGameByPlatformIdAndGameId: async (platformId, gameId) => {
        return await prisma.platformGame.deleteMany({
            where: {
                platformId: platformId,
                gameId: gameId
            }
        });
    },

    getGamesByPlatformName: async (platformName) => {
        return await prisma.platformGame.findMany({
            where: {
                platform: {
                    name: platformName
                }
            },
            select: {
                game: {
                    select: {
                        id: true,
                        name: true,
                        coverImage: true
                    }
                }
            }
        });
    }
};

module.exports = PlatformGameModel;
