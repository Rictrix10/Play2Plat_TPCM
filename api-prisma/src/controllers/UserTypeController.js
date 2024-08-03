const UserTypeModel = require('../models/UserTypeModel');

const UserTypeController = {
    createUserType: async (req, res) => {
        try {
            const { name } = req.body;
            const newUserType = await UserTypeModel.createUserType(name);
            res.status(201).json(newUserType);
        } catch (error) {
            console.error('Erro ao criar tipo de utilizador:', error);
            res.status(500).json({ error: 'Erro ao criar o tipo de utilizador' });
        }
    },
        getUserTypes: async (req, res) => {
            try {
                const usertypes = await UserTypeModel.getUserTypes();
                res.json(usertypes);
            } catch (error) {
                console.error('Erro ao buscar tipos de utilizadores:', error);
                res.status(500).json({ error: 'Erro ao buscar tipos de utilizadores' });
            }
        },
};

module.exports = UserTypeController;
