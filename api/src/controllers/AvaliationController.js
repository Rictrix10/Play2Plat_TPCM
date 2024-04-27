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
                    const avaliations = await AvaliationModel.getAvaliations(); // Certifique-se que o nome da variável está correto aqui
                    res.json(avaliations); // E aqui também
                } catch (error) {
                    console.error('Erro ao buscar avaliações:', error);
                    res.status(500).json({ error: 'Erro ao buscar avaliações' });
                }
            },
        };

module.exports = AvaliationController;