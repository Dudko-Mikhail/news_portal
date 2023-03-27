package by.dudko.newsportal.validation.validator;

import by.dudko.newsportal.validation.annotation.UniqueFieldValue;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class UniqueFieldValueValidator implements ConstraintValidator<UniqueFieldValue, Object> {
    private final EntityManagerFactory entityManagerFactory;
    private String fieldName;
    private Class<?> entityClass;
    private boolean isUnique;

    @Override
    public void initialize(UniqueFieldValue constraintAnnotation) {
        this.fieldName = constraintAnnotation.fieldName();
        this.entityClass = constraintAnnotation.entityClass();
        this.isUnique = constraintAnnotation.isUnique();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Object> query = cb.createQuery();
            Root<?> root = query.from(entityClass);
            query.select(root.get(fieldName));
            query.where(cb.equal(root.get(fieldName), value));
            List<Object> searchResult = entityManager.createQuery(query).getResultList();
            boolean isAbsent = searchResult.isEmpty();
            context.disableDefaultConstraintViolation();
            if (isUnique) {
                context.buildConstraintViolationWithTemplate("Field value is not unique")
                        .addConstraintViolation();
                return isAbsent;
            }
            context.buildConstraintViolationWithTemplate("Invalid field value")
                    .addConstraintViolation();
            return !isAbsent;
        }
    }
}
