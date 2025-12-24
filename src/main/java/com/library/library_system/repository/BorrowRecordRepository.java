package com.library.library_system.repository;

import com.library.library_system.model.BorrowRecord;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BorrowRecordRepository extends MongoRepository<BorrowRecord, String> {
    List<BorrowRecord> findByMemberId(String memberId);
    List<BorrowRecord> findByMemberIdAndStatus(String memberId, String status);
    List<BorrowRecord> findByMemberIdAndBookId(String memberId, String bookId);
}
