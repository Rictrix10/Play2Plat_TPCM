const UserModel = require('../models/userModel');

const UserController = {
    createUser: async (req, res) => {
        try {
            const { name, email, password, username, avatar } = req.body;
            const newUser = await UserModel.createUser(name, email, password, username, avatar);
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
