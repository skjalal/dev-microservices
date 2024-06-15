package com.example.beerorderservice.domain;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import java.sql.Timestamp;
import java.util.Objects;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.descriptor.jdbc.UUIDJdbcType;

@Getter
@Setter
@MappedSuperclass
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseEntity {

  @Id
  @UuidGenerator
  @GeneratedValue(generator = "UUID")
  @JdbcType(value = UUIDJdbcType.class)
  @Column(length = 36, columnDefinition = "varchar", updatable = false, nullable = false)
  UUID id;

  @Version
  Long version;

  @CreationTimestamp
  @Column(updatable = false)
  Timestamp createdDate;

  @UpdateTimestamp
  Timestamp lastModifiedDate;

  public boolean isNew() {
    return Objects.isNull(this.id);
  }
}
