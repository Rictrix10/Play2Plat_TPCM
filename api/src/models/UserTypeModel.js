const { PrismaClient } = require('@prisma/client');
const prisma = new PrismaClient();

const UserTypeModel = {
    createUserType: async (name) => {
        return await prisma.userType.create({
            data: {
                name
            }
        });
    },
    getUserTypes: async () => {
            return await prisma.userType.findMany();
    }
};

module.exports = UserTypeModel;
