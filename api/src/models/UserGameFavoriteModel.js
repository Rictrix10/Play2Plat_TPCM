const { PrismaClient } = require('@prisma/client');
const prisma = new PrismaClient();

const UserGameFavoriteModel = {
    createUserGameFavorite: async (userId, gameId) => {

        return await prisma.userGameFavorite.create({
            data: {
                userId,
                gameId
            }
        });
    },

    getAllFavorites: async () => {

        return await prisma.userGameFavorite.findMany();
    },

    getFavoriteById: async (id) => {

        return await prisma.userGameFavorite.findUnique({
            where: {
                id: id,
            }
        });
    },

    deleteFavoriteById: async (id) => {
        return await prisma.userGameFavorite.delete({
            where: {
                id: id,
            }
        });
    },

getFavoritesByUserId: async (userId) => {
    // Primeiro, obtemos todos os favoritos do usuário
    const userGameFavorites = await prisma.userGameFavorite.findMany({
        where: {
            userId: userId,
        },
        include: {
            game: {
                select: {
                    id: true,
                    name: true,
                    coverImage: true,
                    isDeleted: true, // Incluímos isDeleted para filtrar depois
                }
            }
        }
    });

    // Filtramos os jogos que não foram deletados
    const filteredFavorites = userGameFavorites.filter(favorite => !favorite.game.isDeleted);

    // Retornamos os jogos filtrados com apenas os campos necessários
    return filteredFavorites.map(favorite => ({
        id: favorite.game.id,
        name: favorite.game.name,
        coverImage: favorite.game.coverImage
    }));
};



    getFavoritesByGameId: async (gameId) => {

        return await prisma.userGameFavorite.findMany({
            where: {
                gameId: gameId,
            }
        });
    },
    deleteFavoriteByUserIdAndGameId: async (userId, gameId) => {
            return await prisma.userGameFavorite.deleteMany({
                where: {
                    userId: userId,
                    gameId: gameId
                }
            });
        }
};

module.exports = UserGameFavoriteModel;
