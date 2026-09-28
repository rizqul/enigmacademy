# 16. Rest Client - Mengakses REST API dari Spring

## Tujuan Pembelajaran

Pada langkah ini, kita akan mempelajari cara aplikasi Spring bertindak sebagai **client** untuk memanggil REST API lain. Kita menggunakan `RestClient` untuk mengakses JSONPlaceholder, lalu menyediakan endpoint lokal untuk membaca dan mengelola data comment.

## Konsep-konsep Penting

### REST client

REST client adalah bagian dari aplikasi yang mengirim HTTP request ke aplikasi lain. Pada langkah sebelumnya, controller menerima request dari client. Di sini, `CommentServiceImpl` juga mengirim request ke layanan eksternal, yaitu `https://jsonplaceholder.typicode.com`.

```text
Client aplikasi -> CommentController -> CommentService -> JSONPlaceholder
```

`RestClient` adalah HTTP client sinkron dari Spring. Artinya, pemanggilan akan menunggu response sebelum melanjutkan proses. Untuk aplikasi reaktif/non-blocking, Spring menyediakan pilihan lain seperti `WebClient`.

### JSONPlaceholder

JSONPlaceholder adalah API publik untuk latihan. API ini menyediakan resource `/comments`, misalnya:

- `GET /comments` untuk mengambil daftar comment
- `GET /comments/1` untuk mengambil satu comment
- `POST /comments` untuk mengirim comment baru
- `PUT /comments/1` untuk memperbarui comment
- `DELETE /comments/1` untuk menghapus comment

Endpoint lokal aplikasi menggunakan prefix `/api/v1/json/comments`, sedangkan URL tujuan eksternal menggunakan `/comments`.

### `RestClient` sebagai bean

Konfigurasi client berada di `config/RestClientConfig.java`. Base URL dibaca dari properti `api.jsonplaceholder.base-url`, dengan nilai default jika properti tidak tersedia. Dengan `baseUrl`, service cukup menulis path seperti `/comments`.

Konfigurasi ini membuat dua bean: `restClient` dan `placeholderClient`. Karena `restClient` diberi `@Primary`, injeksi `RestClient` tanpa qualifier di `CommentServiceImpl` akan menggunakan bean tersebut. Bean `placeholderClient` memiliki default header, interceptor, dan timeout, tetapi belum dipakai oleh service saat ini.

```properties
api.jsonplaceholder.base-url=https://jsonplaceholder.typicode.com/
```

Bean membantu kita memusatkan konfigurasi, lalu menyuntikkannya ke service melalui constructor. `@RequiredArgsConstructor` Lombok membuat constructor untuk field `final`:

```java
private final RestClient restClient;
```

### Alur request dengan `RestClient`

Pola umum request adalah memilih method HTTP, menentukan URI, menambahkan header atau request body bila perlu, lalu mengambil response:

```java
restClient.get()
    .uri("/comments/1")
    .accept(MediaType.APPLICATION_JSON)
    .retrieve()
    .body(CommentResponse.class);
```

- `.get()`, `.post()`, `.put()`, dan `.delete()` memilih HTTP method.
- `.uri(...)` memilih path pada server tujuan.
- `.accept(...)` menyatakan format response yang dapat diterima.
- `.body(...)` pada request POST/PUT mengirim data.
- `.retrieve()` menjalankan request dan membuka proses membaca response.
- `.body(T.class)` mengubah response JSON menjadi objek Java.
- `.toBodilessEntity()` digunakan saat response tidak perlu dibaca sebagai objek.

### Mapping JSON ke model

Kelas `model/Comment.java` memiliki field yang sesuai dengan struktur JSON dari JSONPlaceholder: `postId`, `id`, `name`, `email`, dan `body`. Spring menggunakan converter JSON yang tersedia untuk mengisi object tersebut.

Untuk satu object, gunakan `body(Comment.class)`. Untuk daftar object, gunakan `ParameterizedTypeReference<List<Comment>>` agar informasi generic `List<Comment>` tetap tersedia saat response dipetakan.

```java
.body(new ParameterizedTypeReference<List<Comment>>() {})
```

## Struktur Implementasi

```text
config/
  RestClientConfig.java       # Membuat dan mengonfigurasi RestClient
model/
  Comment.java                # Bentuk data comment dari API eksternal
dto/request/
  CommentRequest.java         # Data yang diterima endpoint lokal
dto/response/
  CommentResponse.java        # Bentuk response untuk satu comment
service/
  CommentService.java         # Kontrak operasi comment
service/impl/
  CommentServiceImpl.java     # Memanggil JSONPlaceholder
controller/
  CommentController.java      # Endpoint lokal untuk comment
```

`CommentRequest` dan `CommentResponse` adalah DTO aplikasi, sedangkan `Comment` adalah model yang dipakai untuk memetakan data eksternal. Bentuk field-nya saat ini mirip karena mengikuti format JSONPlaceholder.

## Implementasi Operasi

### Membaca semua comment

`getAllComments()` mengirim GET ke `/comments`. Karena response berupa array JSON, hasilnya dipetakan menjadi `List<Comment>`.

```java
restClient.get()
    .uri("/comments")
    .accept(MediaType.APPLICATION_JSON)
    .retrieve()
    .body(new ParameterizedTypeReference<List<Comment>>() {});
```

### Membuat comment

`addComment()` mengirim object `CommentRequest` sebagai request body. Response kemudian dipetakan ke `Comment`.

```java
restClient.post()
    .uri("/comments")
    .accept(MediaType.APPLICATION_JSON)
    .body(commentRequest)
    .retrieve()
    .body(Comment.class);
```

### Membaca satu comment

ID menjadi bagian dari path tujuan. Untuk kejelasan dan keamanan konstruksi URI, nilai path dapat diberikan sebagai variabel URI:

```java
restClient.get()
    .uri("/comments/{id}", id)
    .accept(MediaType.APPLICATION_JSON)
    .retrieve()
    .body(CommentResponse.class);
```

Pada implementasi saat ini, service membentuk path dengan menggabungkan string `"/comments/" + id`.

### Memperbarui dan menghapus comment

PUT mengirim request body serta ID pada path. DELETE hanya membutuhkan ID dan response-nya tidak dibaca sebagai object.

```java
// PUT
restClient.put()
    .uri("/comments/{id}", id)
    .body(request)
    .retrieve()
    .body(CommentResponse.class);

// DELETE
restClient.delete()
    .uri("/comments/{id}", id)
    .retrieve()
    .toBodilessEntity();
```

Keempat operasi tersebut dipanggil oleh `CommentController` melalui `CommentService`. Controller membungkus hasil service menggunakan `ResponseUtil` dan `CommonResponse`.

## Endpoint Aplikasi

| Operasi | Method lokal | Endpoint lokal | Endpoint eksternal |
|---|---|---|---|
| Melihat semua comment | GET | `/api/v1/json/comments` | `/comments` |
| Melihat comment berdasarkan ID | GET | `/api/v1/json/comments/{id}` | `/comments/{id}` |
| Menambahkan comment | POST | `/api/v1/json/comments` | `/comments` |
| Memperbarui comment | PUT | `/api/v1/json/comments/{id}` | `/comments/{id}` |
| Menghapus comment | DELETE | `/api/v1/json/comments/{id}` | `/comments/{id}` |

Contoh request lokal untuk mengambil comment dengan ID 1:

```http
GET http://localhost:8080/api/v1/json/comments/1
```

Contoh body POST:

```json
{
  "postId": 1,
  "name": "Belajar REST Client",
  "email": "student@example.com",
  "body": "Contoh comment untuk latihan."
}
```

## Penanganan Error

Pemanggilan jaringan dapat gagal, misalnya karena server eksternal tidak tersedia atau mengembalikan HTTP error. Implementasi service saat ini menangkap exception dan menulis pesan ke log. Beberapa method mengembalikan `null`, sedangkan `getAllComments()` mengembalikan list kosong.

Pola ini menjaga exception tidak langsung keluar dari service, tetapi `null` atau list kosong dapat membuat controller sulit membedakan kegagalan dari hasil yang memang kosong. Pada aplikasi nyata, pertimbangkan menerjemahkan kegagalan upstream menjadi exception aplikasi yang jelas dan memetakannya ke status HTTP yang sesuai. Hindari mengirim detail teknis exception kepada pengguna API.

`deleteComment()` juga memvalidasi ID sebelum melakukan request. Validasi input dan penanganan error dari layanan eksternal adalah dua hal berbeda: validasi mencegah request yang tidak masuk akal, sedangkan exception menangani kegagalan saat komunikasi.

## Catatan untuk Latihan

JSONPlaceholder adalah layanan simulasi. Operasi POST, PUT, dan DELETE memberi response seolah-olah perubahan berhasil, tetapi datanya tidak disimpan secara permanen. Karena itu, setelah membuat atau menghapus comment, hasil GET berikutnya belum tentu mencerminkan perubahan tersebut.

Selain itu, pada `CommentController`, method GET semua comment saat ini membangun response dengan `HttpStatus.CREATED`. Secara semantik HTTP, GET yang berhasil biasanya menggunakan `HttpStatus.OK`; siswa dapat menjadikan ini latihan untuk meninjau status code pada controller.

