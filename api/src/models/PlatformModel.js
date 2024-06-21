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
            return await prisma.platform.findMany({
               orderBy: { name: 'asc' }
            });
    },
        getRandomPlatformName: async () => {
            const platforms = await prisma.platform.findMany({
                select: {
                    name: true
                },
                where: {
                    platformGame: {
                        some: {} // Only platforms with at least one associated game in platformGames
                    },
                    _count: {
                       games: {
                            gte: 7
                            }
                    }
                }
            });
            if (platforms.length === 0) {
                return null;
            }
            const randomIndex = Math.floor(Math.random() * platforms.length);
            return platforms[randomIndex].name;
        }
};

module.exports = PlatformModel;
