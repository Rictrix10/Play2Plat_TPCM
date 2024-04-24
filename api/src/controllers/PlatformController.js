const PlatformModel = require('../models/PlatformModel');

const PlatformController = {
    createPlatform: async (req, res) => {
        try {
            const { name } = req.body;
            const newPlatform = await PlatformModel.createPlatform(name);
            res.status(201).json(newPlatform);
        } catch (error) {
            console.error('Erro ao criar plataforma:', error);
            res.status(500).json({ error: 'Erro ao criar a plataforma' });
        }
    },
        getPlatforms: async (req, res) => {
            try {
                const platforms = await PlatformModel.getPlatforms();
                res.json(platforms);
            } catch (error) {
                console.error('Erro ao buscar plataformas:', error);
                res.status(500).json({ error: 'Erro ao buscar plataformas' });
            }
        },
};

module.exports = PlatformController;
