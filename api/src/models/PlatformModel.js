const { PrismaClient } = require('@prisma/client');
const prisma = new PrismaClient();

const PlatformModel = {
    createPlatform: async (name) => {
        return await prisma.platform.create({
            data: {
                name
            }
        });
    },
    getPlatforms: async () => {
            return await prisma.platform.findMany();
    }
};

module.exports = PlatformModel;
