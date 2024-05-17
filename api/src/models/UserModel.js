const { PrismaClient } = require('@prisma/client');
const prisma = new PrismaClient();

const UserModel = {
    createUser: async (email, password, username, avatar, userTypeId) => {
        const existingUserByEmail = await prisma.user.findUnique({
            where: { email }
        });

        if (existingUserByEmail) {
            throw new Error('Email já está em uso');
        }

        const existingUserByUsername = await prisma.user.findUnique({
            where: { username }
        });

        if (existingUserByUsername) {
            throw new Error('Nome de usuário já está em uso');
        }

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
    }
};

module.exports = UserModel;
