const { PrismaClient } = require('@prisma/client');
const prisma = new PrismaClient();

const UserModel = {
    createUser: async (name, email, password, username, avatar) => {
        return await prisma.user.create({
            data: {
                name,
                email,
                password,
                username,
                avatar
            }
        });
    },
    getUsers: async () => {
            return await prisma.user.findMany();
    }
};

module.exports = UserModel;
