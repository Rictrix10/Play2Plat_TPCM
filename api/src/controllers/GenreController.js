const GenreModel = require('../models/GenreModel');

const GenreController = {
    createGenre: async (req, res) => {
        try {
            const { name } = req.body;
            const newGenre = await GenreModel.createGenre(name);
            res.status(201).json(newGenre);
        } catch (error) {
            console.error('Erro ao criar género:', error);
            res.status(500).json({ error: 'Erro ao criar o género' });
        }
    },
        getGenres: async (req, res) => {
            try {
                const genres = await GenreModel.getGenres();
                res.json(genres);
            } catch (error) {
                console.error('Erro ao buscar géneros:', error);
                res.status(500).json({ error: 'Erro ao buscar géneros' });
            }
        },
            getRandomGenreName: async (req, res) => {
                try {
                    const genreName = await GenreModel.getRandomGenreName();
                    if (!genreName) {
                        res.status(404).json({ error: 'Nenhum género encontrado' });
                    } else {
                        res.json({ name: genreName });
                    }
                } catch (error) {
                    console.error('Erro ao buscar nome de género aleatório:', error);
                    res.status(500).json({ error: 'Erro ao buscar nome de género aleatório' });
                }
            },


                getRandomGenreNameExcluding: async (req, res) => {
                    try {
                        const { excludeNames } = req.query;
                        const excludeArray = excludeNames ? excludeNames.split(',') : [];
                        const genreName = await GenreModel.getRandomGenreNameExcluding(excludeArray);
                        if (!genreName) {
                            res.status(404).json({ error: 'Nenhum género encontrado' });
                        } else {
                            res.json({ name: genreName });
                        }
                    } catch (error) {
                        console.error('Erro ao buscar nome de género aleatório com exclusões:', error);
                        res.status(500).json({ error: 'Erro ao buscar nome de género aleatório com exclusões' });
                    }
                },

                getRandomGenreNameExcludingUrl: async (req, res) => {
                    try {
                        const { excludeName1, excludeName2 } = req.params;
                        const excludeArray = [];

                        if (excludeName1 && excludeName1.toLowerCase() !== 'null') {
                            excludeArray.push(excludeName1);
                        }

                        if (excludeName2 && excludeName2.toLowerCase() !== 'null') {
                            excludeArray.push(excludeName2);
                        }

                        const genreName = await GenreModel.getRandomGenreNameExcludingUrl(excludeArray);

                        if (!genreName) {
                            res.status(404).json({ error: 'Nenhum gênero encontrado' });
                        } else {
                            res.json({ name: genreName });
                        }
                    } catch (error) {
                        console.error('Erro ao buscar nome de gênero aleatório com exclusões:', error);
                        res.status(500).json({ error: 'Erro ao buscar nome de gênero aleatório com exclusões' });
                    }
                },

            getRandomGenreNames: async (req, res) => {
                try {
                    let { count } = req.query;
                    count = parseInt(count) || 3; // Padrão para retornar até 3 nomes

                    const genreNames = await GenreModel.getRandomGenreNames(count);

                    if (genreNames.length === 0) {
                        res.status(404).json({ error: 'Nenhum gênero encontrado' });
                    } else {
                        res.json({ names: genreNames });
                    }
                } catch (error) {
                    console.error('Erro ao buscar nomes de gêneros aleatórios:', error);
                    res.status(500).json({ error: 'Erro ao buscar nomes de gêneros aleatórios' });
                }
            },


            };

module.exports = GenreController;
