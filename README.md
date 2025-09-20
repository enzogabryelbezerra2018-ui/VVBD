# VVBD

**VVBD** é um projeto conceitual inspirado por ferramentas como Magisk/TWRP, com o objetivo de facilitar a transferência segura de pacotes `.zip` customizados (por exemplo, ROMs ou imagens) entre um computador e um dispositivo Android.  
Este README descreve a visão, recursos, arquitetura e práticas recomendadas — com foco em segurança e responsabilidade.

---

## Visão
VVBD quer ser uma ferramenta amigável que:
- Aceita arquivos `.zip` assinados e verificáveis;
- Permite transferência via cabo (ADB / sideload) sem corromper o dispositivo;
- Fornece interface para seleção, verificação e backup antes de qualquer operação de escrita;
- Mostra logs e suporte a rollback (quando possível), minimizando riscos.

**Obs:** VVBD **não** fará operações automáticas para contornar proteções do fabricante (ex.: unlock/disable Verified Boot). Operações que alterem o estado de segurança do aparelho devem ser feitas manualmente e com consentimento explícito do usuário.

---

## Funcionalidades principais (proposta)
- Upload/recepção de `.zip` via cabo (desktop ↔ dispositivo) usando protocolos padrão (ADB / ADB sideload).
- Verificação de integridade (SHA256) e validação de assinatura digital do zip antes de qualquer ação.
- Interface que mostra exatamente quais partições/arquivos serão afetados e pede confirmação explícita.
- Backup automático opcional das partições críticas (boot, system, vendor) antes de qualquer modificação.
- Modo “simulação” (dry-run) para validar o pacote sem escrever nada.
- Logs detalhados e opção de exportar relatório de instalação/erro.
- Sistema de permissões e chaves: apenas pacotes assinados por chaves aprovadas podem ser instalados (opcional/administrável).

---

## Componentes do projeto
1. **App Android (cliente)**
   - Interface para selecionar zips, calcular checksum, exibir informações do pacote e enviar para o desktop.
   - Pode operar em modo “recovery-friendly”: instruir o usuário a reiniciar em recovery para aplicação do pacote (sem executar flashes por conta própria).

2. **Desktop Helper (CLI/GUI)**
   - Detecta dispositivo via ADB; quando em modo recovery, oferece `adb sideload` para enviar o zip.
   - Valida assinaturas e checksums localmente.
   - Opcionalmente cria backups com `adb pull` das partições permitidas antes do envio.

3. **Recovery Seguro (opcional)**
   - Uma imagem recovery customizada (conceito) que pode aplicar zips assinados, criar snapshots e suportar rollback.
   - **Nota:** Distribuir/usar um recovery customizado requer que o usuário entenda as consequências (unlock do bootloader, perda de garantia).

4. **Repositório / Servidor de Assinaturas (opcional)**
   - Onde as chaves públicas confiáveis são mantidas para verificação de pacotes.

---

## Segurança e limitações (LEIA COM ATENÇÃO)
- **Riscos técnicos:** Instalar ou modificar firmwares pode causar perda de dados ou "brick". Testar apenas em dispositivos de desenvolvimento.
- **Proteções do fabricante:** Muitos dispositivos possuem Verified Boot, bootloader bloqueado e assinaturas obrigatórias — VVBD **não** remove essas proteções automaticamente.
- **Privacidade:** Sempre peça consentimento do dono do dispositivo antes de qualquer operação.
- **Legalidade:** Dependendo do país, alterar software do dispositivo pode invalidar garantias ou violar termos; o usuário é responsável por ações feitas no seu hardware.
- VVBD será projetado para **minimizar risco**, exigindo confirmações explícitas, assinaturas válidas e backups antes de prosseguir.

---

## Fluxo de uso sugerido (seguro, manual)
1. Usuário gera/obtém um pacote `.zip` assinado.
2. No desktop: verificar assinatura e checksum.  
3. Colocar o dispositivo em modo recovery (ou modo que aceite sideload) seguindo as instruções do fabricante.
4. Desktop Helper detecta o dispositivo e pergunta ao usuário para confirmar backup.
5. Se confirmado, Desktop Helper faz backup das partições suportadas.
6. Desktop Helper efetua `adb sideload` para transferir o `.zip` para o dispositivo. **A aplicação do pacote é realizada pelo recovery do dispositivo**, não pela ferramenta automaticamente.
7. Após a operação, logs são coletados e o usuário é orientado a reiniciar e testar.

---

## Boas práticas para desenvolvedores
- Use assinaturas digitais para garantir origem e integridade dos pacotes.
- Sempre implemente `dry-run` e previews antes de escrita em partições.
- Automatize backups e verificação de integridade pós-restore.
- Foque a UX em clareza: mostre ao usuário o que será feito e os riscos envolvidos.
- Teste amplamente em dispositivos com diferentes vendors e vendor-specific behaviors.

---

## Roadmap (exemplo)
- v0.1 — Prova de conceito: Desktop Helper (CLI) que valida zips e faz `adb sideload` manual.
- v0.2 — App Android: interface para envio e verificação de zips (sem flash automático).
- v0.3 — Recovery companion (opcional): recovery que verifica assinatura e suporta rollback.
- v1.0 — Sistema de chaves/assinaturas e GUI amigável no desktop.

---

## Contribuição
Contribuições são bem-vindas, desde que sigam as orientações de segurança do projeto. Ao abrir PRs, inclua testes, descrições de riscos e notas sobre compatibilidade de dispositivos.
