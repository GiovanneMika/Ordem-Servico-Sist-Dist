# 📡 Sistema Cliente-Servidor em Java (JSON via TCP/IP)

Este projeto é uma aplicação cliente-servidor desenvolvida em Java com comunicação via sockets TCP/IP utilizando mensagens em formato **JSON**. O sistema permite o cadastro, login, edição, leitura e exclusão de usuários até o momento da entrega parcial 1.

## 🗂️ Estrutura do Projeto

```
├── banco/
│   └── UsuarioDB.java
├── cliente/
│   └── ClienteMain.java
├── controller/
│   └── UsuarioController.java
├── modelo/
│   └── Usuario.java
├── servidor/
│   ├── ServidorMain.java
│   └── ServidorThread.java
├── utils/
│   └── Validador.java
```

---

## ✅ Funcionalidades

* Cadastro de usuários
* Login e logout
* Leitura de dados do usuário
* Edição de dados pessoais
* Exclusão da conta

---

## 🚀 Requisitos

* Java 11 ou superior
* Biblioteca [JSON Simple v1.1.1](https://code.google.com/archive/p/json-simple/) (já importada no projeto)
* IDE ou terminal com compilador `javac`

---

## 🖥️ Como executar o Servidor

1. Navegue até a pasta raiz do projeto.

2. Compile os arquivos Java:

   ```bash
   javac servidor/ServidorMain.java servidor/ServidorThread.java controller/UsuarioController.java banco/UsuarioDB.java modelo/Usuario.java utils/Validador.java
   ```

3. Execute o servidor:

   ```bash
   java servidor.ServidorMain
   ```

4. Informe a porta desejada quando solicitado (exemplo: `12345`).

   O terminal exibirá:

   ```
   Servidor rodando na porta 12345
   ```

---

## 💻 Como executar o Cliente

1. Em outro terminal (ou máquina), compile o cliente e as dependências:

   ```bash
   javac cliente/ClienteMain.java
   ```

2. Execute o cliente:

   ```bash
   java cliente.ClienteMain
   ```

3. Informe o IP do servidor (por exemplo, `127.0.0.1` se for local) e a mesma porta configurada no servidor.

---

## 📦 Exemplo de Uso

```text
===== MENU =====
1 - Cadastrar usuário
2 - Login
3 - Logout
4 - Ler meus dados
5 - Editar meus dados
6 - Excluir minha conta
0 - Sair
```

Após cada operação, o cliente enviará um JSON ao servidor e exibirá o JSON de resposta.

---

## 🛡️ Autenticação

Após o login bem-sucedido, o servidor retorna um **token**, que é armazenado pelo cliente e enviado nas requisições que exigem autenticação (como leitura, edição ou exclusão de dados).

---

## 📁 Observações

* Os dados de usuários são armazenados em memória, ou seja, serão perdidos ao encerrar o servidor.
* O sistema suporta múltiplos clientes simultâneos utilizando `threads`.
* Cada usuário pode estar logado em apenas uma sessão por vez.

---

## 🧪 Exemplo de JSONs trocados

**Cadastro:**

```json
{
  "operacao": "cadastro",
  "nome": "João",
  "usuario": "joao123",
  "senha": "1234",
  "perfil": "comum"
}
```

**Resposta:**

```json
{
  "operacao": "cadastro",
  "status": "sucesso",
  "mensagem": "Cadastro realizado com sucesso"
}
```

---

## 👨‍💻 Desenvolvido por

Discente Giovanne Ribeiro Mika para projeto acadêmico da disciplina de Sistemas Distríbuidos.

