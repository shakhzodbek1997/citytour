# City Tour API

**City Tour** — shahar bo‘ylab turlarni yaratish, mavjud turlarni ko‘rish va foydalanuvchilar tomonidan bron qilish (`Booking`) jarayonlarini boshqarish uchun ishlab chiqilgan RESTful Spring Boot ilovasi.

---

## 📋 Loyiha haqida

Ushbu loyiha orqali:

* Shahar bo‘ylab turlarni yaratish va boshqarish
* Mavjud turlarni ko‘rish
* Foydalanuvchilar tomonidan tur bron qilish
* Bron ma'lumotlarini boshqarish
* Kiruvchi ma'lumotlarni tekshirish
* Database sxemasini versiyalash
* Xatoliklarni markazlashgan holda boshqarish
* API'larni Swagger orqali ko‘rish va test qilish

imkoniyatlari taqdim etilgan.

---

## 🛠️ Ishlatilgan texnologiyalar

* **Java:** 25
* **Spring Boot**
* **Spring Data JPA**
* **MySQL**
* **Flyway** — Database migration
* **Jakarta Bean Validation**
* **SpringDoc OpenAPI / Swagger**
* **Maven**
* **JUnit** — Unit testing

---

## 🏗️ Arxitektura

Loyiha klassik **Layered Architecture** tamoyiliga asoslangan:

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
Entity
    ↓
MySQL Database
```

Bunday tuzilma API, biznes mantiqi va Database qatlamlarini bir-biridan ajratib turadi. Bu esa kodni tushunish, test qilish va kelajakda kengaytirishni osonlashtiradi.

---

# 🚀 Loyihani ishga tushirish

## Tizim talablari

Loyihani ishga tushirishdan oldin quyidagilar o‘rnatilgan bo‘lishi kerak:

* **Java 25**
* **Maven 3.8+**
* **MySQL 8.0+**

---

## 1. Loyihani yuklab olish

```bash
git clone <repository-url>
cd city-tour
```

---

## 2. Database yaratish

MySQL CLI, MySQL Workbench, DBeaver yoki boshqa Database boshqaruv dasturi orqali quyidagi Database'ni yarating:

```sql
CREATE DATABASE city_tour_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

---

## 3. Database ulanishini sozlash

Quyidagi faylni oching:

```text
src/main/resources/application.properties
```

Database sozlamalarini tekshiring:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/citytour?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=root

spring.jpa.hibernate.ddl-auto=update

spring.flyway.enabled=true
```

Agar sizning MySQL `username` yoki `password` ma'lumotlaringiz boshqacha bo‘lsa, tegishli qiymatlarni o‘zgartiring.

---

## 4. Loyihani build qilish va testlarni ishga tushirish

```bash
mvn clean install
```

Bu buyruq loyihani build qiladi va mavjud testlarni ishga tushiradi.

---

## 5. Ilovani ishga tushirish

```bash
mvn spring-boot:run
```

Ilova ishga tushgandan so‘ng quyidagi manzilda ishlaydi:

```text
http://localhost:8080
```

---

# 📚 API Documentation

API hujjatlari va endpointlarni test qilish uchun **Swagger UI**:

```text
http://localhost:8080/swagger-ui/index.html
```

Swagger UI orqali:

* Mavjud endpointlarni ko‘rish
* Request va Response modellarini ko‘rish
* API so‘rovlarini yuborish
* API'larni bevosita test qilish

mumkin.

---

# 🧪 Testing

Loyihaning asosiy biznes mantiqi `Service` qatlamida `Unit Test`lar orqali test qilingan.

Barcha testlarni ishga tushirish:

```bash
mvn test
```

Yoki loyihani to‘liq build qilish bilan birga:

```bash
mvn clean install
```

---

# 🗄️ Database Migration

Database sxemasini boshqarish va versiyalash uchun **Flyway** ishlatilgan.

Flyway yordamida:

* Database o‘zgarishlarini versiyalash
* Database sxemasini tartibli boshqarish
* Turli muhitlarda bir xil Database strukturasini yaratish
* Qo‘lda Database o‘zgartirish zaruratini kamaytirish

imkoniyati mavjud.

---

# 📌 Qabul qilingan taxminlar va texnik qarorlar

Talablarda ayrim qismlar aniq ko‘rsatilmagani sababli quyidagi texnik qarorlar qabul qilindi.

## 1. Turlarni o‘chirish — Soft Delete

Turlar Database'dan fizik ravishda o‘chirib tashlanmaydi.

Tur o‘chirilganda uning statusi:

```text
CANCELLED
```

yoki

```text
ARCHIVED
```

holatiga o‘tkaziladi.

Buning asosiy sababi — o‘chirilgan turga tegishli `Booking` tarixi va statistik ma'lumotlarni saqlab qolish.

Shunday qilib, tur o‘chirilganidan keyin ham uning tarixiy ma'lumotlari yo‘qolmaydi.

---

## 2. Bir xil telefon raqamidan bir nechta Booking

`Customer` telefon raqamiga `UNIQUE` constraint qo‘yilmadi.

Buning sababi:

* Bir mijoz bir nechta turga bron qilishi mumkin.
* Bir mijoz bir turning o‘ziga bir necha marta bron qilishi mumkin.

Shuning uchun telefon raqamini yagona qiymat sifatida cheklash ushbu talabga mos kelmaydi.

---

## 3. Customer Entity

Alohida `Customer` Entity va Database table yaratilmadi.

Mijozning asosiy ma'lumotlari:

* Ismi
* Telefon raqami

to‘g‘ridan-to‘g‘ri `Booking` Entity ichida saqlanadi.

Bu qaror loyiha talablarini soddalashtirish va hozirgi funksionallik uchun yetarli bo‘lishi sababli qabul qilindi.

---


# 🔧 Texnik amalga oshirish

## Request Validation

API orqali keladigan ma'lumotlarni tekshirish uchun **Jakarta Bean Validation** ishlatilgan.

Masalan:

```java
@Valid
@NotNull
```

Bu noto‘g‘ri yoki talabga javob bermaydigan ma'lumotlarning biznes mantiqiga kirib borishining oldini oladi.

---

## Global Exception Handling

Xatoliklarni markazlashgan holda boshqarish uchun:

```java
@RestControllerAdvice
```
asosida `GlobalExceptionHandler` yaratilgan.
Xatoliklarni markazlashgan holda tutish va 
foydalanuvchiga aniq, standart javob qaytarish uchun 
@RestControllerAdvice yordamida global GlobalExceptionHandler 
yaratildi. U loyihadagi BusinessLogicException va 
ResourceNotFoundException kabi maxsus (custom) istisnolarni 
tutib, yagona ErrorResponse formatida qaytaradi.
---

## 📚 API Documentation

Swagger UI orqali API endpointlarni ko‘rish va test qilish mumkin:

👉 [Swagger UI](http://localhost:8080/swagger-ui/index.html)

# ⚠️ Ulgurmagan funksionalliklar



---

## Controller Integration Test

`Controller` qatlamini to‘liq HTTP Integration Test orqali qamrab olish uchun `MockMvc` testlari yozilishi rejalashtirilgan edi, ammo vaqt sababli yakunlanmadi.

Shu bilan birga, `Service` qatlamidagi asosiy biznes mantiqi `Unit Test`lar orqali qamrab olingan.

---