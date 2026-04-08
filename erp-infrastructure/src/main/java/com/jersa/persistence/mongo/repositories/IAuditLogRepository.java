package com.jersa.persistence.mongo.repositories;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.jersa.persistence.mongo.documents.AuditLogDocument;

@Repository
public interface IAuditLogRepository extends MongoRepository<AuditLogDocument, ObjectId> {

}
