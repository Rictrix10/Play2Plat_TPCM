const express = require('express');
const cors = require('cors');
const userRoute = require('./src/routes/UserRoute');
const userTypeRoute = require('./src/routes/UserTypeRoute');
const genreRoute = require('./src/routes/GenreRoute');
const platformRoute = require('./src/routes/PlatformRoute');
const companyRoute = require('./src/routes/CompanyRoute');
const sequenceRoute = require('./src/routes/SequenceRoute');
const gameRoute = require('./src/routes/GameRoute');
const dlcRoute = require('./src/routes/DLCRoute');
const avaliationRoute = require('./src/routes/AvaliationRoute');
const usergamecommentsRoute = require('./src/routes/UserGameCommentsRoute');
const usergameRoute = require('./src/routes/UserGameRoute');
const usergameFavoriteRoute = require('./src/routes/UserGameFavoriteRoute');
const gameGenreRoute = require('./src/routes/GameGenreRoute');
const platformGameRoute = require('./src/routes/PlatformGameRoute');
const userPlatformRoute = require('./src/routes/UserPlatformRoute');
const uploadRoute = require('./src/routes/UploadRoute');
const friendRequestRoute = require('./src/routes/FriendRequestRoute');
const messageRoute = require('./src/routes/MessageRoute');

const app = express();

app.use(cors());
app.use(express.json());

const apiRouter = express.Router();

app.use('/api', apiRouter);

apiRouter.use(userRoute);
apiRouter.use(userTypeRoute);
apiRouter.use(genreRoute);
apiRouter.use(platformRoute);
apiRouter.use(companyRoute);
apiRouter.use(sequenceRoute);
apiRouter.use(gameRoute);
apiRouter.use(dlcRoute);
apiRouter.use(avaliationRoute);
apiRouter.use(usergamecommentsRoute);
apiRouter.use(usergameRoute);
apiRouter.use(usergameFavoriteRoute);
apiRouter.use(gameGenreRoute);
apiRouter.use(platformGameRoute);
apiRouter.use(userPlatformRoute);
apiRouter.use(uploadRoute);
apiRouter.use(friendRequestRoute);
apiRouter.use(messageRoute);


// TESTING

const PORT = process.env.PORT || 3001;
app.listen(PORT, () => {
    console.log(`Servidor rodando na porta ${PORT}`);
});
