package org.radarbase.management.web.rest;

import io.swagger.v3.oas.annotations.Parameter;
import org.radarbase.management.security.NotAuthorizedException;
import org.radarbase.management.service.AuditEventService;
import org.radarbase.management.service.AuthService;
import org.radarbase.management.web.rest.util.PaginationUtil;
import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tech.jhipster.web.util.ResponseUtil;

import java.time.LocalDate;
import java.util.List;

import static org.radarbase.auth.authorization.Permission.AUDIT_READ;

/**
 * REST controller for getting the audit events.
 */
@RestController
@RequestMapping("/management/audits")
public class AuditResource {
    private final AuditEventService auditEventService;
    private final AuthService authService;

    public AuditResource(AuditEventService auditEventService, AuthService authService) {
        this.auditEventService = auditEventService;
        this.authService = authService;
    }

    /**
     * GET  /audits : get a page of AuditEvents.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of AuditEvents in body
     */
    @GetMapping
    public ResponseEntity<List<AuditEvent>> getAll(@Parameter Pageable pageable)
            throws NotAuthorizedException {
        authService.checkPermission(AUDIT_READ);
        Page<AuditEvent> page = auditEventService.findAll(pageable);
        HttpHeaders headers = PaginationUtil
                .generatePaginationHttpHeaders(page, "/management/audits");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /audits : get a page of AuditEvents between the fromDate and toDate.
     *
     * @param fromDate the start of the time period of AuditEvents to get
     * @param toDate the end of the time period of AuditEvents to get
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of AuditEvents in body
     */

    @GetMapping(params = {"fromDate", "toDate"})
    public ResponseEntity<List<AuditEvent>> getByDates(
            @RequestParam(value = "fromDate") LocalDate fromDate,
            @RequestParam(value = "toDate") LocalDate toDate,
            @Parameter Pageable pageable) throws NotAuthorizedException {
        authService.checkPermission(AUDIT_READ);
        Page<AuditEvent> page = auditEventService
                .findByDates(fromDate.atTime(0, 0), toDate.atTime(23, 59), pageable);
        HttpHeaders headers = PaginationUtil
                .generatePaginationHttpHeaders(page, "/management/audits");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /audits/:id : get an AuditEvent by id.
     *
     * @param id the id of the entity to get
     * @return the ResponseEntity with status 200 (OK) and the AuditEvent in body, or status
     *     404 (Not Found)
     */
    @GetMapping("/{id:.+}")
    public ResponseEntity<AuditEvent> get(@PathVariable Long id) throws NotAuthorizedException {
        authService.checkPermission(AUDIT_READ);
        return ResponseUtil.wrapOrNotFound(auditEventService.find(id));
    }
}
