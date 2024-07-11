const { PrismaClient } = require('@prisma/client');
const prisma = new PrismaClient();

const FriendRequestModel = {
    createFriendRequest: async (sentUserId, receivedUserId, isAccepted = false) => {
        return await prisma.friendRequest.create({
            data: {
                sentUserId,
                receivedUserId,
                isAccepted,
            }
        });
    },

    getAllFriendRequests: async () => {

        return await prisma.friendRequest.findMany();
    },

    getFriendRequestById: async (id) => {

        return await prisma.friendRequest.findUnique({
            where: {
                id: id,
            }
        });
    },

    deleteFriendRequestById: async (id) => {

        return await prisma.friendRequest.delete({
            where: {
                id: id,
            }
        });
    },
    getAllFriendRequestBySentUserId: async (userId) => {
        return await prisma.friendRequest.findMany({
            where: {
                sentUserId: userId,
            },
        });
    },
        getAcceptedFriendRequestBySentUserId: async (userId) => {
            return await prisma.friendRequest.findMany({
                where: {
                    sentUserId: userId,
                    isAccepted: true
                },
            });
        },

        getNotAcceptedFriendRequestBySentUserId: async (userId) => {
            return await prisma.friendRequest.findMany({
                where: {
                    sentUserId: userId,
                    isAccepted: false
                },
            });
        },

    getAllFriendRequestByReceivedUserId: async (userId) => {
        return await prisma.friendRequest.findMany({
            where: {
                receivedUserId: userId,
            },
        });
    },
        getAcceptedFriendRequestByReceivedUserId: async (userId) => {
            return await prisma.friendRequest.findMany({
                where: {
                    receivedUserId: userId,
                    isAccepted: true
                },
            });
        },

        getNotAcceptedFriendRequestByReceivedUserId: async (userId) => {
            return await prisma.friendRequest.findMany({
                where: {
                    receivedUserId: userId,
                    isAccepted: false
                },
            });
        },

deleteFriendRequestBySentUserIdAndReceivedUserId: async (sentUserId, receivedUserId) => {
        return await prisma.friendRequest.deleteMany({
            where: {
                sentUserId: sentUserId,
                receivedUserId: receivedUserId
            }
        });
    },

    getFriendRequestBySentUserIdAndReceivedUserId: async (sentUserId, receivedUserId) => {
        return await prisma.friendRequest.findUnique({
            where: {
                sentUserId_receivedUserId: {
                    sentUserId: sentUserId,
                    receivedUserId: receivedUserId
                }
            }
        });
    },

    updateFriendRequestBySentUserIdAndReceivedUserId: async (sentUserId, receivedUserId, data) => {
            return await prisma.friendRequest.updateMany({
                where: {
                    sentUserId: sentUserId,
                    receivedUserId: receivedUserId
                },
                data: data
            });
        },

    updateFriendRequestAccepted: async (id, isAccepted) => {

        return await prisma.friendRequest.update({
            where: {
                id: id,
            },
            data: {
                isAccepted: isAccepted,
            }
        });
    },

};

module.exports = FriendRequestModel;
