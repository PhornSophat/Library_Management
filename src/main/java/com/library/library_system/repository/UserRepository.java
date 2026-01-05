package com.library.library_system.repository;

import com.library.library_system.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional; // Added for safer user lookups

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    // Used for the Credential Update logic
    // We use Optional in case a user with that name doesn't exist
    Optional<User> findByName(String name);

    // Authentication lookup by email/username
    Optional<User> findByEmail(String email);

    // Filter by Role (Admin or Member)
    List<User> findByRole(User.Role role);
    
    // Filter by Status (Active, Inactive, Overdue, Suspended)
    List<User> findByStatus(User.Status status);
    
    // Counts for dashboard summary cards (e.g., Total Admins)
    long countByRole(User.Role role);

    // Gets the 3 most recently added members for the Dashboard table
    List<User> findTop3ByOrderByIdDesc();

    // Gets the 5 most recently added members for activity feed
    List<User> findTop5ByRoleOrderByIdDesc(User.Role role);
}