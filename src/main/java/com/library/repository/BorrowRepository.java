package com.library.repository;

import com.library.model.Borrow;
import com.library.model.Borrow.BorrowStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BorrowRepository extends MongoRepository<Borrow, String> {
    List<Borrow> findByMemberId(String memberId);
    List<Borrow> findByBookId(String bookId);
    List<Borrow> findByMemberIdAndStatus(String memberId, BorrowStatus status);
    Optional<Borrow> findFirstByBookIdAndStatus(String bookId, BorrowStatus status);
    List<Borrow> findByDueDateBeforeAndStatus(LocalDate date, BorrowStatus status);
    long countByStatus(BorrowStatus status);
}
