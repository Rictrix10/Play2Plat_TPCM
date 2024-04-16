const SequenceModel = require('../models/SequenceModel');

const SequenceController = {
    createSequence: async (req, res) => {
        try {
            const { name } = req.body;
            const newSequence = await SequenceModel.createSequence(name);
            res.status(201).json(newSequence);
        } catch (error) {
            console.error('Erro ao criar sequência:', error);
            res.status(500).json({ error: 'Erro ao criar a sequência' });
        }
    },
        getSequences: async (req, res) => {
            try {
                const sequences = await SequenceModel.getSequences();
                res.json(sequences);
            } catch (error) {
                console.error('Erro ao buscar sequências:', error);
                res.status(500).json({ error: 'Erro ao buscar sequências' });
            }
        },
};

module.exports = SequenceController;
