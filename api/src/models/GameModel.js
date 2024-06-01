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

        getGamesByParams: async (name, isFree) => {
            try {
                const filters = {};
                if (name) {
                    filters.name = {
                        contains: name,
                        mode: 'insensitive', // Caso queira buscar ignorando maiúsculas e minúsculas
                    };
                }
                if (isFree !== undefined) {
                    filters.isFree = isFree;
                }

                const games = await prisma.game.findMany({
                    where: filters,
                });
                return games;
            } catch (error) {
                console.error('Erro ao buscar jogos por parâmetros:', error);
                throw error;
            }
        },

            getGamesByCompany: async (companyId, companyName) => {
                try {
                    const whereClause = {};
                    if (companyId) {
                        whereClause.companyId = companyId;
                    } else if (companyName) {
                        const company = await prisma.company.findUnique({
                            where: { name: companyName },
                        });
                        if (!company) {
                            throw new Error('Company not found');
                        }
                        whereClause.companyId = company.id;
                    }

                    const games = await prisma.game.findMany({
                        where: whereClause,
                        select: {
                            id: true,
                            name: true,
                            coverImage: true,
                        },
                    });

                    return games;
                } catch (error) {
                    console.error('Erro ao buscar jogos por empresa:', error);
                    throw error;
                    }
                },

                            getGamesBySequence: async (sequenceId, sequenceName) => {
                                try {
                                    const whereClause = {};
                                    if (sequenceId) {
                                        whereClause.sequenceId = sequenceId;
                                    } else if (sequenceName) {
                                        const sequence = await prisma.sequence.findUnique({
                                            where: { name: sequenceName },
                                        });
                                        if (!company) {
                                            throw new Error('Sequence not found');
                                        }
                                        whereClause.sequenceId = sequence.id;
                                    }

                                    const games = await prisma.game.findMany({
                                        where: whereClause,
                                        select: {
                                            id: true,
                                            name: true,
                                            coverImage: true,
                                        },
                                    });

                                    return games;
                                } catch (error) {
                                    console.error('Erro ao buscar jogos por sequencia:', error);
                                    throw error;
                                }

            }

};

module.exports = GameModel;
