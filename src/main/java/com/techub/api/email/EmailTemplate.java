package com.techub.api.email;

import org.springframework.stereotype.Component;

@Component
public class EmailTemplate {
    public String confirmationTemplate(
            String name,
            String confirmationLink,
            String expirationTime
    ) {
        return """
            <!DOCTYPE html>
            <html lang="pt-BR">
            <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Confirme seu cadastro - Resumify</title>
            </head>
            
            <body style="
                margin:0;
                padding:0;
                background-color:#f4f7fb;
                font-family:Arial, Helvetica, sans-serif;
            ">
            
            <table width="100%%" cellpadding="0" cellspacing="0" border="0">
            <tr>
            <td align="center" style="padding:40px 20px;">
            
            <table width="600" cellpadding="0" cellspacing="0" border="0"
            style="
                background:#ffffff;
                border-radius:16px;
                overflow:hidden;
                box-shadow:0 4px 12px rgba(0,0,0,0.08);
            ">
            
            <tr>
            <td
            style="
                background:linear-gradient(135deg,#1A6FD4,#4299E1);
                color:white;
                text-align:center;
                padding:40px;
            ">
            <h1 style="margin:0;font-size:32px;">
            📚 Resumify
            </h1>
            
            <p style="
                margin-top:12px;
                font-size:16px;
                opacity:0.95;
            ">
            Onde o conhecimento flui de forma leve e colaborativa.
            </p>
            </td>
            </tr>
            
            <tr>
            <td style="padding:40px;">
            
            <h2 style="color:#1A6FD4;margin-top:0;">
            Olá, %s 👋
            </h2>
            
            <p style="
                color:#444;
                line-height:1.7;
            ">
            Seja bem-vindo ao <strong>Resumify</strong>, a rede social acadêmica criada para conectar estudantes, compartilhar conhecimento e transformar resumos em aprendizado colaborativo.
            </p>
            
            <p style="
                color:#444;
                line-height:1.7;
            ">
            Estamos felizes em ter você na comunidade! Para concluir seu cadastro e começar sua jornada acadêmica, confirme seu endereço de email clicando no botão abaixo:
            </p>
            
            <div style="text-align:center;margin:35px 0;">
            <a href="%s"
            style="
                background:#1A6FD4;
                color:white;
                text-decoration:none;
                padding:16px 32px;
                border-radius:10px;
                font-weight:bold;
                display:inline-block;
            ">
            ✅ Confirmar Cadastro
            </a>
            </div>
            
            <div
            style="
                background:#f8fafc;
                border-left:4px solid #48BB78;
                padding:16px;
                border-radius:8px;
                margin:24px 0;
                color:#444;
            ">
            ⏳ <strong>Importante:</strong><br>
            Este link permanecerá válido por <strong>%s</strong>.
            </div>
            
            <p style="
                color:#666;
                font-size:14px;
                line-height:1.6;
            ">
            Caso o botão acima não funcione, copie e cole o link abaixo no seu navegador:
            </p>
            
            <p style="
                word-break:break-all;
                font-size:13px;
                color:#1A6FD4;
            ">
            %s
            </p>
            
            <p style="
                color:#666;
                font-size:14px;
                line-height:1.6;
            ">
            Se você não solicitou este cadastro, basta ignorar este email.
            </p>
            
            </td>
            </tr>
            
            <tr>
            <td
            style="
                background:#f8fafc;
                padding:24px;
                text-align:center;
            ">
            <p style="margin:0;color:#666;font-size:13px;">
            🎓 Resumify • Fatec Itaquera
            </p>
            
            <p style="margin-top:8px;color:#888;font-size:12px;">
            Compartilhe conhecimento. Evolua em comunidade.
            </p>
            </td>
            </tr>
            
            </table>
            
            </td>
            </tr>
            </table>
            
            </body>
            </html>
            """
            .formatted(
                    name,
                    confirmationLink,
                    expirationTime,
                    confirmationLink
            );
    }

    public String passwordResetTemplate(String name, String resetLink) {
        return """
        <!DOCTYPE html>
        <html lang="pt-BR">
        <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Redefinir senha - Resumify</title>
        </head>

        <body style="
            margin:0;
            padding:0;
            background-color:#f4f7fb;
            font-family:Arial, Helvetica, sans-serif;
        ">

        <table width="100%%" cellpadding="0" cellspacing="0" border="0">
        <tr>
        <td align="center" style="padding:40px 20px;">

        <table width="600" cellpadding="0" cellspacing="0" border="0"
        style="
            background:#ffffff;
            border-radius:16px;
            overflow:hidden;
            box-shadow:0 4px 12px rgba(0,0,0,0.08);
        ">

        <tr>
        <td style="
            background:linear-gradient(135deg,#1A6FD4,#4299E1);
            color:white;
            text-align:center;
            padding:40px;
        ">
        <h1 style="margin:0;font-size:32px;">
        📚 Resumify
        </h1>
        <p style="margin-top:12px;font-size:16px;opacity:0.95;">
        Redefinição de senha
        </p>
        </td>
        </tr>

        <tr>
        <td style="padding:40px;">

        <h2 style="color:#1A6FD4;margin-top:0;">
        Olá, %s 👋
        </h2>

        <p style="color:#444;line-height:1.7;">
        Recebemos uma solicitação para redefinir a senha da sua conta no <strong>Resumify</strong>.
        </p>

        <p style="color:#444;line-height:1.7;">
        Clique no botão abaixo para criar uma nova senha. O link é válido por <strong>15 minutos</strong>.
        </p>

        <div style="text-align:center;margin:35px 0;">
        <a href="%s"
        style="
            background:#1A6FD4;
            color:white;
            text-decoration:none;
            padding:16px 32px;
            border-radius:10px;
            font-weight:bold;
            display:inline-block;
        ">
        🔑 Redefinir senha
        </a>
        </div>

        <div style="
            background:#fff8e1;
            border-left:4px solid #f59e0b;
            padding:16px;
            border-radius:8px;
            margin:24px 0;
            color:#444;
        ">
        ⚠️ <strong>Atenção:</strong><br>
        Se você não solicitou a redefinição de senha, ignore este email. Sua senha permanece a mesma.
        </div>

        <p style="color:#666;font-size:14px;line-height:1.6;">
        Caso o botão não funcione, copie e cole o link abaixo no seu navegador:
        </p>

        <p style="word-break:break-all;font-size:13px;color:#1A6FD4;">
        %s
        </p>

        </td>
        </tr>

        <tr>
        <td style="background:#f8fafc;padding:24px;text-align:center;">
        <p style="margin:0;color:#666;font-size:13px;">
        🎓 Resumify • Fatec Itaquera
        </p>
        <p style="margin-top:8px;color:#888;font-size:12px;">
        Compartilhe conhecimento. Evolua em comunidade.
        </p>
        </td>
        </tr>

        </table>

        </td>
        </tr>
        </table>

        </body>
        </html>
        """
        .formatted(name, resetLink, resetLink);
    }
}
