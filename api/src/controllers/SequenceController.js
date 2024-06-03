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
                    getRandomSequenceName: async (req, res) => {
                        try {
                            const sequenceName = await SequenceModel.getRandomSequenceName();
                            if (!sequenceName) {
                                res.status(404).json({ error: 'Nenhuma sequencia encontrada' });
                            } else {
                                res.json({ name: sequenceName });
                            }
                        } catch (error) {
                            console.error('Erro ao buscar nome de sequencia aleatória:', error);
                            res.status(500).json({ error: 'Erro ao buscar nome de sequencia aleatória' });
                        }
                    }
};

module.exports = SequenceController;
