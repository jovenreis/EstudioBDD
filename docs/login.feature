# language: pt
Funcionalidade: Login
  Como cliente
  Quero entrar na loja com e-mail e senha
  Para acompanhar meus pedidos

  Cenário: Login com dados corretos
    Dado o cliente "joao@exemplo.com" cadastrado com a senha "abc123"
    Quando ele entra com "joao@exemplo.com" e "abc123"
    Então o acesso é liberado

  Cenário: Login com senha errada é recusado
    Dado o cliente "joao@exemplo.com" cadastrado com a senha "abc123"
    Quando ele entra com "joao@exemplo.com" e "errada"
    Então o acesso é negado
    E a mensagem informa que o e-mail ou a senha estão incorretos
