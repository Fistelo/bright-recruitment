package com.rds.brightrecruitment.persistence.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.rds.brightrecruitment.persistence.entities.Comment;

@Repository
public interface CommentRepository extends PagingAndSortingRepository<Comment, Long> {

}
