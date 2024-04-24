const UserModel = require('../models/UserModel');

const UserController = {
    createUser: async (req, res) => {
        try {
            const { email, password, username, avatar, userTypeId  } = req.body;
            const newUser = await UserModel.createUser(email, password, username, avatar, userTypeId );
            res.status(201).json(newUser);
        } catch (error) {
            console.error('Erro ao criar utilizador:', error);
            res.status(500).json({ error: 'Erro ao criar utilizador' });
        }
    },
        getUsers: async (req, res) => {
            try {
                const users = await UserModel.getUsers();
                res.json(users);
            } catch (error) {
                console.error('Erro ao buscar utilizadores:', error);
                res.status(500).json({ error: 'Erro ao buscar utilizadores' });
            }
        },
};

module.exports = UserController;
