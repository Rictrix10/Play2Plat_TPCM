const { PrismaClient } = require('@prisma/client');
const prisma = new PrismaClient();

const DLCModel = {
    createDLC: async (name, gameId ) => {
        return await prisma.dlc.create({
            data: {
                name,
                gameId
            }
        });
    },
    getDLCs: async () => {
            return await prisma.dlc.findMany();
    },

};

module.exports = DLCModel;