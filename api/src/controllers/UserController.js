const UserModel = require('../models/UserModel');
const bcrypt = require('bcrypt');
const validator = require('validator');

const UserController = {
    createUser: async (req, res) => {
        try {
            const { email, password, username, avatar, userTypeId } = req.body;

            // Verificar se o email é válido
            if (!validator.isEmail(email)) {
                return res.status(443).json({ error: 'Email inválido' });
            }

            // Verificar se a password atende aos critérios
            const passwordRegex = /^(?=.*[A-Z])(?=.*\d)(?=.*[!@#$%^&*])[A-Za-z\d!@#$%^&*]{8,}$/;
            if (!passwordRegex.test(password)) {
                return res.status(442).json({
                    error: 'A password deve ter pelo menos 8 caracteres, incluindo uma letra maiúscula, um número e um caractere especial',
                });
            }

            // Verificar se o email já está em uso
            const existingUserByEmail = await UserModel.findUserByEmail(email);
            if (existingUserByEmail) {
                return res.status(441).json({ error: 'Email já está em uso' });
            }

            // Verificar se o nome de utilizador já está em uso
            const existingUserByUsername = await UserModel.findUserByUsername(username);
            if (existingUserByUsername) {
                return res.status(440).json({ error: 'Nome de utilizador já está em uso' });
            }

            // Hash da password
            const hashedPassword = await bcrypt.hash(password, 10);

            // Criar o novo utilizador
            const newUser = await UserModel.createUser(email, hashedPassword, username, avatar, userTypeId);
            res.status(201).json(newUser);
        } catch (error) {
            console.error('Erro ao criar utilizador:', error.message);
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
     getUserById: async (req, res) => {
            try {
                const userId = parseInt(req.params.id);
                const user = await UserModel.getUserById(userId);
                if (!user) {
                    return res.status(404).json({ error: 'Usuário não encontrado' });
                }
                res.json({
                    id: user.id,
                    name: user.name,
                    email: user.email,
                    username: user.username,
                    avatar: user.avatar,
                    userType: user.userType.name,
                    platforms: user.platforms
                });
            } catch (error) {
                console.error('Erro ao buscar usuário por ID:', error);
                res.status(500).json({ error: 'Erro ao buscar usuário por ID' });
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
            const { username, password } = req.body;
            const user = await UserModel.findUserByUsername(username);

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
