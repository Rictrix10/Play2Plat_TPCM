const { PrismaClient } = require('@prisma/client');
const prisma = new PrismaClient();

const UserPlatformModel = {
    createUserPlatform: async (userId, platformId) => {
        // Cria uma nova relação entre um usuário e uma plataforma
        return await prisma.userPlatform.create({
            data: {
                userId,
                platformId
            }
        });
    },

    getAllUserPlatforms: async () => {
        // Retorna todas as relações entre usuários e plataformas
        return await prisma.userPlatform.findMany();
    },

    getUserPlatformById: async (id) => {
        // Retorna uma relação específica entre um usuário e uma plataforma por ID
        return await prisma.userPlatform.findUnique({
            where: {
                id: id,
            }
        });
    },

    deleteUserPlatformById: async (id) => {
        // Exclui uma relação específica entre um usuário e uma plataforma por ID
        return await prisma.userPlatform.delete({
            where: {
                id: id,
            }
        });
    },

    getUserPlatformsByUserId: async (userId) => {
        // Retorna todas as relações de um usuário específico com plataformas
        return await prisma.userPlatform.findMany({
            where: {
                userId: userId,
            }
        });
    },

    getUserPlatformsByPlatformId: async (platformId) => {
        // Retorna todas as relações de uma plataforma específica com usuários
        return await prisma.userPlatform.findMany({
            where: {
                platformId: platformId,
            }
        });
    },

    deleteUserPlatformByUserIdAndPlatformId: async (userId, platformId) => {
            return await prisma.userPlatform.deleteMany({
                where: {
                    userId: userId,
                    platformId: platformId
                }
            });
        }
};

module.exports = UserPlatformModel;
