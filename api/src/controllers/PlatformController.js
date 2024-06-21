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
    getRandomPlatformName: async (req, res) => {
        try {
            const platformName = await PlatformModel.getRandomPlatformName();
            if (!platformName) {
                res.status(404).json({ error: 'Nenhuma plataforma encontrada' });
            } else {
                res.json({ name: platformName });
            }
        } catch (error) {
            console.error('Erro ao buscar nome de plataforma aleatório:', error);
            res.status(500).json({ error: 'Erro ao buscar nome de plataforma aleatório' });
        }
    }
};

module.exports = PlatformController;
