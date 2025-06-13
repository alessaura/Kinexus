# Documentação Final - App de Gerenciamento de Rotina de Dependentes

## Visão Geral

O aplicativo de Gerenciamento de Rotina de Dependentes é uma solução Android desenvolvida para facilitar a gestão e o compartilhamento de informações sobre rotinas de dependentes entre diferentes entidades (escolas, creches, ONGs) e responsáveis. O sistema é especialmente útil para casos como crianças atípicas que mudam de escola, permitindo a transferência eficiente de informações sobre suas necessidades específicas e rotinas estabelecidas.

## Arquitetura do Sistema

O aplicativo foi desenvolvido utilizando a arquitetura MVVM (Model-View-ViewModel), com integração ao Firebase para autenticação e armazenamento de dados. A estrutura do projeto segue as melhores práticas de desenvolvimento Android, com separação clara entre camadas de dados, lógica de negócios e interface do usuário.

### Componentes Principais

1. **Autenticação**: Implementada utilizando Firebase Authentication com integração ao Google Sign-In.
2. **Banco de Dados**: Utiliza o Cloud Firestore para armazenamento de dados estruturados.
3. **Repositórios**: Camada intermediária que gerencia a comunicação entre o ViewModel e o Firestore.
4. **ViewModels**: Responsáveis pela lógica de negócios e comunicação entre a interface e os repositórios.
5. **Fragments e Activities**: Componentes de interface do usuário que exibem e coletam dados.

## Modelos de Dados

### Company (Empresa/Instituição)
- Representa escolas, creches, ONGs e outras entidades
- Armazena informações como nome, tipo, endereço, especialidades
- Vinculada a um proprietário/administrador

### Responsible (Responsável)
- Representa pais, tutores e outros responsáveis pelos dependentes
- Armazena informações de contato e preferências de notificação
- Vinculado a um usuário autenticado

### Dependent (Dependente)
- Armazena informações sobre o dependente (criança, pessoa atípica, etc.)
- Inclui dados sobre necessidades especiais, condições médicas, restrições alimentares
- Vinculado a responsáveis e empresas/instituições

### Routine (Rotina)
- Define a programação de atividades para um dependente
- Organizada por dias da semana e horários
- Pode ser compartilhada entre instituições

### SharedInfo (Informação Compartilhada)
- Gerencia o compartilhamento de informações entre instituições
- Utiliza códigos de autorização para garantir a segurança
- Permite compartilhamento seletivo de diferentes tipos de informação

## Funcionalidades Principais

### Login Social com Google
- Autenticação segura e simplificada
- Integração com Firebase Authentication
- Persistência de sessão para melhor experiência do usuário

### Gerenciamento de Empresas/Instituições
- Cadastro e edição de informações da empresa
- Visualização de dependentes vinculados
- Gerenciamento de funcionários e permissões

### Gerenciamento de Dependentes
- Cadastro detalhado com informações específicas
- Vinculação a responsáveis e instituições
- Registro de necessidades especiais e condições médicas

### Gerenciamento de Rotinas
- Criação e edição de rotinas personalizadas
- Organização por dias da semana e horários
- Diferentes tipos de atividades (alimentação, medicação, terapias, etc.)

### Compartilhamento de Informações
- Sistema de códigos de autorização para transferência segura
- Compartilhamento seletivo de diferentes tipos de informação
- Histórico de compartilhamentos realizados

## Fluxos Principais

### Fluxo de Login
1. Usuário acessa a tela inicial
2. Clica no botão "Entrar com Google"
3. Seleciona a conta Google desejada
4. É redirecionado para a tela principal após autenticação bem-sucedida

### Fluxo de Gerenciamento de Rotina
1. Usuário acessa a seção "Rotinas"
2. Visualiza lista de rotinas existentes
3. Pode adicionar nova rotina ou editar existentes
4. Define atividades, horários e dias da semana
5. Salva as alterações

### Fluxo de Compartilhamento
1. Usuário seleciona um dependente
2. Escolhe as informações que deseja compartilhar
3. Indica a instituição de destino
4. Gera um código de autorização
5. Compartilha o código com a instituição de destino
6. A instituição de destino utiliza o código para importar as informações

## Requisitos Técnicos

### Requisitos de Sistema
- Android 7.0 (API 24) ou superior
- Conexão com internet
- Conta Google para autenticação

### Dependências Principais
- Firebase Authentication
- Firebase Firestore
- Google Play Services Auth
- AndroidX Components
- Material Design Components

## Considerações de Segurança

- Autenticação segura via Google
- Compartilhamento de informações mediante autorização explícita
- Códigos de autorização temporários para transferência de dados
- Controle granular sobre quais informações são compartilhadas

## Próximos Passos e Melhorias Futuras

1. **Implementação de Notificações Push**: Para alertar sobre alterações em rotinas e compartilhamentos
2. **Modo Offline**: Permitir acesso a informações básicas mesmo sem conexão com internet
3. **Relatórios e Análises**: Geração de relatórios sobre progresso e adaptação dos dependentes
4. **Versão iOS**: Desenvolvimento de versão para dispositivos Apple
5. **Integração com Calendários**: Sincronização com Google Calendar e outros serviços

## Conclusão

O aplicativo de Gerenciamento de Rotina de Dependentes oferece uma solução completa para instituições e responsáveis que precisam gerenciar e compartilhar informações sobre dependentes, com foco especial em casos que requerem atenção personalizada. A arquitetura modular e escalável permite futuras expansões e adaptações conforme as necessidades dos usuários evoluam.
