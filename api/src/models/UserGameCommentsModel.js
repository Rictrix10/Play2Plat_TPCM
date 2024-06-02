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

    deleteCommentById: async (id) => {
        return await prisma.userGameComment.delete({
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

    getPostsPreview: async () => {
        return await prisma.userGameComment.findMany({
            where: {
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

    getPostsByUserId: async (userId) => {
        const userComments = await prisma.userGameComment.findMany({
            where: {
                userId: userId,
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
    }
};

module.exports = UserGameCommentsModel;
