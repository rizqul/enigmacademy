# 02 - Query Methods

## Konsep Query Methods

Query Methods adalah fitur Spring Data JPA yang memungkinkan kita membuat query secara deklaratif menggunakan penamaan method. Dengan mendefinisikan method pada JpaRepository dengan nama yang mengikuti konvensi tertentu, JPA akan secara otomatis membuat implementasinya.

## Jenis-jenis Query Methods

### 1. Derived Query Methods

Derived Query Methods adalah query yang dibuat secara otomatis berdasarkan nama method. Contoh:

```java
List<User> findByNameContainingIgnoreCase(String name);
```

Konvensi penamaan:
- **findBy**: Awalan standar untuk mencari data
- **Name**: Nama field pada entity
- **Containing**: Operator untuk mencari substring (LIKE %..%)
- **IgnoreCase**: Modifier untuk mengabaikan case (case insensitive)

### 2. JPQL Query

JPQL (Java Persistence Query Language) adalah bahasa query yang mirip SQL tapi bekerja dengan entity dan property, bukan tabel dan kolom. Contoh:

```java
@Query("""
        SELECT u
        FROM User u
        WHERE LOWER(u.nama) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%'))
        ORDER BY u.nama ASC
        """)
List<User> searchUsers(@Param("keyword") String keyword);
```

### 3. Native SQL Query

Native SQL Query adalah query SQL biasa yang langsung dijalankan pada database. Contoh:

```java
@Query(value = """
        SELECT *
        FROM users
        WHERE LOWER(nama) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(email) LIKE LOWER(CONCAT('%', :keyword, '%'))
        ORDER BY nama ASC
        """, nativeQuery = true)
List<User> searchUsersNative(@Param("keyword") String keyword);
```

## Keuntungan Menggunakan Query Methods

1. **Type Safety**: Kesalahan terdeteksi saat kompilasi, bukan runtime
2. **Efisiensi**: Query dioptimalkan oleh JPA Provider
3. **Konsistensi**: Penamaan yang konsisten membuat kode lebih mudah dibaca
4. **Portabilitas**: Bekerja dengan berbagai database

## Implementasi Query Methods di Project

### Di UserRepository

```java
public interface UserRepository extends JpaRepository<Menu, String> {
    // Derived Query Methods
    List<User> findByNamaContainingIgnoreCase(String nama);
    Optional<User> findByEmail(String email);
    List<User> findByEmailContainingIgnoreCase(String email);
    List<User> findByNamaContainingIgnoreCaseAndEmailContainingIgnoreCase(
            String nama,
            String email
    );
    List<User> findByCreatedAtAfter(LocalDateTime date);

    // JPQL Query
    @Query("""
            SELECT u
            FROM User u
            WHERE LOWER(u.nama) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%'))
            """)
    List<User> searchByKeyword(@Param("keyword") String keyword);
    @Query("""
            SELECT COUNT(u)
            FROM User u
            WHERE u.email LIKE CONCAT('%', :domain)
            """)
    Long countByEmailDomain(@Param("domain") String domain);

    // Native SQL Query
    @Query(value = """
            SELECT *
            FROM users
            WHERE LOWER(nama) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(email) LIKE LOWER(CONCAT('%', :keyword, '%'))
            """, nativeQuery = true)
    List<User> searchNative(@Param("keyword") String keyword);
}
```

### API Endpoints

| Endpoint | Method | Deskripsi |
|----------|--------|------------|
| `/api/users/search?nama={nama}` | GET | Mencari user berdasarkan nama |
| `/api/users/email/{email}` | GET | Mendapatkan user berdasarkan email |
| `/api/users/nama/{nama}` | GET | Mendapatkan user berdasarkan nama |
| `/api/users/search-email?email={email}` | GET | Mencari user berdasarkan email yang mengandung keyword |
| `/api/users/nama-email?nama={nama}&email={email}` | GET | Mendapatkan user berdasarkan nama dan email |
| `/api/users/created-after?date={date}` | GET | Mendapatkan user yang dibuat setelah tanggal tertentu |
| `/api/users/created-before?date={date}` | GET | Mendapatkan user yang dibuat sebelum tanggal tertentu |
