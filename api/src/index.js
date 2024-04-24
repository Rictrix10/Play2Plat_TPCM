const express = require('express');
const cors = require('cors');
const userRoute = require('./routes/UserRoute');
const userTypeRoute = require('./routes/UserTypeRoute');

const app = express();

app.use(cors());
app.use(express.json());

const apiRouter = express.Router();

app.use('/api', apiRouter);

apiRouter.use(userRoute);
apiRouter.use(userTypeRoute);

const PORT = process.env.PORT || 3001;
app.listen(PORT, () => {
    console.log(`Servidor rodando na porta ${PORT}`);
});
