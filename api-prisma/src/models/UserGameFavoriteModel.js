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
        return await prisma.userGameFavorite.findMany({
            where: {
                userId: userId,
                game: {
                    isDeleted: false
                }
            },
            include: {
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
