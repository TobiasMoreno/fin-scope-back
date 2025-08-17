package tobias.moreno.fin.scope.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tobias.moreno.fin.scope.dto.investments.*;
import tobias.moreno.fin.scope.entities.InvestmentEntity;
import tobias.moreno.fin.scope.models.InvestmentType;
import tobias.moreno.fin.scope.repositories.InvestmentRepository;
import tobias.moreno.fin.scope.services.interfaces.InvestmentService;

import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import tobias.moreno.fin.scope.services.interfaces.IYahooFinanceService;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class InvestmentServiceImpl implements InvestmentService {

    private final InvestmentRepository investmentRepository;
    private final ModelMapper modelMapper;
    private final BinanceService binanceService;
    private final IYahooFinanceService yahooFinanceService;

    @Override
    public List<InvestmentDTO> findAllByUserId(Long userId) {
        log.info("Buscando todas las inversiones para el usuario: {}", userId);
        List<InvestmentEntity> investments = investmentRepository.findByUserId(userId);
        return investments.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<InvestmentDTO> findAllByUserId(Long userId, Pageable pageable) {
        log.info("Buscando inversiones paginadas para el usuario: {}, página: {}", userId, pageable.getPageNumber());
        Page<InvestmentEntity> investments = investmentRepository.findByUserId(userId, pageable);
        return investments.map(this::convertToDTO);
    }

    @Override
    public Optional<InvestmentDTO> findByIdAndUserId(Long id, Long userId) {
        log.info("Buscando inversión con ID: {} para el usuario: {}", id, userId);
        return investmentRepository.findByIdAndUserId(id, userId)
                .map(this::convertToDTO);
    }

    @Override
    public InvestmentDTO create(CreateInvestmentDTO createDto, Long userId) {
        log.info("Creando/actualizando inversión para el usuario: {}", userId);
        
        // Buscar si ya existe una inversión con el mismo símbolo
        Optional<InvestmentEntity> existingInvestment = 
            investmentRepository.findByUserIdAndSymbol(userId, createDto.getSymbol().toUpperCase());
        
        if (existingInvestment.isPresent()) {
            // Actualizar inversión existente
            log.info("Inversión existente encontrada para el símbolo: {}, actualizando...", createDto.getSymbol());
            return updateExistingInvestment(existingInvestment.get(), createDto);
        } else {
            // Crear nueva inversión
            log.info("Creando nueva inversión para el símbolo: {}", createDto.getSymbol());
            return createNewInvestment(createDto, userId);
        }
    }

    @Override
    public InvestmentDTO update(Long id, UpdateInvestmentDTO updateDto, Long userId) {
        log.info("Actualizando inversión con ID: {} para el usuario: {}", id, userId);
        
        InvestmentEntity investment = investmentRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new EntityNotFoundException("Inversión no encontrada con ID: " + id));

        // Actualizar campos si no son null
        if (updateDto.getSymbol() != null) {
            investment.setSymbol(updateDto.getSymbol().toUpperCase());
        }
        if (updateDto.getName() != null) {
            investment.setName(updateDto.getName());
        }
        if (updateDto.getQuantity() != null) {
            investment.setQuantity(updateDto.getQuantity());
        }
        if (updateDto.getLastPrice() != null) {
            investment.setLastPrice(updateDto.getLastPrice());
        }
        if (updateDto.getDailyVariationPercent() != null) {
            investment.setDailyVariationPercent(updateDto.getDailyVariationPercent());
        }
        if (updateDto.getDailyVariationAmount() != null) {
            investment.setDailyVariationAmount(updateDto.getDailyVariationAmount());
        }
        if (updateDto.getCostPerShare() != null) {
            investment.setCostPerShare(updateDto.getCostPerShare());
        }
        if (updateDto.getType() != null) {
            investment.setType(updateDto.getType());
        }

        // Recalcular valores automáticamente
        calculateInvestmentValues(investment);
        
        InvestmentEntity updated = investmentRepository.save(investment);
        log.info("Inversión actualizada exitosamente con ID: {}", updated.getId());
        
        return convertToDTO(updated);
    }

    @Override
    public void deleteById(Long id, Long userId) {
        log.info("Eliminando inversión con ID: {} para el usuario: {}", id, userId);
        
        if (!investmentRepository.existsByIdAndUserId(id, userId)) {
            throw new EntityNotFoundException("Inversión no encontrada con ID: " + id);
        }
        
        investmentRepository.deleteById(id);
        log.info("Inversión eliminada exitosamente con ID: {}", id);
    }

    @Override
    public List<InvestmentDTO> findByUserIdAndType(Long userId, InvestmentType type) {
        log.info("Buscando inversiones de tipo: {} para el usuario: {}", type, userId);
        List<InvestmentEntity> investments = investmentRepository.findByUserIdAndType(userId, type);
        return investments.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<InvestmentDTO> findByUserIdAndType(Long userId, InvestmentType type, Pageable pageable) {
        log.info("Buscando inversiones de tipo: {} paginadas para el usuario: {}", type, userId);
        Page<InvestmentEntity> investments = investmentRepository.findByUserIdAndType(userId, type, pageable);
        return investments.map(this::convertToDTO);
    }

    @Override
    public Page<InvestmentDTO> searchByUserIdAndSymbolOrName(Long userId, String search, Pageable pageable) {
        log.info("Buscando inversiones con término: '{}' para el usuario: {}", search, userId);
        Page<InvestmentEntity> investments = investmentRepository.findByUserIdAndSymbolOrNameContainingIgnoreCase(userId, search, pageable);
        return investments.map(this::convertToDTO);
    }

    @Override
    public DashboardDataDTO getDashboardData(Long userId) {
        log.info("Generando datos del dashboard con precios en tiempo real para el usuario: {}", userId);
        
        // Obtener inversiones con precios en tiempo real desde Binance
        List<InvestmentDTO> investmentsWithRealTimePrices = getInvestmentsWithRealTimePrices(userId);
        
        // Agrupar por tipo
        List<DashboardSectionDTO> sections = List.of(InvestmentType.values()).stream()
                .map(type -> createDashboardSectionWithRealTimePrices(type, investmentsWithRealTimePrices))
                .filter(section -> !section.getInvestments().isEmpty())
                .collect(Collectors.toList());

        // Calcular totales con precios en tiempo real
        BigDecimal totalDailyVariation = investmentsWithRealTimePrices.stream()
                .map(inv -> inv.getDailyVariationAmount() != null ? inv.getDailyVariationAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalGainLoss = investmentsWithRealTimePrices.stream()
                .map(inv -> inv.getGainLossAmount() != null ? inv.getGainLossAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        TotalVariationDTO totalVariation = TotalVariationDTO.builder()
                .daily(totalDailyVariation)
                .total(totalGainLoss)
                .build();

        return DashboardDataDTO.builder()
                .sections(sections)
                .totalVariation(totalVariation)
                .currency("USD")
                .build();
    }

    @Override
    public InvestmentSummaryDTO getSummary(Long userId) {
        log.info("Generando resumen de inversiones con precios en tiempo real para el usuario: {}", userId);
        
        // Obtener inversiones con precios en tiempo real
        List<InvestmentDTO> investmentsWithRealTimePrices = getInvestmentsWithRealTimePrices(userId);
        
        // Calcular totales con precios en tiempo real
        BigDecimal totalCurrent = investmentsWithRealTimePrices.stream()
                .map(inv -> inv.getTotal() != null ? inv.getTotal() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalGainLoss = investmentsWithRealTimePrices.stream()
                .map(inv -> inv.getGainLossAmount() != null ? inv.getGainLossAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalDailyVariation = investmentsWithRealTimePrices.stream()
                .map(inv -> inv.getDailyVariationAmount() != null ? inv.getDailyVariationAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Calcular total invertido (suma de costPerShare * quantity)
        List<InvestmentEntity> investments = investmentRepository.findByUserId(userId);
        BigDecimal totalInvested = investments.stream()
                .map(inv -> inv.getCostPerShare().multiply(inv.getQuantity()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Calcular porcentaje de ganancia
        BigDecimal gainPercentage = totalInvested.compareTo(BigDecimal.ZERO) > 0 ?
                totalGainLoss.divide(totalInvested, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)) :
                BigDecimal.ZERO;

        return InvestmentSummaryDTO.builder()
                .totalInvested(totalInvested)
                .totalCurrent(totalCurrent)
                .totalGain(totalGainLoss)
                .gainPercentage(gainPercentage)
                .dailyVariation(totalDailyVariation)
                .currency("USD")
                .build();
    }

    @Override
    public void recalculateInvestmentValues(Long investmentId) {
        log.info("Recalculando valores para la inversión: {}", investmentId);
        
        InvestmentEntity investment = investmentRepository.findById(investmentId)
                .orElseThrow(() -> new EntityNotFoundException("Inversión no encontrada con ID: " + investmentId));
        
        calculateInvestmentValues(investment);
        investmentRepository.save(investment);
    }

    @Override
    public void recalculateAllInvestmentValues(Long userId) {
        log.info("Recalculando valores para todas las inversiones del usuario: {}", userId);
        
        List<InvestmentEntity> investments = investmentRepository.findByUserId(userId);
        investments.forEach(this::calculateInvestmentValues);
        investmentRepository.saveAll(investments);
    }

    // Métodos privados de ayuda
    private void calculateInvestmentValues(InvestmentEntity investment) {
        // Calcular total (quantity * lastPrice)
        BigDecimal total = investment.getQuantity().multiply(investment.getLastPrice());
        investment.setTotal(total);

        // Calcular ganancia/pérdida
        BigDecimal totalCost = investment.getQuantity().multiply(investment.getCostPerShare());
        BigDecimal gainLossAmount = total.subtract(totalCost);
        investment.setGainLossAmount(gainLossAmount);

        // Calcular porcentaje de ganancia/pérdida
        if (totalCost.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal gainLossPercent = gainLossAmount.divide(totalCost, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
            investment.setGainLossPercent(gainLossPercent);
        } else {
            investment.setGainLossPercent(BigDecimal.ZERO);
        }

        // Calcular variación diaria en monto si no está establecida
        if (investment.getDailyVariationAmount() == null && investment.getDailyVariationPercent() != null) {
            BigDecimal dailyVariationAmount = total.multiply(investment.getDailyVariationPercent())
                    .divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP);
            investment.setDailyVariationAmount(dailyVariationAmount);
        }
    }

    private DashboardSectionDTO createDashboardSectionWithRealTimePrices(InvestmentType type, List<InvestmentDTO> allInvestments) {
        List<InvestmentDTO> typeInvestments = allInvestments.stream()
                .filter(inv -> inv.getType() == type)
                .collect(Collectors.toList());

        BigDecimal sectionTotal = typeInvestments.stream()
                .map(inv -> inv.getTotal() != null ? inv.getTotal() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return DashboardSectionDTO.builder()
                .type(type)
                .total(sectionTotal)
                .investments(typeInvestments)
                .build();
    }

    private InvestmentDTO convertToDTO(InvestmentEntity entity) {
        return modelMapper.map(entity, InvestmentDTO.class);
    }
    
    // Implementación de nuevos métodos para el enfoque híbrido
    
    @Override
    public ConsolidatedDashboardDataDTO getConsolidatedDashboardData(Long userId) {
        log.info("Generando dashboard consolidado para el usuario: {}", userId);
        
        List<ConsolidatedInvestmentDTO> consolidatedInvestments = getConsolidatedInvestments(userId);
        
        // Agrupar por tipo
        List<ConsolidatedDashboardSectionDTO> sections = List.of(InvestmentType.values()).stream()
                .map(type -> createConsolidatedDashboardSection(type, consolidatedInvestments))
                .filter(section -> !section.getInvestments().isEmpty())
                .collect(Collectors.toList());

        // Calcular totales
        BigDecimal totalDailyVariation = consolidatedInvestments.stream()
                .map(inv -> inv.getTotalDailyVariation() != null ? inv.getTotalDailyVariation() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalGainLoss = consolidatedInvestments.stream()
                .map(inv -> inv.getTotalGainLoss() != null ? inv.getTotalGainLoss() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        TotalVariationDTO totalVariation = TotalVariationDTO.builder()
                .daily(totalDailyVariation)
                .total(totalGainLoss)
                .build();

        return ConsolidatedDashboardDataDTO.builder()
                .sections(sections)
                .totalVariation(totalVariation)
                .currency("USD")
                .build();
    }
    
    @Override
    public List<ConsolidatedInvestmentDTO> getConsolidatedInvestments(Long userId) {
        log.info("Obteniendo inversiones consolidadas para el usuario: {}", userId);
        
        List<Object[]> consolidatedData = investmentRepository.getConsolidatedInvestmentsByUserId(userId);
        
        return consolidatedData.stream()
                .map(this::convertToConsolidatedDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<InvestmentDTO> getInvestmentHistory(Long userId, String symbol) {
        log.info("Obteniendo historial de inversiones para el usuario: {} y símbolo: {}", userId, symbol);
        
        List<InvestmentEntity> history = investmentRepository.findHistoryByUserIdAndSymbol(userId, symbol.toUpperCase());
        
        return history.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<String> getDistinctSymbols(Long userId) {
        log.info("Obteniendo símbolos únicos para el usuario: {}", userId);
        
        return investmentRepository.findDistinctSymbolsByUserId(userId);
    }
    
    // Métodos privados de ayuda para el enfoque híbrido
    
    private ConsolidatedInvestmentDTO convertToConsolidatedDTO(Object[] data) {
        String symbol = (String) data[0];
        String name = (String) data[1];
        InvestmentType type = (InvestmentType) data[2];
        BigDecimal totalQuantity = (BigDecimal) data[3];
        BigDecimal averageLastPrice = (BigDecimal) data[4];
        BigDecimal averageCostPerShare = (BigDecimal) data[5];
        BigDecimal totalValue = (BigDecimal) data[6];
        BigDecimal totalGainLoss = (BigDecimal) data[7];
        BigDecimal totalDailyVariation = (BigDecimal) data[8];
        
        // Calcular porcentajes
        BigDecimal gainLossPercent = averageCostPerShare.compareTo(BigDecimal.ZERO) > 0 ?
                totalGainLoss.divide(averageCostPerShare.multiply(totalQuantity), 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100)) : BigDecimal.ZERO;
        
        BigDecimal dailyVariationPercent = totalValue.compareTo(BigDecimal.ZERO) > 0 ?
                totalDailyVariation.divide(totalValue, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100)) : BigDecimal.ZERO;
        
        return ConsolidatedInvestmentDTO.builder()
                .symbol(symbol)
                .name(name)
                .type(type)
                .totalQuantity(totalQuantity)
                .averageLastPrice(averageLastPrice)
                .averageCostPerShare(averageCostPerShare)
                .totalValue(totalValue)
                .totalGainLoss(totalGainLoss)
                .totalDailyVariation(totalDailyVariation)
                .gainLossPercent(gainLossPercent)
                .dailyVariationPercent(dailyVariationPercent)
                .build();
    }
    
    private ConsolidatedDashboardSectionDTO createConsolidatedDashboardSection(InvestmentType type, List<ConsolidatedInvestmentDTO> allInvestments) {
        List<ConsolidatedInvestmentDTO> typeInvestments = allInvestments.stream()
                .filter(inv -> inv.getType() == type)
                .collect(Collectors.toList());

        return ConsolidatedDashboardSectionDTO.builder()
                .type(type)
                .investments(typeInvestments)
                .build();
    }
    
    
    /**
     * Actualiza una inversión existente con nueva compra
     */
    private InvestmentDTO updateExistingInvestment(InvestmentEntity existing, CreateInvestmentDTO newPurchase) {
        log.info("Actualizando inversión existente: {} con nueva compra", existing.getSymbol());
        
        // Calcular nueva cantidad total
        BigDecimal newTotalQuantity = existing.getQuantity().add(newPurchase.getQuantity());
        
        // Calcular nuevo PPC promedio ponderado
        BigDecimal existingTotalCost = existing.getQuantity().multiply(existing.getCostPerShare());
        BigDecimal newTotalCost = newPurchase.getQuantity().multiply(newPurchase.getLastPrice());
        BigDecimal totalCost = existingTotalCost.add(newTotalCost);
        BigDecimal newAverageCost = totalCost.divide(newTotalQuantity, 4, RoundingMode.HALF_UP);
        
        log.info("PPC anterior: {}, PPC nuevo: {}", existing.getCostPerShare(), newAverageCost);
        
        // Actualizar valores
        existing.setQuantity(newTotalQuantity);
        existing.setCostPerShare(newAverageCost);
        existing.setLastPrice(newPurchase.getLastPrice()); // Precio actual = último precio enviado
        
        // Recalcular valores automáticamente
        calculateInvestmentValues(existing);
        
        InvestmentEntity updated = investmentRepository.save(existing);
        log.info("Inversión actualizada exitosamente con ID: {}", updated.getId());
        
        return convertToDTO(updated);
    }
    
    /**
     * Crea una nueva inversión
     */
    private InvestmentDTO createNewInvestment(CreateInvestmentDTO createDto, Long userId) {
        log.info("Creando nueva inversión para el símbolo: {}", createDto.getSymbol());
        
        // Generar nombre automáticamente si no se proporciona
        String name = createDto.getName();
        
        // Detectar tipo automáticamente
        // Para nueva inversión, costPerShare = lastPrice inicialmente
        InvestmentEntity investment = InvestmentEntity.builder()
                .userId(userId)
                .symbol(createDto.getSymbol().toUpperCase())
                .name(name)
                .quantity(createDto.getQuantity())
                .lastPrice(createDto.getLastPrice())
                .costPerShare(createDto.getLastPrice()) // ← PPC = precio actual para nueva inversión
                .type(createDto.getType())
                .dailyVariationPercent(BigDecimal.ZERO)
                .dailyVariationAmount(BigDecimal.ZERO)
                .build();

        // Calcular valores automáticamente
        calculateInvestmentValues(investment);
        
        InvestmentEntity saved = investmentRepository.save(investment);
        log.info("Nueva inversión creada exitosamente con ID: {}", saved.getId());
        
        return convertToDTO(saved);
    }
    
    @Override
    public Map<String, Object> updatePricesFromBinance(Long userId) {
        log.info("Actualizando precios desde Binance para el usuario: {}", userId);
        
        List<InvestmentEntity> investments = investmentRepository.findByUserId(userId);
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> updatedInvestments = new ArrayList<>();
        int updatedCount = 0;
        
        for (InvestmentEntity investment : investments) {
            try {
                // Obtener precio actual desde Binance
                BigDecimal currentPrice = getCurrentPriceFromBinance(investment);
                
                if (currentPrice != null && !currentPrice.equals(investment.getLastPrice())) {
                    // Actualizar precio
                    investment.setLastPrice(currentPrice);
                    
                    // Recalcular valores
                    calculateInvestmentValues(investment);
                    
                    // Guardar cambios
                    investmentRepository.save(investment);
                    
                    // Agregar a resultados
                    Map<String, Object> investmentResult = new HashMap<>();
                    investmentResult.put("symbol", investment.getSymbol());
                    investmentResult.put("oldPrice", investment.getLastPrice());
                    investmentResult.put("newPrice", currentPrice);
                    investmentResult.put("gainLossAmount", investment.getGainLossAmount());
                    investmentResult.put("gainLossPercent", investment.getGainLossPercent());
                    updatedInvestments.add(investmentResult);
                    
                    updatedCount++;
                    log.info("Precio actualizado para {}: {} -> {}", 
                            investment.getSymbol(), investment.getLastPrice(), currentPrice);
                }
            } catch (Exception e) {
                log.error("Error actualizando precio para {}: {}", investment.getSymbol(), e.getMessage());
            }
        }
        
        result.put("updatedCount", updatedCount);
        result.put("totalInvestments", investments.size());
        result.put("updatedInvestments", updatedInvestments);
        result.put("message", "Precios actualizados exitosamente");
        
        return result;
    }
    
    /**
     * Obtiene inversiones con precios en tiempo real desde Binance
     */
    @Override
    public List<InvestmentDTO> getInvestmentsWithRealTimePrices(Long userId) {
        log.info("Obteniendo inversiones con precios en tiempo real para el usuario: {}", userId);
        
        List<InvestmentEntity> investments = investmentRepository.findByUserId(userId);
        List<InvestmentDTO> result = new ArrayList<>();
        
        for (InvestmentEntity investment : investments) {
            try {
                // Obtener precio actual desde Binance
                BigDecimal realTimePrice = getCurrentPriceFromBinance(investment);
                
                if (realTimePrice != null) {
                    // Crear copia temporal para cálculos
                    InvestmentEntity tempInvestment = new InvestmentEntity();
                    tempInvestment.setQuantity(investment.getQuantity());
                    tempInvestment.setCostPerShare(investment.getCostPerShare());
                    tempInvestment.setLastPrice(realTimePrice);
                    
                    // Calcular valores con precio real
                    calculateInvestmentValues(tempInvestment);
                    
                    // Convertir a DTO con precio real
                    InvestmentDTO dto = convertToDTO(investment);
                    dto.setLastPrice(realTimePrice);
                    dto.setTotal(tempInvestment.getTotal());
                    dto.setGainLossAmount(tempInvestment.getGainLossAmount());
                    dto.setGainLossPercent(tempInvestment.getGainLossPercent());
                    
                    result.add(dto);
                } else {
                    // Si no se puede obtener precio real, usar el guardado
                    result.add(convertToDTO(investment));
                }
            } catch (Exception e) {
                log.error("Error obteniendo precio real para {}: {}", investment.getSymbol(), e.getMessage());
                // Usar precio guardado como fallback
                result.add(convertToDTO(investment));
            }
        }
        
        return result;
    }
    
    /**
     * Obtiene el precio actual desde Binance o Yahoo Finance según el tipo de inversión
     */
    private BigDecimal getCurrentPriceFromBinance(InvestmentEntity investment) {
        String symbol = investment.getSymbol();
        // Detectar el tipo de inversión basado en el símbolo
        if (investment.getType() == InvestmentType.CRIPTO) {
            // Para criptomonedas, usar Binance
            try {
                String response = binanceService.getSymbol(symbol);
                log.info("Respuesta de Binance para {}: {}", symbol, response);
                
                // Parsear la respuesta JSON {"symbol":"BTCUSDT","price":"117558.21000000"}
                if (response != null && response.contains("\"price\":\"")) {
                    String priceStr = response.split("\"price\":\"")[1].split("\"")[0];
                    return new BigDecimal(priceStr);
                } else {
                    log.error("Formato de respuesta inesperado para {}: {}", symbol, response);
                    return null;
                }
            } catch (Exception e) {
                log.error("Error obteniendo precio de Binance para {}: {}", symbol, e.getMessage());
                return null;
            }
        } else {
            // Para acciones y CEDEARs, usar Yahoo Finance
            if(investment.getType() == InvestmentType.CEDEAR) {
                symbol = symbol + ".BA";
            }
            try {
                BigDecimal price = yahooFinanceService.getCurrentPrice(symbol);
                log.info("Precio obtenido de Yahoo Finance para {}: {}", symbol, price);
                return price;
            } catch (Exception e) {
                log.error("Error obteniendo precio de Yahoo Finance para {}: {}", symbol, e.getMessage());
                return null;
            }
        }
    }
}
