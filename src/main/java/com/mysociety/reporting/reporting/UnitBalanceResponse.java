package com.mysociety.reporting.reporting;

import java.math.BigDecimal;
import java.util.UUID;
public record UnitBalanceResponse(UUID unitId, BigDecimal totalInvoiced, BigDecimal totalPaid,
                                  BigDecimal totalOutstanding, long overdueInvoiceCount) { }
