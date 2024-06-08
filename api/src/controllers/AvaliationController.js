const AvaliationModel = require('../models/AvaliationModel');

const AvaliationController = {
    createAvaliation: async (req, res) => {
        try {
            const { stars, userId, gameId } = req.body;
            const newAvaliation = await AvaliationModel.createAvaliation(stars, userId, gameId);
            res.status(201).json(newAvaliation);
        } catch (error) {
            console.error('Erro ao criar avaliação:', error);
            res.status(500).json({ error: 'Erro ao criar a avaliação' });
        }
    },
    getAvaliations: async (req, res) => {
        try {
            const avaliations = await AvaliationModel.getAvaliations();
            res.json(avaliations);
        } catch (error) {
            console.error('Erro ao buscar avaliações:', error);
            res.status(500).json({ error: 'Erro ao buscar avaliações' });
        }
    },
    getAvaliationByUserId: async (req, res) => {
        try {
            const userId = parseInt(req.params.userId);
            const avaliations = await AvaliationModel.getAvaliationByUserId(userId);
            res.json(avaliations);
        } catch (error) {
            console.error('Erro ao buscar avaliações por userId:', error);
            res.status(500).json({ error: 'Erro ao buscar avaliações por userId' });
        }
    },
    patchAvaliationByUserIdAndGameId: async (req, res) => {
        try {
            const userId = parseInt(req.params.userId);
            const gameId = parseInt(req.params.gameId);
            const data = req.body;
            const updatedAvaliation = await AvaliationModel.updateAvaliationByUserIdAndGameId(userId, gameId, data);
            if (updatedAvaliation.count > 0) {
                res.json({ message: 'Avaliação atualizada com sucesso' });
            } else {
                res.status(404).json({ error: 'Avaliação não encontrada' });
            }
        } catch (error) {
            console.error('Erro ao atualizar avaliação:', error);
            res.status(500).json({ error: 'Erro ao atualizar avaliação' });
        }
    },
    deleteAvaliationByUserIdAndGameId: async (req, res) => {
        try {
            const userId = parseInt(req.params.userId);
            const gameId = parseInt(req.params.gameId);
            const deletedAvaliation = await AvaliationModel.deleteAvaliationByUserIdAndGameId(userId, gameId);
            if (deletedAvaliation.count > 0) {
                res.status(204).end();
            } else {
                res.status(404).json({ error: 'Avaliação não encontrada' });
            }
        } catch (error) {
            console.error('Erro ao excluir avaliação:', error);
            res.status(500).json({ error: 'Erro ao excluir avaliação' });
        }
    },
    getAverageStarsByGameId: async (req, res) => {
        try {
            const gameId = parseInt(req.params.gameId);
            const averageStars = await AvaliationModel.getAverageStarsByGameId(gameId);
            if (averageStars === null) {
                return res.status(404).json({ error: 'Avaliações não encontradas para este gameId' });
            }
            res.json({ averageStars });
        } catch (error) {
            console.error('Erro ao calcular média de estrelas:', error);
            res.status(500).json({ error: 'Erro ao calcular média de estrelas' });
        }
    },
};

module.exports = AvaliationController;
