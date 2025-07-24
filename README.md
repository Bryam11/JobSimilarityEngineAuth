# Auth Service - Sistema de Autenticación

Este servicio proporciona registro de usuarios y autenticación mediante JWT con firma asimétrica RSA.

## Características

- Registro de usuarios con campos requeridos (nombres completos, email, contraseña) y opcionales (título profesional, empresa)
- Autenticación mediante JWT firmado con clave privada RSA
- Endpoint para obtener la clave pública RSA para validación en el gateway
- Arquitectura hexagonal (puertos y adaptadores)
- Persistencia en PostgreSQL

## Requisitos

- Java 17+
- PostgreSQL
- Maven

## Configuración

### Configuración de Base de Datos

Ajusta la configuración de PostgreSQL en `application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/auth_db
spring.datasource.username=postgres
spring.datasource.password=postgres
```

### Generación de Claves RSA para JWT (Opción Manual)

Para generar manualmente el par de claves RSA, ejecuta los siguientes comandos OpenSSL:

1. Generar la clave privada RSA:
   ```
   openssl genrsa -out src/main/resources/app.key 2048
   ```

2. Extraer la clave pública de la clave privada:
   ```
   openssl rsa -in src/main/resources/app.key -pubout -out src/main/resources/app.pub
   ```

3. Ajusta la configuración JWT en `application.properties`:
   ```properties
   jwt.private.key=classpath:app.key
   jwt.public.key=classpath:app.pub
   jwt.expiration.time=86400000
   ```

> **Nota**: La implementación actual genera las claves RSA automáticamente en tiempo de ejecución a través de la clase `JwtTokenProvider`, por lo que los pasos anteriores son opcionales.

## Endpoints

### Registro de Usuario
```
POST /api/auth/register
```
Cuerpo de la solicitud:
```json
{
  "fullName": "Nombre Completo",
  "email": "usuario@ejemplo.com",
  "professionalTitle": "Ingeniero de Software", // Opcional
  "company": "TecAzuay", // Opcional
  "password": "contraseña123"
}
```

### Login
```
POST /api/auth/login
```
Cuerpo de la solicitud:
```json
{
  "email": "usuario@ejemplo.com",
  "password": "contraseña123"
}
```

### Obtener Clave Pública
```
GET /api/auth/public-key
```
Respuesta: String con la clave pública en formato Base64

## Integración con Gateway

Para validar tokens JWT en el gateway, implementa un validador usando la clave pública:

```java
@Component
public class JwtTokenValidator {
    
    private final PublicKey publicKey;
    
    public JwtTokenValidator(@Value("${jwt.public.key.base64}") String publicKeyBase64) {
        this.publicKey = getPublicKeyFromBase64(publicKeyBase64);
    }
    
    public Claims validateToken(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(publicKey)
            .build()
            .parseClaimsJws(token)
            .getBody();
    }
    
    private PublicKey getPublicKeyFromBase64(String publicKeyBase64) {
        try {
            byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyBase64);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(keySpec);
        } catch (Exception e) {
            throw new RuntimeException("Error reconstructing public key", e);
        }
    }
}
```

## Estructura del Proyecto (Arquitectura Hexagonal)

- **domain/** - Lógica de negocio central
  - **model/** - Entidades de dominio
  - **port/** - Interfaces (puertos) para comunicarse con el exterior
  - **service/** - Implementación de la lógica de negocio

- **application/** - Capa de aplicación
  - **controller/** - Controladores REST
  - **dto/** - Objetos de transferencia de datos

- **infrastructure/** - Adaptadores e implementaciones técnicas
  - **config/** - Configuraciones (Spring Security, etc.)
  - **persistence/** - Implementación de repositorios
    - **entity/** - Entidades JPA
