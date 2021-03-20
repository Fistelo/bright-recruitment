package com.rds.brightrecruitment.persistence.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.rds.brightrecruitment.persistence.entities.Book;

@Repository
public interface BookRepository extends PagingAndSortingRepository<Book, Long> {

}
