const nodemailer = require('nodemailer');


const sendEmail = async (option) => {
    const transporter = nodemailer.createTransport({
        host: process.env.EMAIL_HOST,
        port: process.env.EMAIL_PORT,
        auth: {
            user: process.env.EMAIL_USER,
            pass: process.env.EMAIL_PASSWORD,
        }
    })

    const emailOptions = {
         from: 'ddkricplay2plat@gmail.com',
         to: email,
         subject: 'Password Reset',
         text: `You have requested password recovery. Click on the link to reset your password: ${resetUrl}`,
        }

        await transporter.sendEmail(emailOptions);
    }


module.exports = sendEmail