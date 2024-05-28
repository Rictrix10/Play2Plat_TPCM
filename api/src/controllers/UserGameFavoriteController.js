const UserGameFavoriteModel = require('../models/UserGameFavoriteModel');

const UserGameFavoriteController = {
    createUserGameFavorite: async (req, res) => {
        try {
            const { userId, gameId } = req.body;

            // Cria uma nova relação de usuário com jogo favorito
            const newFavorite = await UserGameFavoriteModel.createUserGameFavorite(userId, gameId);

            res.status(201).json(newFavorite);
        } catch (error) {
            console.error('Erro ao criar favorito:', error);
            res.status(500).json({ error: 'Erro ao criar o favorito' });
        }
    },

    getAllFavorites: async (req, res) => {
        try {
            const favorites = await UserGameFavoriteModel.getAllFavorites();
            res.json(favorites);
        } catch (error) {
            console.error('Erro ao buscar favoritos:', error);
            res.status(500).json({ error: 'Erro ao buscar favoritos' });
        }
    },

    getFavoriteById: async (req, res) => {
        try {
            const favoriteId = parseInt(req.params.favoriteId);

            // Busca uma relação específica de usuário com jogo favorito por ID
            const favorite = await UserGameFavoriteModel.getFavoriteById(favoriteId);

            if (favorite) {
                res.json(favorite);
            } else {
                res.status(404).json({ error: 'Relação de usuário com jogo favorito não encontrada' });
            }
        } catch (error) {
            console.error('Erro ao buscar favorito por ID:', error);
            res.status(500).json({ error: 'Erro ao buscar favorito' });
        }
    },

    deleteUserGameFavorite: async (req, res) => {
        try {
            const favoriteId = parseInt(req.params.favoriteId);

            // Exclui uma relação específica de usuário com jogo favorito por ID
            const deletedFavorite = await UserGameFavoriteModel.deleteFavoriteById(favoriteId);

            if (deletedFavorite) {
                res.status(204).end();
            } else {
                res.status(404).json({ error: 'Relação de usuário com jogo favorito não encontrada' });
            }
        } catch (error) {
            console.error('Erro ao excluir favorito:', error);
            res.status(500).json({ error: 'Erro ao excluir favorito' });
        }
    },

    getFavoritesByUserId: async (req, res) => {
        try {
            const userId = parseInt(req.params.userId);

            // Retorna todas as relações de um usuário específico com jogos favoritos
            const favorites = await UserGameFavoriteModel.getFavoritesByUserId(userId);

            res.json(favorites);
        } catch (error) {
            console.error('Erro ao buscar favoritos por userId:', error);
            res.status(500).json({ error: 'Erro ao buscar favoritos' });
        }
    },

    getFavoritesByGameId: async (req, res) => {
        try {
            const gameId = parseInt(req.params.gameId);

            // Retorna todas as relações de um jogo específico com usuários favoritos
            const favorites = await UserGameFavoriteModel.getFavoritesByGameId(gameId);

            res.json(favorites);
        } catch (error) {
            console.error('Erro ao buscar favoritos por gameId:', error);
            res.status(500).json({ error: 'Erro ao buscar favoritos' });
        }
    },
    deleteUserGameFavoriteByUserIdAndGameId: async (req, res) => {
        try {
            const userId = parseInt(req.params.userId);
            const gameId = parseInt(req.params.gameId);

            const result = await UserGameFavoriteModel.deleteFavoriteByUserIdAndGameId(userId, gameId);
            if (result.count > 0) {
                res.status(204).end();
            } else {
                res.status(404).json({ error: 'Relação de usuário com jogo favorito não encontrada' });
            }
        } catch (error) {
            console.error('Erro ao excluir favorito:', error);
            res.status(500).json({ error: 'Erro ao excluir favorito' });
        }
    }

};

module.exports = UserGameFavoriteController;
