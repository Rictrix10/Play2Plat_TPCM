const CompanyModel = require('../models/CompanyModel');

const CompanyController = {
    createCompany: async (req, res) => {
        try {
            const { name } = req.body;
            const newCompany = await CompanyModel.createCompany(name);
            res.status(201).json(newCompany);
        } catch (error) {
            console.error('Erro ao criar empresa:', error);
            res.status(500).json({ error: 'Erro ao criar a empresa' });
        }
    },
        getCompanies: async (req, res) => {
            try {
                const companies = await CompanyModel.getCompanies();
                res.json(companies);
            } catch (error) {
                console.error('Erro ao buscar empresas:', error);
                res.status(500).json({ error: 'Erro ao buscar empresas' });
            }
        },
    getRandomCompanyName: async (req, res) => {
        try {
            const companyName = await CompanyModel.getRandomCompanyName();
            if (!companyName) {
                res.status(404).json({ error: 'Nenhuma companhia encontrada' });
            } else {
                res.json({ name: companyName });
            }
        } catch (error) {
            console.error('Erro ao buscar nome de companhia aleatório:', error);
            res.status(500).json({ error: 'Erro ao buscar nome de companhia aleatório' });
        }
    }
};

module.exports = CompanyController;
