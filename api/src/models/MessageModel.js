const { PrismaClient } = require('@prisma/client');
const prisma = new PrismaClient();

const MessageModel = {
    createMessage: async (message, image, isAnswer, userOneId, userTwoId, date) => {
        return await prisma.message.create({
            data: {
                message,
                image,
                isAnswer,
                userOneId,
                userTwoId,
                date
            }
        });
    },

    getAllMessages: async () => {
        return await prisma.message.findMany();
    },

    getMessageById: async (id) => {
        return await prisma.message.findUnique({
            where: {
                id: id,
            }
        });
    },



        getMessagesByUserId: async (userId) => {
            return await prisma.message.findMany({
                where: {
                    OR: [
                        { userOneId: userId },
                        { userTwoId: userId }
                    ]
                },
                include: {
                    userOne: {
                        select: {
                            id: true,
                            username: true,
                            avatar: true
                        }
                    },
                    userTwo: {
                        select: {
                            id: true,
                            username: true,
                            avatar: true
                        }
                    }
                }
            });
        },

        getMessagesByUsers: async (userOneId, userTwoId) => {
            return await prisma.message.findMany({
                where: {
                    OR: [
                        {
                            AND: [
                                { userOneId: userOneId },
                                { userTwoId: userTwoId }
                            ]
                        },
                        {
                            AND: [
                                { userOneId: userTwoId },
                                { userTwoId: userOneId }
                            ]
                        }
                    ]
                }
            });
        },


    getResponsesByMessageId: async (messageId) => {
        return await prisma.message.findMany({
            where: {
                isAnswer: messageId
            },
            orderBy: {
                id: 'asc' // Ordena pelos mais antigas
            },
                        include: {
                            userOne: {
                                select: {
                                    id: true,
                                    username: true,
                                    avatar: true
                                }
                            },
                            userTwo: {
                                select: {
                                    id: true,
                                    username: true,
                                    avatar: true
                                }
                            }
                        }
        });
    },

     updateMessageById: async (id, message, image, isAnswer, date) => {
             return await prisma.message.update({
                 where: { id: id },
                 data: {
                     message: message,
                     image: image,
                     isAnswer: isAnswer,
                     date: date
                 }
             });
         },


    deleteMessageById: async (id) => {
        // Função auxiliar para deletar em cascata
        const deleteCascade = async (messageId) => {
            // Encontre todos as mensagens que têm esta mensagem como resposta
            const replies = await prisma.message.findMany({
                where: {
                    isAnswer: messageId
                }
            });

            // Recursivamente delete todas as respostas
            for (const reply of replies) {
                await deleteCascade(reply.id);
            }

            // Delete o comentário atual
            await prisma.message.delete({
                where: { id: messageId }
            });
        };

        // Inicie a exclusão em cascata a partir da mensagem original
        return await deleteCascade(id);
    },



    // ENDPOINT DE LISTAR USERS QUE SE ENVIOU MENSAGENS

        getUsersByMessageId: async (userId) => {
            const messages = await prisma.message.findMany({
                where: {
                    OR: [
                        { userOneId: userId },
                        { userTwoId: userId }
                    ]
                },
                orderBy: {
                    id: 'desc'
                },
                include: {
                    userOne: {
                        select: {
                            id: true,
                            username: true,
                            avatar: true
                        }
                    },
                    userTwo: {
                        select: {
                            id: true,
                            username: true,
                            avatar: true
                        }
                    }
                }
            });

            const userIds = new Set();

            // Adiciona usuários ao conjunto para evitar duplicados
            messages.forEach(message => {
                if (message.userOneId !== userId && !userIds.has(message.userOneId)) {
                    userIds.add(message.userOneId);
                }
                if (message.userTwoId !== userId && !userIds.has(message.userTwoId)) {
                    userIds.add(message.userTwoId);
                }
            });

            // Converte o conjunto em array e busca detalhes dos usuários
            const users = await prisma.user.findMany({
                where: {
                    id: {
                        in: Array.from(userIds)
                    }
                },
                select: {
                    id: true,
                    username: true,
                    avatar: true
                },
                orderBy: {
                    id: 'desc'
                }
            });

            return users;
        }

    };

module.exports = UserGameCommentsModel;

