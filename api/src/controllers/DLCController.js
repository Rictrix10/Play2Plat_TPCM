const DLCModel = require('../models/DLCModel');

const DLCController = {
    createDLC: async (req, res) => {
        try {
            const { name, gameId } = req.body;
            const newDLC = await DLCModel.createDLC(name, gameId);
            res.status(201).json(newDLC);
        } catch (error) {
            console.error('Erro ao criar DLC:', error);
            res.status(500).json({ error: 'Erro ao criar a DLC' });
        }
    },
          getDLCs: async (req, res) => {
                try {
                    const dlcs = await DLCModel.getDLCs(); // Certifique-se que o nome da variável está correto aqui
                    res.json(dlcs); // E aqui também
                } catch (error) {
                    console.error('Erro ao buscar DLCs:', error);
                    res.status(500).json({ error: 'Erro ao buscar DLCs' });
                }
            },
        };

module.exports = DLCController;