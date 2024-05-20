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
        }

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
};

module.exports = GameModel;
