package com.challenge.taskapp.model;

import com.challenge.taskapp.enums.TaskStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.TenantId;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "TASKS")
public final class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = ColumnNames.ID)
    private UUID id;

    @Column(name = ColumnNames.TITLE, nullable = false, length = 100)
    private String title;

    @Column(name = ColumnNames.DESCRIPTION)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = ColumnNames.STATUS, nullable = false)
    private TaskStatus status = TaskStatus.PENDING;

    @Column(name = ColumnNames.CREATED_AT, nullable = false)
    @CreationTimestamp
    private Timestamp createdAt;

    @Column(name = ColumnNames.UPDATED_AT, nullable = false)
    @UpdateTimestamp
    private Timestamp updatedAt;

    @Column(name = ColumnNames.TENANT_ID)
    @TenantId
    private String tenantId;

    public Task(final String title, final String description) {
        this.title = title;
        this.description = description;
    }

    public void setStatus(final TaskStatus newStatus) {
        if (this.status != TaskStatus.IN_PROGRESS && newStatus == TaskStatus.DONE) {
            throw new IllegalArgumentException("Invalid status");
        }
        this.status = newStatus;
    }

    static final class ColumnNames {
        public static final String ID = "ID";
        public static final String TITLE = "TITLE";
        public static final String DESCRIPTION = "DESCRIPTION";
        public static final String STATUS = "STATUS";
        public static final String CREATED_AT = "CREATED_AT";
        public static final String UPDATED_AT = "UPDATED_AT";
        public static final String TENANT_ID = "TENANT_ID";
    }

}
