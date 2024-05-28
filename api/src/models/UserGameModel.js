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
            }
        });
    },
    getUserGamesByGameId: async (gameId) => {
        return await prisma.userGame.findMany({
            where: {
                gameId: gameId,
            }
        });
    }
getUserGamesByUserId: async (userId) => {
        return await prisma.userGame.findMany({
            where: {
                userId: userId,
            }
        });
    },
};

module.exports = UserGameModel;
