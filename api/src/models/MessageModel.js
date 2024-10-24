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
                },
                orderBy: {
                    id: 'asc' // Ordena as mensagens pelo ID de forma crescente
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
         // Verifica se o id é um número antes de chamar o prisma
         if (typeof id !== 'number') {
             throw new Error('O ID deve ser um número.');
         }

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

     for (const message of messages) {
         let otherUser = null;
         if (message.userOneId !== userId) {
             otherUser = message.userOne;
         } else if (message.userTwoId !== userId) {
             otherUser = message.userTwo;
         }

         if (otherUser && !userMap.has(otherUser.id)) {
             // Verificar se há uma amizade entre userId e otherUser.id
             const isFriend = await prisma.friendRequest.findFirst({
                 where: {
                     OR: [
                         { sentUserId: userId, receivedUserId: otherUser.id, isAccepted: true },
                         { sentUserId: otherUser.id, receivedUserId: userId, isAccepted: true }
                     ]
                 }
             });

             userMap.set(otherUser.id, {
                 id: otherUser.id,
                 username: otherUser.username,
                 avatar: otherUser.avatar,
                 messageId: message.id,
                 isFriend: !!isFriend // Retorna true se a amizade for encontrada, false caso contrário
             });
         }
     }

     // Passo 3: Buscar todos os amigos do usuário, mesmo sem mensagens
     const friends = await prisma.friendRequest.findMany({
         where: {
             isAccepted: true,
             OR: [
                 { sentUserId: userId },
                 { receivedUserId: userId }
             ]
         },
         include: {
             sentUser: {
                 select: {
                     id: true,
                     username: true,
                     avatar: true
                 }
             },
             receivedUser: {
                 select: {
                     id: true,
                     username: true,
                     avatar: true
                 }
             }
         }
     });

     // Passo 4: Adicionar os amigos que ainda não estão no userMap
     for (const friend of friends) {
         const friendId = friend.sentUserId === userId ? friend.receivedUser.id : friend.sentUser.id;
         const friendData = friend.sentUserId === userId ? friend.receivedUser : friend.sentUser;

         if (!userMap.has(friendId)) {
             userMap.set(friendId, {
                 id: friendData.id,
                 username: friendData.username,
                 avatar: friendData.avatar,
                 isFriend: true,
                 messageId: null // Usado para identificar que não há mensagem associada
             });
         }
     }

     // Passo 5: Converter o mapa para um array e ordenar pelo ID da mensagem (os amigos sem mensagens ficam no fim)
     const users = Array.from(userMap.values()).sort((a, b) => {
         // Colocar aqueles com mensagens primeiro (messageId não nulo) e ordenar pelo ID da mensagem
         if (a.messageId && !b.messageId) return -1;
         if (!a.messageId && b.messageId) return 1;
         return b.messageId - a.messageId;
     });

     // Remover o campo messageId antes de retornar
     return users.map(user => ({
         id: user.id,
         username: user.username,
         avatar: user.avatar,
         isFriend: user.isFriend
     }));
 },






                getMessagesByUsersWithUserDetails: async (userOneId, userTwoId) => {
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
                        },
                        include: {
                            userOne: {
                                select: {
                                    id: true,
                                    username: true,
                                    avatar: true,
                                    isDeleted: true
                                }
                            },
                            userTwo: {
                                select: {
                                    id: true,
                                    username: true,
                                    avatar: true,
                                    isDeleted: true
                                }
                            }
                        }
                    });
                },

    };

module.exports = MessageModel;

