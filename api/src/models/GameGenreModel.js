const { PrismaClient } = require('@prisma/client');
const prisma = new PrismaClient();

const GameGenreModel = {
    createGameGenre: async (gameId, genreId) => {
        return await prisma.gameGenre.create({
            data: {
                gameId,
                genreId
            }
        });
    },

    getAllGameGenres: async () => {
        return await prisma.gameGenre.findMany();
    },

    getGameGenreById: async (id) => {
        return await prisma.gameGenre.findUnique({
            where: {
                id: id,
            }
        });
    },

    deleteGameGenreById: async (id) => {
        return await prisma.gameGenre.delete({
            where: {
                id: id,
            }
        });
    },

    getGameGenresByGameId: async (gameId) => {
        return await prisma.gameGenre.findMany({
            where: {
                gameId: gameId,
            }
        });
    },

    getGameGenresByGenreId: async (genreId) => {
        return await prisma.gameGenre.findMany({
            where: {
                genreId: genreId,
            }
        });
    },

    getGamesByGenreId: async (genreId) => {
        return await prisma.gameGenre.findMany({
            where: {
                genreId: genreId,
                game: {
                    isDeleted: false
                }
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
    },

        getGamesByGenreName: async (genreName) => {
            return await prisma.gameGenre.findMany({
                where: {
                    genre: {
                        name: genreName
                    },
                    game: {
                        isDeleted: false
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
        },

        deleteGameGenreByGameIdAndGenreId: async (gameId, genreId) => {
            return await prisma.gameGenre.deleteMany({
                where: {
                    gameId: gameId,
                    genreId: genreId,
                }
            });
        },

        const deleteGameGenresByGameId = async (gameId) => {
            return await prisma.gameGenre.deleteMany({
                where: {
                    gameId: gameId,
                },
            });
};

module.exports = GameGenreModel;


