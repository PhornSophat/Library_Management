package com.library.library_system.borrowing;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface BorrowingRepository extends MongoRepository<Borrowing, String> {
    List<Borrowing> findByMemberId(String memberId);

    List<Borrowing> findByBookId(String bookId);

    List<Borrowing> findByStatus(String status);

    @Query("{ 'status': 'BORROWED', 'dueDate': { $lt: ?0 } }")
    List<Borrowing> findOverdueBooks(LocalDate date);
}
