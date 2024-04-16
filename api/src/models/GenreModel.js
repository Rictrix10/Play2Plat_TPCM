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
    }
};

module.exports = GenreModel;
