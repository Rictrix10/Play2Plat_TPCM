const UserGameModel = require('../models/UserGameModel');

const UserGameController = {
    createUserGame: async (req, res) => {
        try {
            const { userId, gameId, state } = req.body;


            const newUserGame = await UserGameModel.createUserGame(userId, gameId, state);

            res.status(201).json(newUserGame);
        } catch (error) {
            console.error('Erro ao criar relação usuário-jogo:', error);
            res.status(500).json({ error: 'Erro ao criar relação usuário-jogo' });
        }
    },

    getAllUserGames: async (req, res) => {
        try {
            const userGames = await UserGameModel.getAllUserGames();
            res.json(userGames);
        } catch (error) {
            console.error('Erro ao buscar relações usuário-jogo:', error);
            res.status(500).json({ error: 'Erro ao buscar relações usuário-jogo' });
        }
    },

    getUserGameById: async (req, res) => {
        try {
            const userGameId = parseInt(req.params.id);


            const userGame = await UserGameModel.getUserGameById(userGameId);

            if (userGame) {
                res.json(userGame);
            } else {
                res.status(404).json({ error: 'Relação usuário-jogo não encontrada' });
            }
        } catch (error) {
            console.error('Erro ao buscar relação usuário-jogo por ID:', error);
            res.status(500).json({ error: 'Erro ao buscar relação usuário-jogo' });
        }
    },

    updateUserGameState: async (req, res) => {
        try {
            const userGameId = req.params.id;
            const { newState } = req.body;

            // Atualiza o estado de uma relação específica de usuário com jogo por ID
            const updatedUserGame = await UserGameModel.updateUserGameState(userGameId, newState);

            if (updatedUserGame) {
                res.json(updatedUserGame);
            } else {
                res.status(404).json({ error: 'Relação usuário-jogo não encontrada' });
            }
        } catch (error) {
            console.error('Erro ao atualizar estado da relação usuário-jogo:', error);
            res.status(500).json({ error: 'Erro ao atualizar estado da relação usuário-jogo' });
        }
    },

    deleteUserGame: async (req, res) => {
        try {
            const userGameId = req.params.id;

            // Exclui uma relação específica de usuário com jogo por ID
            const deletedUserGame = await UserGameModel.deleteUserGameById(userGameId);

            if (deletedUserGame) {
                res.status(204).end();
            } else {
                res.status(404).json({ error: 'Relação usuário-jogo não encontrada' });
            }
        } catch (error) {
            console.error('Erro ao excluir relação usuário-jogo:', error);
            res.status(500).json({ error: 'Erro ao excluir relação usuário-jogo' });
        }
    },

    getUserGamesByUserId: async (req, res) => {
        try {
            const userId = parseInt(req.params.userId);
            const userGames = await UserGameModel.getUserGamesByUserId(userId); // Chama o método do modelo para buscar os jogos associados ao usuário
            res.json(userGames);
        } catch (error) {
            console.error('Erro ao buscar jogos do usuário por ID:', error);
            res.status(500).json({ error: 'Erro ao buscar jogos do usuário por ID' });
        }
    },
 deleteUserGameByUserIdAndGameId: async (req, res) => {
        try {
            const userId = parseInt(req.params.userId);
            const gameId = parseInt(req.params.gameId);

            const result = await UserGameModel.deleteUserGameByUserIdAndGameId(userId, gameId);
            if (result.count > 0) {
                res.status(204).end();
            } else {
                res.status(404).json({ error: 'Relação usuário-jogo não encontrada' });
            }
        } catch (error) {
            console.error('Erro ao excluir relação usuário-jogo:', error);
            res.status(500).json({ error: 'Erro ao excluir relação usuário-jogo' });
        }
    },
updateUserGameByUserIdAndGameId: async (req, res) => {
        try {
            const userId = parseInt(req.params.userId);
            const gameId = parseInt(req.params.gameId);
            const data = req.body; // Dados a serem atualizados

            const result = await UserGameModel.updateUserGameByUserIdAndGameId(userId, gameId, data);
            if (result.count > 0) {
                res.json({ message: 'Relação usuário-jogo atualizada com sucesso' });
            } else {
                res.status(404).json({ error: 'Relação usuário-jogo não encontrada' });
            }
        } catch (error) {
            console.error('Erro ao atualizar relação usuário-jogo:', error);
            res.status(500).json({ error: 'Erro ao atualizar relação usuário-jogo' });
        }
    },

getUserGamesByUserIdAndState: async (req, res) => {
    try {
        const userId = parseInt(req.params.userId);
        const state = req.params.state;

        const userGames = await UserGameModel.getUserGamesByUserIdAndState(userId, state);

        const gamesWithState = userGames.map(ug => ({
            state: ug.state,
            ...ug.game
        }));

        res.json(gamesWithState);
    } catch (error) {
        console.error('Erro ao buscar jogos do usuário por ID e estado:', error);
        res.status(500).json({ error: 'Erro ao buscar jogos do usuário por ID e estado' });
    }
},


};

module.exports = UserGameController;


