const { PrismaClient } = require('@prisma/client');
const prisma = new PrismaClient();

const UserModel = {
    createUser: async (email, password, username, avatar, userTypeId, isDeleted = false) => {
        return await prisma.user.create({
            data: {
                email,
                password,
                username,
                avatar,
                userTypeId,
                isDeleted
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
                    },
                });
                if (!user) {
                    return null;
                }
                const platforms = await UserModel.getPlatformsByUserId(id);
                user.platforms = platforms;
                return user;
            } catch (error) {
                console.error('Erro ao buscar usuário por ID:', error);
                throw error;
            }
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
    },

    softDeleteUser: async (id) => {
        try {
            await prisma.$transaction([
                prisma.userGame.deleteMany({
                    where: { userId: id },
                }),
                prisma.avaliation.deleteMany({
                    where: { userId: id },
                }),
                prisma.userGameFavorite.deleteMany({
                    where: { userId: id },
                }),
                prisma.user.update({
                    where: { id },
                    data: {
                        username: "eliminated",
                        email: "eliminated",
                        password: "eliminated",
                        avatar: "eliminated",
                        isDeleted: true
                    }
                }),
            ]);
            return await prisma.user.findUnique({ where: { id } });
        } catch (error) {
            console.error('Erro ao excluir usuário:', error);
            throw error;
        }
    },

            getPasswordByUserId: async (id) => {
                try {
                    const user = await prisma.user.findUnique({
                        where: { id },
                        select: { password: true }
                    });
                    return user ? user.password : null;
                } catch (error) {
                    console.error('Erro ao buscar senha por ID de usuário:', error);
                    throw error;
                }
            },
};

module.exports = UserModel;
