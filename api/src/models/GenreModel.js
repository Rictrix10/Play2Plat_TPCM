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

    getRandomGenreNames: async (count, genreName) => {
        try {
            // Encontre o gênero pelo nome
            const genre = await prisma.genre.findUnique({
                where: {
                    name: genreName
                },
                select: {
                    id: true
                }
            });

            if (!genre) {
                return [];
            }

            // Obtenha todos os gêneros que têm pelo menos 3 jogos associados e onde genreId corresponde ao id encontrado
            const genresWithCounts = await prisma.genre.findMany({
                where: {
                    gameGenres: {
                        some: {
                            genreId: genre.id
                        }
                    }
                },
                select: {
                    name: true,
                    _count: {
                        select: { gameGenres: true }
                    }
                }
            });

            // Filtre os gêneros que têm pelo menos 3 jogos associados
            const validGenres = genresWithCounts.filter(genre => genre._count.gameGenres >= 3);

            if (validGenres.length === 0) {
                return [];
            }

            // Embaralhe os gêneros e selecione o número desejado
            const shuffledGenres = validGenres.sort(() => Math.random() - 0.5);
            const selectedGenres = shuffledGenres.slice(0, Math.min(count, shuffledGenres.length)).map(genre => genre.name);

            return selectedGenres;
        } catch (error) {
            console.error('Erro ao buscar nomes de gêneros aleatórios:', error);
            throw error;
        }
    }
};

module.exports = GenreModel;
