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

    /*
    getRandomGenreNames: async (count) => {
        const genres = await prisma.genre.findMany({
            select: {
                name: true
            },
            where: {
                gameGenres: {
                    some: {} // Only genres with at least one associated gameGenre
                }
            }
        });

        if (genres.length === 0) {
            return [];
        }

        const shuffledGenres = genres.sort(() => Math.random() - 0.5);
        return shuffledGenres.slice(0, Math.min(count, shuffledGenres.length)).map(genre => genre.name);
    }
    */

            getRandomGenreNames: async () => {
                const genres = await prisma.genre.findMany({
                    select: {
                        id: true,
                        name: true
                    },
                    where: {
                        gameGenres: {
                            some: {} // Only companies with at least one associated game
                        }
                    }
                });

                const genresWithGamesCount = await Promise.all(genres.map(async genre => {
                    const gamesCount = await prisma.gameGenre.count({
                        where: {
                            genreId: genre.id
                        }
                    });
                    return {
                        ...genre,
                        gamesCount
                    };
                }));

                const filteredGenres = genresWithGamesCount.filter(genre => genre.gamesCount >= 7);

                if (filteredGenres.length === 0) {
                    return null;
                }

                const randomIndex = Math.floor(Math.random() * filteredGenres.length);
                return filteredGenres[randomIndex].name;
            }


};

module.exports = GenreModel;
