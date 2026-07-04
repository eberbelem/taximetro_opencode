# Permissões - Taxímetro Digital

## Permissões solicitadas

| Permissão | Obrigatória | Finalidade |
|-----------|-------------|------------|
| `android.permission.ACCESS_FINE_LOCATION` | Sim | GPS de alta precisão para cálculo de distância percorrida e velocidade |
| `android.permission.ACCESS_COARSE_LOCATION` | Não (fallback) | Usada apenas quando FINE_LOCATION não está disponível |
| `android.permission.FOREGROUND_SERVICE` | Sim | Necessária para manter o GPS ativo em background durante a corrida |
| `android.permission.FOREGROUND_SERVICE_LOCATION` | Sim (API 34+) | Tipo de serviço em foreground para localização |
| `android.permission.POST_NOTIFICATIONS` | Sim (API 33+) | Notificação persistente indicando "Corrida em andamento" |
| `android.permission.BLUETOOTH` | Sim | Descoberta de dispositivos Bluetooth |
| `android.permission.BLUETOOTH_ADMIN` | Sim | Gerenciamento de conexões Bluetooth |
| `android.permission.BLUETOOTH_CONNECT` | Sim (API 31+) | Conexão com impressora térmica Bluetooth |
| `android.permission.INTERNET` | Sim | Carregamento de mapas (Google Maps) |
| `android.permission.ACCESS_NETWORK_STATE` | Sim | Verificação de conectividade para mapas |

## Quando as permissões são solicitadas

1. **Localização**: Na primeira abertura do app (HomeScreen)
2. **Notificação**: Ao iniciar a primeira corrida (API 33+)
3. **Bluetooth**: Ao tentar imprimir recibo

## Comportamento sem permissões

- **Sem GPS**: App não inicia corrida. Botão desabilitado.
- **Sem notificação**: Serviço em foreground não funciona. Corrida pode ser interrompida pelo sistema.
- **Sem Bluetooth**: Impressão de recibo não funciona. Demais funcionalidades operam normalmente.
- **Sem internet**: Mapas não carregam. Cálculo da corrida funciona offline normalmente.
