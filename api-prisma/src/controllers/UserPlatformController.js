const UserPlatformModel = require('../models/UserPlatformModel');

const UserPlatformController = {
    createUserPlatform: async (req, res) => {
        try {
            const { userId, platformId } = req.body;

            // Cria uma nova relação entre um usuário e uma plataforma
            const newUserPlatform = await UserPlatformModel.createUserPlatform(userId, platformId);

            res.status(201).json(newUserPlatform);
        } catch (error) {
            console.error('Erro ao criar relação usuário-plataforma:', error);
            res.status(500).json({ error: 'Erro ao criar relação usuário-plataforma' });
        }
    },

    getAllUserPlatforms: async (req, res) => {
        try {
            const userPlatforms = await UserPlatformModel.getAllUserPlatforms();
            res.json(userPlatforms);
        } catch (error) {
            console.error('Erro ao buscar relações usuário-plataforma:', error);
            res.status(500).json({ error: 'Erro ao buscar relações usuário-plataforma' });
        }
    },

    getUserPlatformById: async (req, res) => {
        try {
            const userPlatformId = req.params.id;

            // Busca uma relação específica entre um usuário e uma plataforma por ID
            const userPlatform = await UserPlatformModel.getUserPlatformById(userPlatformId);

            if (userPlatform) {
                res.json(userPlatform);
            } else {
                res.status(404).json({ error: 'Relação usuário-plataforma não encontrada' });
            }
        } catch (error) {
            console.error('Erro ao buscar relação usuário-plataforma por ID:', error);
            res.status(500).json({ error: 'Erro ao buscar relação usuário-plataforma' });
        }
    },

    deleteUserPlatform: async (req, res) => {
        try {
            const userPlatformId = req.params.id;

            // Exclui uma relação específica entre um usuário e uma plataforma por ID
            const deletedUserPlatform = await UserPlatformModel.deleteUserPlatformById(userPlatformId);

            if (deletedUserPlatform) {
                res.status(204).end();
            } else {
                res.status(404).json({ error: 'Relação usuário-plataforma não encontrada' });
            }
        } catch (error) {
            console.error('Erro ao excluir relação usuário-plataforma:', error);
            res.status(500).json({ error: 'Erro ao excluir relação usuário-plataforma' });
        }
    },

    getUserPlatformsByUserId: async (req, res) => {
        try {
            const userId = req.params.userId;

            // Retorna todas as relações de um usuário específico com plataformas
            const userPlatforms = await UserPlatformModel.getUserPlatformsByUserId(userId);

            res.json(userPlatforms);
        } catch (error) {
            console.error('Erro ao buscar relações usuário-plataforma por userId:', error);
            res.status(500).json({ error: 'Erro ao buscar relações usuário-plataforma' });
        }
    },

    getUserPlatformsByPlatformId: async (req, res) => {
        try {
            const platformId = req.params.platformId;

            // Retorna todas as relações de uma plataforma específica com usuários
            const userPlatforms = await UserPlatformModel.getUserPlatformsByPlatformId(platformId);

            res.json(userPlatforms);
        } catch (error) {
            console.error('Erro ao buscar relações usuário-plataforma por platformId:', error);
            res.status(500).json({ error: 'Erro ao buscar relações usuário-plataforma' });
        }
    },

     deleteUserPlatformByUserIdAndPlatformId: async (req, res) => {
            try {
                const userId = parseInt(req.params.userId);
                const platformId = parseInt(req.params.platformId);

                const result = await UserPlatformModel.deleteUserPlatformByUserIdAndPlatformId(userId, platformId);
                if (result.count > 0) {
                    res.status(204).end();
                } else {
                    res.status(404).json({ error: 'Relação utilizador-plataforma não encontrada' });
                }
            } catch (error) {
                console.error('Erro ao excluir relação utilizador-plataforma:', error);
                res.status(500).json({ error: 'Erro ao excluir relação utilizador-plataforma' });
            }
        }
};

module.exports = UserPlatformController;
