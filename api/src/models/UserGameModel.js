const { PrismaClient } = require('@prisma/client');
const prisma = new PrismaClient();

const UserGameModel = {
    createUserGame: async (userId, gameId, state) => {
        // Cria uma nova relação entre usuário e jogo
        return await prisma.userGame.create({
            data: {
                userId,
                gameId,
                state,
            }
        });
    },

    getAllUserGames: async () => {

        return await prisma.userGame.findMany();
    },

    getUserGameById: async (id) => {

        return await prisma.userGame.findUnique({
            where: {
                id: id,
            }
        });
    },

    updateUserGameState: async (id, newState) => {

        return await prisma.userGame.update({
            where: {
                id: id,
            },
            data: {
                state: newState,
            }
        });
    },

    deleteUserGameById: async (id) => {

        return await prisma.userGame.delete({
            where: {
                id: id,
            }
        });
    },
    getUserGamesByUserId: async (userId) => {
        return await prisma.userGame.findMany({
            where: {
                userId: userId,
                game:{
                    isDeleted: false
                }
            },
        });
    },
    getUserGamesByGameId: async (gameId) => {
        return await prisma.userGame.findMany({
            where: {
                gameId: gameId,
            }
        });
    },
deleteUserGameByUserIdAndGameId: async (userId, gameId) => {
        return await prisma.userGame.deleteMany({
            where: {
                userId: userId,
                gameId: gameId
            }
        });
    },
updateUserGameByUserIdAndGameId: async (userId, gameId, data) => {
        return await prisma.userGame.updateMany({
            where: {
                userId: userId,
                gameId: gameId
            },
            data: data
        });
    },

getUserGamesByUserIdAndState: async (userId, state) => {
    return await prisma.userGame.findMany({
        where: {
            userId: userId,
            state: state,
            game:{
                isDeleted: false
            }
        },
        select: {
            state: true,
            game: {
                select: {
                    id: true,
                    name: true,
                    isFree: true,
                    coverImage: true
                }
            }
        }
    });
},

    getUserGameByUserIdAndGameId: async (userId, gameId) => {
        return await prisma.userGame.findUnique({
            where: {
                userId_gameId: {
                    userId: userId,
                    gameId: gameId
                }
            }
        });
    },


};

module.exports = UserGameModel;
