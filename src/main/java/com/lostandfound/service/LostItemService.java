package com.lostandfound.service;

import com.lostandfound.dto.LostItemRequest;
import com.lostandfound.dto.MarkFoundRequest;
import com.lostandfound.model.LostItem;
import com.lostandfound.repository.LostItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class LostItemService {

    @Autowired
    private LostItemRepository lostItemRepository;

    public LostItem reportLostItem(LostItemRequest request) {
        LostItem item = new LostItem();
        item.setItemName(request.getItemName());
        item.setDescription(request.getDescription());
        item.setLocation(request.getLocation());
        item.setStudentName(request.getStudentName());
        item.setContactInfo(request.getContactInfo());
        item.setReportedDate(LocalDateTime.now());
        item.setFound(false);
        
        return lostItemRepository.save(item);
    }

    public Optional<LostItem> markAsFound(Long id, MarkFoundRequest request) {
        Optional<LostItem> itemOpt = lostItemRepository.findById(id);
        
        if (itemOpt.isPresent()) {
            LostItem item = itemOpt.get();
            item.setFound(true);
            item.setFoundDate(LocalDateTime.now());
            item.setFoundBy(request.getFoundBy());
            item.setRemarks(request.getRemarks());
            
            return Optional.of(lostItemRepository.save(item));
        }
        
        return Optional.empty();
    }

    public List<LostItem> getAllItems() {
        return lostItemRepository.findAll();
    }

    public List<LostItem> getLostItems() {
        return lostItemRepository.findByFound(false);
    }

    public List<LostItem> getFoundItems() {
        return lostItemRepository.findByFound(true);
    }

    public Optional<LostItem> getItemById(Long id) {
        return lostItemRepository.findById(id);
    }

    public List<LostItem> searchByItemName(String itemName) {
        return lostItemRepository.findByItemNameContainingIgnoreCase(itemName);
    }

    public List<LostItem> searchByLocation(String location) {
        return lostItemRepository.findByLocationContainingIgnoreCase(location);
    }

    public List<LostItem> searchByStudentName(String studentName) {
        return lostItemRepository.findByStudentNameContainingIgnoreCase(studentName);
    }

    public boolean deleteItem(Long id) {
        if (lostItemRepository.existsById(id)) {
            lostItemRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
