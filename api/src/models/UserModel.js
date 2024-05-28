const { PrismaClient } = require('@prisma/client');
const prisma = new PrismaClient();

const UserModel = {
    createUser: async (email, password, username, avatar, userTypeId) => {
        return await prisma.user.create({
            data: {
                email,
                password,
                username,
                avatar,
                userTypeId
            }
        });
    },
    getUsers: async () => {
        return await prisma.user.findMany();
    },
    getUserById: async (id) => {
            return await prisma.user.findUnique({
                where: { id },

            });
        },

        getPlatformsByUserId: async (userId) => {
            try {
                const userPlatforms = await prisma.userPlatform.findMany({
                    where: {
                        userId: userId
                    },
                    include: {
                        platform: true
                    }
                });
                const platforms = userPlatforms.map(userPlatform => userPlatform.platform.name);
                return platforms;
            } catch (error) {
                console.error('Erro ao buscar plataformas por ID de usuário:', error);
                throw error;
            }
        },
    updateUser: async (id, data) => {
        return await prisma.user.update({
            where: { id },
            data,
        });
    },
    deleteUser: async (id) => {
        return await prisma.user.delete({
            where: { id },
        });
    },
    findUserByEmail: async (email) => {
        return await prisma.user.findUnique({
            where: { email }
        });
    },
    findUserByUsername: async (username) => {
        return await prisma.user.findUnique({
            where: { username }
        });
    }
};

module.exports = UserModel;
