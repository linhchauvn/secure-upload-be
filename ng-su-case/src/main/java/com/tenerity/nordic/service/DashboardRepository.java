package com.tenerity.nordic.service;

import com.tenerity.nordic.dto.DashboardSearchParameter;
import com.tenerity.nordic.entity.Case;
import com.tenerity.nordic.entity.Workspace;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class DashboardRepository {
    @PersistenceContext
    private EntityManager em;

    public List<Case> searchCases(DashboardSearchParameter param) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Case> cr = cb.createQuery(Case.class);
        Root<Case> root = cr.from(Case.class);
        cr.select(root);

        List<Predicate> predicates = new ArrayList<>();
        if (StringUtils.isNotBlank(param.getKeyword())) {
            Predicate searchSO = cb.like(cb.lower(root.get("superOfficeID")),"%"+param.getKeyword().toLowerCase()+"%");
            Predicate searchEmail = cb.like(cb.lower(root.get("customerEmail")),"%"+param.getKeyword().toLowerCase()+"%");
            Predicate searchAgent = root.get("assignedAgent").in(param.getAgentFilteredIds());
            Predicate searchClient = root.get("clientId").in(param.getClientFilteredIds());
            predicates.add(cb.or(searchSO, searchEmail, searchAgent, searchClient));
        }
        if (param.getAgentId() != null) {
            predicates.add(cb.equal(root.get("assignedAgent"), param.getAgentId()));
        }
        if (param.getThirdPartyId() != null) {
            CriteriaQuery<Workspace> crW = cb.createQuery(Workspace.class);
            Root<Workspace> rootW = crW.from(Workspace.class);
            crW.select(rootW);
            crW.where(cb.equal(rootW.get("thirdPartyId"), param.getThirdPartyId()));
            List<Workspace> workspaces = em.createQuery(crW).getResultList();
            if (!workspaces.isEmpty()) {
                List<UUID> referenceCaseId = workspaces.stream().map(item -> item.getCasee().getId()).collect(Collectors.toList());
                predicates.add(root.get("id").in(referenceCaseId));
            }
            else {
                return new ArrayList<>();
            }
        }
        if (param.getStatus() != null) {
            predicates.add(cb.equal(root.get("status"),param.getStatus()));
        }

        LocalDateTime twentyFourHoursAgo = LocalDateTime.now().minusHours(24);
        LocalDateTime seventyTwoHoursAgo = LocalDateTime.now().minusHours(72);
        Set<DayOfWeek> excludeWeekendDays = new HashSet<>(Arrays.asList(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY));
        if (excludeWeekendDays.contains(LocalDateTime.now().getDayOfWeek())) {
            seventyTwoHoursAgo = seventyTwoHoursAgo.minusHours(24);
        }
        switch (param.getCaseStatistic()) {
            case NEW_CASE:
                predicates.add(cb.or(cb.isNull(root.get("needAgentNotification")), cb.isFalse(root.get("needAgentNotification"))));
                break;
            case OPEN_CASE_EXCEED_24H:
                predicates.add(cb.isTrue(root.get("needAgentNotification")));
                predicates.add(cb.greaterThan(root.get("lastUpdated"), seventyTwoHoursAgo));
                break;
            case OPEN_CASE_EXCEED_72H:
                predicates.add(cb.isTrue(root.get("needAgentNotification")));
                predicates.add(cb.lessThan(root.get("lastUpdated"), seventyTwoHoursAgo));
                break;
            case ALL:
            case RESOLVED_CASE:
                if (param.getTimeFrom() != null) {
                    predicates.add(cb.and(cb.isNotNull(root.get("lastUpdated")),cb.greaterThan(root.get("lastUpdated"), param.getTimeFrom())));
                }
                if (param.getTimeTo() != null) {
                    predicates.add(cb.and(cb.isNotNull(root.get("lastUpdated")),cb.lessThan(root.get("lastUpdated"), param.getTimeTo())));
                }
                break;
        }

        cr.where(cb.and(predicates.toArray(new Predicate[0])));
        List<Case> allEntities = em.createQuery(cr).getResultList();
        return allEntities;
    }
}
