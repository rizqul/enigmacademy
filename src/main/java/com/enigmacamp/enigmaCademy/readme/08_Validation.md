## Konsep Validasi

Validasi data adalah proses memeriksa apakah data meuserhi aturan atau batasan tertentu sebelum diproses lebih lanjut. Dalam konteks aplikasi web, validasi sangat penting untuk:

1. **Keamanan** - Mencegah input yang berbahaya atau malicious
2. **Integritas Data** - Memastikan data yang disimpan konsisten dan akurat
3. **User Experience** - Memberikan feedback yang jelas kepada pengguna

Spring Boot menyediakan dukungan validasi melalui Bean Validation API (JSR-380) yang terintegrasi dengan baik dalam ekosistem Spring.

## Komponen Validasi

### 1. Constraint Annotations

Anotasi yang digunakan untuk mendefinisikan aturan validasi pada field:

- `@NotNull` - Field tidak boleh null
- `@NotEmpty` - Field tidak boleh null dan tidak boleh kosong (untuk koleksi)
- `@NotBlank` - Field tidak boleh null, tidak boleh kosong, dan tidak boleh hanya whitespace
- `@Size` - Membatasi ukuran (String, Collection, Map, Array)
- `@Min`, `@Max` - Membatasi nilai minimum dan maksimum untuk angka
- `@Pattern` - Validasi menggunakan regular expression
- `@Email` - Memastikan format email valid
- `@Past`, `@Future` - Memastikan tanggal di masa lalu atau masa depan
- `@Positive`, `@Negative` - Memastikan angka positif atau negatif

### 2. Validator

Komponen yang melakukan pemeriksaan validasi berdasarkan anotasi constraint.

### 3. Validation Groups

Memungkinkan validasi yang berbeda untuk konteks yang berbeda (misalnya: create vs update).

## Implementasi Validasi dalam Spring Boot

### 1. Menambahkan Dependency

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

### 2. Menambahkan Validasi pada Model/DTO

Dalam aplikasi Restaurant Simple, kita mengimplementasikan validasi pada `UserRequest`:
```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {

    @NotBlank(
        message = "Nama tidak boleh kosong",
        groups = {ValidationGroups.Create.class, ValidationGroups.Update.class}
    )
    @Size(
        min = 3,
        max = 100,
        message = "Nama harus antara 3-100 karakter",
        groups = {ValidationGroups.Create.class, ValidationGroups.Update.class}
    )
    private String nama;

    @NotBlank(
        message = "Email tidak boleh kosong",
        groups = {ValidationGroups.Create.class, ValidationGroups.Update.class}
    )
    @Email(
        message = "Format email tidak valid",
        groups = {ValidationGroups.Create.class, ValidationGroups.Update.class}
    )
    private String email;

    @NotBlank(
        message = "Password tidak boleh kosong",
        groups = ValidationGroups.Create.class
    )
    @Size(
        min = 8,
        max = 100,
        message = "Password harus antara 8-100 karakter",
        groups = ValidationGroups.Create.class
    )
    private String password;

    @NotNull(
        message = "Role tidak boleh kosong",
        groups = ValidationGroups.Create.class
    )
    private UserRole role;
}
```

### 3. Mengaktifkan Validasi di Controller

Ada dua cara untuk mengaktifkan validasi di controller:

#### 3.1 Validasi Request Body dengan `@Valid` atau `@Validated`

```java
// Membuat user baru dengan validasi
@PostMapping
public ResponseEntity<CommonResponse<UserResponse>> createUser(
        @Validated({ValidationGroups.Create.class, Default.class})
        @RequestBody UserRequest request) {

    UserResponse userResponse = userService.createUser(request);

    return ResponseUtil.buildResponse(
            HttpStatus.CREATED,
            ResponseMessage.SUCCESS_SAVE_DATA,
            userResponse
    );
}
```

#### 3.2 Validasi Request Parameter

```java
// Validasi parameter request dengan anotasi @Validated di level class@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Validated // Penting untuk mengaktifkan validasi @RequestParam
public class UserController {

    // Validasi request parameter
    @GetMapping("/age/range")
    public ResponseEntity<CommonResponse<List<UserResponse>>> getUsersByAgeRange(
            @RequestParam
            @Min(value = 0, message = "Umur minimum tidak boleh negatif")
            Integer minAge,

            @RequestParam
            @Min(value = 0, message = "Umur maksimum tidak boleh negatif")
            Integer maxAge) {

        List<UserResponse> userResponses =
                userService.getUsersByAgeRange(minAge, maxAge);

        return ResponseUtil.buildResponse(
                HttpStatus.OK,
                ResponseMessage.SUCCESS_GET_DATA,
                userResponses
        );
    }
}
```

## Custom Validators dan Annotations

### 1. Membuat Custom Constraint Annotation

Untuk memvalidasi kategori user, kita membuat custom annotation `@ValidCategoryName`:

```java
@Documented
@Constraint(validatedBy = CategoryNameValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidCategoryName {
    String message() default "Kategori harus berupa: BackEnd, FrontEnd, atau Mobile";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
```

### 2. Implementasi Validator

```java
 public class CategoryNameValidator implements ConstraintValidator<ValidCategoryName, String> {

        List<String> categoryName = List.of("BackEnd", "FrontEnd", "Mobile");

        @Override
        public void initialize(ValidKategori constraintAnnotation) {
            // Inisialisasi jika diperlukan
        }

        @Override
        public boolean isValid(String value, ConstraintValidatorContext context) {
            if (value == null || value.isEmpty()) {
                return true;
            }

            if (!categoryName.contains(value.toLowerCase())){
                return false;
            }

            return true;
        }
    }
```

### 3. Validator untuk Aturan Harga

Kita juga membuat validator untuk memastikan harga dalam kelipatan 500:

```java
@Documented
@Constraint(validatedBy = UserRoleValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidUserRole {
    String message() default "Format role tidak sesuai";
    Class<?>[] groups() default { };
    Class<? extends Payload>[] payload() default { };
}

```

```java
public class UserRoleValidator implements ConstraintValidator<ValidUserRole, String> {

    @Override
    public boolean isValid(
            String value,
            ConstraintValidatorContext context
    ){
        if (value == null  || value.isBlank()) {
            return true;
        }

        return UserRole.isValid(value.toUpperCase());
    }
}
```

## Class-Level Validation

Selain validasi field, kita juga dapat memvalidasi hubungan antar field dengan validator di level class:

```java
@ValidDateRange(
    message = "Tanggal mulai harus sebelum tanggal selesai"
)
public class UserRequest {

    private LocalDate startDate;
    private LocalDate endDate;
}
```

## Validation Groups

Validation Groups memungkinkan kita mendefinisikan set validasi yang berbeda untuk konteks yang berbeda.

### 1. Mendefinisikan Validation Groups

```java
public interface ValidationGroups {
    // Grup untuk operasi create
    interface Create {}
    
    // Grup untuk operasi update
    interface Update {}
}
```

### 2. Menerapkan Groups di Controller

```java
// POST - Membuat user baru dengan validasi grup Create
@PostMapping
public ResponseEntity<CommonResponse<UserResponse>> createUser(
        @Validated({ValidationGroups.Create.class, Default.class}) @RequestBody UserRequest request) {
    UserResponse userResponse = userService.createUser(request);
    return ResponseUtil.buildResponse(HttpStatus.CREATED, ResponseMessage.SUCCESS_SAVE_DATA, userResponse);
}

// PUT - Memperbarui user dengan validasi grup Update
@PutMapping("/{id}")
public ResponseEntity<CommonResponse<UserResponse>> updateUser(
        @PathVariable String id, 
        @Validated({ValidationGroups.Update.class, Default.class}) @RequestBody UserRequest request) {
    UserResponse userResponse = userService.updateUser(id, request);
    // ...
}
```

## Exception Handling untuk Validasi

Untuk menangani error validasi dengan baik, kita perlu membuat GlobalExceptionHandler:

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Handle request body validation errors (@Valid or @Validated on @RequestBody)
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
                .build();
        
        return ResponseEntity.badRequest().body(response);
    }
    
    // Handle parameter validation errors (@Valid or constraints on method parameters)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<CommonResponse<Map<String, String>>> handleConstraintViolation(ConstraintViolationException ex) {
        Map<String, String> errors = ex.getConstraintViolations().stream()
                .collect(Collectors.toMap(
                        violation -> extractParameterName(violation),
                        ConstraintViolation::getMessage,
                        (error1, error2) -> error1 + "; " + error2
                ));
        
        CommonResponse<Map<String, String>> response = CommonResponse.<Map<String, String>>builder()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .message("Validasi parameter gagal")
                .data(errors)
                .build();
        
        return ResponseEntity.badRequest().body(response);
    }
    
    private String extractParameterName(ConstraintViolation<?> violation) {
        String propertyPath = violation.getPropertyPath().toString();
        String[] pathParts = propertyPath.split("\\.");
        return pathParts.length > 0 ? pathParts[pathParts.length - 1] : propertyPath;
    }
}
```

## Perbedaan @Valid vs @Validated

| Fitur | @Valid | @Validated |
|-------|--------|------------|
| Asal  | Bean Validation API (JSR-380) | Spring Framework |
| Mendukung Validation Groups | Tidak | Ya |
| Validasi Parameter Method | Tidak | Ya |
| Rekursif | Ya (otomatis) | Ya |
| Posisi | Method parameter | Method, Class atau parameter |

## Best Practices

1. **Gunakan Message yang Deskriptif** - Pesan error harus jelas dan membantu pengguna memahami apa yang salah
2. **Validasi di Lapisan yang Tepat** - Validasi teknis di controller, validasi bisnis di service
3. **Manfaatkan Groups untuk Use Case yang Berbeda** - Pisahkan validasi untuk create, update, dan konteks lainnya
4. **Buat Custom Validator untuk Logic Kompleks** - Gunakan untuk validasi yang tidak bisa diekspresikan dengan anotasi bawaan
5. **Gunakan Class-Level Validation** - Untuk validasi yang melibatkan beberapa field
6. **Pertimbangkan Performa** - Batasi validasi yang kompleks atau mahal
7. **Jangan Duplikasi Validasi** - Validasi pada satu lapisan saja jika memungkinkan

## Contoh Response Error Validasi

```json
{
  "statusCode": 400,
  "message": "Validasi gagal",
  "data": {
    "nama": "Nama user harus antara 3-100 karakter"}
}
```

## Kesimpulan

Validasi data adalah aspek penting dalam pengembangan aplikasi yang robust. Spring Boot dengan Bean Validation API menyediakan cara yang powerful dan fleksibel untuk mengimplementasikan validasi di berbagai lapisan aplikasi. Dengan menerapkan validasi yang tepat, kita dapat meningkatkan kualitas, keamanan, dan user experience aplikasi kita.