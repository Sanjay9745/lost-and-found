package com.lostandfound.repository;

import com.lostandfound.model.LostItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LostItemRepository extends JpaRepository<LostItem, Long> {
    List<LostItem> findByFound(Boolean found);
    List<LostItem> findByItemNameContainingIgnoreCase(String itemName);
    List<LostItem> findByLocationContainingIgnoreCase(String location);
    List<LostItem> findByStudentNameContainingIgnoreCase(String studentName);
}
