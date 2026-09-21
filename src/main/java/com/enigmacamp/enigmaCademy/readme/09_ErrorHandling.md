# 09 - Exception Handling

## Konsep Exception Handling dalam Spring Boot

Exception Handling (penanganan kesalahan) adalah komponen penting dalam pengembangan aplikasi yang robust dan user-friendly. Dalam Spring Boot, penanganan exception yang efektif memungkinkan kita untuk:

1. **Mengelola kesalahan secara terpusat** - Menghindari duplikasi kode try-catch di setiap controller
2. **Memberikan respons yang konsisten** - Format error yang seragam di seluruh aplikasi
3. **Meningkatkan user experience** - Pesan kesalahan yang jelas dan informatif
4. **Menyembunyikan detail teknis** - Mencegah kebocoran informasi sensitif ke client

Spring Boot menyediakan beberapa cara untuk menangani exception secara efektif.

## Komponen Utama Exception Handling

### 1. @ControllerAdvice dan @RestControllerAdvice

`@ControllerAdvice` adalah anotasi yang memungkinkan kita membuat komponen global yang dapat menangkap exception dari seluruh aplikasi. Versi `@RestControllerAdvice` menggabungkan `@ControllerAdvice` dan `@ResponseBody`, cocok untuk API RESTful.

### 2. @ExceptionHandler

`@ExceptionHandler` adalah anotasi yang digunakan untuk menangani exception spesifik dan memetakannya ke method handler tertentu.

### 3. ResponseEntityExceptionHandler

Kelas dasar yang menyediakan handler untuk exception standar Spring MVC yang dapat diekstend untuk kustomisasi lebih lanjut.

## Implementasi Exception Handling

### 1. Membuat Custom Exception

Custom Exception memungkinkan kita mendefinisikan jenis kesalahan spesifik untuk domain aplikasi:

```java
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
    
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s dengan %s '%s' tidak ditemukan", resourceName, fieldName, fieldValue));
    }
}

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}

public class BusinessLogicException extends RuntimeException {
    public BusinessLogicException(String message) {
        super(message);
    }
}
```

### 2. Membuat Global Exception Handler

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Handle ResourceNotFoundException
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<CommonResponse<Object>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        CommonResponse<Object> response = CommonResponse.builder()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
    
    // Handle validation exceptions (@Valid or @Validated)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CommonResponse<Map<String, String>>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        CommonResponse<Map<String, String>> response = CommonResponse.<Map<String, String>>builder()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .message("Validasi gagal")
                .data(errors)
                .timestamp(LocalDateTime.now())
                .build();
        
        return ResponseEntity.badRequest().body(response);
    }
    
    // Handle parameter validation errors (@Valid or constraints on method parameters)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<CommonResponse<Map<String, String>>> handleConstraintViolation(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        
        ex.getConstraintViolations().forEach(violation -> {
            String propertyPath = violation.getPropertyPath().toString();
            String fieldName = propertyPath.substring(propertyPath.lastIndexOf('.') + 1);
            String errorMessage = violation.getMessage();
            errors.put(fieldName, errorMessage);
        });
        
        CommonResponse<Map<String, String>> response = CommonResponse.<Map<String, String>>builder()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .message("Validasi parameter gagal")
                .data(errors)
                .timestamp(LocalDateTime.now())
                .build();
        
        return ResponseEntity.badRequest().body(response);
    }
    
    // Handle DataIntegrityViolationException
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<CommonResponse<Object>> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        HttpStatus status;
        String message;
        
        // Analisis pesan error untuk memberikan respons yang lebih spesifik
        String errorMessage = ex.getMessage().toLowerCase();
        
        if (errorMessage.contains("foreign key constraint") || 
            errorMessage.contains("violates foreign key constraint")) {
            // Kasus pelanggaran foreign key constraint
            status = HttpStatus.BAD_REQUEST;
            message = "Tidak dapat menghapus data karena masih direferensikan oleh data lain";
        } else if (errorMessage.contains("unique constraint") || 
                  errorMessage.contains("duplicate key") ||
                  errorMessage.contains("already exists")) {
            // Kasus pelanggaran unique constraint
            status = HttpStatus.CONFLICT;
            message = "Data dengan nilai tersebut sudah ada";
        } else if (errorMessage.contains("not-null constraint") || 
                  errorMessage.contains("null value")) {
            // Kasus pelanggaran not-null constraint
            status = HttpStatus.BAD_REQUEST;
            message = "Data tidak boleh kosong";
        } else {
            // Kasus lainnya
            status = HttpStatus.BAD_REQUEST;
            message = "Terjadi kesalahan integritas data";
        }
        
        CommonResponse<Object> response = CommonResponse.builder()
                .statusCode(status.value())
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
        
        return ResponseEntity.status(status).body(response);
    }
    
    // Generic exception handler
    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResponse<Object>> handleGlobalException(Exception ex) {
        // Log exception untuk debugging (dalam produksi gunakan logger)
        ex.printStackTrace();
        
        CommonResponse<Object> response = CommonResponse.builder()
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message("Terjadi kesalahan pada server")
                .timestamp(LocalDateTime.now())
                .build();
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
```

### 3. Menggunakan Exception dalam Service/Controller

```java
@Service
public class UserServiceImpl implements UserService {
    // ...
    
    @Override
    public UserResponse getUserById(String id) {
        return userRepository.findById(id)
                .map(UserMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }
    
    // ...
}
```

## Jenis-jenis Error dan Penanganannya

### 1. Validation Error
Error yang terjadi ketika input tidak memenuhi kriteria validasi.

```json
{
  "statusCode": 400,
  "message": "Validasi gagal",
  "data": {
    "nama": "Nama harus antara 3-100 karakter",
  },
  "timestamp": "2025-04-28T14:30:45.123"
}
```

### 2. Resource Not Found
Error yang terjadi ketika resource yang diminta tidak ditemukan.

```json
{
  "statusCode": 404,
  "message": "User dengan id '123' tidak ditemukan",
  "timestamp": "2025-04-28T14:32:22.456"
}
```

### 3. Business Logic Error
Error yang terjadi karena aturan bisnis tidak terpenuhi.

```json
{
  "statusCode": 400,
  "message": "Format email salah",
  "timestamp": "2026-09-21T17:46:10.789"
}
```

### 4. Data Integrity Error
Error yang terjadi karena pelanggaran integritas data di database.

```json
{
  "statusCode": 409,
  "message": "Data email sudah ditemukan",
  "timestamp": "2025-04-28T14:40:05.123"
}
```

### 5. Server Error
Error yang terjadi karena kesalahan pada server.

```json
{
  "statusCode": 500,
  "message": "Terjadi kesalahan pada server",
  "timestamp": "2025-04-28T14:45:30.456"
}
```

## Hirarki Exception di Spring Boot

Spring Boot menggunakan hirarki exception yang terstruktur:

1. **`Exception`** - Base class untuk semua checked exceptions
2. **`RuntimeException`** - Base class untuk unchecked exceptions
    - **`IllegalArgumentException`** - Exception untuk argument yang tidak valid
        - **`NumberFormatException`** - Exception untuk format angka yang tidak valid
    - **`NullPointerException`** - Exception untuk referensi null
    - **`IllegalStateException`** - Exception untuk state yang tidak valid
3. **Spring Framework Exceptions**
    - **`DataAccessException`** - Exception untuk akses data
        - **`DataIntegrityViolationException`** - Pelanggaran integritas data
    - **`WebApplicationException`** - Exception untuk aplikasi web


## Demo Error Controller

Untuk tujuan pembelajaran, kita membuat controller khusus yang dapat digunakan untuk mendemonstrasikan berbagai jenis exception:

```java
@RestController
@RequestMapping("/api/error-demo")
public class ErrorDemoController {

    @GetMapping("/not-found")
    public ResponseEntity<CommonResponse<Object>> triggerResourceNotFoundException() {
        throw new ResourceNotFoundException("Resource yang diminta tidak ditemukan");
    }

    @GetMapping("/bad-request")
    public ResponseEntity<CommonResponse<Object>> triggerBadRequestException() {
        throw new BadRequestException("Parameter request tidak valid");
    }

    @GetMapping("/business-logic")
    public ResponseEntity<CommonResponse<Object>> triggerBusinessLogicException() {
        throw new BusinessLogicException("Operasi melanggar aturan bisnis");
    }
}
```

## Best Practices

1. **Gunakan custom exception untuk bisnis logic**
    - Buat exception spesifik untuk domain aplikasi Anda
    - Berikan pesan error yang jelas dan informatif

2. **Handle exception di level yang tepat**
    - Tangkap exception di tempat yang memiliki konteks cukup untuk menanganinya
    - Gunakan global exception handler untuk konsistensi

3. **Berikan respons yang sesuai**
    - Gunakan HTTP status code yang tepat (404 untuk tidak ditemukan, 400 untuk request tidak valid, 409 untuk konflik, dll)
    - Berikan pesan error yang jelas tanpa detail teknis yang sensitif

4. **Log exception dengan benar**
    - Log exception stack trace untuk debugging
    - Jangan menampilkan stack trace ke client

5. **Validasi input seawal mungkin**
    - Gunakan validasi di level DTO
    - Tambahkan custom validator untuk validasi kompleks

6. **Analisis pesan error database**
    - Parse error dari database untuk memberikan pesan yang lebih ramah pengguna
    - Klasifikasikan jenis error database dan berikan HTTP status code yang sesuai

7. **Uji exception handling**
    - Buat test untuk memastikan exception ditangani dengan benar
    - Verifikasi HTTP status code dan pesan error yang dikembalikan

## Kesimpulan

Implementasi exception handling yang baik merupakan aspek penting dalam pengembangan API yang profesional. Dengan menerapkan exception handling yang tepat, aplikasi akan lebih robust, lebih mudah di-debug, dan memberikan pengalaman pengguna yang lebih baik, terutama ketika terjadi kesalahan.

Pendekatan terpusat dengan `@RestControllerAdvice` dan `@ExceptionHandler` memungkinkan kita untuk mengelola semua jenis exception dengan cara yang konsisten dan terstandarisasi, sambil mengurangi duplikasi kode dan kompleksitas di controller dan service.
