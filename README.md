# prueba-back-web-digital-document

Microservicio WEB — API REST de consulta y republicación de documentos digitales de empleados.

> La arquitectura completa del sistema se documenta en el micro principal:
> **[prueba-back-snk-digital-document](../prueba-back-snk-digital-document/README.md)**

---

## Responsabilidad

Este micro expone la capa de consulta HTTP del sistema y el endpoint de republicación para el batch BTC.
No procesa eventos Kafka ni genera PDFs — solo lee de la base de datos compartida y escribe en el Outbox.

---

## Endpoints

| Método | Ruta                                                              | Descripción                               | Código   |
|--------|-------------------------------------------------------------------|-------------------------------------------|----------|
| GET    | `/api/v1/employees/{employeeId}/managed-groups/{mg}/document`    | Documento de un empleado concreto          | 200/404  |
| GET    | `/api/v1/documents/{documentId}`                                  | Documento por ID                           | 200/404  |
| GET    | `/api/v1/documents/{documentId}/status`                           | Estado actual del documento                | 200/404  |
| GET    | `/api/v1/documents?status={status}&page={p}&size={s}`             | Listado filtrado por estado                | 200/400  |
| POST   | `/api/v1/documents/{documentId}/retry`                            | Reinicio manual de backoff (FAILED→batch)  | 202/404/409 |
| POST   | `/api/v1/utils/documents/republish`                               | Republicación (llamado por BTC)            | 202      |

---

## Stack técnico

- Java 21, Spring Boot 3.3.x, fwkcna-parent 5.2.1
- PostgreSQL (solo lectura de `digital_document` + escritura en `o_outbox`)
- Transactional Outbox (fwkcna-starter-outbox-avro-jpa-register) para republicación
- MapStruct, OpenAPI (contratos en `driving/api-rest/contracts/`)
- Flyway **deshabilitado** (el esquema lo gestiona el SNK)

---

## Arranque local

```bash
# Puerto: 8083
mvn -f /ruta/prueba-back-web-digital-document/pom.xml --projects boot \
  spring-boot:run -Dspring-boot.run.profiles=local
```

Requiere que el SNK haya ejecutado las migraciones Flyway y que PostgreSQL esté levantado.
