# 08 - JPA Specification

## Konsep JPA Specification

JPA Specification adalah bagian dari Spring Data JPA yang memungkinkan kita membuat query dinamis menggunakan Criteria API. Dengan Specification, kita dapat membangun query kompleks secara programatik berdasarkan kondisi yang ditentukan pada runtime.

## Keuntungan Menggunakan Specification

1. **Fleksibilitas**
    - Memungkinkan pembuatan query dengan kondisi yang beragam
    - Kondisi/filter dapat ditambahkan secara dinamis berdasarkan input pengguna

2. **Type Safety**
    - Criteria API memberikan pengecekan tipe pada saat kompilasi
    - Mengurangi risiko SQL Injection

3. **Kemudahan Kombinasi**
    - Specification dapat dengan mudah digabungkan menggunakan operator AND, OR, NOT, dll.
    - Mendukung penggunaan nested specification untuk query yang lebih kompleks

4. **Integrasi dengan Repository**
    - Mudah digunakan dengan JpaRepository melalui JpaSpecificationExecutor

## Implementasi JPA Specification

### Langkah-langkah Implementasi

1. **Extend JpaSpecificationExecutor pada Repository**

```java
@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    // ...
}
```

2. **Buat Class untuk Specification**

```java
public class MenuSpecification {
    public static Specification<Menu> getSpecification(SearchMenuRequest request) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // Tambahkan kondisi sesuai request
            if (request.getNama() != null && !request.getNama().isEmpty()) {
                predicates.add(
                    criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("nama")), 
                        "%" + request.getNama().toLowerCase() + "%"
                    )
                );
            }
            
            // Kondisi lainnya
            // ...
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
```

3. **Gunakan Specification di Service**

```java
public List<MenuResponse> searchMenus(SearchMenuRequest request) {
    Specification<Menu> specification = MenuSpecification.getSpecification(request);
    List<Menu> menus = menuRepository.findAll(specification);
    return MenuMapper.toResponseList(menus);
}
```

## Komponen Utama dalam Criteria API

1. **Root** (`root`)
    - Merepresentasikan entity yang menjadi dasar query
    - Digunakan untuk mengakses atribut entity: `root.get("nama")`

2. **CriteriaQuery** (`query`)
    - Merepresentasikan query yang sedang dibangun
    - Mendefinisikan apa yang dipilih, diurutkan, dan dikelompokkan

3. **CriteriaBuilder** (`criteriaBuilder`)
    - Factory untuk membuat predicate, ekspresi, dan kondisi
    - Menyediakan method seperti `equal()`, `like()`, `greaterThan()`, dll.

4. **Predicate**
    - Kondisi yang akan digunakan dalam klausa WHERE
    - Dapat digabungkan menggunakan `and()`, `or()`, dan `not()`

## Contoh Operasi dalam Specification

```java
// Equal
criteriaBuilder.equal(root.get("kategori"), "Makanan");

// Like (case insensitive)
criteriaBuilder.like(criteriaBuilder.lower(root.get("nama")), "%ayam%");

// Greater Than
criteriaBuilder.greaterThan(root.get("harga"), 50000);

// Between
criteriaBuilder.between(root.get("harga"), 10000, 50000);

// AND
criteriaBuilder.and(predicate1, predicate2);

// OR
criteriaBuilder.or(predicate1, predicate2);

// IN
root.get("kategori").in("Makanan", "Minuman");

// IS NULL
criteriaBuilder.isNull(root.get("deskripsi"));
```

## Perbandingan dengan Query Methods

| Aspek | Query Methods | JPA Specification |
|-------|--------------|-------------------|
| **Fleksibilitas** | Terbatas pada kombinasi yang telah didefinisikan | Sangat fleksibel, query dibuat secara dinamis |
| **Kompleksitas** | Sederhana untuk query sederhana | Lebih kompleks tapi mendukung query yang rumit |
| **Dinamis** | Tidak mendukung filter dinamis | Sangat baik untuk filter dinamis |
| **Usability** | Mudah digunakan untuk kasus sederhana | Membutuhkan lebih banyak kode untuk setup |

## Kapan Menggunakan Specification

- Ketika Anda memiliki form pencarian dengan banyak filter opsional
- Ketika filter atau kondisi query ditentukan pada saat runtime
- Ketika Anda membutuhkan query yang sangat dinamis dan kompleks
- Ketika jumlah kombinasi filter terlalu banyak untuk diakomodasi dengan Query Methods
