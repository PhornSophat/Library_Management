package com.library.library_system.service;

import com.library.library_system.model.User;
import com.library.library_system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // Fetch only Admins for the Admin Card component
    public List<User> getAdmins() {
        return userRepository.findByRole(User.Role.ADMIN);
    }

    // Fetch only Members for the Member Table component
    public List<User> getMembers() {
        return userRepository.findByRole(User.Role.MEMBER);
    }

    // Fetch Overdue members for the "Action Required" section
    public List<User> getOverdueMembers() {
        return userRepository.findByStatus(User.Status.Overdue);
    }

    public long getTotalMemberCount() {
        return userRepository.countByRole(User.Role.MEMBER);
    }
    
    public List<User> getRecentMembers() {
        return userRepository.findTop3ByOrderByIdDesc();
    }

    // Get last 5 members for activity feed
    public List<User> getLast5Members() {
        return userRepository.findTop5ByRoleOrderByIdDesc(User.Role.MEMBER);
    }

    public java.util.Optional<User> getUserById(String id) {
        return userRepository.findById(id);
    }

    public boolean updateAdminPassword(String name, String oldPwd, String newPwd) {
    // findByName now returns Optional<User> to prevent NullPointerExceptions
        return userRepository.findByName(name)
            .map(admin -> {
                // Validate the current password matches before saving new one
                if (admin.getPassword().equals(oldPwd)) {
                    admin.setPassword(newPwd);
                    userRepository.save(admin);
                    return true;
                }
                return false;
            }).orElse(false);
    }

    // Create a new user (member)
    public User createUser(User user) {
        return userRepository.save(user);
    }

    // Update an existing user (member)
    public User updateUser(User user) {
        return userRepository.save(user);
    }

    // Delete a user by ID
    public void deleteUser(String id) {
        userRepository.deleteById(id);
    }

    // Search members by name or email
    public List<User> searchMembers(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getMembers();
        }
        String lowerQuery = query.toLowerCase();
        return userRepository.findByRole(User.Role.MEMBER).stream()
            .filter(user -> 
                (user.getName() != null && user.getName().toLowerCase().contains(lowerQuery)) ||
                (user.getEmail() != null && user.getEmail().toLowerCase().contains(lowerQuery))
            )
            .collect(Collectors.toList());
    }

    // Filter members by status
    public List<User> filterMembersByStatus(User.Status status) {
        return userRepository.findByRole(User.Role.MEMBER).stream()
            .filter(user -> user.getStatus() == status)
            .collect(Collectors.toList());
    }

    // Get count by status for statistics
    public long getCountByStatus(User.Status status) {
        return userRepository.findByStatus(status).size();
    }

    // Get active members count
    public long getActiveMemberCount() {
        return userRepository.findByRole(User.Role.MEMBER).stream()
            .filter(user -> user.getStatus() == User.Status.Active)
            .count();
    }

    // Get inactive members count
    public long getInactiveMemberCount() {
        return userRepository.findByRole(User.Role.MEMBER).stream()
            .filter(user -> user.getStatus() == User.Status.Inactive)
            .count();
    }

}