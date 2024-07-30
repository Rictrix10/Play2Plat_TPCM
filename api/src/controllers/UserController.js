const UserModel = require('../models/UserModel');
const bcrypt = require('bcrypt');
const validator = require('validator');
const nodemailer = require('nodemailer');

const transporter = nodemailer.createTransport({
  service: 'gmail', // ou outro serviço de email
  auth: {
    user: 'ddkricplay2plat@gmail.com',
    pass: 'PletoPl@to03',
  },
});



const UserController = {
    createUser: async (req, res) => {
        try {
            const { email, password, username, avatar, userTypeId, isDeleted = false } = req.body;

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
            const newUser = await UserModel.createUser(email, hashedPassword, username, avatar, userTypeId, isDeleted);
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
                    isDeleted: user.isDeleted,
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
            const { email, password, username, avatar, userTypeId, isDeleted } = req.body;

                    const currentUser = await UserModel.getUserById(userId);
                        if (!currentUser) {
                            return res.status(404).json({ error: 'Utilizador não encontrado' });
                        }

                        // Verificar se o email ou o username são diferentes dos atuais
                        if (email && email !== currentUser.email) {
                            // Verificar se o novo email já está em uso
                            const existingUserByEmail = await UserModel.findUserByEmail(email);
                            if (!validator.isEmail(email)) {
                                  return res.status(443).json({ error: 'Email inválido' });
                            }
                            if (existingUserByEmail) {
                                return res.status(441).json({ error: 'Email já está em uso' });
                            }
                        }

                        if (username && username !== currentUser.username) {
                            // Verificar se o novo username já está em uso
                            const existingUserByUsername = await UserModel.findUserByUsername(username);
                            if (existingUserByUsername) {
                                return res.status(440).json({ error: 'Nome de utilizador já está em uso' });
                            }
                        }

            const data = {
                email,
                username,
                avatar,
                userTypeId,
                isDeleted
            };

            if (password) {
                const passwordRegex = /^(?=.*[A-Z])(?=.*\d)(?=.*[!@#$%^&*])[A-Za-z\d!@#$%^&*]{8,}$/;
                    if (!passwordRegex.test(password)) {
                        return res.status(442).json({
                            error: 'A password deve ter pelo menos 8 caracteres, incluindo uma letra maiúscula, um número e um caractere especial',
                        });
                    }
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

            if (!user || user.isDeleted) {
                return res.status(441).json({ error: 'Utilizador não existe' });
            }

            const isPasswordValid = await bcrypt.compare(password, user.password);

            if (!isPasswordValid) {
                return res.status(442).json({ error: 'Credenciais inválidas' });
            }

            res.json({ message: 'Login bem-sucedido', user });
        } catch (error) {
            console.error('Erro ao fazer login:', error);
            res.status(500).json({ error: 'Erro ao fazer login' });
        }
    },
        softDeleteUser: async (req, res) => {
            try {
                const userId = parseInt(req.params.id);
                const user = await UserModel.softDeleteUser(userId);

                res.json(user);
            } catch (error) {
                console.error('Erro ao excluir utilizador:', error);
                res.status(500).json({ error: 'Erro ao excluir utilizador' });
            }
        },
            verifyPassword: async (req, res) => {
                try {
                    const userId = parseInt(req.params.id);
                    const { password } = req.body;

                    const storedPassword = await UserModel.getPasswordByUserId(userId);
                    if (!storedPassword) {
                        return res.status(404).json({ error: 'Usuário não encontrado' });
                    }

                    const isPasswordValid = await bcrypt.compare(password, storedPassword);

                    if (!isPasswordValid) {
                        return res.status(400).json({ error: 'Senha incorreta' });
                    }

                    res.json({ message: 'Senha correta' });
                } catch (error) {
                    console.error('Erro ao verificar senha:', error);
                    res.status(500).json({ error: 'Erro ao verificar senha' });
                }
            },

    getUsersByPartialName: async (req, res) => {
                try {
                    const { name } = req.params;
                    if (!name) {
                        return res.status(400).json({ error: 'O parâmetro name é obrigatório' });
                    }

                    const users = await UserModel.getUsersByPartialName(name);
                    res.json(users);
                } catch (error) {
                    res.status(500).json({ error: 'Erro ao buscar users por nome parcial' });
                }
            },

        requestPasswordReset: async (req, res) => {
            try {
                const { email } = req.body;

                const result = await UserModel.createPasswordResetToken(email);
                if (!result) {
                    return res.status(404).json({ error: 'Usuário não encontrado' });
                }

                const resetUrl = `https://your-app.vercel.app/reset-password?token=${result.token}`;

                const mailOptions = {
                    from: 'your-email@gmail.com',
                    to: email,
                    subject: 'Recuperação de Senha',
                    text: `Você solicitou a recuperação de senha. Clique no link para redefinir sua senha: ${resetUrl}`,
                };

                transporter.sendMail(mailOptions, (error, info) => {
                    if (error) {
                        return res.status(500).json({ error: 'Erro ao enviar o email' });
                    }
                    res.status(200).json({ message: 'Email enviado com sucesso' });
                });
            } catch (error) {
                console.error('Erro ao solicitar recuperação de senha:', error);
                res.status(500).json({ error: 'Erro ao solicitar recuperação de senha' });
            }
        },

        resetPassword: async (req, res) => {
            try {
                const { token, newPassword } = req.body;

                const passwordRegex = /^(?=.*[A-Z])(?=.*\d)(?=.*[!@#$%^&*])[A-Za-z\d!@#$%^&*]{8,}$/;
                if (!passwordRegex.test(newPassword)) {
                    return res.status(442).json({
                        error: 'A senha deve ter pelo menos 8 caracteres, incluindo uma letra maiúscula, um número e um caractere especial',
                    });
                }

                const user = await UserModel.resetPassword(token, newPassword);
                if (!user) {
                    return res.status(400).json({ error: 'Token inválido ou expirado' });
                }

                res.status(200).json({ message: 'Senha redefinida com sucesso' });
            } catch (error) {
                console.error('Erro ao redefinir senha:', error);
                res.status(500).json({ error: 'Erro ao redefinir senha' });
            }
        },

};

module.exports = UserController;
