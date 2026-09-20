package com.mysociety.reporting.reporting;

import java.math.BigDecimal;
import java.time.LocalDate;
public record CollectionSummaryResponse(LocalDate month, BigDecimal billedAmount, BigDecimal collectedAmount,
                                        BigDecimal outstandingAmount) { }
