const DLCModel = require('../models/DLCModel');

const DLCController = {
    createDLC: async (req, res) => {
        try {
            const { name, gameId } = req.body;
            const newDLC = await DLCModel.createDLC(name);
            res.status(201).json(newDLC);
        } catch (error) {
            console.error('Erro ao criar DLC:', error);
            res.status(500).json({ error: 'Erro ao criar a DLC' });
        }
    },
        getDLCs: async (req, res) => {
            try {
                const DLCs = await DLCModel.getDLCs();
                res.json(DLCs);
            } catch (error) {
                console.error('Erro ao buscar DLCs:', error);
                res.status(500).json({ error: 'Erro ao buscar DLCs' });
            }
        },
};

module.exports = DLCController;