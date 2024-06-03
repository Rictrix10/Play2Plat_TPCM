const { PrismaClient } = require('@prisma/client');
const prisma = new PrismaClient();

const GenreModel = {
    createGenre: async (name) => {
        return await prisma.genre.create({
            data: {
                name
            }
        });
    },
    getGenres: async () => {
            return await prisma.genre.findMany();
    },
        getRandomGenreName: async () => {
            const genres = await prisma.genre.findMany({
                select: {
                    name: true
                }
            });
            if (genres.length === 0) {
                return null;
            }
            const randomIndex = Math.floor(Math.random() * genres.length);
            return genres[randomIndex].name;
        }
};

module.exports = GenreModel;
