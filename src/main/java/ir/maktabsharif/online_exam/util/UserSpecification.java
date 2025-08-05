package ir.maktabsharif.online_exam.util;

import ir.maktabsharif.online_exam.model.Role;
import ir.maktabsharif.online_exam.model.User;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import java.util.ArrayList;
import java.util.List;

public class UserSpecification {
    public static Specification<User> searchByKeyword(String keyword) {
        return (Root<User> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            if (keyword == null || keyword.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            String pattern = "%" + keyword.toLowerCase() + "%";
            List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();

            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")), pattern));
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")), pattern));
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("username")), pattern));
            Join<User, Role> roleJoin = root.join("role");

            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(roleJoin.get("name")), pattern));

            return criteriaBuilder.or(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };
    }
}


