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
                // Passo 1: Obter mensagens com usuários associados ordenadas por ID da mensagem decrescente
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

                // Passo 2: Armazenar os usuários únicos com o ID da mensagem mais recente
                const userMap = new Map();

                messages.forEach(message => {
                    if (message.userOneId !== userId && !userMap.has(message.userOneId)) {
                        userMap.set(message.userOneId, {
                            id: message.userOne.id,
                            username: message.userOne.username,
                            avatar: message.userOne.avatar,
                            messageId: message.id
                        });
                    }
                    if (message.userTwoId !== userId && !userMap.has(message.userTwoId)) {
                        userMap.set(message.userTwoId, {
                            id: message.userTwo.id,
                            username: message.userTwo.username,
                            avatar: message.userTwo.avatar,
                            messageId: message.id
                        });
                    }
                });

                // Passo 3: Converter o mapa para um array e ordenar pelo ID da mensagem
                const users = Array.from(userMap.values()).sort((a, b) => b.messageId - a.messageId);

                // Remover o campo messageId antes de retornar
                return users.map(user => ({
                    id: user.id,
                    username: user.username,
                    avatar: user.avatar
                }));
            }

    };

module.exports = MessageModel;

