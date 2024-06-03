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
            }
};

module.exports = GenreController;
