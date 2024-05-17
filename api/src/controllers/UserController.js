const UserModel = require('../models/UserModel');
const bcrypt = require('bcrypt');

const UserController = {
    createUser: async (req, res) => {
        try {
            const { email, password, username, avatar, userTypeId } = req.body;

            const hashedPassword = await bcrypt.hash(password, 10);

            const newUser = await UserModel.createUser(email, hashedPassword, username, avatar, userTypeId);
            res.status(201).json(newUser);
        } catch (error) {
            console.error('Erro ao criar utilizador:', error.message);
            res.status(400).json({ error: error.message });
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
    getUserById: async (req, res) => {
        try {
            const userId = parseInt(req.params.id);
            const user = await UserModel.getUserById(userId);

            if (!user) {
                return res.status(404).json({ error: 'Usuário não encontrado' });
            }

            res.json(user);
        } catch (error) {
            console.error('Erro ao buscar utilizador:', error);
            res.status(500).json({ error: 'Erro ao buscar utilizador' });
        }
    },
    updateUser: async (req, res) => {
        try {
            const userId = parseInt(req.params.id); // Converter o ID para Int
            const { email, password, username, avatar, userTypeId } = req.body;

            const data = {
                email,
                username,
                avatar,
                userTypeId
            };

            if (password) {
                data.password = await bcrypt.hash(password, 10);
            }

            const updatedUser = await UserModel.updateUser(userId, data);
            res.json(updatedUser);
        } catch (error) {
            console.error('Erro ao atualizar utilizador:', error);
            res.status(500).json({ error: 'Erro ao atualizar utilizador' });
        }
    },
    deleteUser: async (req, res) => {
        try {
            const userId = parseInt(req.params.id);

            await UserModel.deleteUser(userId);

            res.sendStatus(204);
        } catch (error) {
            console.error('Erro ao excluir utilizador:', error);
            res.status(500).json({ error: 'Erro ao excluir utilizador' });
        }
    },
    loginUser: async (req, res) => {
        try {
            const { email, password } = req.body;
            const user = await UserModel.findUserByEmail(email);

            if (!user) {
                return res.status(401).json({ error: 'Credenciais inválidas' });
            }

            const isPasswordValid = await bcrypt.compare(password, user.password);

            if (!isPasswordValid) {
                return res.status(401).json({ error: 'Credenciais inválidas' });
            }

            res.json({ message: 'Login bem-sucedido', user });
        } catch (error) {
            console.error('Erro ao fazer login:', error);
            res.status(500).json({ error: 'Erro ao fazer login' });
        }
    }
};

module.exports = UserController;
