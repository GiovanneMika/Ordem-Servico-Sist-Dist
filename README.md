# 📡 Sistema Cliente-Servidor em Java (JSON via TCP/IP)

Este projeto é uma aplicação **cliente-servidor** desenvolvida em Java, com comunicação via **sockets TCP/IP** utilizando mensagens em formato **JSON**. A aplicação suporta múltiplos usuários com perfis distintos (**comum** e **administrador**) e conta com uma **interface gráfica completa (Swing)** para **cliente e servidor**, além de funcionalidades de **ordens de serviço** e gerenciamento de usuários.

---

## 🗂️ Estrutura do Projeto

```
├── banco/
│   └── UsuarioDB.java
├── cliente/
│   ├── ClienteMain.java
│   └── gui/
│       ├── TelaLoginCliente.java
│       ├── TelaMenuComumCliente.java
│       ├── TelaEditarUsuarioCliente.java
│       ├── TelaMenuAdministrador.java
│       ├── PainelUsuariosAdm.java
│       └── PainelOrdensAdm.java
├── servidor/
│   ├── ServidorMain.java
│   ├── ServidorThread.java
│   └── gui/
│       └── TelaServidorGUI.java
├── controller/
│   └── UsuarioController.java
├── modelo/
│   └── Usuario.java
├── utils/
│   └── Validador.java
```

---

## ✅ Funcionalidades

### 👤 Usuário Comum (com interface gráfica)

* Login e logout
* Leitura e edição dos próprios dados (nome, usuário, senha)
* **Autoexclusão da conta**, com retorno à tela de login
* Cadastro e edição de **ordens de serviço**
* **Filtro de ordens** por status: `todas`, `pendente`, `finalizada`, `cancelada`
* Visualização apenas das **ordens do próprio usuário**

---

### 🛠️ Administrador (com interface gráfica)

* Todas as funções do usuário comum
* Cadastro de usuários com qualquer perfil (`comum` ou `adm`)
* Listagem, edição (nome, senha, perfil) e exclusão de **qualquer usuário**
* Listagem e edição de **todas as ordens de serviço**
* Filtro por status de ordens (igual ao comum)
* Interface dividida em **abas**: *Usuários* e *Ordens de Serviço*

---

### 💡 Servidor (com interface gráfica)

* Escolha da porta e inicialização do servidor
* Botão para encerrar servidor com segurança
* Aba **Log** com todas as atividades registradas
* Aba de **IPs conectados** em tempo real
* Aba de **usuários logados** (sessões ativas)
* Encerramento automático das conexões ao parar o servidor

---

## 🚀 Requisitos

* Java 11 ou superior
* Biblioteca [JSON Simple v1.1.1](https://code.google.com/archive/p/json-simple/) (já importada no projeto)
* IDE com suporte a Swing (Eclipse, IntelliJ, NetBeans)

---

## 🖥️ Como Executar

### 🔁 Servidor com GUI

1. Compile:

```bash
javac servidor/*.java servidor/gui/*.java controller/*.java banco/*.java modelo/*.java utils/*.java
```

2. Execute:

```bash
java servidor.gui.TelaServidorGUI
```

3. Digite a porta desejada (ex: `12345`) e clique em **Iniciar Servidor**.

---

### 👨‍💻 Cliente com Interface Gráfica

1. Compile:

```bash
javac cliente/gui/*.java cliente/ClienteMain.java
```

2. Execute:

```bash
java cliente.gui.TelaLoginCliente
```

3. Informe IP e porta do servidor e realize o login.

---

### 📟 Cliente Terminal (opcional / legado)

1. Compile:

```bash
javac cliente/ClienteMain.java
```

2. Execute:

```bash
java cliente.ClienteMain
```

---

## 📦 Exemplos de Uso

### Autoexclusão do Usuário Comum (JSON enviado)

```json
{
  "operacao": "excluir_usuario",
  "token": "TOKEN_DO_USUARIO"
}
```

### Listagem de Ordens Filtradas

```json
{
  "operacao": "listar_ordens",
  "token": "TOKEN_VALIDO",
  "filtro": "pendente"
}
```

---

## 🧠 Observações Técnicas

* Todos os dados são mantidos **em memória** (sem persistência em banco de dados).
* A aplicação é **multithreaded**: cada cliente conectado roda em sua própria `ServidorThread`.
* O controle de sessões permite **um único login por usuário**.
* Tokens de autenticação são obrigatórios para qualquer operação pós-login.
* O servidor gráfico remove automaticamente usuários logados e IPs ao desconectar ou parar.

---

## 🧑‍💻 Desenvolvido por

**Giovanne Ribeiro Mika**
Projeto acadêmico da disciplina de **Sistemas Distribuídos**
