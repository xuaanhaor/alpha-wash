# Alpha Wash Store
> ✨ Alpha Wash – Không chỉ là rửa xe, mà là chăm xe đúng cách.

### About Alpha Wash Store
Alpha Wash Store là xưởng chăm sóc xe hơi hiện đại, nơi mang đến trải nghiệm làm đẹp, bảo vệ và nâng tầm giá trị cho xế yêu của bạn.
Đây là repository chính thức cho hệ thống webserver vận hành nền tảng số của Alpha Wash — nơi công nghệ gặp gỡ đam mê detailing.
Trong thời gian tới, Alpha Wash hướng đến việc mở rộng dịch vụ sang mảng bảo trì – bảo dưỡng định kỳ, tích hợp tính năng đăng ký hội viên, hệ thống ưu đãi cá nhân hóa, cùng nhiều tiện ích thông minh giúp khách hàng chăm xe dễ dàng và hiệu quả hơn bao giờ hết.

### Specs

**Languague**:  Java 17 (17.0.6)\
**Build-tool**: Gradle 8.14.2\
**Framework**:  Spring Framework 3.4.7

### Guildline chạy server (để code)

1. Cài đặt Java 17 (https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
2. Chạy lệnh `./gradlew :bootRun` ( Không cần tải gradle nếu chỉ chạy server )

### Guideline chạy server bằng Docker (Recommended)

1. Dùng lệnh `docker-compose up --build` để build image (Khi thay đổi code thì nên chạy lệnh này)
2. Các lần sau nếu code không thay đổi thì chỉ cần chạy `docker-compose up` là được
3. Dừng server `docker-compose down`

### Build file jar
1. Build file jar `./gradlew :bootJar`
2. Using Java 17 and run cmd `java -jar build/libs/alphawash-x.x.x-SNAPSHOT.jar` với `x.x.x` là version sau khi build xong file jar, có thể copy tên file ở thư mục `build/libs`
