const PlatformGameModel = require('../models/PlatformGameModel');

const PlatformGameController = {
    createPlatformGame: async (req, res) => {
        try {
            const { platformId, gameId } = req.body;

            // Cria uma nova relação entre uma plataforma e um jogo
            const newPlatformGame = await PlatformGameModel.createPlatformGame(platformId, gameId);

            res.status(201).json(newPlatformGame);
        } catch (error) {
            console.error('Erro ao criar relação plataforma-jogo:', error);
            res.status(500).json({ error: 'Erro ao criar relação plataforma-jogo' });
        }
    },

    getAllPlatformGames: async (req, res) => {
        try {
            const platformGames = await PlatformGameModel.getAllPlatformGames();
            res.json(platformGames);
        } catch (error) {
            console.error('Erro ao buscar relações plataforma-jogo:', error);
            res.status(500).json({ error: 'Erro ao buscar relações plataforma-jogo' });
        }
    },

    getPlatformGameById: async (req, res) => {
        try {
            const platformGameId = req.params.id;

            // Busca uma relação específica entre uma plataforma e um jogo por ID
            const platformGame = await PlatformGameModel.getPlatformGameById(platformGameId);

            if (platformGame) {
                res.json(platformGame);
            } else {
                res.status(404).json({ error: 'Relação plataforma-jogo não encontrada' });
            }
        } catch (error) {
            console.error('Erro ao buscar relação plataforma-jogo por ID:', error);
            res.status(500).json({ error: 'Erro ao buscar relação plataforma-jogo' });
        }
    },

    deletePlatformGame: async (req, res) => {
        try {
            const platformGameId = req.params.id;

            // Exclui uma relação específica entre uma plataforma e um jogo por ID
            const deletedPlatformGame = await PlatformGameModel.deletePlatformGameById(platformGameId);

            if (deletedPlatformGame) {
                res.status(204).end();
            } else {
                res.status(404).json({ error: 'Relação plataforma-jogo não encontrada' });
            }
        } catch (error) {
            console.error('Erro ao excluir relação plataforma-jogo:', error);
            res.status(500).json({ error: 'Erro ao excluir relação plataforma-jogo' });
        }
    },

    getPlatformGamesByPlatformId: async (req, res) => {
        try {
            const platformId = req.params.platformId;

            // Retorna todas as relações de uma plataforma específica com jogos
            const platformGames = await PlatformGameModel.getPlatformGamesByPlatformId(platformId);

            res.json(platformGames);
        } catch (error) {
            console.error('Erro ao buscar relações plataforma-jogo por platformId:', error);
            res.status(500).json({ error: 'Erro ao buscar relações plataforma-jogo' });
        }
    },

    getPlatformGamesByGameId: async (req, res) => {
        try {
            const gameId = req.params.gameId;

            // Retorna todas as relações de um jogo específico com plataformas
            const platformGames = await PlatformGameModel.getPlatformGamesByGameId(gameId);

            res.json(platformGames);
        } catch (error) {
            console.error('Erro ao buscar relações plataforma-jogo por gameId:', error);
            res.status(500).json({ error: 'Erro ao buscar relações plataforma-jogo' });
        }
    }
};

module.exports = PlatformGameController;
