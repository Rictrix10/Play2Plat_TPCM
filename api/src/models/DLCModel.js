const { PrismaClient } = require('@prisma/client');
const prisma = new PrismaClient();

const DLCModel = {
    createDLC: async (name, gameId ) => {
        return await prisma.DLC.create({
            data: {
                name,
                gameId
            }
        });
    },
    getDLCs: async () => {
            return await prisma.DLC.findMany();
    },

};

module.exports = DLCModel;