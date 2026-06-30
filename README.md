# prueba-back-web-digital-document

Microservicio WEB — API REST de consulta de documentos digitales de empleados y republicación de eventos Kafka.

> La arquitectura completa del sistema se documenta en el micro principal:
> **[prueba-back-snk-digital-document](../prueba-back-snk-digital-document/README.md)**

---

## Responsabilidad

Este micro expone la capa de consulta HTTP del sistema y el endpoint de republicación para el batch BTC.
No procesa eventos Kafka ni genera PDFs — solo lee de la base de datos compartida, sirve el PDF desde MinIO y escribe en el Outbox para republicación.

---

## Endpoints

| Método | Ruta | Descripción | Códigos |
|--------|------|-------------|---------|
| `GET` | `/api/v1/employees/{employeeId}/managed-groups/{managedGroupId}/document` | Documento de un empleado concreto | 200 / 404 |
| `GET` | `/api/v1/documents/{documentId}` | Documento por ID | 200 / 404 |
| `GET` | `/api/v1/documents/{documentId}/status` | Estado actual del documento | 200 / 404 |
| `GET` | `/api/v1/documents/{documentId}/content` | Redirect 302 al PDF en MinIO (URL firmada, 120s) | 302 / 404 / 422 |
| `GET` | `/api/v1/documents?status={status}&page={p}&size={s}` | Listado paginado filtrado por estado | 200 / 400 |
| `POST` | `/api/v1/utils/documents/republish` | Republicación de evento Kafka (llamado por BTC) | 202 / 400 |

### Estados válidos para el listado

`PENDING` · `ENRICHED` · `PDF_GENERATED` · `STORED` · `PUBLISHED` · `FAILED`

### Endpoint de contenido PDF

`GET /api/v1/documents/{documentId}/content` devuelve un **302 redirect** a la URL firmada de MinIO.
El navegador sigue el redirect y muestra el PDF directamente sin pasar los bytes por la API.

- 302 → documento con `storageKey` informado
- 422 → documento existe pero aún no tiene PDF almacenado (`storageKey` null)
- 404 → documento no encontrado

---

## Stack técnico

- Java 21, Spring Boot 3.3.x, fwkcna-parent 5.2.1
- PostgreSQL — solo lectura de `digital_document` + escritura en `o_outbox`
- MinIO (local) / NetApp (entornos) — acceso vía `fwkcna-starter-buckets`
- Transactional Outbox (`fwkcna-starter-outbox-avro-jpa-register`) para republicación a Kafka
- MapStruct, API-First con `openapi-generator-maven-plugin` (contrato en `driving/api-rest/contracts/digital-document-api.yaml`)
- Flyway **deshabilitado** — el esquema lo gestiona el SNK

---

## Arquitectura hexagonal

```
driving/api-rest          → Controllers (implementan interfaces generadas por OpenAPI)
application               → Use cases + ports
driven/postgres-repository → Adapters JPA, Outbox y BucketService
boot                      → Configuración y arranque
```

---

## Infraestructura local requerida

| Servicio | Host | Puerto | Credenciales |
|----------|------|--------|--------------|
| PostgreSQL | localhost | 5432 | sa / root |
| MinIO | localhost | 9000 | minio / minio123 |
| Kafka | localhost | 29092 | — |
| Schema Registry | localhost | 9081 | — |

Todos los contenedores se levantan desde el docker-compose del SNK.

---

## Arranque local

```bash
# Desde la raíz del proyecto
mvn clean package -DskipTests

/path/to/java21/bin/java \
  -jar boot/target/prueba-back-web-digital-document-boot-1.0.0-SNAPSHOT.jar \
  --spring.profiles.active=local
```

Puerto: **8083**

Requiere que el SNK haya ejecutado las migraciones Flyway previamente.

---

## Colección Postman

Disponible en `devops/postman/prueba-back-web-digital-document.postman_collection.json`.

Importar en Postman y ajustar la variable `baseUrl` si es necesario (default: `http://localhost:8083`).

> Para ver el `302` del endpoint de contenido: desactiva **Follow Redirects** en Settings de Postman.
