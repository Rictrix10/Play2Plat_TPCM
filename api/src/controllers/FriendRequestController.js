const FriendRequestModel = require('../models/FriendRequestModel');

const FriendRequestController = {
    createFriendRequest: async (req, res) => {
        try {
            const { sentUserId, receivedUserId, isAccepted = false } = req.body;
            const newFriendRequest = await FriendRequestModel.createFriendRequest(sentUserId, receivedUserId, isAccepted);

            res.status(201).json(newFriendRequest);
        } catch (error) {
            console.error('Erro ao criar friend request:', error);
            res.status(500).json({ error: 'Erro ao criar friend request' });
        }
    },

    getAllFriendRequests: async (req, res) => {
        try {
            const friendRequests = await FriendRequestModel.getAllFriendRequests();
            res.json(friendRequests);
        } catch (error) {
            console.error('Erro ao buscar friend requests:', error);
            res.status(500).json({ error: 'Erro ao buscar friend requests' });
        }
    },

    getFriendRequestById: async (req, res) => {
        try {
            const friendRequestId = parseInt(req.params.id);
            const friendRequest = await FriendRequestModel.getFriendRequestById(friendRequestId);

            if (friendRequest) {
                res.json(friendRequest);
            } else {
                res.status(404).json({ error: 'Friend Request não encontrado' });
            }
        } catch (error) {
            console.error('Erro ao buscar Friend Request por ID:', error);
            res.status(500).json({ error: 'Erro ao buscar Friend Request' });
        }
    },

    deleteFriendRequest: async (req, res) => {
        try {
            const friendRequestId = req.params.id;

            const friendRequest = await FriendRequestModel.deleteFriendRequestById(friendRequestId);

            if (friendRequest) {
                res.status(204).end();
            } else {
                res.status(404).json({ error: 'Friend Request não encontrado' });
            }
        } catch (error) {
            console.error('Erro ao excluir Friend Request:', error);
            res.status(500).json({ error: 'Erro ao excluir Friend Request' });
        }
    },

    getAllFriendRequestBySentUserId: async (req, res) => {
        try {
            const userId = parseInt(req.params.userId);
            const friendRequests = await FriendRequestModel.getAllFriendRequestBySentUserId(userId);
            res.json(friendRequests);
        } catch (error) {
            res.status(500).json({ error: 'Erro ao buscar friend requests' });
        }
    },

    getAcceptedFriendRequestBySentUserId: async (req, res) => {
        try {
            const userId = parseInt(req.params.userId);
            const friendRequests = await FriendRequestModel.getAcceptedFriendRequestBySentUserId(userId);
            res.json(friendRequests);
        } catch (error) {
            res.status(500).json({ error: 'Erro ao buscar friend requests' });
        }
    },

    getNotAcceptedFriendRequestBySentUserId: async (req, res) => {
        try {
            const userId = parseInt(req.params.userId);
            const friendRequests = await FriendRequestModel.getNotAcceptedFriendRequestBySentUserId(userId);
            res.json(friendRequests);
        } catch (error) {
            res.status(500).json({ error: 'Erro ao buscar friend requests' });
        }
    },

    getAllFriendRequestByReceivedUserId: async (req, res) => {
        try {
            const userId = parseInt(req.params.userId);
            const friendRequests = await FriendRequestModel.getAllFriendRequestByReceivedUserId(userId);
            res.json(friendRequests);
        } catch (error) {
            res.status(500).json({ error: 'Erro ao buscar friend requests' });
        }
    },

    getAcceptedFriendRequestByReceivedUserId: async (req, res) => {
        try {
            const userId = parseInt(req.params.userId);
            const friendRequests = await FriendRequestModel.getAcceptedFriendRequestByReceivedUserId(userId);
            res.json(friendRequests);
        } catch (error) {
            res.status(500).json({ error: 'Erro ao buscar friend requests' });
        }
    },

    getNotAcceptedFriendRequestByReceivedUserId: async (req, res) => {
        try {
            const userId = parseInt(req.params.userId);
            const friendRequests = await FriendRequestModel.getNotAcceptedFriendRequestByReceivedUserId(userId);
            res.json(friendRequests);
        } catch (error) {
            res.status(500).json({ error: 'Erro ao buscar friend requests' });
        }
    },

 deleteFriendRequestBySentUserIdAndReceivedUserId: async (req, res) => {
        try {
            const sentUserId = parseInt(req.params.sentUserId);
            const receivedUserId = parseInt(req.params.receivedUserId);

            const result = await FriendRequestModel.deleteFriendRequestBySentUserIdAndReceivedUserId(sentUserId, receivedUserId);
            if (result.count > 0) {
                res.status(204).end();
            } else {
                res.status(404).json({ error: 'Friend request não encontrado' });
            }
        } catch (error) {
            res.status(500).json({ error: 'Erro ao excluir Friend request' });
        }
    },


    getFriendRequestBySentUserIdAndReceivedUserId: async (req, res) => {
        try {
            const sentUserId = parseInt(req.params.sentUserId);
            const receivedUserId = parseInt(req.params.receivedUserId);

            const friendRequest = await FriendRequestModel.getFriendRequestBySentUserIdAndReceivedUserId(sentUserId, receivedUserId);

            if (friendRequest) {
                res.json(friendRequest);
            } else {
                res.status(404).json({ error: 'Friend Request não encontrado' });
            }
        } catch (error) {
            res.status(500).json({ error: 'Erro ao buscar Friend Request' });
        }
    },

        updateFriendRequestBySentUserIdAndReceivedUserId: async (req, res) => {
                try {
                    const sentUserId = parseInt(req.params.sentUserId);
                    const receivedUserId = parseInt(req.params.receivedUserId);
                    const data = req.body; // Dados a serem atualizados

                    const result = await FriendRequestModel.updateFriendRequestBySentUserIdAndReceivedUserId(sentUserId, receivedUserId, data);
                    if (result.count > 0) {
                        res.json({ message: 'Friend Request atualizado com sucesso' });
                    } else {
                        res.status(404).json({ error: 'Friend Request não encontrada' });
                    }
                } catch (error) {

                    res.status(500).json({ error: 'Erro ao atualizar Friend Request' });
                }
            },

        updateFriendRequestAccepted: async (req, res) => {
            try {
                const friendRequestId = req.params.id;
                const { isAccepted } = req.body;

                // Atualiza o estado de uma relação específica de usuário com jogo por ID
                const friendRequest = await FriendRequestModel.updateFriendRequestAccepted(friendRequestId, isAccepted);

                if (friendRequest) {
                    res.json(friendRequest);
                } else {
                    res.status(404).json({ error: 'Friend Request não encontrado' });
                }
            } catch (error) {
                res.status(500).json({ error: 'Erro ao atualizar Friend Request' });
            }
        },


};

module.exports = FriendRequestController;


