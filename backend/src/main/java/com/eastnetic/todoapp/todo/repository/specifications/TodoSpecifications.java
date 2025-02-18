package com.eastnetic.todoapp.todo.repository.specifications;

import com.eastnetic.todoapp.common.util.DateTimeUtils;
import com.eastnetic.todoapp.todo.domain.entity.Todo;
import com.eastnetic.todoapp.todo.domain.request.TodoFilterRequest;
import com.eastnetic.todoapp.todo.enums.Status;
import com.eastnetic.todoapp.user.exception.UserNotFoundException;
import org.springframework.data.jpa.domain.Specification;

import static com.eastnetic.todoapp.common.util.SpecificationUtils.wildcardsAndLower;

public interface TodoSpecifications {
	
	static Specification<Todo> criteriaFilter(Long userId, TodoFilterRequest filterTodoRequest) {
		return (root, query, cb) -> {
			var predicates = cb.conjunction();
			if (userId == null) {
				throw new UserNotFoundException(userId);
			}
			
			predicates = cb.and(predicates, cb.equal(root.get("userId"), userId));
			
			if (filterTodoRequest.getName() != null) {
				predicates = cb.and(predicates,
				                    cb.like(root.get("name"), wildcardsAndLower(filterTodoRequest.getName())));
			}
			if (filterTodoRequest.getStatus() != null) {
				predicates = cb.and(predicates,
				                    cb.equal(root.get("status"), Status.fromName(filterTodoRequest.getStatus())));
			}
			if (filterTodoRequest.getDueDate() != null) {
				var greaterThanPredicate = cb.greaterThanOrEqualTo(root.get("dueDate"),
				                                                   filterTodoRequest.getDueDate());
				var lessThanPredicate = cb.lessThan(root.get("dueDate"),
				                                    DateTimeUtils.addDay(filterTodoRequest.getDueDate(), 1));
				
				predicates = cb.and(predicates, greaterThanPredicate, lessThanPredicate);
			}
			if (filterTodoRequest.getIsImportant() != null) {
				predicates = cb.and(predicates, cb.equal(root.get("isImportant"), filterTodoRequest.getIsImportant()));
			}
			
			return predicates;
		};
	}
}
