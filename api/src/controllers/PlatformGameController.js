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

getGamesByPlatformName: async (req, res) => {
        try {
            const { platformName } = req.params;
            console.log(`Buscando plataforma com nome: ${platformName}`);
            const platform = await GameModel.getPlatformByName(platformName);

            if (!platform) {
                console.log(`Plataforma não encontrada: ${platformName}`);
                return res.status(404).json({ error: 'Plataforma não encontrada' });
            }

            console.log(`Plataforma encontrada: ${platform.name} (ID: ${platform.id})`);
            const games = await GameModel.getGamesByPlatformId(platform.id);
            res.json(games);
        } catch (error) {
            console.error('Erro ao buscar jogos por nome da plataforma:', error);
            res.status(500).json({ error: 'Erro ao buscar jogos por nome da plataforma' });
        }
    }
};

module.exports = PlatformGameController;
