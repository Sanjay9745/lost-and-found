package com.lostandfound.controller;

import com.lostandfound.dto.LostItemRequest;
import com.lostandfound.dto.MarkFoundRequest;
import com.lostandfound.model.LostItem;
import com.lostandfound.service.LostItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/items")
@CrossOrigin(origins = "*")
public class LostItemController {

    @Autowired
    private LostItemService lostItemService;

    @PostMapping("/report")
    public ResponseEntity<LostItem> reportLostItem(@RequestBody LostItemRequest request) {
        LostItem item = lostItemService.reportLostItem(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(item);
    }

    @PutMapping("/{id}/found")
    public ResponseEntity<?> markAsFound(
            @PathVariable Long id,
            @RequestBody MarkFoundRequest request) {
        
        Optional<LostItem> item = lostItemService.markAsFound(id, request);
        
        if (item.isPresent()) {
            return ResponseEntity.ok(item.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Item not found with id: " + id);
        }
    }

    @GetMapping
    public ResponseEntity<List<LostItem>> getAllItems() {
        return ResponseEntity.ok(lostItemService.getAllItems());
    }

    @GetMapping("/lost")
    public ResponseEntity<List<LostItem>> getLostItems() {
        return ResponseEntity.ok(lostItemService.getLostItems());
    }

    @GetMapping("/found")
    public ResponseEntity<List<LostItem>> getFoundItems() {
        return ResponseEntity.ok(lostItemService.getFoundItems());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getItemById(@PathVariable Long id) {
        Optional<LostItem> item = lostItemService.getItemById(id);
        
        if (item.isPresent()) {
            return ResponseEntity.ok(item.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Item not found with id: " + id);
        }
    }

    @GetMapping("/search/item")
    public ResponseEntity<List<LostItem>> searchByItemName(@RequestParam String name) {
        return ResponseEntity.ok(lostItemService.searchByItemName(name));
    }

    @GetMapping("/search/location")
    public ResponseEntity<List<LostItem>> searchByLocation(@RequestParam String location) {
        return ResponseEntity.ok(lostItemService.searchByLocation(location));
    }

    @GetMapping("/search/student")
    public ResponseEntity<List<LostItem>> searchByStudentName(@RequestParam String name) {
        return ResponseEntity.ok(lostItemService.searchByStudentName(name));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteItem(@PathVariable Long id) {
        boolean deleted = lostItemService.deleteItem(id);
        
        if (deleted) {
            return ResponseEntity.ok("Item deleted successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Item not found with id: " + id);
        }
    }
}
