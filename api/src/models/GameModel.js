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
    getGenresByGameId: async (gameId) => {
        try {
            const gameGenres = await prisma.gameGenre.findMany({
                where: {
                    gameId: gameId
                },
                include: {
                    genre: true
                }
            });
            const genres = gameGenres.map(gameGenre => gameGenre.genre.name);
            return genres;
        } catch (error) {
            console.error('Erro ao buscar gêneros por ID de jogo:', error);
            throw error;
        }
    },
    getPlatformsByGameId: async (gameId) => {
            try {
                const platformGames = await prisma.platformGame.findMany({
                    where: {
                        gameId: gameId
                    },
                    include: {
                        platform: true
                    }
                });
                const platforms = platformGames.map(platformGame => platformGame.platform.name);
                return platforms;
            } catch (error) {
                console.error('Erro ao buscar gêneros por ID de jogo:', error);
                throw error;
            }
        },

getGameById: async (id) => {
    try {
        const game = await prisma.game.findUnique({
            where: { id },
            include: {
                sequence: true,
                company: true,
            },
        });
        if (!game) {
            return null;
        }
        const genres = await GameModel.getGenresByGameId(id); // Chamando o novo método
        game.genres = genres; // Adicionando os gêneros ao objeto do jogo
        const platforms = await GameModel.getPlatformsByGameId(id);
        game.platforms = platforms
        return game;
    } catch (error) {
        console.error('Erro ao buscar jogo por ID:', error);
        throw error;
    }
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
getPlatformByName: async (platformName) => {
        try {
            const platform = await prisma.platform.findUnique({
                where: {
                    name: platformName
                }
            });
            return platform;
        } catch (error) {
            console.error('Erro ao buscar plataforma por nome:', error);
            throw error;
        }
    },

        getFilteredGames: async (filters) => {
            const { name, genre, platform, company, sequence, isFree } = filters;

            const games = await prisma.game.findMany({
                where: {
                    AND: [
                        name ? { name: { contains: name, mode: 'insensitive' } } : undefined,
                        isFree !== undefined ? { isFree: isFree } : undefined,
                        company ? { company: { name: { contains: company, mode: 'insensitive' } } } : undefined,
                        sequence ? { sequence: { name: { contains: sequence, mode: 'insensitive' } } } : undefined,
                        genre ? {
                            genres: {
                                some: { name: { contains: genre, mode: 'insensitive' } }
                            }
                        } : undefined,
                        platform ? {
                            platforms: {
                                some: { name: { contains: platform, mode: 'insensitive' } }
                            }
                        } : undefined
                    ]
                },
                include: {
                    company: true,
                    sequence: true,
                    genres: true,
                    platforms: true
                }
            });

            return games;
        },

};

module.exports = GameModel;
