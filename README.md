# 📡 Sistema Cliente-Servidor em Java (JSON via TCP/IP)

Este projeto é uma aplicação cliente-servidor desenvolvida em Java com comunicação via sockets TCP/IP utilizando mensagens em formato **JSON**. O sistema permite cadastro, login, autenticação via token e funcionalidades específicas para usuários comuns e administradores.

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

### 👤 Usuário Comum

* Login e logout
* Leitura dos próprios dados
* Edição dos próprios dados (nome, usuário, senha)
* Exclusão da própria conta

### 🛠️ Administrador

* Todas as funções do usuário comum
* Cadastro de novos usuários (comum ou admin)
* Listagem de todos os usuários do sistema
* Edição de qualquer usuário do sistema (inclusive troca de perfil)
* Exclusão de qualquer usuário do sistema

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

   A seguinte mensagem será exibida:

   ```
   Servidor rodando na porta 12345
   ```

---

## 💻 Como executar o Cliente

1. Em outro terminal (ou máquina), compile o cliente:

   ```bash
   javac cliente/ClienteMain.java
   ```

2. Execute o cliente:

   ```bash
   java cliente.ClienteMain
   ```

3. Informe o IP do servidor (ex: `127.0.0.1`) e a porta usada no servidor.

---

## 📦 Exemplos de Uso

### Após o login:

#### Usuário comum verá o menu:

```
===== MENU =====
1 - Logout
2 - Ler meus dados
3 - Editar meus dados
4 - Excluir minha conta
0 - Sair
```

#### Usuário administrador verá o menu:

```
===== MENU ADMINISTRATIVO =====
1 - Logout
2 - Ler meus dados
3 - Cadastrar novo usuário
4 - Listar todos os usuários
5 - Editar usuário do sistema
6 - Excluir usuário do sistema
0 - Sair
```

---

## 🛡️ Autenticação

Após login bem-sucedido, o servidor retorna um **token de autenticação** e o **perfil** do usuário (`comum` ou `adm`). Esse token deve ser incluído em todas as requisições subsequentes que exigem autenticação.

---

## 📁 Observações

* Os dados são mantidos **em memória**, portanto **serão perdidos** ao reiniciar o servidor.
* O sistema suporta múltiplos clientes simultaneamente via **threads**.
* Um usuário só pode estar **logado em uma sessão por vez**.
* As operações administrativas são permitidas **somente a usuários com perfil `adm`**.

---

## 🧪 Exemplo de JSONs trocados

### Cadastro (pelo admin):

```json
{
  "operacao": "cadastro",
  "nome": "João",
  "usuario": "joao123",
  "senha": "1234",
  "perfil": "comum",
  "token": "TOKEN_DO_ADMIN"
}
```

### Edição de outro usuário (admin):

```json
{
  "operacao": "editar_usuario",
  "usuario_alvo": "joao123",
  "novo_nome": "João Silva",
  "nova_senha": "novaSenha",
  "novo_perfil": "adm",
  "token": "TOKEN_DO_ADMIN"
}
```

---

## 👨‍💻 Desenvolvido por

Discente **Giovanne Ribeiro Mika** para projeto acadêmico da disciplina de **Sistemas Distribuídos**.

