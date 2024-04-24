const { PrismaClient } = require('@prisma/client');
const prisma = new PrismaClient();

const UserModel = {
    createUser: async (email, password, username, avatar, userTypeId ) => {
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
};

module.exports = UserModel;
