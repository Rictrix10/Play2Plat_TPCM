const nodemailer = require('nodemailer');


const sendEmail = async (option) => {
    /*
    const transporter = nodemailer.createTransport({
        host: process.env.EMAIL_HOST,
        port: process.env.EMAIL_PORT,
        auth: {
            user: process.env.EMAIL_USER,
            pass: process.env.EMAIL_PASSWORD,
        }
    })
    */

        const transporter = nodemailer.createTransport({
            host: 'sandbox.smtp.mailtrap.io',
            port: 25,
            auth: {
                user: '3f21e39269d693',
                pass: '4fc5203d229eb7',
            }
        })

    const emailOptions = {
         from: 'ddkricplay2plat@gmail.com',
         to: option.email,
         //subject: 'Password Reset',
         //text: `You have requested password recovery. Click on the link to reset your password: ${resetUrl}`,
         subject: option.subject,
         text: option.message
        }

        await transporter.sendEmail(emailOptions);
    }


module.exports = sendEmail
