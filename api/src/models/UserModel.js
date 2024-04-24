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
};

module.exports = UserModel;
