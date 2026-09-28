# 17. Unit Testing dengan JUnit

## Tujuan Pembelajaran

Pada branch ini, kita akan belajar membuat unit test untuk kode Java menggunakan JUnit 5. Kita juga akan menggunakan Mockito untuk menggantikan dependency seperti repository, sehingga satu unit dapat diuji secara terisolasi tanpa database.

## Apa Itu Unit Test?

Unit test adalah pengujian otomatis untuk unit kode kecil, biasanya satu method atau satu class, secara terisolasi. Test memeriksa apakah output dan perilaku unit sesuai dengan yang diharapkan.

Contoh: ketika menguji `UserServiceImpl`, kita ingin memastikan service menyimpan user dan mengubah hasilnya menjadi `UserResponse`. Kita tidak perlu menguji cara kerja database sungguhan dalam unit test service tersebut. Karena itu, `UserRepository` diganti dengan mock.

```text
UserServiceImpl -> UserRepository (mock)
```

Unit test membantu menemukan regresi lebih awal dan membuat aturan bisnis lebih mudah dipahami. Unit test berbeda dari integration test, yang menguji kerja sama beberapa komponen nyata seperti service, repository, dan database.

## JUnit 5 dan Mockito

### JUnit 5

JUnit adalah framework untuk menulis dan menjalankan test Java. Beberapa anotasi dan assertion yang sering digunakan:

| Fitur | Kegunaan |
|---|---|
| `@Test` | Menandai method sebagai test case |
| `@BeforeEach` | Menjalankan persiapan sebelum setiap test |
| `@AfterEach` | Menjalankan pembersihan setelah setiap test |
| `@DisplayName` | Memberi nama test yang mudah dibaca |
| `assertEquals(expected, actual)` | Memeriksa apakah nilai sesuai |
| `assertNotNull(actual)` | Memastikan hasil tidak `null` |
| `assertTrue(condition)` | Memastikan kondisi bernilai benar |
| `assertThrows(Type.class, executable)` | Memastikan kode melempar exception tertentu |

Setiap test sebaiknya dapat dijalankan sendiri, tidak bergantung pada urutan test lain, dan menghasilkan hasil yang konsisten.

### Mockito

Mockito membuat object tiruan (**mock**) untuk dependency. Kita dapat mengatur hasil pemanggilannya tanpa menjalankan implementasi aslinya, lalu memeriksa apakah dependency dipanggil dengan benar.

| Fitur Mockito | Kegunaan |
|---|---|
| `@Mock` | Membuat dependency tiruan |
| `@InjectMocks` | Membuat object yang diuji dan menyuntikkan mock ke dalamnya |
| `@ExtendWith(MockitoExtension.class)` | Mengaktifkan integrasi Mockito dengan JUnit 5 |
| `when(...).thenReturn(...)` | Mengatur nilai balikan mock |
| `verify(...)` | Memastikan method dependency dipanggil |
| `ArgumentCaptor` | Menangkap argumen yang diberikan ke dependency |
| `verifyNoInteractions(...)` | Memastikan dependency sama sekali tidak dipanggil |

## Dependency
Tambahkan dependency berikut pada `pom.xml`

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <version>4.1.1</version>
</dependency>
```

## Pola Arrange–Act–Assert

Susun isi setiap test dalam tiga bagian agar mudah dibaca:

1. **Arrange** — siapkan input dan dependency tiruan.
2. **Act** — panggil method yang sedang diuji.
3. **Assert** — periksa hasil dan interaksi yang diharapkan.

Nama test yang deskriptif biasanya mengikuti pola `namaMethod_kondisi_hasilYangDiharapkan`, misalnya `getUserById_whenUserDoesNotExist_shouldThrowResourceNotFoundException`.

## Contoh 1: Test Class Sederhana

`CalculatorServiceImpl` tidak memiliki dependency eksternal, jadi kita bisa membuat object-nya langsung.

```java
class CalculatorServiceImplTest {

    private final CalculatorServiceImpl calculatorService = new CalculatorServiceImpl();

    @Test
    void sumNumbers_shouldReturnTheSum() {
        // Arrange: siapkan angka yang akan dijumlahkan.
        int a = 6;
        int b = 7;

        // Act: jalankan method yang diuji.
        int result = calculatorService.sumNumbers(a, b);

        // Assert: bandingkan hasil aktual dengan hasil yang diharapkan.
        assertEquals(13, result);
    }
}
```

Perhatikan urutan argumen assertion: nilai yang diharapkan ditulis lebih dulu, kemudian nilai aktual.

## Contoh 2: Menguji Service dengan Dependency Mock

`UserServiceImpl` memiliki dependency `UserRepository`. Dengan Mockito, repository dapat diganti mock sehingga test tidak memerlukan database.

```java
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void getUserById_whenUserExists_shouldReturnUserResponse() {
        // Arrange: siapkan entity dan atur hasil repository mock.
        Long id = 1L;
        User user = new User(
                "Ipan",
                "ipan_unyu",
                "ipan@student.enigmacademy.com",
                UserRole.STUDENT,
                "password123");
        user.setId(id);
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        // Act: panggil service.
        UserResponse response = userService.getUserById(id);

        // Assert: pastikan data entity dipetakan ke response DTO.
        assertNotNull(response);
        assertEquals(id, response.getId());
        assertEquals("Ipan", response.getName());
        assertEquals("ipan@student.enigmacademy.com", response.getEmail());
        verify(userRepository).findById(id);
    }
}
```

`when(userRepository.findById(id)).thenReturn(...)` mengatur perilaku mock. Pemanggilan service tetap nyata; hanya dependency repository yang digantikan.

## Menguji Exception

Selain hasil sukses, uji juga kondisi gagal yang memang menjadi bagian dari kontrak method. `getUserById` melempar `ResourceNotFoundException` ketika repository tidak menemukan user.

```java
@Test
void getUserById_whenUserDoesNotExist_shouldThrowResourceNotFoundException() {
    // Arrange: repository melaporkan bahwa user tidak ada.
    when(userRepository.findById(99L)).thenReturn(Optional.empty());

    // Act dan Assert: pastikan pemanggilan service menghasilkan exception yang benar.
    assertThrows(
            ResourceNotFoundException.class,
            () -> userService.getUserById(99L));
}
```

`assertThrows` menerima tipe exception dan lambda yang menjalankan kode. Jika exception tidak terjadi atau tipenya berbeda, test gagal.

## Memeriksa Interaksi dan Argumen

Kadang hasil return saja belum cukup. Saat menyimpan user, kita juga perlu memastikan object yang dikirim ke repository berisi data yang benar. `ArgumentCaptor` menangkap argumen tersebut:

```java
ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
verify(userRepository).save(captor.capture());

assertEquals("Ipan", captor.getValue().getName());
assertEquals(UserRole.STUDENT, captor.getValue().getUserRole());
```

Untuk memastikan validasi menghentikan proses sebelum mengakses repository, gunakan:

```java
assertThrows(BadRequestException.class, () -> userService.deleteById(0L));
verifyNoInteractions(userRepository);
```

Gunakan `verify` untuk interaksi yang menjadi perilaku penting. Jangan memverifikasi setiap detail internal tanpa alasan, karena test dapat menjadi rapuh saat implementasi dirapikan.

## Menentukan Test Case

Untuk setiap method, pikirkan setidaknya beberapa kelompok kondisi berikut:

1. **Kasus sukses** — input valid dan dependency memberi hasil yang diharapkan.
2. **Input batas atau default** — misalnya ID nol, parameter pagination kosong, atau role kosong.
3. **Data tidak ditemukan** — misalnya repository mengembalikan `Optional.empty()`.
4. **Interaksi dependency** — repository dipanggil dengan argumen yang benar atau tidak dipanggil ketika validasi gagal.

Tidak semua method membutuhkan semua kelompok. Pilih kasus berdasarkan aturan dan risiko method tersebut. Buat satu test untuk satu perilaku yang jelas; kondisi berbeda umumnya lebih mudah dipahami jika dipisah menjadi test terpisah.

## Panduan Menulis Test di Project Ini

File test sebaiknya berada di package yang mencerminkan source yang diuji. Contoh:

```text
src/main/java/com/enigmacamp/enigmaCademy/service/impl/UserServiceImpl.java
src/test/java/com/enigmacamp/enigmaCademy/service/impl/UserServiceImplTest.java
```

Langkah menulis unit test service:

1. Buat class test dengan nama class produksi ditambah `Test`.
2. Aktifkan `MockitoExtension` jika class memiliki dependency mock.
3. Tandai dependency sebagai `@Mock`.
4. Tandai service yang diuji sebagai `@InjectMocks`.
5. Siapkan data contoh dan perilaku mock di bagian Arrange.
6. Panggil hanya method service yang sedang diuji di bagian Act.
7. Periksa return value, perubahan data, exception, atau interaksi di bagian Assert.
8. Jalankan test dan baca pesan kegagalan untuk mengetahui assertion yang tidak terpenuhi.

Di `UserServiceImplTest`, helper seperti `user(...)` dan `userRequest(...)` membuat data contoh lebih konsisten dan mengurangi pengulangan. Helper sebaiknya tetap sederhana agar pembaca dapat mengetahui data apa yang digunakan test.

## Menjalankan Test

Dari root project, jalankan semua unit test dengan Maven:

```bash
mvn test
```

Untuk menjalankan satu class test:

```bash
mvn -Dtest=UserServiceImplTest test
```

Di IDE, test juga dapat dijalankan dengan tombol Run pada class atau method yang memiliki anotasi `@Test`. Pastikan hasil akhirnya menunjukkan semua test berhasil; jika gagal, periksa nama test, assertion, dan konfigurasi mock pada bagian Arrange.

## Kesalahan Umum

- **Tidak mengatur return value mock** — mock biasanya mengembalikan nilai default seperti `null`, `false`, atau angka nol.
- **Menguji database sungguhan dalam unit test service** — gunakan mock repository; pengujian integrasi dibahas terpisah.
- **Assertion terlalu sedikit** — test hanya memastikan object tidak `null`, tetapi tidak memeriksa isi pentingnya.
- **Test saling bergantung** — setiap test harus menyiapkan data dan mock sendiri.
- **Menggunakan `@SpringBootTest` untuk semua test** — ini memuat banyak konfigurasi aplikasi dan memperlambat test unit.
- **Memeriksa implementasi, bukan perilaku** — utamakan hasil dan interaksi yang terlihat dari kontrak method.

## Ringkasan

JUnit menyediakan struktur dan assertion untuk menjalankan test otomatis. Mockito membantu mengisolasi unit dari dependency eksternal. Dengan pola Arrange–Act–Assert, nama test yang jelas, dan pemeriksaan untuk kasus sukses serta gagal, unit test dapat menjadi dokumentasi perilaku sekaligus perlindungan terhadap regresi.
