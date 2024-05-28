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
            try {
                const user = await prisma.user.findUnique({
                    where: { id },
                    include: {
                        userType: true,
                        platforms: true,
                    },
                });
                return user;
            } catch (error) {
                console.error('Erro ao buscar usuário por ID:', error);
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
