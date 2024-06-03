const express = require('express');
const router = express.Router();
const CompanyController = require('../controllers/CompanyController');

router.post('/companies', CompanyController.createCompany);

router.get('/companies', CompanyController.getCompanies);

router.get('/companies/random-name', CompanyController.getRandomCompanyName);

module.exports = router;
