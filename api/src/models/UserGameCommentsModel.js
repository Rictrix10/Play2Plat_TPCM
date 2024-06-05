const { PrismaClient } = require('@prisma/client');
const prisma = new PrismaClient();

const UserGameCommentsModel = {
    createUserGameComment: async (comments, image, isAnswer, userId, gameId, latitude, longitude) => {
        return await prisma.userGameComment.create({
            data: {
                comments,
                image,
                isAnswer,
                userId,
                gameId,
                latitude,
                longitude
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
        const userComments = await prisma.userGameComment.findMany({
            where: {
                userId: userId,
                gameId: gameId,
                isAnswer: null
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

        const otherComments = await prisma.userGameComment.findMany({
            where: {
                userId: {
                    not: userId
                },
                gameId: gameId,
                isAnswer: null
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

    getResponsesByPostIdGameId: async (postId, gameId) => {
        return await prisma.userGameComment.findMany({
            where: {
                isAnswer: postId,
                gameId: gameId
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

     updateUserGameCommentById: async (id, comments, image, isAnswer, latitude, longitude) => {
             return await prisma.userGameComment.update({
                 where: { id: id },
                 data: {
                     comments: comments,
                     image: image,
                     isAnswer: isAnswer,
                     latitude: latitude,
                     longitude: longitude
                 }
             });
         },

        deleteUserGameCommentById: async (id) => {
                return await prisma.userGameComment.delete({
                    where: { id: id }
                });
            }
        };

module.exports = UserGameCommentsModel;

