# 🛠️ Instalação de Ferramentas Necessárias

Este guia ajuda você a instalar todas as ferramentas necessárias para desenvolver o projeto FlightOnTime API.

---

## ☕ Java 17

### Windows

#### Opção 1: Usando Instalador (Recomendado)
1. Acesse: https://adoptium.net/
2. Baixe o **Eclipse Temurin 17 (LTS)** para Windows
3. Execute o instalador
4. ✅ Marque a opção "Add to PATH"
5. Finalize a instalação

#### Opção 2: Usando Chocolatey
```powershell
choco install temurin17
```

#### Opção 3: Usando WinGet
```powershell
winget install EclipseAdoptium.Temurin.17.JDK
```

### Verificar instalação
```bash
java -version
```

**Saída esperada:**
```
openjdk version "17.0.x"
OpenJDK Runtime Environment Temurin-17+x
```

---

## 📦 Maven

### Windows

#### Opção 1: Usando Chocolatey (Recomendado)
```powershell
choco install maven
```

#### Opção 2: Instalação Manual
1. Baixe o Maven: https://maven.apache.org/download.cgi
   - Arquivo: `apache-maven-3.9.x-bin.zip`

2. Extraia para `C:\Program Files\Apache\maven`

3. Adicione ao PATH:
   - Pesquise "Variáveis de Ambiente" no Windows
   - Em "Variáveis do Sistema", clique em "Path" → "Editar"
   - Adicione: `C:\Program Files\Apache\maven\bin`
   - Clique OK

4. **Reinicie o terminal/PowerShell**

5. Verifique:
```bash
mvn -version
```

**Saída esperada:**
```
Apache Maven 3.9.x
Maven home: C:\Program Files\Apache\maven
Java version: 17.0.x
```

---

## 🎨 IDE (Escolha uma)

### IntelliJ IDEA Community (Recomendado para Java)

#### Download
https://www.jetbrains.com/idea/download/

#### Instalação
1. Baixe a versão **Community** (gratuita)
2. Execute o instalador
3. ✅ Marque: "Add 'bin' folder to PATH"
4. ✅ Marque: "Create Desktop Shortcut"

#### Importar o Projeto
1. File → Open
2. Selecione a pasta `flight-ontime-api`
3. Aguarde o Maven baixar as dependências

### VS Code (Alternativa leve)

#### Download
https://code.visualstudio.com/

#### Extensões necessárias:
1. **Extension Pack for Java** (Microsoft)
   - Inclui: Language Support, Debugger, Test Runner, Maven

2. **Spring Boot Extension Pack**
   - Inclui: Spring Initializr, Spring Boot Dashboard

#### Instalar extensões:
```
Ctrl+Shift+X → Pesquisar "Extension Pack for Java" → Install
Ctrl+Shift+X → Pesquisar "Spring Boot Extension Pack" → Install
```

#### Abrir projeto:
```
File → Open Folder → Selecionar flight-ontime-api
```

---

## 📮 Postman

### Download
https://www.postman.com/downloads/

### Instalação
1. Baixe o instalador para Windows
2. Execute e siga o assistente
3. Crie uma conta gratuita (ou use sem login)

### Importar Collection
1. Collections → Import
2. File → Selecione `postman/FlightOnTime_API.postman_collection.json`
3. Pronto! Você terá todos os testes prontos

---

## 🐙 Git (Controle de Versão)

### Windows

#### Instalação
https://git-scm.com/download/win

1. Baixe o instalador
2. Execute
3. Aceite as opções padrão
4. Finalize

### Verificar instalação
```bash
git --version
```

### Configuração inicial
```bash
git config --global user.name "Seu Nome"
git config --global user.email "seu.email@example.com"
```

---

## 🔧 Ferramentas Opcionais

### Chocolatey (Gerenciador de Pacotes para Windows)

Facilita instalação de ferramentas via linha de comando.

#### Instalação
1. Abra PowerShell **como Administrador**
2. Execute:
```powershell
Set-ExecutionPolicy Bypass -Scope Process -Force; [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072; iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))
```

3. Reinicie o terminal

4. Verifique:
```bash
choco --version
```

#### Usar Chocolatey para instalar tudo de uma vez:
```powershell
# Abrir PowerShell como Administrador
choco install temurin17 maven git postman vscode -y
```

---

## ✅ Checklist de Instalação

Verifique se tudo está instalado corretamente:

```bash
# Java
java -version

# Maven
mvn -version

# Git
git --version
```

Se todos os comandos retornarem as versões corretas, você está pronto! ✅

---

## 🚀 Próximos Passos

Agora que tudo está instalado:

1. **Clone ou abra o projeto**
```bash
cd "c:\Users\alves\OneDrive\Documentos\Projetos\Hackaton\flight-ontime-api"
```

2. **Compile o projeto**
```bash
mvn clean install
```

3. **Execute a aplicação**
```bash
mvn spring-boot:run
```

4. **Acesse o Swagger**
```
http://localhost:8080/swagger-ui.html
```

5. **Teste no Postman**
- Importe a collection
- Execute os testes

---

## 🐛 Problemas Comuns

### "mvn não é reconhecido"
- ✅ Verifique se adicionou ao PATH
- ✅ Reinicie o terminal/PowerShell
- ✅ Reinicie o computador se necessário

### "JAVA_HOME não está definido"
1. Descubra onde o Java foi instalado:
```bash
where java
```

2. Defina JAVA_HOME:
- Variáveis de Ambiente → Novo (Sistema)
- Nome: `JAVA_HOME`
- Valor: `C:\Program Files\Eclipse Adoptium\jdk-17.x.x-hotspot`

### Porta 8080 em uso
```bash
# Ver quem está usando
netstat -ano | findstr :8080

# Matar o processo (substitua PID)
taskkill /PID <PID> /F
```

---

## 📞 Suporte

**Problemas com instalação?**
1. Verifique a documentação oficial de cada ferramenta
2. Consulte o Tech Lead
3. Peça ajuda no grupo do time

---

## 📚 Recursos Úteis

- **Documentação Java**: https://docs.oracle.com/en/java/javase/17/
- **Documentação Maven**: https://maven.apache.org/guides/
- **Documentação Spring Boot**: https://spring.io/guides
- **Tutoriais**: https://www.baeldung.com/

---

**Boa sorte! 🚀**
