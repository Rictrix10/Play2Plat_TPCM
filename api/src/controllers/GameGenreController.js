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
            const gameGenreId = req.params.id;
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
            const gameGenreId = req.params.id;
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
            const gameId = req.params.gameId;
            const gameGenres = await GameGenreModel.getGameGenresByGameId(gameId);
            res.json(gameGenres);
        } catch (error) {
            console.error('Erro ao buscar relações jogo-gênero por gameId:', error);
            res.status(500).json({ error: 'Erro ao buscar relações jogo-gênero' });
        }
    },

    getGameGenresByGenreId: async (req, res) => {
        try {
            const genreId = req.params.genreId;
            const gameGenres = await GameGenreModel.getGameGenresByGenreId(genreId);
            res.json(gameGenres);
        } catch (error) {
            console.error('Erro ao buscar relações jogo-gênero por genreId:', error);
            res.status(500).json({ error: 'Erro ao buscar relações jogo-gênero' });
        }
    },

    getGamesByGenreId: async (req, res) => {
        try {
            const genreId = req.params.genreId;
            const games = await GameGenreModel.getGamesByGenreId(genreId);
            res.json(games);
        } catch (error) {
            console.error('Erro ao buscar jogos por genreId:', error);
            res.status(500).json({ error: 'Erro ao buscar jogos por genreId' });
        }
    }
};

module.exports = GameGenreController;

