# Manual de Testes - Taxímetro Digital

## Permissões necessárias

O aplicativo solicita na primeira execução:

| Permissão | Finalidade |
|-----------|------------|
| `ACCESS_FINE_LOCATION` | Leitura precisa do GPS para cálculo de distância |
| `POST_NOTIFICATIONS` | Notificação do serviço em foreground (corrida ativa) |
| `BLUETOOTH_CONNECT` | Conexão com impressora térmica para recibo |

## Checklist de validação

### 0. Pré-configuração

- [ ] Configurar dados do motorista (nome) em Configurações > Motorista
- [ ] Configurar dados do veículo (modelo, placa, prefixo) em Configurações > Veículo
- [ ] (Opcional) Configurar MAC da impressora Bluetooth em Configurações > Impressora
- [ ] Verificar tarifas em Configurações > Tarifas (provisório para testes)

### 1. Instalação e primeira execução

- [ ] APK instala sem erros
- [ ] Aplicativo abre na HomeScreen
- [ ] Solicita permissão de localização
- [ ] Indicador GPS aparece vermelho "Aguardando sinal GPS..."
- [ ] Botão "INICIAR CORRIDA" desabilitado enquanto GPS não estiver pronto

### 2. GPS

- [ ] Em ambiente externo, GPS fica verde "GPS OK" em até 60s
- [ ] Botão "INICIAR CORRIDA" habilita quando GPS está OK
- [ ] Precisão GPS é exibida na barra superior (ex: "3.2m")
- [ ] Hora atualizada é exibida na barra superior

### 3. Seleção de bandeira

- [ ] Botão circular central mostra "BANDEIRA 1" (verde)
- [ ] Ao tocar, alterna para "BANDEIRA 2" (âmbar)
- [ ] Ao tocar novamente, volta para "BANDEIRA 1"
- [ ] Animação suave de transição entre bandeiras

### 4. Corrida

- [ ] Ao tocar "INICIAR CORRIDA", transiciona para tela de corrida
- [ ] Valor começa com o valor da bandeirada
- [ ] Valor é o maior elemento visual (~35% da tela)
- [ ] Distância, tempo e velocidade são atualizados em tempo real
- [ ] Bandeira selecionada persiste na tela de corrida

### 5. Cálculo INMETRO

- [ ] A cada ~150m percorridos (Bandeira 1), valor incrementa R$ 0,25
- [ ] A cada ~115m percorridos (Bandeira 2), valor incrementa R$ 0,25
- [ ] Em velocidade abaixo de 12 km/h, conta por tempo (~30s por fração)
- [ ] Ao trocar bandeira durante a corrida, cálculo continua sem erros
- [ ] Tempo parado é acumulado separadamente

### 6. Troca de bandeira durante corrida

- [ ] Toque no círculo central alterna bandeira instantaneamente
- [ ] Cálculo não é interrompido ou corrompido
- [ ] Distâncias acumuladas são corretas para cada bandeira

### 7. Encerrar corrida

- [ ] Botão "ENCERRAR CORRIDA" vermelho na parte inferior
- [ ] Ao tocar, diálogo de confirmação aparece
- [ ] Diálogo mostra valor total e resumo
- [ ] "Continuar Corrida" retorna sem encerrar
- [ ] "Encerrar" finaliza e vai para tela de resumo

### 8. Resumo da corrida

- [ ] Valor total exibido em destaque
- [ ] Bandeirada, distância e tempo são mostrados
- [ ] Valores separados por bandeira (se houve troca)
- [ ] Botão "Imprimir Recibo" presente
- [ ] Botão "Nova Corrida" retorna à HomeScreen

### 9. Histórico

- [ ] Corridas finalizadas aparecem no histórico
- [ ] Data, distância, tempo e valor são exibidos
- [ ] Ordenado por data decrescente

### 10. Configurações

- [ ] É possível alterar valores de bandeirada, tarifas e frações
- [ ] Após salvar, valores persistem entre execuções
- [ ] Valores inválidos são ignorados (mantém anterior)

### 11. Recuperação

- [ ] Ao perder GPS por >8 segundos, aparece "GPS PERDIDO" (vermelho)
- [ ] Ao retornar GPS, volta a "GPS OK" e corrida continua
- [ ] Dados da corrida são salvos a cada atualização do GPS

### 12. Recibo

- [ ] Recibo exibe nome do motorista
- [ ] Recibo exibe modelo, placa e prefixo do veículo
- [ ] Recibo exibe data, hora início, hora término, duração
- [ ] Recibo exibe endereço de origem (geocodificado)
- [ ] Recibo exibe endereço de destino (geocodificado)
- [ ] Recibo exibe bandeirada, bandeira 1 e/ou 2 com valores parciais
- [ ] Recibo exibe distância total e tempo parado
- [ ] Recibo exibe TOTAL em destaque
- [ ] Recibo exibe rodapé: "Taxímetro Digital v1.0.0" + data/hora emissão

### 13. Configurações

- [ ] Abrir Configurações pela HomeScreen
- [ ] Seção Motorista: campo "Nome do motorista" salva e persiste
- [ ] Seção Veículo: campos "Modelo", "Placa", "Prefixo" salvam e persistem
- [ ] Seção Impressora: campo "Endereço MAC Bluetooth" salva e persiste
- [ ] Seção Tarifas: valores editáveis salvam e persistem
- [ ] Após salvar, botão mostra "Salvo!" temporariamente

### 14. Logs de GPS

- [ ] Arquivos CSV são gerados em `gps_logs/`
- [ ] Cada linha contém: timestamp, latitude, longitude, speed, accuracy
- [ ] Arquivo separado por dia e ID da corrida

## Roteiro de teste em campo

### Teste 0: Configurações iniciais

1. Abrir app > Configurações
2. Preencher Nome do motorista, Modelo, Placa, Prefixo
3. Salvar
4. Voltar à HomeScreen

### Teste 1: Corrida simples (Bandeira 1)

1. Abrir app
2. Aguardar GPS OK
3. Manter Bandeira 1
4. INICIAR CORRIDA
5. Percorrer ~2 km em velocidade > 12 km/h
6. Verificar se o valor incrementa corretamente (R$ 0,25 a cada ~150m)
7. ENCERRAR CORRIDA
8. Verificar resumo

### Teste 2: Corrida com trânsito (tempo parado)

1. INICIAR CORRIDA
2. Percorrer trecho com trânsito, ficar parado ~1 min
3. Verificar se valor incrementa por tempo
4. Verificar se tempo parado foi registrado

### Teste 3: Troca de bandeira

1. INICIAR CORRIDA com Bandeira 1
2. Percorrer 1 km
3. Tocar no botão para Bandeira 2
4. Percorrer mais 1 km
5. ENCERRAR
6. Verificar valores separados por bandeira no resumo

### Teste 4: Recibo com endereços

1. INICIAR CORRIDA com internet disponível
2. Percorrer ~500m
3. ENCERRAR CORRIDA
4. No resumo, tocar "Imprimir Recibo"
5. Verificar se origem aparece como endereço (ex: "Av. Paulista, 1000, São Paulo")
6. Verificar se destino aparece como endereço
7. Verificar dados do motorista/veículo no recibo
8. Verificar rodapé com versão e data/hora

### Teste 5: Perda de GPS

1. INICIAR CORRIDA
2. Entrar em túnel ou local sem sinal GPS
3. Aguardar >8 segundos
4. Verificar indicador "GPS PERDIDO"
5. Sair do túnel
6. Verificar se GPS volta e corrida continua
