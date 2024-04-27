const { PrismaClient } = require('@prisma/client');
const prisma = new PrismaClient();

const GameGenreModel = {
    createGameGenre: async (gameId, genreId) => {
        // Cria uma nova relação entre um jogo e um gênero
        return await prisma.gameGenre.create({
            data: {
                gameId,
                genreId
            }
        });
    },

    getAllGameGenres: async () => {
        // Retorna todas as relações entre jogos e gêneros
        return await prisma.gameGenre.findMany();
    },

    getGameGenreById: async (id) => {
        // Retorna uma relação específica entre um jogo e um gênero por ID
        return await prisma.gameGenre.findUnique({
            where: {
                id: id,
            }
        });
    },

    deleteGameGenreById: async (id) => {
        // Exclui uma relação específica entre um jogo e um gênero por ID
        return await prisma.gameGenre.delete({
            where: {
                id: id,
            }
        });
    },

    getGameGenresByGameId: async (gameId) => {
        // Retorna todas as relações de um jogo específico com gêneros
        return await prisma.gameGenre.findMany({
            where: {
                gameId: gameId,
            }
        });
    },

    getGameGenresByGenreId: async (genreId) => {
        // Retorna todas as relações de um gênero específico com jogos
        return await prisma.gameGenre.findMany({
            where: {
                genreId: genreId,
            }
        });
    }
};

module.exports = GameGenreModel;
