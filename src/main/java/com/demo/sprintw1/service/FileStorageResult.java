package com.demo.sprintw1.service;

// Disk üzerinde saklanan dosya adı (UUID.uzantı) ile
// kullanıcının yüklediği orijinal dosya adını birlikte taşımak için.
public record FileStorageResult(String storedFileName, String originalFileName) {
}