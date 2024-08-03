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
    /*
    getRandomSequenceName: async () => {
        const sequences = await prisma.sequence.findMany({
            select: {
                name: true
            },
            where: {
                games: {
                    some: {} // Only sequences with at least one associated game
                }
            }
        });
        if (sequences.length === 0) {
            return null;
        }
        const randomIndex = Math.floor(Math.random() * sequences.length);
        return sequences[randomIndex].name;
    }
    */
        getRandomSequenceName: async () => {
            const sequences = await prisma.sequence.findMany({
                select: {
                    id: true,
                    name: true
                },
                where: {
                    games: {
                        some: {} // Only companies with at least one associated game
                    }
                }
            });

            const sequencesWithGamesCount = await Promise.all(sequences.map(async sequence => {
                const gamesCount = await prisma.game.count({
                    where: {
                        sequenceId: sequence.id
                    }
                });
                return {
                    ...sequence,
                    gamesCount
                };
            }));

            const filteredSequences = sequencesWithGamesCount.filter(sequence => sequence.gamesCount >= 7);

            if (filteredSequences.length === 0) {
                return null;
            }

            const randomIndex = Math.floor(Math.random() * filteredSequences.length);
            return filteredSequences[randomIndex].name;
        }
};

module.exports = SequenceModel;
