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
            return await prisma.sequence.findMany({
              orderBy: { name: 'asc' }
            });
    },
    getRandomSequenceName: async () => {
        const sequences = await prisma.sequence.findMany({
            select: {
                name: true
            },
            where: {
                games: {
                    some: {} // Only sequences with at least one associated game
                },
                    _count: {
                        games: {
                           gte: 7
                        }
                    }
            }
        });
        if (sequences.length === 0) {
            return null;
        }
        const randomIndex = Math.floor(Math.random() * sequences.length);
        return sequences[randomIndex].name;
    }
};

module.exports = SequenceModel;
