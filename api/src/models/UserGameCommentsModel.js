const { PrismaClient } = require('@prisma/client');
const prisma = new PrismaClient();

const UserGameCommentsModel = {
    createUserGameComment: async (comments, image, isAnswer, userId, gameId, latitude, longitude, location) => {
        return await prisma.userGameComment.create({
            data: {
                comments,
                image,
                isAnswer,
                userId,
                gameId,
                latitude,
                longitude,
                location
            }
        });
    },

    getAllComments: async () => {
        return await prisma.userGameComment.findMany();
    },

    getCommentById: async (id) => {
        return await prisma.userGameComment.findUnique({
            where: {
                id: id,
            }
        });
    },



    getCommentsByGameId: async (gameId) => {
        console.log('gameId:', gameId); // Adicione este log para verificar gameId
        return await prisma.userGameComment.findMany({
            where: {
                gameId: gameId,
            },
            include: {
                user: {
                    select: {
                        id: true,
                        username: true,
                        avatar: true
                    }
                },
                game: {
                    select: {
                        id: true,
                        name: true
                    }
                }
            }
        });
    },

    getGamePostsPreview: async (gameId) => {
        return await prisma.userGameComment.findMany({
            where: {
                gameId: gameId,
                isAnswer: null
            },
            orderBy: {
                id: 'desc' // Ordena pelos mais recentes
            },
            take: 5, // Limita a 5 resultados
            include: {
                user: {
                    select: {
                        id: true,
                        username: true,
                        avatar: true
                    }
                },
                game: {
                    select: {
                        id: true,
                        name: true
                    }
                }
            }
        });
    },

    getPostsByUserIdGameId: async (userId, gameId) => {
        // Obter no máximo dois posts mais recentes do usuário especificado
        const userComments = await prisma.userGameComment.findMany({
            where: {
                userId: userId,
                gameId: gameId,
                isAnswer: null
            },
            orderBy: {
                id: 'desc' // Ordena pelos mais recentes
            },
            take: 2, // Limita a no máximo 2 resultados
            include: {
                user: {
                    select: {
                        id: true,
                        username: true,
                        avatar: true
                    }
                },
                game: {
                    select: {
                        id: true,
                        name: true
                    }
                }
            }
        });

        // Obter os posts restantes, incluindo os do usuário especificado mas excluindo os já listados
        const userCommentIds = userComments.map(comment => comment.id);
        const otherComments = await prisma.userGameComment.findMany({
            where: {
                gameId: gameId,
                isAnswer: null,
                id: {
                    notIn: userCommentIds
                }
            },
            orderBy: {
                id: 'desc' // Ordena pelos mais recentes
            },
            include: {
                user: {
                    select: {
                        id: true,
                        username: true,
                        avatar: true
                    }
                },
                game: {
                    select: {
                        id: true,
                        name: true
                    }
                }
            }
        });

        return [...userComments, ...otherComments];
    },

    getResponsesByPostId: async (postId) => {
        return await prisma.userGameComment.findMany({
            where: {
                isAnswer: postId
            },
            orderBy: {
                id: 'desc' // Ordena pelos mais recentes
            },
            include: {
                user: {
                    select: {
                        id: true,
                        username: true,
                        avatar: true
                    }
                },
                game: {
                    select: {
                        id: true,
                        name: true
                    }
                }
            }
        });
    },

     updateUserGameCommentById: async (id, comments, image, isAnswer, latitude, longitude, location) => {
             return await prisma.userGameComment.update({
                 where: { id: id },
                 data: {
                     comments: comments,
                     image: image,
                     isAnswer: isAnswer,
                     latitude: latitude,
                     longitude: longitude,
                     location: location
                 }
             });
         },

/*
        deleteUserGameCommentById: async (id) => {
                return await prisma.userGameComment.delete({
                    where: { id: id }
                });
            }
        };
        */
    deleteUserGameCommentById: async (id) => {
        // Função auxiliar para deletar em cascata
        const deleteCascade = async (commentId) => {
            // Encontre todos os comentários que têm este comentário como resposta
            const replies = await prisma.userGameComment.findMany({
                where: {
                    isAnswer: commentId
                }
            });

            // Recursivamente delete todas as respostas
            for (const reply of replies) {
                await deleteCascade(reply.id);
            }

            // Delete o comentário atual
            await prisma.userGameComment.delete({
                where: { id: commentId }
            });
        };

        // Inicie a exclusão em cascata a partir do comentário original
        return await deleteCascade(id);
    },

    getLocationCommentsByGameId: async (gameId) => {
        console.log('gameId:', gameId); // Adicione este log para verificar gameId
        return await prisma.userGameComment.findMany({
            where: {
                gameId: gameId,
                latitude: {
                    not: null
                },
                longitude: {
                    not: null
                }
            },
            include: {
                user: {
                    select: {
                        id: true,
                        username: true,
                        avatar: true
                    }
                },
                game: {
                    select: {
                        id: true,
                        name: true
                    }
                }
            }
        });
    }

    };

module.exports = UserGameCommentsModel;

