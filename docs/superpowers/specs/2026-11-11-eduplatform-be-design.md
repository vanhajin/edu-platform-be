# EduPlatform BE — thiết kế khởi tạo microservices

## Mục đích

Khởi tạo backend `eduplatform-be` làm nền tảng cho hệ thống giáo dục theo
microservices. Bản đầu tiên cung cấp cấu trúc Maven build thống nhất, thư viện
dùng chung, API gateway và dịch vụ xác thực; mục tiêu là có thể build thành JAR
ngay và mở rộng thêm service sau này.

## Mục tiêu

- Dùng Java 21, Maven, cấu hình YAML và đóng gói JAR.
- Cho phép `parent/mvnw clean package` build toàn bộ module theo đúng thứ tự.
- Cung cấp module `common` được các service import để dùng chung các cấu hình
  MySQL/JPA và các thành phần cơ sở.
- Tạo `api-gateway` bằng Spring Cloud Gateway và `auth-svc` bằng Spring Boot
  Web/JPA.
- Quản lý tập trung phiên bản Spring Boot, Spring Cloud và plugin Maven.

## Không thuộc phạm vi

- Chưa triển khai endpoint đăng nhập, JWT, migration database, service
  discovery/config server, Docker/Kubernetes hay database thật.
- Không đặt mật khẩu database trong Git; các giá trị database có mặc định phát
  triển và cho phép ghi đè qua biến môi trường.

## Ràng buộc và phiên bản

- Java `21`; Maven wrapper hiện có sẽ được dùng.
- Spring Boot `4.1.2`; Spring Cloud BOM `2025.1.2`, là release train tương
  thích với Spring Boot 4.x.
- MySQL Connector/J, Spring Data JPA, Lombok và Spring Cloud được khai báo
  qua BOM/starter thay vì tự pin phiên bản thư viện con.

## Các phương án đã xem xét

1. `parent` là Maven aggregator, còn mọi module là thư mục ngang hàng — **được
   chọn**. Cấu trúc dễ mở rộng và chỉ cần một điểm build.
2. Đặt các module bên trong `parent` — ít thư mục root hơn nhưng các đường dẫn
   và phân tách code khó nhìn hơn.
3. Dùng root aggregator và `parent` thứ hai cho dependency management — chuẩn
   hơn về mặt mô hình Maven nhưng dư một lớp với yêu cầu hiện tại.

## Cấu trúc đã chọn

```text
eduplatform-be/
├── parent/                    # aggregator + dependency/plugin management
├── common/                    # JAR thư viện dùng chung
├── api-gateway/               # JAR Spring Cloud Gateway (reactive)
├── auth-svc/                  # JAR Spring MVC + JPA
└── docs/superpowers/specs/
```

`parent/pom.xml` có packaging `pom`, liệt kê ba module còn lại, import Spring
Cloud BOM và cấu hình Java/compiler/Spring Boot Maven plugin. `common` có
packaging `jar`; gateway và auth service phụ thuộc `common` cùng POM parent.

## Cấu hình và hành vi khởi tạo

- `common` xuất bản auto-configuration nhẹ chứa `DataSourceProperties` để
  chuẩn hoá prefix cấu hình `eduplatform.datasource`; các service có thể dùng
  lại mà không ép gateway phải khởi tạo JPA.
- `auth-svc/application.yml` khai báo datasource MySQL qua biến môi trường,
  JPA Hibernate và cổng `45900`.
- `api-gateway/application.yml` dùng cổng `6868`, có route mẫu `/auth/**` đến
  `http://localhost:8081`; không thêm JPA/MySQL vì gateway không truy cập DB.
- `auth-svc` có actuator health check; ứng dụng khởi động được khi không có
  MySQL chỉ sau khi người dùng cung cấp cấu hình database phù hợp.

## Ghi chú về mã hiện có

Thư mục đã có skeleton Spring Initializr cho `parent`, `api-gateway` và
`auth-svc`. Khi thực thi, các POM/application class/config mẫu này sẽ được
chuyển thành cấu trúc trên và thêm `common`; không đụng đến `.idea`.

## Xác minh và bàn giao

- Chạy Maven Wrapper từ `parent` với `clean package`.
- Xác nhận sinh JAR cho `common`, `api-gateway`, `auth-svc`.
- Đóng gói thư mục `eduplatform-be` (không gồm `target` và `.idea`) thành
  `eduplatform-be.zip` tại workspace.
