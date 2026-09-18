# 07 - Pagination

## Konsep Pagination

Pagination (paginasi) adalah teknik untuk membagi hasil query yang besar menjadi beberapa halaman yang lebih kecil. Fitur ini sangat penting untuk:

1. **Meningkatkan Performa** - Mengurangi beban server dan waktu respons
2. **Pengalaman Pengguna** - Menampilkan data dalam jumlah yang mudah dicerna
3. **Efisiensi Bandwidth** - Mengurangi jumlah data yang ditransfer

## Komponen Pagination di Spring Data JPA

Spring Data JPA menyediakan dukungan pagination melalui beberapa kelas:

1. **Pageable** - Interface yang berisi informasi pagination (nomor halaman, ukuran halaman, sorting)
2. **PageRequest** - Implementasi konkrit dari Pageable
3. **Page** - Container untuk hasil query yang diberi pagination, berisi data dan metadata

## Implementasi Pagination

### 1. DTO untuk Response Pagination

```java
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PagingResponse {
    private Integer currentPage;
    private Integer totalPage;
    private Integer size;
    private Long totalElements;
    private Boolean hasNext;
    private Boolean hasPrevious;
}
```

### 2. Menambahkan Paging ke Response

```java
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommonResponse<T> {
    private Integer statusCode;
    private String message;
    private T data;
    private PagingResponse paging;
    
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
}
```

### 3. Utility untuk Membangun Response dengan Pagination

```java
public static <T> ResponseEntity<CommonResponse<T>> buildResponse(
        HttpStatus httpStatus, String message, T data, Page<?> page) {
    
    PagingResponse pagingResponse = PagingResponse.builder()
            .currentPage(page.getNumber() + 1) // Page dimulai dari 0, tapi untuk UI biasanya dari 1
            .totalPage(page.getTotalPages())
            .size(page.getSize())
            .totalElements(page.getTotalElements())
            .hasNext(page.hasNext())
            .hasPrevious(page.hasPrevious())
            .build();
            
    CommonResponse<T> response = CommonResponse.<T>builder()
            .statusCode(httpStatus.value())
            .message(message)
            .data(data)
            .paging(pagingResponse)
            .build();
            
    return ResponseEntity.status(httpStatus).body(response);
}
```

### 4. Request DTO dengan Parameter Pagination

```java
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchUserRequest {
    // filtering fields
    private String name;
    private String email;
    private String userRole;
    private String createdAt;

    // pagination dan sorting
    private Integer page;
    private Integer size;
    private String sortBy;
    private String direction;
}
```

### 5. Service Method dengan Pagination

```java
public Page<UserResponse> searchUserWithPagination(SearchUserRequest request) {
    // Default values
    int page = (request.getPage() == null || request.getPage() <= 0) ? 0 : request.getPage() - 1;
    int size = (request.getSize() == null || request.getSize() <= 0) ? 10 : request.getSize();
    String sortBy = (request.getSortBy() == null) ? "name" : request.getSortBy();
    String direction = (request.getDirection() == null) ? "asc" : request.getDirection();
    
    // Create Sort
    Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
    
    // Create PageRequest
    PageRequest pageRequest = PageRequest.of(page, size, sort);
    
    // Create Specification
    Specification<User> specification = UserSpecification.getSpecification(request);
    
    // Execute query with pagination
    Page<User> userPage = userRepository.findAll(specification, pageRequest);
    
    // Convert to DTOs
    List<UserResponse> userResponses = UserMapper.toResponseList(userPage.getContent());
    
    // Return as Page
    return new PageImpl<>(userResponses, pageRequest, userPage.getTotalElements());
}
```

### 6. Controller Endpoint dengan Pagination

```java
    @GetMapping("/advanced-search/page")
public ResponseEntity<CommonResponse<List<UserResponse>>> searchWithPaging(
        @RequestParam(required = false) String name,
        @RequestParam(required = false) String email,
        @RequestParam(required = false, defaultValue = "STUDENT") String role,
        @RequestParam(required = false) String createdAt,
//            @RequestParam(defaultValue = "1") Integer page,
//            @RequestParam(defaultValue = "10") Integer size,
//            @RequestParam(defaultValue = "name") String sortBy,
//            @RequestParam(defaultValue = "asc") String direction
        @RequestParam(required = false) Integer page,
        @RequestParam(required = false) Integer size,
        @RequestParam(required = false) String sortBy,
        @RequestParam(required = false) String direction
) {
    SearchUserRequest request = SearchUserRequest.builder()
            .name(name)
            .email(email)
            .userRole(role)
            .createdAt(createdAt)
            .page(page)
            .size(size)
            .sortBy(sortBy)
            .direction(direction)
            .build();

    Page<UserResponse> userResponses = userService.searchUserWithPagination(request);

    return ResponseUtil.buildResponse(
            HttpStatus.OK,
            ResponseMessage.SUCCES_GET_DATA,
            userResponses.getContent(),
            userResponses);
}
```

## Contoh Endpoint Pagination

### Request

```
GET /api/users/advanced-search/page?name=budi&email=gmail&createdAt=2026-01-01&userRole=USER&page=1&size=10&sortBy=name&direction=asc
```

Parameter query:
- `name`: Filter/search berdasarkan nama user (opsional)
- `email`: Filter/search berdasarkan email user (opsional)
- `createdAt`: Filter user yang dibuat mulai dari tanggal tertentu (opsional)
- `userRole`: Filter berdasarkan role user, contoh `USER` atau `ADMIN` (opsional)
- `page`: Nomor halaman (default: `1`)
- `size`: Jumlah data per halaman (default: `10`)
- `sortBy`: Field yang digunakan untuk pengurutan (default: `name`)
- `direction`: Arah pengurutan, `asc` atau `desc` (default: `asc`)

## Format Response dengan Pagination

```json
{
  "statusCode": 200,
  "message": "Successfully get data",
  "data": [
    {
      "statusCode": 200,
      "message": "Successfully get data",
      "data": [
        {
          "id": 1,
          "name": "Budi",
          "email": "budi@gmail.com",
          "userRole": "USER",
          "createdAt": "2026-01-15T10:30:00"
        },
    // More items...
  ],
  "paging": {
    "currentPage": 1,
    "totalPage": 5,
    "size": 10,
    "totalElements": 42,
    "hasNext": true,
    "hasPrevious": false
  },
  "timestamp": "2025-04-28T12:34:56.789"
}
```

## Best Practices untuk Pagination

1. **Gunakan HTTP GET untuk Pagination** - Pagination adalah operasi yang hanya mengambil data, sehingga penggunaan metode GET adalah praktik terbaik sesuai dengan prinsip RESTful API
2. **Berikan Default Values** - Selalu sediakan nilai default untuk page dan size
3. **Validasi Input** - Pastikan nilai page dan size masuk akal (tidak negatif, tidak terlalu besar)
4. **Konsisten dalam Response** - Selalu sertakan metadata pagination yang lengkap
5. **Dokumentasi** - Jelaskan bagaimana pagination bekerja di API documentation
6. **Sorting** - Sediakan opsi sorting yang jelas dan konsisten
7. **Caching** - Pertimbangkan caching untuk page yang sering diakses

## Keuntungan Pagination dalam API

1. **Performa** - Mengurangi beban database dan meningkatkan respons
2. **Skalabilitas** - Memungkinkan menangani dataset yang sangat besar
3. **UX** - Memudahkan client untuk menampilkan data secara bertahap
4. **Bandwidth** - Mengurangi jumlah data yang dikirim dalam satu respons
5. **Kontrol** - Memberikan client lebih banyak kontrol atas data yang diambil
