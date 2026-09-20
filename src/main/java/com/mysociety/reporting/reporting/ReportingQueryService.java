package com.mysociety.reporting.reporting;

import com.mysociety.reporting.security.TenantContextHolder;
import java.util.List;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class ReportingQueryService {
    private final NamedParameterJdbcTemplate jdbc;
    private final TenantContextHolder tenants;
    public ReportingQueryService(NamedParameterJdbcTemplate jdbc, TenantContextHolder tenants) {
        this.jdbc = jdbc; this.tenants = tenants;
    }
    private MapSqlParameterSource tenant() {
        return new MapSqlParameterSource("societyId", tenants.current().societyId());
    }
    public List<UnitBalanceResponse> unitBalances() {
        return jdbc.query("""
                SELECT unit_id, total_invoiced, total_paid, total_outstanding, overdue_invoice_count
                FROM mysociety.v_unit_balances WHERE society_id = :societyId ORDER BY unit_id
                """, tenant(),
                (rs, row) -> new UnitBalanceResponse(rs.getObject("unit_id", java.util.UUID.class),
                        rs.getBigDecimal("total_invoiced"), rs.getBigDecimal("total_paid"),
                        rs.getBigDecimal("total_outstanding"), rs.getLong("overdue_invoice_count")));
    }
    public List<CollectionSummaryResponse> collections() {
        return jdbc.query("""
                SELECT month, billed_amount, collected_amount, outstanding_amount
                FROM mysociety.v_collection_summary_monthly WHERE society_id = :societyId ORDER BY month DESC
                """, tenant(),
                (rs, row) -> new CollectionSummaryResponse(rs.getObject("month", java.time.LocalDate.class),
                        rs.getBigDecimal("billed_amount"), rs.getBigDecimal("collected_amount"), rs.getBigDecimal("outstanding_amount")));
    }
    public List<ComplaintSlaResponse> complaintSla() {
        return jdbc.query("""
                SELECT id, complaint_number, priority, status, sla_due_at, sla_status
                FROM mysociety.v_complaint_sla_status WHERE society_id = :societyId ORDER BY sla_due_at NULLS LAST
                """, tenant(),
                (rs, row) -> new ComplaintSlaResponse(rs.getObject("id", java.util.UUID.class), rs.getString("complaint_number"),
                        rs.getString("priority"), rs.getString("status"), rs.getObject("sla_due_at", java.time.Instant.class), rs.getString("sla_status")));
    }
    public List<CurrentVisitorResponse> currentVisitors() {
        return jdbc.query("""
                SELECT entry_id, unit_id, check_in_at, checked_in_by FROM mysociety.v_current_visitors
                WHERE society_id = :societyId ORDER BY check_in_at DESC
                """, tenant(),
                (rs, row) -> new CurrentVisitorResponse(rs.getObject("entry_id", java.util.UUID.class), rs.getObject("unit_id", java.util.UUID.class),
                        rs.getObject("check_in_at", java.time.Instant.class), rs.getObject("checked_in_by", java.util.UUID.class)));
    }
}
