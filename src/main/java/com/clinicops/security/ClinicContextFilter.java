package com.clinicops.security;

import java.io.IOException;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.clinicops.common.exception.AuthorizationException;
import com.clinicops.domain.clinic.model.Clinic;
import com.clinicops.domain.clinic.model.ClinicStatus;
import com.clinicops.domain.clinic.model.MembershipStatus;
import com.clinicops.domain.clinic.repository.ClinicMemberRepository;
import com.clinicops.domain.clinic.repository.ClinicRepository;
import com.clinicops.domain.organization.model.Organization;
import com.clinicops.domain.organization.model.OrganizationStatus;
import com.clinicops.domain.organization.repository.OrganizationRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class ClinicContextFilter extends OncePerRequestFilter {

    private final ClinicMemberRepository clinicMemberRepository;
    private final ClinicRepository clinicRepository;
    private final OrganizationRepository organizationRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws IOException, ServletException {

        if (request.getRequestURI().startsWith("/auth")
                || "/me".equals(request.getRequestURI())) {

            filterChain.doFilter(request, response);
            return;
        }

        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication != null
                && authentication.getPrincipal() instanceof AuthenticatedUser user)) {

            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthenticated");
            return;
        }

        ObjectId userId = user.getUserId();
        ObjectId clinicId = user.getClinicId();

        if (clinicId == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Clinic context missing");
            return;
        }

        boolean validMembership =
                clinicMemberRepository.existsByUserIdAndClinicIdAndStatusAndDeletedFalse(
                        userId,
                        clinicId,
                        MembershipStatus.ACTIVE
                );

        if (!validMembership) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid clinic access");
            return;
        }

        Optional<Clinic> clinic;
		clinic = clinicRepository.findById(clinicId);

        if (clinic != null && clinic.get().getStatus() != ClinicStatus.ACTIVE) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Clinic inactive");
            return;
        }

        Optional<Organization> org = organizationRepository.findById(clinic.get().getOrganizationId());

        if (org!=null && org.get().getStatus() != OrganizationStatus.ACTIVE) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Organization inactive");
            return;
        }

        request.setAttribute("CLINIC_ID", clinicId);

        filterChain.doFilter(request, response);
    }
}
