const axios = require('axios');
const fs = require('fs');
require('dotenv').config(); // Importar as variáveis de ambiente do arquivo .env

const vercelToken = process.env.BLOB_READ_WRITE_TOKEN; // Usar o token definido no arquivo .env
const apiEndPt = 'https://api.vercel.com/v9/projects';

let config = {
  method: 'get',
  url: apiEndPt,
  headers: {
    Authorization: 'Bearer ' + vercelToken,
  },
};
let results = [];

(function loop() {
  axios(config)
    .then(function (response) {
      results.push(...response.data.projects);
      if (response.data.pagination.next !== null) {
        config.url = `${apiEndPt}?until=${response.data.pagination.next}`;
        loop();
      } else {
        // Você pode usar o objeto de resultados final e, por exemplo, salvá-lo em um arquivo JSON
        fs.writeFileSync('projects.json', JSON.stringify(results));
      }
    })
    .catch(function (error) {
      console.log(error);
    });
})();
