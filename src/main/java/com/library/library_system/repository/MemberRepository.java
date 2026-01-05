package com.library.library_system.repository;

<<<<<<< HEAD
import org.springframework.stereotype.Repository;

@Repository("jpaMemberRepository")
public interface MemberRepository {
    // JPA repository removed — not used. Kept as a marker to avoid package changes.
=======
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.library.library_system.model.Member;
import com.library.library_system.model.Member.Status;

public interface MemberRepository extends MongoRepository<Member, String> {
    // Change from findById to findByStatus
    List<Member> findByStatus(Member.Status status);

    // Spring Data magic: find the first 3 members where status equals the parameter
    List<Member> findFirst3ByStatus(Status status);
>>>>>>> 7a9ba538864d4b472065c71cd4b7242122d23a6e
}
