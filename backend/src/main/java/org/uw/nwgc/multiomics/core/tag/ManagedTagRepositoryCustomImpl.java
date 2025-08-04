package org.uw.nwgc.multiomics.core.tag;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.uw.nwgc.multiomics.core.enums.ManageTagType;
import jakarta.persistence.Tuple;

import java.util.List;
import java.util.UUID;

public class ManagedTagRepositoryCustomImpl implements ManagedTagRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<ManagedTagWithSampleCount> findWithSampleCountsByType(
            ManageTagType type,
            Specification<ManagedTag> spec,
            Pageable pageable
    ) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // Main query
        CriteriaQuery<Tuple> query = cb.createTupleQuery();
        Root<ManagedTag> tagRoot = query.from(ManagedTag.class);
        Join<Object, Object> sampleJoin = tagRoot.join("samples", JoinType.LEFT);

        Predicate typePredicate = cb.equal(tagRoot.get("type"), type);
        Predicate specPredicate = spec != null ? spec.toPredicate(tagRoot, query, cb) : cb.conjunction();

        query.multiselect(
                        tagRoot.get("id").alias("id"),
                        tagRoot.get("value").alias("value"),
                        tagRoot.get("description").alias("description"),
                        cb.count(sampleJoin.get("id")).alias("sampleCount")
                )
                .where(cb.and(typePredicate, specPredicate))
                .groupBy(tagRoot.get("id"))
                .orderBy(QueryUtils.toOrders(pageable.getSort(), tagRoot, cb)); // Spring utility for sorting

        List<Tuple> resultTuples = entityManager.createQuery(query)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        List<ManagedTagWithSampleCount> content = resultTuples.stream()
                .map(tuple -> (ManagedTagWithSampleCount) new ManagedTagWithSampleCountImpl(
                        tuple.get("id", UUID.class),
                        tuple.get("value", String.class),
                        tuple.get("description", String.class),
                        tuple.get("sampleCount", Long.class)
                ))
                .toList();

        // Count query
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<ManagedTag> countRoot = countQuery.from(ManagedTag.class);
        countQuery.select(cb.countDistinct(countRoot))
                .where(cb.and(
                        cb.equal(countRoot.get("type"), type),
                        spec != null ? spec.toPredicate(countRoot, countQuery, cb) : cb.conjunction()
                ));

        long total = entityManager.createQuery(countQuery).getSingleResult();

        return new PageImpl<>(content, pageable, total);
    }
}
