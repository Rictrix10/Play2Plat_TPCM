const { PrismaClient } = require('@prisma/client');
const prisma = new PrismaClient();

const SequenceModel = {
    createSequence: async (name) => {
        return await prisma.sequence.create({
            data: {
                name
            }
        });
    },
    getSequences: async () => {
            return await prisma.sequence.findMany();
    }
};

module.exports = SequenceModel;
