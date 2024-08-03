const GameGenreModel = require('../models/GameGenreModel');

const GameGenreController = {
    createGameGenre: async (req, res) => {
        try {
            const { gameId, genreId } = req.body;
            const newGameGenre = await GameGenreModel.createGameGenre(gameId, genreId);
            res.status(201).json(newGameGenre);
        } catch (error) {
            console.error('Erro ao criar relação jogo-gênero:', error);
            res.status(500).json({ error: 'Erro ao criar relação jogo-gênero' });
        }
    },

    getAllGameGenres: async (req, res) => {
        try {
            const gameGenres = await GameGenreModel.getAllGameGenres();
            res.json(gameGenres);
        } catch (error) {
            console.error('Erro ao buscar relações jogo-gênero:', error);
            res.status(500).json({ error: 'Erro ao buscar relações jogo-gênero' });
        }
    },

    getGameGenreById: async (req, res) => {
        try {
            const gameGenreId = parseInt(req.params.gameGenreId);
            const gameGenre = await GameGenreModel.getGameGenreById(gameGenreId);
            if (gameGenre) {
                res.json(gameGenre);
            } else {
                res.status(404).json({ error: 'Relação jogo-gênero não encontrada' });
            }
        } catch (error) {
            console.error('Erro ao buscar relação jogo-gênero por ID:', error);
            res.status(500).json({ error: 'Erro ao buscar relação jogo-gênero' });
        }
    },

    deleteGameGenre: async (req, res) => {
        try {
            const gameGenreId = parseInt(req.params.gameGenreId);
            const deletedGameGenre = await GameGenreModel.deleteGameGenreById(gameGenreId);
            if (deletedGameGenre) {
                res.status(204).end();
            } else {
                res.status(404).json({ error: 'Relação jogo-gênero não encontrada' });
            }
        } catch (error) {
            console.error('Erro ao excluir relação jogo-gênero:', error);
            res.status(500).json({ error: 'Erro ao excluir relação jogo-gênero' });
        }
    },

    getGameGenresByGameId: async (req, res) => {
        try {
            const gameId = parseInt(req.params.gameId);
            const gameGenres = await GameGenreModel.getGameGenresByGameId(gameId);
            res.json(gameGenres);
        } catch (error) {
            console.error('Erro ao buscar relações jogo-gênero por gameId:', error);
            res.status(500).json({ error: 'Erro ao buscar relações jogo-gênero' });
        }
    },

    getGameGenresByGenreId: async (req, res) => {
        try {
            const genreId = parseInt(req.params.genreId);
            const gameGenres = await GameGenreModel.getGameGenresByGenreId(genreId);
            res.json(gameGenres);
        } catch (error) {
            console.error('Erro ao buscar relações jogo-gênero por genreId:', error);
            res.status(500).json({ error: 'Erro ao buscar relações jogo-gênero' });
        }
    },

    getGamesByGenreId: async (req, res) => {
        try {
            const genreId = parseInt(req.params.genreId, 10);
            if (isNaN(genreId)) {
                return res.status(400).json({ error: 'genreId inválido' });
            }

            const genreGames = await GameGenreModel.getGamesByGenreId(genreId);
            const games = genreGames.map(gg => gg.game);

            res.json(games);
        } catch (error) {
            console.error('Erro ao buscar jogos por ID do gênero:', error);
            res.status(500).json({ error: 'Erro ao buscar jogos por ID do gênero' });
        }
    },

                getGamesByGenreName: async (req, res) => {
                    try {
                        const genreName = req.params.genreName;

                        const genreGames = await GameGenreModel.getGamesByGenreName(genreName);
                        const games = genreGames.map(pg => pg.game);

                        res.json(games);
                    } catch (error) {
                        console.error('Erro ao buscar jogos por nome do genero:', error);
                        res.status(500).json({ error: 'Erro ao buscar jogos por nome do genero' });
                    }
                },
     deleteGameGenreByGameIdAndGenreId: async (req, res) => {
            try {
                const gameId = parseInt(req.params.gameId);
                const genreId = parseInt(req.params.genreId);

                const result = await GameGenreModel.deleteGameGenreByGameIdAndGenreId(gameId, genreId);
                if (result.count > 0) {
                    res.status(204).end();
                } else {
                    res.status(404).json({ error: 'Relação genero-jogo não encontrada' });
                }
            } catch (error) {
                console.error('Erro ao excluir relação genero-jogo:', error);
                res.status(500).json({ error: 'Erro ao excluir relação genero-jogo' });
            }
        },

             deleteGameGenreByGameId: async (req, res) => {
                    try {
                        const gameId = parseInt(req.params.gameId);

                        const result = await GameGenreModel.deleteGameGenreByGameId(gameId);
                        if (result.count > 0) {
                            res.status(204).end();
                        } else {
                            res.status(404).json({ error: 'Relação genero-jogo não encontrada' });
                        }
                    } catch (error) {
                        console.error('Erro ao excluir relação genero-jogo:', error);
                        res.status(500).json({ error: 'Erro ao excluir relação genero-jogo' });
                    }
                },


};

module.exports = GameGenreController;


