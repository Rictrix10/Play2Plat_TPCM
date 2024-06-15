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
            return await prisma.genre.findMany({
                orderBy: { name: 'asc' }
            });
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
        },

            getRandomGenreNameExcluding: async (excludeNames) => {
                const genres = await prisma.genre.findMany({
                    select: {
                        name: true
                    }
                });
                const filteredGenres = genres.filter(genre => !excludeNames.includes(genre.name));
                if (filteredGenres.length === 0) {
                    return null;
                }
                const randomIndex = Math.floor(Math.random() * filteredGenres.length);
                return filteredGenres[randomIndex].name;
            },

                        getRandomGenreNameExcludingUrl: async (excludeNames) => {
                            const genres = await prisma.genre.findMany({
                                select: {
                                    name: true
                                }
                            });
                            const filteredGenres = genres.filter(genre => !excludeNames.includes(genre.name));
                            if (filteredGenres.length === 0) {
                                return null;
                            }
                            const randomIndex = Math.floor(Math.random() * filteredGenres.length);
                            return filteredGenres[randomIndex].name;
                        },

        getRandomGenreNamesWithGames: async (count) => {
            const genresWithGames = await prisma.genre.findMany({
                where: {
                    gameGenres: {
                        some: {}
                    }
                },
                select: {
                    name: true
                }
            });

            if (genresWithGames.length === 0) {
                return [];
            }

            const shuffledGenres = genresWithGames.sort(() => Math.random() - 0.5);
            return shuffledGenres.slice(0, Math.min(count, shuffledGenres.length)).map(genre => genre.name);
        },


};

module.exports = GenreModel;
