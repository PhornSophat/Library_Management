package com.library.library_system.repository;

import com.library.library_system.model.Loan;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LoanRepository extends MongoRepository<Loan, String> {
    List<Loan> findByMemberIdAndStatus(String memberId, String status);
    List<Loan> findByBookIdAndStatus(String bookId, String status);
    List<Loan> findByMemberId(String memberId);
}
